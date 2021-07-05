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
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public FramedSignBlock()
    {
        super(BlockType.FRAMED_SIGN, IFramedBlock.createProperties().noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.ROTATION_16, BlockStateProperties.WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        int rotation = MathHelper.floor((double) ((180.0F + context.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15;
        return withWater(defaultBlockState().setValue(BlockStateProperties.ROTATION_16, rotation), context.getLevel(), context.getClickedPos());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return SHAPE;
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !canSurvive(state, world, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos)
    {
        return world.getBlockState(pos.below()).getMaterial().isSolid();
    }

    @Override
    public BlockItem createItemBlock() { return new FramedSignItem(); }
}