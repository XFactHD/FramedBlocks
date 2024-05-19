package xfacthd.framedblocks.common.block.stairs.vertical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.data.property.StairsType;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.function.Consumer;

public class FramedVerticalDoubleStairsBlock extends FramedVerticalStairsBlock implements IFramedDoubleBlock
{
    public FramedVerticalDoubleStairsBlock()
    {
        super(BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        FramedUtils.removeProperty(builder, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        BlockState partTwo = switch (type)
        {
            case VERTICAL -> FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getOpposite());
            case TOP_FWD -> FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite())
                    .setValue(FramedProperties.TOP, true);
            case TOP_CCW -> FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getClockWise())
                    .setValue(FramedProperties.TOP, true)
                    .setValue(PropertyHolder.RIGHT, true);
            case TOP_BOTH -> FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite())
                    .setValue(PropertyHolder.STAIRS_TYPE, StairsType.BOTTOM_BOTH);
            case BOTTOM_FWD -> FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
            case BOTTOM_CCW -> FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getClockWise())
                    .setValue(PropertyHolder.RIGHT, true);
            case BOTTOM_BOTH -> FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite())
                    .setValue(PropertyHolder.STAIRS_TYPE, StairsType.TOP_BOTH);
        };

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(PropertyHolder.STAIRS_TYPE, type),
                partTwo
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if (side == facing)
        {
            return switch (type)
            {
                case VERTICAL, TOP_CCW, BOTTOM_CCW ->
                        CamoGetter.FIRST;
                case TOP_FWD, TOP_BOTH ->
                        CamoGetter.get(edge == facing.getCounterClockWise() || edge == Direction.DOWN, false);
                case BOTTOM_FWD, BOTTOM_BOTH ->
                        CamoGetter.get(edge == facing.getCounterClockWise() || edge == Direction.UP, false);
            };
        }
        if (side == facing.getCounterClockWise())
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, BOTTOM_FWD ->
                        CamoGetter.FIRST;
                case TOP_CCW, TOP_BOTH ->
                        CamoGetter.get(edge == facing || edge == Direction.DOWN, false);
                case BOTTOM_CCW, BOTTOM_BOTH ->
                        CamoGetter.get(edge == facing || edge == Direction.UP, false);
            };
        }
        if (side == Direction.UP)
        {
            return switch (type)
            {
                case VERTICAL, BOTTOM_FWD, BOTTOM_CCW, BOTTOM_BOTH ->
                        CamoGetter.get(edge == facing || edge == facing.getCounterClockWise(), false);
                case TOP_CCW ->
                        CamoGetter.get(edge == facing, edge == facing.getOpposite());
                case TOP_FWD ->
                        CamoGetter.get(edge == facing.getCounterClockWise(), edge == facing.getClockWise());
                case TOP_BOTH ->
                        CamoGetter.get(false, edge == facing.getOpposite() || edge == facing.getClockWise());
            };
        }
        if (side == Direction.DOWN)
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, TOP_CCW, TOP_BOTH ->
                        CamoGetter.get(edge == facing || edge == facing.getCounterClockWise(), false);
                case BOTTOM_CCW ->
                        CamoGetter.get(edge == facing, edge == facing.getOpposite());
                case BOTTOM_FWD ->
                        CamoGetter.get(edge == facing.getCounterClockWise(), edge == facing.getClockWise());
                case BOTTOM_BOTH ->
                        CamoGetter.get(false, edge == facing.getOpposite() || edge == facing.getClockWise());
            };
        }
        if (side == facing.getOpposite())
        {
            return switch (type)
            {
                case VERTICAL, TOP_FWD, BOTTOM_FWD ->
                        CamoGetter.get(edge == facing.getCounterClockWise(), edge == facing.getClockWise());
                case TOP_CCW, TOP_BOTH ->
                        CamoGetter.get(false, edge == facing.getClockWise() || edge == Direction.UP);
                case BOTTOM_CCW, BOTTOM_BOTH ->
                        CamoGetter.get(false, edge == facing.getClockWise() || edge == Direction.DOWN);
            };
        }
        if (side == facing.getClockWise())
        {
            return switch (type)
            {
                case VERTICAL, TOP_CCW, BOTTOM_CCW ->
                        CamoGetter.get(edge == facing, edge == facing.getOpposite());
                case TOP_FWD, TOP_BOTH ->
                        CamoGetter.get(false, edge == facing.getOpposite() || edge == Direction.UP);
                case BOTTOM_FWD, BOTTOM_BOTH ->
                        CamoGetter.get(false, edge == facing.getOpposite() || edge == Direction.DOWN);
            };
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if (side == facing && (type == StairsType.VERTICAL || type == StairsType.TOP_CCW || type == StairsType.BOTTOM_CCW))
        {
            return SolidityCheck.FIRST;
        }
        if (side == facing.getCounterClockWise() && (type == StairsType.VERTICAL || type == StairsType.TOP_FWD || type == StairsType.BOTTOM_FWD))
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedDoubleBlockRenderProperties.INSTANCE);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
