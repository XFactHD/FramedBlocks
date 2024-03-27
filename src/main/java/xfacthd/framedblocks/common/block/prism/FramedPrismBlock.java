package xfacthd.framedblocks.common.block.prism;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public class FramedPrismBlock extends FramedBlock implements IFramedPrismBlock
{
    public FramedPrismBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                PropertyHolder.FACING_AXIS, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(context, this);
    }

    public static BlockState getStateForPlacement(BlockPlaceContext ctx, Block block)
    {
        return PlacementStateBuilder.of(block, ctx)
                .withCustom((state, modCtx) ->
                {
                    Direction face = modCtx.getClickedFace();
                    Direction.Axis axis = modCtx.getHorizontalDirection().getAxis();
                    if (!Utils.isY(face))
                    {
                        Vec3 subHit = Utils.fraction(modCtx.getClickLocation());

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
                    return state.setValue(PropertyHolder.FACING_AXIS, DirectionAxis.of(face, axis));
                })
                .withYSlope(Utils.isY(ctx.getClickedFace()))
                .tryWithWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().is(Utils.WRENCH))
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.Y_SLOPE, !state.getValue(FramedProperties.Y_SLOPE)));
            return true;
        }
        return false;
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

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism()
    {
        return getBlockType() != BlockType.FRAMED_PRISM;
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                box( 0, 0, 0,   16, .5, 16),
                box(.5, 0, 0, 15.5,  4, 16),
                box( 4, 0, 0,   12,  8, 16)
        );

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                box( 0, 15.5, 0,   16, 16, 16),
                box(.5,   12, 0, 15.5, 16, 16),
                box( 4,    8, 0,   12, 16, 16)
        );

        VoxelShape shapeXZ = ShapeUtils.orUnoptimized(
                box(0,  0, 15.5, 16,   16, 16),
                box(0, .5,   12, 16, 15.5, 16),
                box(0,  4,    8, 16,   12, 16)
        );

        VoxelShape shapeY = ShapeUtils.orUnoptimized(
                box( 0, 0, 15.5,   16, 16, 16),
                box(.5, 0,   12, 15.5, 16, 16),
                box( 4, 0,    8,   12, 16, 16)
        );

        VoxelShape[] shapes = new VoxelShape[DirectionAxis.COUNT];
        for (DirectionAxis dirAxis : DirectionAxis.values())
        {
            Direction facing = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();

            if (Utils.isY(facing))
            {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            }
            else
            {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        facing,
                        axis == Direction.Axis.Y ? shapeY : shapeXZ
                );
            }
        }

        for (BlockState state : states)
        {
            DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
            builder.put(state, shapes[dirAxis.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }

    /*public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                box(   4, 0, 0,   12,   12, 16),
                box(15.5, 0, 0,   16,   16, 16),
                box(   0, 0, 0,   .5,   16, 16),
                box(  12, 0, 0, 15.5, 15.5, 16),
                box(  .5, 0, 0,    4, 15.5, 16)
        );

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
               box(   4,  4, 0,   12, 16, 16),
               box(15.5,  0, 0,   16, 16, 16),
               box(   0,  0, 0,   .5, 16, 16),
               box(  12, .5, 0, 15.5, 16, 16),
               box(  .5, .5, 0,    4, 16, 16)
        );

        VoxelShape shapeXZ = ShapeUtils.orUnoptimized(
                box(0,    4,  4, 16,   12, 16),
                box(0,    0,  0, 16,   .5, 16),
                box(0, 15.5,  0, 16,   16, 16),
                box(0,   .5, .5, 16,    4, 16),
                box(0,   12, .5, 16, 15.5, 16)
        );

        VoxelShape shapeY = ShapeUtils.orUnoptimized(
                box(   4, 0,  4,   12, 16, 16),
                box(15.5, 0,  0,   16, 16, 16),
                box(   0, 0,  0,   .5, 16, 16),
                box(  12, 0, .5, 15.5, 16, 16),
                box(  .5, 0, .5,    4, 16, 16)
        );

        VoxelShape[] shapes = new VoxelShape[12];
        for (DirectionAxis dirAxis : DirectionAxis.values())
        {
            Direction facing = dirAxis.direction();
            Direction.Axis axis = dirAxis.axis();

            if (Utils.isY(facing))
            {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                        facing == Direction.UP ? shapeBottom : shapeTop
                );
            }
            else
            {
                shapes[dirAxis.ordinal()] = ShapeUtils.rotateShape(
                        Direction.NORTH,
                        facing.getOpposite(),
                        axis == Direction.Axis.Y ? shapeY : shapeXZ
                );
            }
        }

        for (BlockState state : states)
        {
            DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
            builder.put(state, shapes[dirAxis.ordinal()]);
        }

        return ShapeProvider.of(builder.build());
    }*/
}
