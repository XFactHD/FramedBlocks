package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

public class FramedHalfStairsBlock extends FramedBlock
{
    public FramedHalfStairsBlock()
    {
        super(BlockType.FRAMED_HALF_STAIRS);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.TOP, false)
                .setValue(PropertyHolder.RIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        Vector3d hit = Utils.fraction(context.getClickLocation());

        Direction facing;
        if (face.getAxis() != Direction.Axis.Y)
        {
            facing = face.getOpposite();
        }
        else
        {
            facing = context.getHorizontalDirection();
        }
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        boolean top = face == Direction.DOWN || hit.y() > .5;
        state = state.setValue(PropertyHolder.TOP, top);

        double xz = facing.getAxis() == Direction.Axis.X ? hit.z() : hit.x();
        boolean rightPlus = facing.getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
        boolean right = (xz <= .5) == rightPlus;
        state = state.setValue(PropertyHolder.RIGHT, right);

        return state;
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape bottomLeft = VoxelShapes.joinUnoptimized(
                box(8, 0, 0, 16, 8, 16),
                box(8, 8, 8, 16, 16, 16),
                IBooleanFunction.OR
        );

        VoxelShape bottomRight = VoxelShapes.joinUnoptimized(
                box(0, 0, 0, 8, 8, 16),
                box(0, 8, 8, 8, 16, 16),
                IBooleanFunction.OR
        );

        VoxelShape topLeft = VoxelShapes.joinUnoptimized(
                box(8, 8, 0, 16, 16, 16),
                box(8, 0, 8, 16, 8, 16),
                IBooleanFunction.OR
        );

        VoxelShape topRight = VoxelShapes.joinUnoptimized(
                box(0, 8, 0, 8, 16, 16),
                box(0, 0, 8, 8, 8, 16),
                IBooleanFunction.OR
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            boolean top = state.getValue(PropertyHolder.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            VoxelShape shape;
            if (top)
            {
                shape = right ? topRight : topLeft;
            }
            else
            {
                shape = right ? bottomRight : bottomLeft;
            }
            builder.put(state, Utils.rotateShape(Direction.SOUTH, dir, shape));
        }

        return builder.build();
    }
}
