package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedSignItem;

@SuppressWarnings("deprecation")
public class FramedSignBlock extends AbstractFramedSignBlock
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public FramedSignBlock()
    {
        super(BlockType.FRAMED_SIGN, IFramedBlock.createProperties().doesNotBlockMovement());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.ROTATION_0_15, BlockStateProperties.WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        int rotation = MathHelper.floor((double) ((180.0F + context.getPlacementYaw()) * 16.0F / 360.0F) + 0.5D) & 15;
        return withWater(getDefaultState().with(BlockStateProperties.ROTATION_0_15, rotation), context.getWorld(), context.getPos());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return SHAPE;
    }

    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !isValidPosition(state, world, pos))
        {
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public BlockItem createItemBlock() { return new FramedSignItem(); }
}