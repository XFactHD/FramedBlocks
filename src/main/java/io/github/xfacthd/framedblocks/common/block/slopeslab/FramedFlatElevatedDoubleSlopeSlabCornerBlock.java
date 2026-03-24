package io.github.xfacthd.framedblocks.common.block.slopeslab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeslab.FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedFlatElevatedDoubleSlopeSlabCornerBlock extends FramedDoubleBlock implements SlopeToggleBlock
{
    private final boolean isInner;

    public FramedFlatElevatedDoubleSlopeSlabCornerBlock(BlockType blockType, Properties props)
    {
        super(blockType, props);
        this.isInner = blockType == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.ALT_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(FramedProperties.TOP);
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
        return BlockUtils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        BlockState defStateOne;
        BlockState defStateTwo;
        if (getBlockType() == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER)
        {
            defStateOne = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.value().defaultBlockState();
            defStateTwo = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value().defaultBlockState();
        }
        else
        {
            defStateOne = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.value().defaultBlockState();
            defStateTwo = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value().defaultBlockState();
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return new DoubleBlockParts(
                defStateOne.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                defStateTwo.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(PropertyHolder.TOP_HALF, !top)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (state.getValue(FramedProperties.TOP))
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (DirUtils.isY(side))
        {
            if (side == dirTwo)
            {
                return CamoGetter.FIRST;
            }
            else if (side == dirTwo.getOpposite())
            {
                return CamoGetter.SECOND;
            }
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (isInner && (side == facing || side == facing.getCounterClockWise()))
        {
            return CamoGetter.FIRST;
        }
        else if (!DirUtils.isY(side))
        {
            if (edge == dirTwo)
            {
                return CamoGetter.FIRST;
            }
            else if (edge == dirTwo.getOpposite())
            {
                return CamoGetter.SECOND;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        if (side == Direction.UP)
        {
            return top ? SolidityCheck.FIRST : SolidityCheck.SECOND;
        }
        else if (side == Direction.DOWN)
        {
            return top ? SolidityCheck.SECOND : SolidityCheck.FIRST;
        }
        else if (isInner)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                return SolidityCheck.FIRST;
            }
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity(pos, state);
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
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
