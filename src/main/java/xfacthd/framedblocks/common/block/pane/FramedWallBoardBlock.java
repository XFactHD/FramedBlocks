package xfacthd.framedblocks.common.block.pane;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedWallBoardBlock extends FramedBlock
{
    public FramedWallBoardBlock() { super(BlockType.FRAMED_WALL_BOARD); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, context.getHorizontalDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shape = box(0, 0, 0, 16, 16, 1);
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, shapes[dir.get2DDataValue()]);
        }

        return ShapeProvider.of(builder.build());
    }
}
