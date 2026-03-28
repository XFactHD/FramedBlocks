package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.component.CollapsibleCopycatBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.model.data.ModelData;

public class FramedCollapsibleCopycatBlockEntity extends FramedBlockEntity implements CollapsibleCopycatBlockEntity {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int MAX_OFFSET_BEACON_OCCLUSION = 5;
    private static final int FACE_OFFSET_BITS = 4;
    public static final int OFFSET_BITS = FACE_OFFSET_BITS * DIRECTIONS.length;

    private int packedOffsets = 0;
    private boolean occludesBeacon = true;

    public FramedCollapsibleCopycatBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value(), pos, state);
    }

    public void handleDeform(Player player) {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        Direction faceHit = blockHit.getDirection();
        boolean sneak = player.isShiftKeyDown();
        boolean changed = false;
        Rotation rotation = getBlockState().getValue(PropertyHolder.COPYCAT_ROTATION);
        int offset = getFaceOffset(faceHit, rotation);
        if (sneak && offset > 0) {
            setFaceOffset(faceHit, rotation, offset - 1);
            changed = true;
        } else if (!sneak && offset < 15 - getFaceOffset(faceHit.getOpposite(), rotation)) {
            setFaceOffset(faceHit, rotation, offset + 1);
            changed = true;
        }
        if (changed) {
            updateBeaconOcclusion();
            if (!updateFaceSolidity()) {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            setChangedWithoutSignalUpdate();
        }
    }

    private void setFaceOffset(Direction side, Rotation rotation, int offset) {
        int idx = rotation.rotate(side).ordinal() * 4;
        int mask = 0x0F << idx;
        packedOffsets = (packedOffsets & ~mask) | (offset << idx);
    }

    public int getFaceOffset(Direction side, Rotation rotation) {
        int srcIdx = rotation.rotate(side).ordinal();
        return (byte) (packedOffsets >> (srcIdx * 4) & 0xF);
    }

    @Override
    public int getFaceOffset(BlockState state, Direction side) {
        return getFaceOffset(side, state.getValue(PropertyHolder.COPYCAT_ROTATION));
    }

    @Override
    public int getPackedOffsets(BlockState state) {
        return packedOffsets;
    }

    public boolean doesOccludeBeaconBeam() {
        return occludesBeacon;
    }

    public boolean updateFaceSolidity() {
        BlockState state = getBlockState();
        int solid = computeSolidFaces(packedOffsets, state.getValue(PropertyHolder.COPYCAT_ROTATION));
        if (state.getValue(PropertyHolder.SOLID_FACES) != solid) {
            level().setBlockAndUpdate(worldPosition, state.setValue(PropertyHolder.SOLID_FACES, solid));
            return true;
        }
        return false;
    }

    private void updateBeaconOcclusion() {
        occludesBeacon = true;
        for (Direction face : HORIZONTAL_DIRECTIONS) {
            if (getFaceOffset(face, Rotation.NONE) > MAX_OFFSET_BEACON_OCCLUSION) {
                occludesBeacon = false;
                break;
            }
        }
    }

    @Override
    public void setBlockState(BlockState state) {
        Rotation oldRot = getBlockState().getValue(PropertyHolder.COPYCAT_ROTATION);
        super.setBlockState(state);
        if (level != null && !level.isClientSide() && oldRot != state.getValue(PropertyHolder.COPYCAT_ROTATION)) {
            updateFaceSolidity();
        }
    }

    @Override
    public void onLoad() {
        if (!level().isClientSide()) {
            updateFaceSolidity();
        }
        super.onLoad();
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder) {
        int offsets = getPackedOffsets(getBlockState());
        builder.with(PackedCollapsibleBlockOffsets.PROPERTY, new PackedCollapsibleBlockOffsets.Single(offsets));
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput) {
        super.writeToDataPacket(valueOutput);
        valueOutput.putInt("offsets", packedOffsets);
        valueOutput.putBoolean("occludesBeacon", occludesBeacon);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input) {
        super.readFromDataPacket(input);

        int packed = input.getIntOr("offsets", 0);
        if (packed != packedOffsets) {
            packedOffsets = packed;

            input.requestRenderUpdate();
            input.requestCullingUpdate();
        }

        occludesBeacon = input.getBooleanOr("occludesBeacon", true);
    }

    @Override
    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData) {
        return blueprintData.withCustomData(FBContent.DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA, new CollapsibleCopycatBlockData(packedOffsets));
    }

    @Override
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) {
        if (auxData.value() instanceof CollapsibleCopycatBlockData(int offsets)) {
            packedOffsets = offsets;
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard("offsets");
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder) {
        builder.set(FBContent.DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA, new CollapsibleCopycatBlockData(packedOffsets));
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input) {
        CollapsibleCopycatBlockData blockData = input.get(FBContent.DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA);
        if (blockData != null) {
            packedOffsets = blockData.offsets();
            updateFaceSolidity();
            updateBeaconOcclusion();
        }
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("offsets", packedOffsets);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        packedOffsets = valueInput.getIntOr("offsets", 0);
        updateBeaconOcclusion();
    }

    public static byte[] unpackOffsets(int packed, Rotation rotation) {
        byte[] offsets = new byte[DIRECTIONS.length];
        for (Direction face : DIRECTIONS) {
            int srcIdx = rotation.rotate(face).ordinal();
            offsets[face.ordinal()] = (byte) (packed >> (srcIdx * 4) & 0xF);
        }
        return offsets;
    }

    public static int computeSolidFaces(int packedOffsets, Rotation rotation) {
        int solid = 0;
        for (Direction face : DIRECTIONS) {
            int srcIdx = rotation.rotate(face).ordinal();
            if (((packedOffsets >> (srcIdx * 4)) & 0xF) == 0) {
                solid |= (1 << face.ordinal());
            }
        }
        return solid;
    }
}
