package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
        if (dir == Direction.UP && type == SlopeType.TOP)
        {
            return true;
        }
        else if (dir == Direction.DOWN && type == SlopeType.BOTTOM)
        {
            return true;
        }
        else if (type == SlopeType.HORIZONTAL)
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            return dir == facing || dir == facing.rotateYCCW();
        }
        return state.get(PropertyHolder.FACING_HOR) == dir;
    };

    public FramedSlopeBlock() { super("framed_slope", BlockType.FRAMED_SLOPE); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = withSlopeType(getDefaultState(), context.getFace(), context.getPlacementHorizontalFacing(), context.getHitVec());
        return withWater(state, context.getWorld(), context.getPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = VoxelShapes.or(
                makeCuboidShape(0,  0, 0, 16,  4, 16),
                makeCuboidShape(0,  4, 0, 16,  8, 12),
                makeCuboidShape(0,  8, 0, 16, 12,  8),
                makeCuboidShape(0, 12, 0, 16, 16,  4)
        ).simplify();

        VoxelShape shapeTop = VoxelShapes.or(
                makeCuboidShape(0,  0, 0, 16,  4,  4),
                makeCuboidShape(0,  4, 0, 16,  8,  8),
                makeCuboidShape(0,  8, 0, 16, 12, 12),
                makeCuboidShape(0, 12, 0, 16, 16, 16)
        ).simplify();

        VoxelShape shapeHorizontal = VoxelShapes.or(
                makeCuboidShape( 0, 0, 0,  4, 16, 16),
                makeCuboidShape( 4, 0, 0,  8, 16, 12),
                makeCuboidShape( 8, 0, 0, 12, 16,  8),
                makeCuboidShape(12, 0, 0, 16, 16,  4)
        ).simplify();

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type == SlopeType.BOTTOM)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
            else if (type == SlopeType.TOP)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeHorizontal));
            }
        }

        return builder.build();
    }
}