package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.ROTATION_16, BlockStateProperties.WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        int rotation = Mth.floor((double) ((180.0F + context.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15;
        return withWater(defaultBlockState().setValue(BlockStateProperties.ROTATION_16, rotation), context.getLevel(), context.getClickedPos());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPE;
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !canSurvive(state, level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).getMaterial().isSolid();
    }

    @Override
    public BlockItem createItemBlock() { return new FramedSignItem(); }
}