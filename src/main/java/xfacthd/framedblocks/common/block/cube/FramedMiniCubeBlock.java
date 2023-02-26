package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

@SuppressWarnings("deprecation")
public class FramedMiniCubeBlock extends FramedBlock
{
    public FramedMiniCubeBlock()
    {
        super(BlockType.FRAMED_MINI_CUBE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(
                BlockStateProperties.ROTATION_16,
                RotationSegment.convertToSegment(context.getRotation() + 180.0F)
        );
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rotation)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        if (rotation == Rotation.CLOCKWISE_90)
        {
            rot = (rot + 1) % 16;
        }
        else
        {
            rot = (rot + 15) % 16;
        }
        return state.setValue(BlockStateProperties.ROTATION_16, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(rot, 16));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rot, 16));
    }
}
