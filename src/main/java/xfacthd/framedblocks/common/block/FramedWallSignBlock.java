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
        super(BlockType.FRAMED_WALL_SIGN, IFramedBlock.createProperties(BlockType.FRAMED_WALL_SIGN).noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        IWorldReader world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] dirs = context.getNearestLookingDirections();

        for(Direction direction : dirs)
        {
            if (direction.getAxis().isHorizontal())
            {
                Direction dir = direction.getOpposite();
                state = state.setValue(PropertyHolder.FACING_HOR, dir);
                if (state.canSurvive(world, pos))
                {
                    return withWater(state, world, pos);
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.getValue(PropertyHolder.FACING_HOR) && !state.canSurvive(world, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR).getOpposite();
        return world.getBlockState(pos.relative(dir)).getMaterial().isSolid();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            switch (state.getValue(PropertyHolder.FACING_HOR))
            {
                case NORTH:
                {
                    builder.put(state, box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D));
                    break;
                }
                case EAST:
                {
                    builder.put(state, box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D));
                    break;
                }
                case SOUTH:
                {
                    builder.put(state, box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D));
                    break;
                }
                case WEST:
                {
                    builder.put(state, box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D));
                    break;
                }
            }
        }

        return builder.build();
    }
}