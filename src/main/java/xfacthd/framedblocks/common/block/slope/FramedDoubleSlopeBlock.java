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
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.blockentity.doubled.slope.FramedDoubleSlopeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDoubleSlopeBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleSlopeBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLOPE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx).withHorizontalFacingAndSlopeType().build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
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
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE.value().defaultBlockState();
        return new Tuple<>(
                defState.setValue(PropertyHolder.SLOPE_TYPE, type)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defState.setValue(PropertyHolder.SLOPE_TYPE, type == SlopeType.HORIZONTAL ? type : type.getOpposite())
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return switch (state.getValue(PropertyHolder.SLOPE_TYPE))
        {
            case BOTTOM -> DoubleBlockTopInteractionMode.SECOND;
            case TOP -> DoubleBlockTopInteractionMode.FIRST;
            case HORIZONTAL -> DoubleBlockTopInteractionMode.EITHER;
        };
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        return switch (type)
        {
            case HORIZONTAL ->
            {
                if (matchesHor(side, facing) || (Utils.isY(side) && matchesHor(edge, facing)))
                {
                    yield CamoGetter.FIRST;
                }
                Direction oppFacing = facing.getOpposite();
                if (matchesHor(side, oppFacing) || (Utils.isY(side) && matchesHor(edge, oppFacing)))
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case TOP ->
            {
                if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    if (edge == facing || edge == Direction.UP)
                    {
                        yield CamoGetter.FIRST;
                    }
                    if (edge == facing.getOpposite() || edge == Direction.DOWN)
                    {
                        yield CamoGetter.SECOND;
                    }
                    yield CamoGetter.NONE;
                }
                if (side == facing || side == Direction.UP)
                {
                    yield CamoGetter.FIRST;
                }
                if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case BOTTOM ->
            {
                if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    if (edge == facing || edge == Direction.DOWN)
                    {
                        yield CamoGetter.FIRST;
                    }
                    if (edge == facing.getOpposite() || edge == Direction.UP)
                    {
                        yield CamoGetter.SECOND;
                    }
                    yield CamoGetter.NONE;
                }
                if (side == facing || side == Direction.DOWN)
                {
                    yield CamoGetter.FIRST;
                }
                if (side == facing.getOpposite() || side == Direction.UP)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
        };
    }

    private static boolean matchesHor(Direction side, Direction facing)
    {
        return side == facing || side == facing.getCounterClockWise();
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        return switch (type)
        {
            case HORIZONTAL ->
            {
                if (side == facing || side == facing.getCounterClockWise())
                {
                    yield SolidityCheck.FIRST;
                }
                else if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case TOP ->
            {
                if (side == facing || side == Direction.UP)
                {
                    yield SolidityCheck.FIRST;
                }
                else if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case BOTTOM ->
            {
                if (side == facing || side == Direction.UP)
                {
                    yield SolidityCheck.SECOND;
                }
                else if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    yield SolidityCheck.FIRST;
                }
                yield SolidityCheck.BOTH;
            }
        };
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopeBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST)
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
    }
}