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
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.slope.FramedSlopeBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public class FramedElevatedPrismBlock extends FramedBlock implements IFramedPrismBlock
{
    public FramedElevatedPrismBlock(BlockType type)
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
        return FramedPrismBlock.getStateForPlacement(context, this);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.rotate(rot));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
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
        return getBlockType() == BlockType.FRAMED_ELEVATED_INNER_PRISM;
    }



    /*public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
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
    }*/

    public static final class InnerShapeGen implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generateShapes(states, FramedSlopeBlock.SHAPES);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generateShapes(states, FramedSlopeBlock.OCCLUSION_SHAPES);
        }

        private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<SlopeType> shapeCache)
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                    shapeCache.get(SlopeType.BOTTOM),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, shapeCache.get(SlopeType.BOTTOM))
            );
            VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                    shapeCache.get(SlopeType.TOP),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, shapeCache.get(SlopeType.TOP))
            );
            VoxelShape shapeXZ = ShapeUtils.orUnoptimized(
                    shapeCache.get(SlopeType.BOTTOM),
                    shapeCache.get(SlopeType.TOP)
            );
            VoxelShape shapeY = ShapeUtils.orUnoptimized(
                    shapeCache.get(SlopeType.HORIZONTAL),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.EAST, shapeCache.get(SlopeType.HORIZONTAL))
            );

            VoxelShape[] shapes = new VoxelShape[12];
            for (DirectionAxis dirAxis : DirectionAxis.values())
            {
                Direction facing = dirAxis.direction();
                Direction.Axis axis = dirAxis.axis();

                if (Utils.isY(facing))
                {
                    shapes[dirAxis.ordinal()] = ShapeUtils.rotateShapeAroundY(
                            Direction.EAST,
                            Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                            facing == Direction.UP ? shapeBottom : shapeTop
                    );
                }
                else
                {
                    shapes[dirAxis.ordinal()] = ShapeUtils.rotateShapeAroundY(
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
        }
    }
}
