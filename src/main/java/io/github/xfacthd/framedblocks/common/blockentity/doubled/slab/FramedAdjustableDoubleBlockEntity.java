package io.github.xfacthd.framedblocks.common.blockentity.doubled.slab;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.slab.FramedAdjustableDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.special.CollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.special.CollapsibleCubeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.component.AdjustableDoubleBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;

public class FramedAdjustableDoubleBlockEntity extends FramedDoubleBlockEntity implements CollapsibleBlockEntity, CollapsibleCubeBlockEntity {
    private static final int MIN_PART_HEIGHT = 1;
    private static final int MAX_PART_HEIGHT = 15;
    public static final int CENTER_PART_HEIGHT = 8;

    private int firstHeight = CENTER_PART_HEIGHT;

    public FramedAdjustableDoubleBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_ADJ_DOUBLE_BLOCK.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction facing = getFacing(getBlockState());
        Direction face = hit.getDirection();
        if (face == facing.getOpposite()) {
            return false;
        }
        if (face == facing) {
            return true;
        }

        int y = (int) (MathUtils.fractionInDir(hit.getLocation(), facing) * 16F);
        return y >= firstHeight;
    }

    public boolean handleDeform(Player player) {
        HitResult hit = player.pick(10D, 1F, false);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return false;
        }

        Direction facing = getFacing(getBlockState());
        Direction faceHit = blockHit.getDirection();
        if (faceHit.getAxis() != facing.getAxis()) {
            return false;
        }

        if (!level().isClientSide()) {
            boolean upwards = faceHit == facing.getOpposite() ^ player.isShiftKeyDown();
            boolean changed = false;
            if (!upwards && firstHeight > MIN_PART_HEIGHT) {
                firstHeight--;
                changed = true;
            } else if (upwards && firstHeight < MAX_PART_HEIGHT) {
                firstHeight++;
                changed = true;
            }
            if (changed) {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                setChangedWithoutSignalUpdate();
            }
        }
        return true;
    }

    @Override
    public int getVertexOffset(BlockState state, int vertex) {
        DoubleBlockParts parts = getParts();
        if (state == parts.stateOne()) {
            return 16 - firstHeight;
        }
        if (state == parts.stateTwo()) {
            return firstHeight;
        }
        return 0;
    }

    @Override
    public int getFaceOffset(BlockState state, Direction side) {
        Direction facing = getFacing(getBlockState());
        DoubleBlockParts parts = getParts();
        if (state == parts.stateOne() && side == facing) {
            return 16 - firstHeight;
        }
        if (state == parts.stateTwo() && side == facing.getOpposite()) {
            return firstHeight;
        }
        return 0;
    }

    @Override
    public int getPackedOffsets(BlockState state) {
        DoubleBlockParts parts = getParts();
        if (state == parts.stateOne()) {
            return packOffsets(getBlockState(), firstHeight, false);
        }
        if (state == parts.stateTwo()) {
            return packOffsets(getBlockState(), firstHeight, true);
        }
        return 0;
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder) {
        builder.with(PackedCollapsibleBlockOffsets.PROPERTY, packDoubleOffsets(getBlockState(), firstHeight));
    }

    private static Direction getFacing(BlockState state) {
        return ((FramedAdjustableDoubleBlock) state.getBlock()).getFacing(state);
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput) {
        super.writeToDataPacket(valueOutput);
        valueOutput.putInt("first_height", firstHeight);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input) {
        super.readFromDataPacket(input);

        int height = input.getIntOr("first_height", CENTER_PART_HEIGHT);
        if (height != firstHeight) {
            firstHeight = height;

            input.requestRenderUpdate();
            input.requestCullingUpdate();
        }
    }

    @Override
    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData) {
        return blueprintData.withCustomData(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA, new AdjustableDoubleBlockData(firstHeight));
    }

    @Override
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) {
        if (auxData.value() instanceof AdjustableDoubleBlockData(int height)) {
            firstHeight = height;
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard("first_height");
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder) {
        builder.set(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA, new AdjustableDoubleBlockData(firstHeight));
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input) {
        AdjustableDoubleBlockData blockData = input.get(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA);
        if (blockData != null) {
            firstHeight = blockData.firstHeight();
        }
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("first_height", firstHeight);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        firstHeight = valueInput.getIntOr("first_height", CENTER_PART_HEIGHT);
    }

    public static int packOffsets(BlockState state, int firstHeight, boolean right) {
        Direction facing = getFacing(state);
        if (right) {
            facing = facing.getOpposite();
        } else {
            firstHeight = 16 - firstHeight;
        }
        return firstHeight << (facing.ordinal() * 4);
    }

    public static PackedCollapsibleBlockOffsets packDoubleOffsets(BlockState state, int firstHeight) {
        DoubleBlockParts parts = ((IFramedDoubleBlock) state.getBlock()).getCache(state).getParts();
        return new PackedCollapsibleBlockOffsets.Double(parts, packOffsets(state, firstHeight, false), packOffsets(state, firstHeight, true));
    }
}
