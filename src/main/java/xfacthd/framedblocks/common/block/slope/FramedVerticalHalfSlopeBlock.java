package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.IdentityHashMap;

public class FramedVerticalHalfSlopeBlock extends FramedBlock
{
    public FramedVerticalHalfSlopeBlock()
    {
        super(BlockType.FRAMED_VERTICAL_HALF_SLOPE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction side = context.getClickedFace();
        Vec3 hitVec = context.getClickLocation();

        Direction dir = side.getOpposite();
        if (Utils.fractionInDir(hitVec, side.getCounterClockWise()) > .5D)
        {
            dir = dir.getClockWise();
        }
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, dir);

        state = withTop(state, side, hitVec);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(facing));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }



    public static final ShapeCache<Boolean> SHAPES = new ShapeCache<>(new IdentityHashMap<>(), map ->
    {
        map.put(false, ShapeUtils.orUnoptimized(
                box(   0, 0, 0,   .5,  8,   16),
                box(   0, 0, 0,    4,  8, 15.5),
                box(   4, 0, 0,    8,  8,   12),
                box(   8, 0, 0,   12,  8,    8),
                box(  12, 0, 0, 15.5,  8,    4),
                box(15.5, 0, 0,   16,  8,   .5)
        ));

        map.put(true, ShapeUtils.orUnoptimized(
                box(   0, 8, 0,   .5, 16,   16),
                box(   0, 8, 0,    4, 16, 15.5),
                box(   4, 8, 0,    8, 16,   12),
                box(   8, 8, 0,   12, 16,    8),
                box(  12, 8, 0, 15.5, 16,    4),
                box(15.5, 8, 0,   16, 16,   .5)
        ));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                SHAPES.get(Boolean.FALSE), SHAPES.get(Boolean.TRUE), Direction.NORTH
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
