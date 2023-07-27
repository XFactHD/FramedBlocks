package xfacthd.framedblocks.common.block.stairs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
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
        super.createBlockStateDefinition(builder);
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
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE) { return state; }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && Utils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isZ(dir)))
        {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }
        return state.cycle(PropertyHolder.RIGHT);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape bottomLeft = ShapeUtils.orUnoptimized(
                box(8, 0, 0, 16, 8, 16),
                box(8, 8, 8, 16, 16, 16)
        );

        VoxelShape bottomRight = ShapeUtils.orUnoptimized(
                box(0, 0, 0, 8, 8, 16),
                box(0, 8, 8, 8, 16, 16)
        );

        VoxelShape topLeft = ShapeUtils.orUnoptimized(
                box(8, 8, 0, 16, 16, 16),
                box(8, 0, 8, 16, 8, 16)
        );

        VoxelShape topRight = ShapeUtils.orUnoptimized(
                box(0, 8, 0, 8, 16, 16),
                box(0, 0, 8, 8, 8, 16)
        );

        int maskTop = 0b0100;
        int maskRight = 0b1000;
        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            int horId = dir.get2DDataValue();
            shapes[horId] = ShapeUtils.rotateShape(Direction.SOUTH, dir, bottomLeft);
            shapes[horId | maskRight] = ShapeUtils.rotateShape(Direction.SOUTH, dir, bottomRight);
            shapes[horId | maskTop] = ShapeUtils.rotateShape(Direction.SOUTH, dir, topLeft);
            shapes[horId | maskTop | maskRight] = ShapeUtils.rotateShape(Direction.SOUTH, dir, topRight);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int right = state.getValue(PropertyHolder.RIGHT) ? maskRight : 0;
            builder.put(state, shapes[dir.get2DDataValue() | top | right]);
        }

        return ShapeProvider.of(builder.build());
    }
}
