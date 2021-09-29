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
        setDefaultState(getDefaultState().with(PropertyHolder.TOP, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction face = context.getFace();
        Vector3d hitPoint = Utils.fraction(context.getHitVec());
        if (face.getAxis().isHorizontal())
        {
            boolean xAxis = face.getAxis() == Direction.Axis.X;
            boolean positive = face.rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.getZ() : hitPoint.getX();

            Direction dir = face.getOpposite();
            if ((xz > .5D) == positive)
            {
                dir = dir.rotateY();
            }
            state = state.with(PropertyHolder.FACING_HOR, dir);
        }
        else
        {
            double x = hitPoint.getX();
            double z = hitPoint.getZ();

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) == (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)) { dir = dir.rotateY(); }
            state = state.with(PropertyHolder.FACING_HOR, dir);
        }

        state = withTop(state, face, context.getHitVec());
        return withWater(state, context.getWorld(), context.getPos());
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return type == PathType.WATER && world.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBot = makeCuboidShape(0, 0, 0, 8, 8, 8);
        VoxelShape shapeTop = makeCuboidShape(0, 8, 0, 8, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            boolean top = state.get(PropertyHolder.TOP);
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, top ? shapeTop : shapeBot));
        }

        return builder.build();
    }
}