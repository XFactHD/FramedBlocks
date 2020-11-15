package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedWallSignBlock extends FramedBlock
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        //TODO: implement sign click logic
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        //TODO: replace with framed sign tile entity
        return super.createTileEntity(state, world);
    }

    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.get(PropertyHolder.FACING_HOR) && !state.isValidPosition(world, pos))
        {
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public boolean canSpawnInBlock() { return true; }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            //TODO: implement
            builder.put(state, VoxelShapes.fullCube());
        }

        return builder.build();
    }
}