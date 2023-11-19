package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDividedSlopeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDividedSlopeBlock extends AbstractFramedDoubleBlock implements ISlopeBlock
{
    public FramedDividedSlopeBlock()
    {
        super(BlockType.FRAMED_DIVIDED_SLOPE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED,
                FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (Utils.isY(face) || (type != SlopeType.HORIZONTAL && face == dir.getOpposite()))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE && face == dir)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return Utils.mirrorFaceBlock(state, mirror);
        }
    }

    @Override
    public Direction getFacing(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public SlopeType getSlopeType(BlockState state)
    {
        return state.getValue(PropertyHolder.SLOPE_TYPE);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (type == SlopeType.HORIZONTAL)
        {
            BlockState defState = FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value().defaultBlockState();
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
        }
        else
        {
            BlockState defState = FBContent.BLOCK_FRAMED_HALF_SLOPE.value().defaultBlockState();
            boolean top = type == SlopeType.TOP;
            boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, false)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, true)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
        }
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        boolean horizontal = state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL;
        return horizontal ? DoubleBlockTopInteractionMode.SECOND : DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            if (side == Direction.UP)
            {
                return CamoGetter.SECOND;
            }
            if (side == Direction.DOWN)
            {
                return CamoGetter.FIRST;
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                if (edge == Direction.UP)
                {
                    return CamoGetter.SECOND;
                }
                if (edge == Direction.DOWN)
                {
                    return CamoGetter.FIRST;
                }
            }
        }
        else
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (side == facing.getClockWise())
            {
                return CamoGetter.SECOND;
            }
            if (side == facing.getCounterClockWise())
            {
                return CamoGetter.FIRST;
            }

            Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
            if (side == facing || side == dirTwo)
            {
                if (edge == facing.getClockWise())
                {
                    return CamoGetter.SECOND;
                }
                if (edge == facing.getCounterClockWise())
                {
                    return CamoGetter.FIRST;
                }
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        Direction secDir = switch (type)
        {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> facing.getCounterClockWise();
        };

        if (side == facing || side == secDir)
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDividedSlopeBlockEntity(pos, state);
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
