package xfacthd.framedblocks.common.block.slopeedge;

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
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.block.stairs.FramedVerticalStairsBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.property.StairsType;

@SuppressWarnings("deprecation")
public class FramedElevatedSlopeEdgeBlock extends FramedBlock implements IComplexSlopeSource
{
    public FramedElevatedSlopeEdgeBlock()
    {
        super(BlockType.FRAMED_ELEVATED_SLOPE_EDGE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, FramedProperties.SOLID,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction face = hit.getDirection();
        if (hit.getDirection() == dir.getOpposite())
        {
            Direction coordDir = switch (state.getValue(PropertyHolder.SLOPE_TYPE))
            {
                case BOTTOM -> Direction.UP;
                case HORIZONTAL -> dir.getClockWise();
                case TOP -> Direction.DOWN;
            };
            if (Utils.fractionInDir(hit.getLocation(), coordDir) < .5)
            {
                face = dir;
            }
        }
        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (Utils.isY(face) || (type != SlopeType.HORIZONTAL && face == dir.getOpposite()))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE && face == dir)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return Utils.mirrorFaceBlock(state, mirror);
        }
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public boolean isHorizontalSlope(BlockState state)
    {
        return state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL;
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                ShapeUtils.orUnoptimized(box(0, 0, 0, 16, 8, 16), box(0, 8, 0, 16, 16, 8)),
                FramedSlopeEdgeBlock.SHAPES.get(new FramedSlopeEdgeBlock.ShapeKey(SlopeType.BOTTOM, true))
        );
        VoxelShape shapeHorizontal = ShapeUtils.orUnoptimized(
                FramedVerticalStairsBlock.SHAPES.get(new FramedVerticalStairsBlock.ShapeKey(
                        Direction.NORTH, StairsType.VERTICAL
                )),
                FramedSlopeEdgeBlock.SHAPES.get(new FramedSlopeEdgeBlock.ShapeKey(SlopeType.HORIZONTAL, true))
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                ShapeUtils.orUnoptimized(box(0, 8, 0, 16, 16, 16), box(0, 0, 0, 16, 8, 8)),
                FramedSlopeEdgeBlock.SHAPES.get(new FramedSlopeEdgeBlock.ShapeKey(SlopeType.TOP, true))
        );

        VoxelShape[] shapes = new VoxelShape[4 * 3];

        ShapeUtils.makeHorizontalRotations(shapeBottom, Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(shapeHorizontal, Direction.NORTH, shapes, SlopeType.HORIZONTAL.ordinal() << 2);
        ShapeUtils.makeHorizontalRotations(shapeTop, Direction.NORTH, shapes, SlopeType.TOP.ordinal() << 2);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();

        for (BlockState state : states)
        {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int idx = (type.ordinal() << 2) + dir.get2DDataValue();
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
