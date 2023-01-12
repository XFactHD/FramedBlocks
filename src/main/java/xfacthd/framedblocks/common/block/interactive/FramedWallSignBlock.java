package xfacthd.framedblocks.common.block.interactive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.block.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.data.BlockType;

@SuppressWarnings("deprecation")
public class FramedWallSignBlock extends AbstractFramedSignBlock
{
    public FramedWallSignBlock()
    {
        super(BlockType.FRAMED_WALL_SIGN, IFramedBlock.createProperties(BlockType.FRAMED_WALL_SIGN).noCollission());
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
        BlockState state = defaultBlockState();
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] dirs = context.getNearestLookingDirections();

        for(Direction direction : dirs)
        {
            if (direction.getAxis().isHorizontal())
            {
                Direction dir = direction.getOpposite();
                state = state.setValue(FramedProperties.FACING_HOR, dir);
                if (state.canSurvive(level, pos))
                {
                    return withWater(state, level, pos);
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.getValue(FramedProperties.FACING_HOR) && !state.canSurvive(level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR).getOpposite();
        return level.getBlockState(pos.relative(dir)).getMaterial().isSolid();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            switch (state.getValue(FramedProperties.FACING_HOR))
            {
                case NORTH -> builder.put(state, box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D));
                case EAST -> builder.put(state, box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D));
                case SOUTH -> builder.put(state, box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D));
                case WEST -> builder.put(state, box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D));
            }
        }

        return ShapeProvider.of(builder.build());
    }
}