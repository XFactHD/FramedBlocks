package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public class FramedThreewayCornerBlock extends FramedBlock
{
    public FramedThreewayCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face) || face == dir.getOpposite() || face == dir.getClockWise())
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if ((face == dir || face == dir.getCounterClockWise()) && rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }



    public static final class ThreewayShapeGen implements SplitShapeGenerator
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

            VoxelShape shapeTop = ShapeUtils.andUnoptimized(
                    shapeCache.get(SlopeType.TOP),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH, Direction.WEST, shapeCache.get(SlopeType.TOP)
                    ),
                    shapeCache.get(SlopeType.HORIZONTAL)
            );

            VoxelShape shapeBottom = ShapeUtils.andUnoptimized(
                    shapeCache.get(SlopeType.BOTTOM),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH, Direction.WEST, shapeCache.get(SlopeType.BOTTOM)
                    ),
                    shapeCache.get(SlopeType.HORIZONTAL)
            );

            VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

            for (BlockState state : states)
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                boolean top = state.getValue(FramedProperties.TOP);
                builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
            }

            return ShapeProvider.of(builder.build());
        }
    }

    public static final class InnerThreewayShapeGen implements SplitShapeGenerator
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

            VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                    shapeCache.get(SlopeType.TOP),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH, Direction.WEST, shapeCache.get(SlopeType.TOP)
                    ),
                    shapeCache.get(SlopeType.HORIZONTAL)
            );

            VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                    shapeCache.get(SlopeType.BOTTOM),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(
                            Direction.NORTH, Direction.WEST, shapeCache.get(SlopeType.BOTTOM)
                    ),
                    shapeCache.get(SlopeType.HORIZONTAL)
            );

            VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

            for (BlockState state : states)
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                boolean top = state.getValue(FramedProperties.TOP);
                builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
            }

            return ShapeProvider.of(builder.build());
        }
    }
}
