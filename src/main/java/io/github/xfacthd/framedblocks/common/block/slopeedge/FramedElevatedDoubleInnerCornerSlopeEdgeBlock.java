package io.github.xfacthd.framedblocks.common.block.slopeedge;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
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
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeedge.FramedElevatedDoubleInnerCornerSlopeEdgeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedElevatedDoubleInnerCornerSlopeEdgeBlock extends FramedDoubleBlock implements SlopeToggleBlock
{
    public FramedElevatedDoubleInnerCornerSlopeEdgeBlock(Properties props)
    {
        super(BlockType.FRAMED_ELEV_DOUBLE_INNER_CORNER_SLOPE_EDGE, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndCornerType()
                .build();
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
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedElevatedDoubleInnerCornerSlopeEdgeBlockEntity(pos, state);
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
        return defaultBlockState();
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.BOTTOM || (type.isHorizontal() && type.isTop()))
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
        CornerType typeTwo;
        if (type.isHorizontal())
        {
            typeTwo = type.rotate(type.isRight() != type.isTop() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
        }
        else
        {
            typeTwo = type.verticalOpposite();
        }
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);
        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(PropertyHolder.CORNER_TYPE, type)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                        .setValue(PropertyHolder.CORNER_TYPE, typeTwo)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction baseFace = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            default -> dir;
        };

        if (side == baseFace) return SolidityCheck.FIRST;

        Direction xFront;
        Direction yFront;
        if (type.isHorizontal())
        {
            xFront = type.isRight() ? dir.getCounterClockWise() : dir.getClockWise();
            yFront = type.isTop() ? Direction.DOWN : Direction.UP;
        }
        else
        {
            xFront = dir.getClockWise();
            yFront = dir.getOpposite();
        }
        return side == xFront || side == yFront || side == baseFace.getOpposite() ? SolidityCheck.BOTH : SolidityCheck.FIRST;
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
        if (side == baseFace || side == xBack || side == yBack || edge == baseFace)
        {
            return CamoGetter.FIRST;
        }
        if ((side == xBack.getOpposite() && edge == yBack) || (side == yBack.getOpposite() && edge == xBack))
        {
            return CamoGetter.FIRST;
        }
        if (side == baseFace.getOpposite() && (edge == xBack || edge == yBack))
        {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }
}
