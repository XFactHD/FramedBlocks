package io.github.xfacthd.framedblocks.common.block.slope;

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
import io.github.xfacthd.framedblocks.common.block.IComplexSlopeSource;
import io.github.xfacthd.framedblocks.common.block.ISlopeBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
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
import org.jspecify.annotations.Nullable;

public class FramedDividedSlopeBlock extends FramedDoubleBlock implements ISlopeBlock, IComplexSlopeSource
{
    public FramedDividedSlopeBlock(Properties props)
    {
        super(BlockType.FRAMED_DIVIDED_SLOPE, props);
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
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
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
        return switch (mode)
        {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> direction.cycle(state, PropertyHolder.SLOPE_TYPE);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return BlockUtils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return BlockUtils.mirrorFaceBlock(state, mirror);
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
    public DoubleBlockParts calculateParts(BlockState state)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        if (type == SlopeType.HORIZONTAL)
        {
            BlockState defState = FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value().defaultBlockState();
            return new DoubleBlockParts(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
        }
        else
        {
            BlockState defState = FBContent.BLOCK_FRAMED_HALF_SLOPE.value().defaultBlockState();
            boolean top = type == SlopeType.TOP;
            return new DoubleBlockParts(
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
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            if (side == Direction.UP && (edge == facing || edge == facing.getCounterClockWise()))
            {
                return CamoGetter.SECOND;
            }
            if (side == Direction.DOWN && (edge == facing || edge == facing.getCounterClockWise()))
            {
                return CamoGetter.FIRST;
            }
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
            Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
            if (side == facing.getClockWise() && (edge == facing || edge == dirTwo))
            {
                return CamoGetter.SECOND;
            }
            if (side == facing.getCounterClockWise() && (edge == facing || edge == dirTwo))
            {
                return CamoGetter.FIRST;
            }
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
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }

    @Override
    public boolean isHorizontalSlope(BlockState state)
    {
        return state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL;
    }
}
