package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlabCornerBlock extends FramedBlock
{
    public FramedSlabCornerBlock()
    {
        super(BlockType.FRAMED_SLAB_CORNER);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        Vector3d hitPoint = Utils.fraction(context.getClickLocation());
        if (face.getAxis().isHorizontal())
        {
            boolean xAxis = face.getAxis() == Direction.Axis.X;
            boolean positive = face.getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.z() : hitPoint.x();

            Direction dir = face.getOpposite();
            if ((xz > .5D) == positive)
            {
                dir = dir.getClockWise();
            }
            state = state.setValue(PropertyHolder.FACING_HOR, dir);
        }
        else
        {
            double x = hitPoint.x();
            double z = hitPoint.z();

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) == (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)) { dir = dir.getClockWise(); }
            state = state.setValue(PropertyHolder.FACING_HOR, dir);
        }

        state = withTop(state, face, context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return type == PathType.WATER && world.getFluidState(pos).is(FluidTags.WATER);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBot = box(0, 0, 0, 8, 8, 8);
        VoxelShape shapeTop = box(0, 8, 0, 8, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            boolean top = state.getValue(PropertyHolder.TOP);
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, top ? shapeTop : shapeBot));
        }

        return builder.build();
    }
}