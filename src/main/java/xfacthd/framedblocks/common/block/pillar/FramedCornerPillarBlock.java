package xfacthd.framedblocks.common.block.pillar;

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
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.api.util.Utils;

import java.util.EnumMap;

public class FramedCornerPillarBlock extends FramedBlock
{
    public FramedCornerPillarBlock()
    {
        super(BlockType.FRAMED_CORNER_PILLAR);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        Vec3 hitPoint = Utils.fraction(context.getClickLocation());
        if (face.getAxis().isHorizontal())
        {
            state = withCornerFacing(
                    state,
                    context.getClickedFace(),
                    context.getHorizontalDirection(),
                    context.getClickLocation()
            );
        }
        else
        {
            double x = hitPoint.x();
            double z = hitPoint.z();

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) != Utils.isPositive(dir)) { dir = dir.getClockWise(); }
            state = state.setValue(FramedProperties.FACING_HOR, dir);
        }

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }



    public static final ShapeCache<Direction> SHAPES = new ShapeCache<>(new EnumMap<>(Direction.class), map ->
    {
        VoxelShape shape = box(0, 0, 0, 8, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, SHAPES.get(dir));
        }

        return ShapeProvider.of(builder.build());
    }
}