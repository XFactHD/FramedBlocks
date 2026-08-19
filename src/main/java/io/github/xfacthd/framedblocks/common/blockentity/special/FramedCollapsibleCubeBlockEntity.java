package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.block.blockentity.RotationSource;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.component.CollapsibleCubeData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.model.data.ModelData;

public class FramedCollapsibleCubeBlockEntity extends FramedBlockEntity implements CollapsibleCubeBlockEntity {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final int MAX_OFFSET_BEACON_OCCLUSION = 5;

    private int packedOffsets = 0;
    private boolean occludesBeacon = true;

    public FramedCollapsibleCubeBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_COLLAPSIBLE_CUBE.value(), pos, state);
    }

    public void handleDeform(Player player) {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        Direction faceHit = blockHit.getDirection();
        boolean sneak = player.isShiftKeyDown();
        boolean changed = false;
        int offset = getFaceOffset(faceHit);
        if (sneak && offset > 0) {
            setFaceOffset(faceHit, offset - 1);
            changed = true;
        } else if (!sneak && offset < 15 - getFaceOffset(faceHit.getOpposite())) {
            setFaceOffset(faceHit, offset + 1);
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

    private void setFaceOffset(Direction side, int offset) {
        int idx = side.ordinal() * 4;
        int mask = 0x0F << idx;
        packedOffsets = (packedOffsets & ~mask) | (offset << idx);
    }

    public int getFaceOffset(Direction side) {
        return (byte) (packedOffsets >> (side.ordinal() * 4) & 0xF);
    }

    @Override
    public int getFaceOffset(BlockState state, Direction side) {
        return getFaceOffset(side);
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
        int solid = computeSolidFaces(packedOffsets);
        if (state.getValue(PropertyHolder.SOLID_FACES) != solid) {
            level().setBlockAndUpdate(worldPosition, state.setValue(PropertyHolder.SOLID_FACES, solid));
            return true;
        }
        return false;
    }

    private void updateBeaconOcclusion() {
        occludesBeacon = true;
        for (Direction face : HORIZONTAL_DIRECTIONS) {
            if (getFaceOffset(face) > MAX_OFFSET_BEACON_OCCLUSION) {
                occludesBeacon = false;
                break;
            }
        }
    }

    @Override
    protected boolean applyExternalRotation(Mirror mirror, Rotation rotation, RotationSource source) {
        int prevOffsets = packedOffsets;
        if (mirror != Mirror.NONE) {
            Direction.Axis mirrorAxis = DirUtils.getMirrorAxis(mirror);
            int offNeg = getFaceOffset(mirrorAxis.getNegative());
            int offPos = getFaceOffset(mirrorAxis.getPositive());
            setFaceOffset(mirrorAxis.getNegative(), offPos);
            setFaceOffset(mirrorAxis.getPositive(), offNeg);
        }
        if (rotation != Rotation.NONE) {
            int[] horOffsets = new int[4];
            for (Direction dir : HORIZONTAL_DIRECTIONS) {
                horOffsets[dir.get2DDataValue()] = getFaceOffset(dir);
            }
            for (Direction dir : HORIZONTAL_DIRECTIONS) {
                setFaceOffset(rotation.rotate(dir), horOffsets[dir.get2DDataValue()]);
            }
        }
        boolean offsetsChanged = packedOffsets != prevOffsets;
        if (source == RotationSource.STRUCTURE || offsetsChanged) {
            boolean camoChanged = super.applyExternalRotation(mirror, rotation, source);
            return offsetsChanged || camoChanged;
        }
        return false;
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
        return blueprintData.withCustomData(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA, new CollapsibleCubeData(packedOffsets));
    }

    @Override
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) {
        if (auxData.value() instanceof CollapsibleCubeData(int offsets)) {
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
        builder.set(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA, new CollapsibleCubeData(packedOffsets));
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input) {
        CollapsibleCubeData blockData = input.get(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA);
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

    public static byte[] unpackOffsets(int packed) {
        byte[] offsets = new byte[DIRECTIONS.length];
        for (int i = 0; i < DIRECTIONS.length; i++) {
            offsets[i] = (byte) (packed >> (i * 4) & 0xF);
        }
        return offsets;
    }

    public static int computeSolidFaces(int packedOffsets) {
        int solid = 0;
        for (Direction face : DIRECTIONS) {
            if (((packedOffsets >> (face.ordinal() * 4)) & 0xF) == 0) {
                solid |= (1 << face.ordinal());
            }
        }
        return solid;
    }

    public static int rotateSolidFaces(int solidFaces, Rotation rotation) {
        boolean[] horSolidFaces = new boolean[4];
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            horSolidFaces[dir.get2DDataValue()] = MathUtils.readBit(solidFaces, dir.ordinal());
        }
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            Direction outDir = rotation.rotate(dir);
            solidFaces = MathUtils.writeBit(solidFaces, outDir.ordinal(), horSolidFaces[dir.get2DDataValue()]);
        }
        return solidFaces;
    }

    public static int mirrorSolidFaces(int solidFaces, Mirror mirror) {
        Direction.Axis mirrorAxis = DirUtils.getMirrorAxis(mirror);
        boolean solidNeg = MathUtils.readBit(solidFaces, mirrorAxis.getNegative().ordinal());
        boolean solidPos = MathUtils.readBit(solidFaces, mirrorAxis.getPositive().ordinal());
        solidFaces = MathUtils.writeBit(solidFaces, mirrorAxis.getNegative().ordinal(), solidPos);
        solidFaces = MathUtils.writeBit(solidFaces, mirrorAxis.getPositive().ordinal(), solidNeg);
        return solidFaces;
    }
}
