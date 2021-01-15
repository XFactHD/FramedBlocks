package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

@SuppressWarnings("deprecation")
public class FramedWallSignBlock extends AbstractFramedSignBlock
{
    public FramedWallSignBlock()
    {
        super("framed_wall_sign", BlockType.FRAMED_WALL_SIGN, IFramedBlock.createProperties().doesNotBlockMovement());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();
        IWorldReader world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction[] dirs = context.getNearestLookingDirections();

        for(Direction direction : dirs)
        {
            if (direction.getAxis().isHorizontal())
            {
                Direction dir = direction.getOpposite();
                state = state.with(PropertyHolder.FACING_HOR, dir);
                if (state.isValidPosition(world, pos))
                {
                    return withWater(state, world, pos);
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.get(PropertyHolder.FACING_HOR) && !state.isValidPosition(world, pos))
        {
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR).getOpposite();
        return world.getBlockState(pos.offset(dir)).getMaterial().isSolid();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            switch (state.get(PropertyHolder.FACING_HOR))
            {
                case NORTH:
                {
                    builder.put(state, makeCuboidShape(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D));
                    break;
                }
                case EAST:
                {
                    builder.put(state, makeCuboidShape(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D));
                    break;
                }
                case SOUTH:
                {
                    builder.put(state, makeCuboidShape(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D));
                    break;
                }
                case WEST:
                {
                    builder.put(state, makeCuboidShape(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D));
                    break;
                }
            }
        }

        return builder.build();
    }
}