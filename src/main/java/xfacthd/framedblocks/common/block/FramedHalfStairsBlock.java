package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedHalfStairsBlock extends FramedBlock
{
    public FramedHalfStairsBlock()
    {
        super(BlockType.FRAMED_HALF_STAIRS);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.RIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        Vec3 hit = Utils.fraction(context.getClickLocation());

        Direction facing;
        if (!Utils.isY(face))
        {
            facing = face.getOpposite();
        }
        else
        {
            facing = context.getHorizontalDirection();
        }
        state = state.setValue(FramedProperties.FACING_HOR, facing);

        boolean top = face == Direction.DOWN || hit.y > .5;
        state = state.setValue(FramedProperties.TOP, top);

        double xz = Utils.isX(facing) ? hit.z() : hit.x();
        boolean rightPlus = Utils.isPositive(facing.getCounterClockWise());
        boolean right = (xz <= .5) == rightPlus;
        state = state.setValue(PropertyHolder.RIGHT, right);

        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }

        if (rot == Rotation.NONE)
        {
            return state;
        }

        if (face.getAxis() == dir.getAxis())
        {
            return state.cycle(PropertyHolder.RIGHT);
        }
        else
        {
            return state.cycle(FramedProperties.TOP);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape bottomLeft = Shapes.join(
                box(8, 0, 0, 16, 8, 16),
                box(8, 8, 8, 16, 16, 16),
                BooleanOp.OR
        );

        VoxelShape bottomRight = Shapes.join(
                box(0, 0, 0, 8, 8, 16),
                box(0, 8, 8, 8, 16, 16),
                BooleanOp.OR
        );

        VoxelShape topLeft = Shapes.join(
                box(8, 8, 0, 16, 16, 16),
                box(8, 0, 8, 16, 8, 16),
                BooleanOp.OR
        );

        VoxelShape topRight = Shapes.join(
                box(0, 8, 0, 8, 16, 16),
                box(0, 0, 8, 8, 8, 16),
                BooleanOp.OR
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
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
