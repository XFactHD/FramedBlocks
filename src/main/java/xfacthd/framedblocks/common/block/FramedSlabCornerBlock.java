package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlabCornerBlock extends FramedBlock
{
    public FramedSlabCornerBlock()
    {
        super(BlockType.FRAMED_SLAB_CORNER);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        Vec3 hitPoint = Utils.fraction(context.getClickLocation());
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