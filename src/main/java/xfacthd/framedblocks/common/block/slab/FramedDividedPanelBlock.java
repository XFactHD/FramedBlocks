package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDividedPanelBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedDividedPanelBlock extends AbstractFramedDoubleBlock
{
    public FramedDividedPanelBlock(BlockType type)
    {
        super(type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rotation.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDividedPanelBlockEntity(pos, state);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (getBlockType() == BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
        {
            BlockState defState = FBContent.BLOCK_FRAMED_SLAB_EDGE.get()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir);

            return new Tuple<>(defState, defState.setValue(FramedProperties.TOP, true));
        }
        else
        {
            BlockState defState = FBContent.BLOCK_FRAMED_CORNER_PILLAR.get().defaultBlockState();
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, dir),
                    defState.setValue(FramedProperties.FACING_HOR, dir.getClockWise())
            );
        }
    }
}
