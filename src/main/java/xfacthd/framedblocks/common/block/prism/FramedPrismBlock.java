package xfacthd.framedblocks.common.block.prism;

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
import xfacthd.framedblocks.api.predicate.CtmPredicate;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public class FramedPrismBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
            side == state.getValue(PropertyHolder.FACING_AXIS).direction().getOpposite();

    public static final CtmPredicate CTM_PREDICATE_INNER = (state, side) ->
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return side != dirAxis.direction() && side != null && side.getAxis() != dirAxis.axis();
    };

    public FramedPrismBlock(BlockType type) { super(type); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_AXIS, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    public static BlockState getStateForPlacement(BlockPlaceContext context, BlockState state, IBlockType blockType)
    {
        Direction face = context.getClickedFace();
        Direction.Axis axis;
        if (Utils.isY(face))
        {
            axis = context.getHorizontalDirection().getAxis();
        }
        else
        {
            Vec3 subHit = Utils.fraction(context.getClickLocation());

            double xz = (Utils.isX(face) ? subHit.z() : subHit.x()) - .5;
            double y = subHit.y() - .5;

            if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
            {
                axis = face.getClockWise().getAxis();
            }
            else
            {
                axis = Direction.Axis.Y;
            }
        }
        state = state.setValue(PropertyHolder.FACING_AXIS, DirectionAxis.of(face, axis));

        if (blockType == BlockType.FRAMED_PRISM)
        {
            state = withWater(state, context.getLevel(), context.getClickedPos());
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.rotate(rot));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.mirror(mirror));
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = Shapes.or(
                box( 0, 0, 0,   16, .5, 16),
                box(.5, 0, 0, 15.5,  4, 16),
                box( 4, 0, 0,   12,  8, 16)
        ).optimize();

        VoxelShape shapeTop = Shapes.or(
                box( 0, 15.5, 0,   16, 16, 16),
                box(.5,   12, 0, 15.5, 16, 16),
                box( 4,    8, 0,   12, 16, 16)
        ).optimize();

        VoxelShape shapeXZ = Shapes.or(
                box(0,  0, 15.5, 16,   16, 16),
                box(0, .5,   12, 16, 15.5, 16),
                box(0,  4,    8, 16,   12, 16)
        ).optimize();

        VoxelShape shapeY = Shapes.or(
                box( 0, 0, 15.5,   16, 16, 16),
                box(.5, 0,   12, 15.5, 16, 16),
                box( 4, 0,    8,   12, 16, 16)
        ).optimize();

        for (BlockState state : states)
        {
            DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
            Direction facing = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();

            if (Utils.isY(facing))
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                                facing == Direction.UP ? shapeBottom : shapeTop
                        )
                );
            }
            else
            {
                builder.put(
                        state,
                        Utils.rotateShape(Direction.NORTH, facing, axis == Direction.Axis.Y ? shapeY : shapeXZ)
                );
            }
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = Shapes.or(
                box(   4, 0, 0,   12,   12, 16),
                box(15.5, 0, 0,   16,   16, 16),
                box(   0, 0, 0,   .5,   16, 16),
                box(  12, 0, 0, 15.5, 15.5, 16),
                box(  .5, 0, 0,    4, 15.5, 16)
        );

        VoxelShape shapeTop = Shapes.or(
               box(   4,  4, 0,   12, 16, 16),
               box(15.5,  0, 0,   16, 16, 16),
               box(   0,  0, 0,   .5, 16, 16),
               box(  12, .5, 0, 15.5, 16, 16),
               box(  .5, .5, 0,    4, 16, 16)
        );

        VoxelShape shapeXZ = Shapes.or(
                box(0,    4,  4, 16,   12, 16),
                box(0,    0,  0, 16,   .5, 16),
                box(0, 15.5,  0, 16,   16, 16),
                box(0,   .5, .5, 16,    4, 16),
                box(0,   12, .5, 16, 15.5, 16)
        );

        VoxelShape shapeY = Shapes.or(
                box(   4, 0,  4,   12, 16, 16),
                box(15.5, 0,  0,   16, 16, 16),
                box(   0, 0,  0,   .5, 16, 16),
                box(  12, 0, .5, 15.5, 16, 16),
                box(  .5, 0, .5,    4, 16, 16)
        );

        for (BlockState state : states)
        {
            DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
            Direction facing = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();

            if (Utils.isY(facing))
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                                facing == Direction.UP ? shapeBottom : shapeTop
                        )
                );
            }
            else
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                facing.getOpposite(),
                                axis == Direction.Axis.Y ? shapeY : shapeXZ
                        )
                );
            }
        }

        return ShapeProvider.of(builder.build());
    }
}
