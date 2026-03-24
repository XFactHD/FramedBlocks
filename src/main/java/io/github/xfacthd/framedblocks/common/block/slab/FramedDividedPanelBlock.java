package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedDividedPanelBlock extends FramedDoubleBlock
{
    public FramedDividedPanelBlock(BlockType type, Properties props)
    {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withWater()
                .build();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return BlockUtils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (getBlockType() == BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
        {
            BlockState defState = FBContent.BLOCK_FRAMED_SLAB_EDGE.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir);

            return new DoubleBlockParts(defState, defState.setValue(FramedProperties.TOP, true));
        }
        else
        {
            BlockState defState = FBContent.BLOCK_FRAMED_CORNER_PILLAR.value().defaultBlockState();
            return new DoubleBlockParts(
                    defState.setValue(FramedProperties.FACING_HOR, dir),
                    defState.setValue(FramedProperties.FACING_HOR, dir.getClockWise())
            );
        }
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (getBlockType() == BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
        {
            return DoubleBlockTopInteractionMode.SECOND;
        }
        else
        {
            return DoubleBlockTopInteractionMode.EITHER;
        }
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (edge == null)
        {
            return CamoGetter.NONE;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean vertical = state.getBlock() == FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.value();
        if (edge == facing)
        {
            if ((!vertical && side == Direction.DOWN) || (vertical && side == facing.getCounterClockWise()))
            {
                return CamoGetter.FIRST;
            }
            if ((!vertical && side == Direction.UP) || (vertical && side == facing.getClockWise()))
            {
                return CamoGetter.SECOND;
            }
        }
        else if (side == facing)
        {
            if ((!vertical && edge == Direction.DOWN) || (vertical && edge == facing.getCounterClockWise()))
            {
                return CamoGetter.FIRST;
            }
            if ((!vertical && edge == Direction.UP) || (vertical && edge == facing.getClockWise()))
            {
                return CamoGetter.SECOND;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        if (side == state.getValue(FramedProperties.FACING_HOR))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
