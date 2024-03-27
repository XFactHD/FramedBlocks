package xfacthd.framedblocks.common.block.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

@SuppressWarnings("deprecation")
public class FramedCheckeredPanelSegmentBlock extends FramedBlock
{
    public FramedCheckeredPanelSegmentBlock()
    {
        super(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.SECOND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SECOND, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.SECOND, Utils.isX(ctx.getHorizontalDirection())
                ))
                .withWater()
                .build();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot != Rotation.CLOCKWISE_180)
        {
            state = state.cycle(PropertyHolder.SECOND);
        }
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror).cycle(PropertyHolder.SECOND);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeFirst = ShapeUtils.orUnoptimized(
                box(0, 0, 0,  8,  8,  8),
                box(8, 8, 0, 16, 16,  8)
        );
        VoxelShape shapeSecond = ShapeUtils.orUnoptimized(
                box(0, 8, 0,  8, 16,  8),
                box(8, 0, 0, 16,  8,  8)
        );

        VoxelShape[] shapes = new VoxelShape[8];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            int idx = dir.get2DDataValue();
            boolean x = Utils.isX(dir);
            shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, x ? shapeSecond : shapeFirst);
            shapes[idx + 4] = ShapeUtils.rotateShape(Direction.NORTH, dir, x ? shapeFirst : shapeSecond);
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean second = state.getValue(PropertyHolder.SECOND);
            int idx = dir.get2DDataValue() + (second ? 4 : 0);
            builder.put(state, shapes[idx]);
        }
        return ShapeProvider.of(builder.build());
    }
}
