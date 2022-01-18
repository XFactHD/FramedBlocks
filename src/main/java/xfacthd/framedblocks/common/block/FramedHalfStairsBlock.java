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
        setDefaultState(getDefaultState()
                .with(PropertyHolder.TOP, false)
                .with(PropertyHolder.RIGHT, false)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction face = context.getFace();
        Vector3d hit = Utils.fraction(context.getHitVec());

        Direction facing;
        if (face.getAxis() != Direction.Axis.Y)
        {
            facing = face.getOpposite();
        }
        else
        {
            facing = context.getPlacementHorizontalFacing();
        }
        state = state.with(PropertyHolder.FACING_HOR, facing);

        boolean top = face == Direction.DOWN || hit.getY() > .5;
        state = state.with(PropertyHolder.TOP, top);

        double xz = facing.getAxis() == Direction.Axis.X ? hit.getZ() : hit.getX();
        boolean rightPlus = facing.rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
        boolean right = (xz <= .5) == rightPlus;
        state = state.with(PropertyHolder.RIGHT, right);

        return state;
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape bottomLeft = VoxelShapes.combine(
                makeCuboidShape(8, 0, 0, 16, 8, 16),
                makeCuboidShape(8, 8, 8, 16, 16, 16),
                IBooleanFunction.OR
        );

        VoxelShape bottomRight = VoxelShapes.combine(
                makeCuboidShape(0, 0, 0, 8, 8, 16),
                makeCuboidShape(0, 8, 8, 8, 16, 16),
                IBooleanFunction.OR
        );

        VoxelShape topLeft = VoxelShapes.combine(
                makeCuboidShape(8, 8, 0, 16, 16, 16),
                makeCuboidShape(8, 0, 8, 16, 8, 16),
                IBooleanFunction.OR
        );

        VoxelShape topRight = VoxelShapes.combine(
                makeCuboidShape(0, 8, 0, 8, 16, 16),
                makeCuboidShape(0, 0, 8, 8, 8, 16),
                IBooleanFunction.OR
        );

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            boolean top = state.get(PropertyHolder.TOP);
            boolean right = state.get(PropertyHolder.RIGHT);

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
