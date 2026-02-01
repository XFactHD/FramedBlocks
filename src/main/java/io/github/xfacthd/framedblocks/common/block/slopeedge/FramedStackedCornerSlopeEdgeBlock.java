package io.github.xfacthd.framedblocks.common.block.slopeedge;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.block.slope.FramedCornerSlopeBlock;
import io.github.xfacthd.framedblocks.common.block.stairs.standard.FramedStairsBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jspecify.annotations.Nullable;

public class FramedStackedCornerSlopeEdgeBlock extends FramedDoubleBlock
{
    public FramedStackedCornerSlopeEdgeBlock(Properties props)
    {
        super(BlockType.FRAMED_STACKED_CORNER_SLOPE_EDGE, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE, FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    @Nullable
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
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return FramedCornerSlopeBlock.rotateCorner(state, direction, mode);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            BlockState newState = BlockUtils.mirrorFaceBlock(state, mirror);
            if (newState != state)
            {
                return newState.setValue(PropertyHolder.CORNER_TYPE, type.horizontalOpposite());
            }
            return state;
        }
        else
        {
            return BlockUtils.mirrorCornerBlock(state, mirror);
        }
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
    public DoubleBlockParts calculateParts(BlockState state)
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
            stateOne = FramedStairsBlock.STATE_MERGER.apply(stateOne);
        }
        return new DoubleBlockParts(
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
