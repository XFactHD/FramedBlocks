package xfacthd.framedblocks.common.block.slopeedge;

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
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedStackedCornerSlopeEdgeBlock extends AbstractFramedDoubleBlock
{
    public FramedStackedCornerSlopeEdgeBlock()
    {
        super(BlockType.FRAMED_STACKED_CORNER_SLOPE_EDGE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE, FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndCornerType()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            return state.setValue(PropertyHolder.CORNER_TYPE, type.rotate(rot));
        }
        return rotate(state, rot);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            BlockState newState = Utils.mirrorFaceBlock(state, mirror);
            if (newState != state)
            {
                return newState.setValue(PropertyHolder.CORNER_TYPE, type.horizontalOpposite());
            }
            return state;
        }
        else
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (state.getValue(PropertyHolder.CORNER_TYPE) == CornerType.BOTTOM)
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);
        BlockState stateOne;
        if (type.isHorizontal())
        {
            boolean right = type.isRight();
            StairsType stairsType = StairsType.get(!type.isTop(), right, !right);
            stateOne = FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, right ? dir.getClockWise() : dir)
                    .setValue(PropertyHolder.STAIRS_TYPE, stairsType);
        }
        else
        {
            stateOne = FBContent.BLOCK_FRAMED_STAIRS.value()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
                    .setValue(BlockStateProperties.HALF, type.isTop() ? Half.TOP : Half.BOTTOM)
                    .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_LEFT);
        }
        return new Tuple<>(
                stateOne,
                FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(PropertyHolder.CORNER_TYPE, type)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
                        .setValue(PropertyHolder.ALT_TYPE, true)
        );
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction baseFace = switch (state.getValue(PropertyHolder.CORNER_TYPE))
        {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            default -> state.getValue(FramedProperties.FACING_HOR);
        };
        return side == baseFace ? SolidityCheck.FIRST : SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction baseFace = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            default -> dir;
        };
        if (side == baseFace || edge == baseFace)
        {
            return CamoGetter.FIRST;
        }
        Direction xBack;
        Direction yBack;
        if (type.isHorizontal())
        {
            xBack = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            yBack = type.isTop() ? Direction.UP : Direction.DOWN;
        }
        else
        {
            xBack = dir;
            yBack = dir.getCounterClockWise();
        }
        if ((side == xBack && edge == yBack) || (side == yBack && edge == xBack))
        {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }
}
