package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedSignItem;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

@SuppressWarnings("deprecation")
public class FramedSignBlock extends FramedBlock
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public FramedSignBlock()
    {
        super("framed_sign", BlockType.FRAMED_SIGN, IFramedBlock.createProperties().doesNotBlockMovement());
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        //TODO: implement sign click logic
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return SHAPE;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedSignTileEntity(); }

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
    public boolean canSpawnInBlock() { return true; }

    @Override
    public BlockItem createItemBlock() { return new FramedSignItem(); }
}