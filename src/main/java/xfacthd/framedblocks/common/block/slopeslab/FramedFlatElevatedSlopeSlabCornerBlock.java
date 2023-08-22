package xfacthd.framedblocks.common.block.slopeslab;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedFlatElevatedSlopeSlabCornerBlock extends FramedBlock
{
    public FramedFlatElevatedSlopeSlabCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.SOLID,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withCornerFacing(
                defaultBlockState(),
                context.getClickedFace(),
                context.getHorizontalDirection(),
                context.getClickLocation()
        );

        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction face = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face == dir.getOpposite() || face == dir.getClockWise())
        {
            boolean top = state.getValue(FramedProperties.TOP);
            Vec3 vec = Utils.fraction(hit.getLocation());

            if (getBlockType() == BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
            {
                if ((vec.y > .5) != top)
                {
                    face = Direction.UP;
                }
            }
            else //FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER
            {
                Direction perpDir = face == dir.getClockWise() ? dir : dir.getCounterClockWise();

                double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
                if (!Utils.isPositive(perpDir))
                {
                    hor = 1D - hor;
                }

                double y = vec.y();
                if (top)
                {
                    y = 1D - y;
                }
                y -= .5D;
                if ((y * 2D) >= hor)
                {
                    face = Direction.UP;
                }
            }
        }
        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
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
        return Utils.mirrorCornerBlock(state, mirror);
    }



    private record ShapeKey(Direction dir, boolean top) { }

    private static final ShapeCache<ShapeKey> FINAL_SHAPES = new ShapeCache<>(map ->
    {
        VoxelShape shapeSlopeBottom = FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE).move(0, .5, 0);
        VoxelShape shapeSlopeTop = FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE);

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                ShapeUtils.andUnoptimized(
                        shapeSlopeBottom,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
                ),
                box(0, 0, 0, 16, 8, 16)
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                ShapeUtils.andUnoptimized(
                        shapeSlopeTop,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop)
                ),
                box(0, 8, 0, 16, 16, 16)
        );

        ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH, map, ShapeKey::new);
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, FINAL_SHAPES.get(new ShapeKey(dir, top)));
        }

        return ShapeProvider.of(builder.build());
    }

    private static final ShapeCache<ShapeKey> FINAL_INNER_SHAPES = new ShapeCache<>(map ->
    {
        VoxelShape shapeSlopeBottom = FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE).move(0, .5, 0);
        VoxelShape shapeSlopeTop = FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE);

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                ShapeUtils.orUnoptimized(
                        shapeSlopeBottom,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
                ),
                box(0, 0, 0, 16, 8, 16)
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                ShapeUtils.orUnoptimized(
                        shapeSlopeTop,
                        ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop)
                ),
                box(0, 8, 0, 16, 16, 16)
        );

        ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH, map, ShapeKey::new);
    });

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, FINAL_INNER_SHAPES.get(new ShapeKey(dir, top)));
        }

        return ShapeProvider.of(builder.build());
    }
}
