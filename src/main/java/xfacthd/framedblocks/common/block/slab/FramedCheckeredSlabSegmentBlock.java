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
public class FramedCheckeredSlabSegmentBlock extends FramedBlock
{
    public FramedCheckeredSlabSegmentBlock()
    {
        super(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.SECOND, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, PropertyHolder.SECOND, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.SECOND, Utils.isX(ctx.getHorizontalDirection())
                ))
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        if (Utils.isY(side))
        {
            if (rot != Rotation.NONE && rot != Rotation.CLOCKWISE_180)
            {
                return state.cycle(PropertyHolder.SECOND);
            }
        }
        else
        {
            if (rot != Rotation.NONE)
            {
                return state.cycle(FramedProperties.TOP);
            }
        }
        return super.rotate(state, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror != Mirror.NONE)
        {
            return state.cycle(PropertyHolder.SECOND);
        }
        return super.mirror(state, mirror);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBotFirst = ShapeUtils.or(
                box(0, 0, 0,  8,  8,  8),
                box(8, 0, 8, 16,  8, 16)
        );
        VoxelShape shapeTopFirst = ShapeUtils.or(
                box(8, 8, 0, 16, 16,  8),
                box(0, 8, 8,  8, 16, 16)
        );
        VoxelShape shapeBotSecond = ShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, shapeBotFirst);
        VoxelShape shapeTopSecond = ShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, shapeTopFirst);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            boolean top = state.getValue(FramedProperties.TOP);
            boolean second = state.getValue(PropertyHolder.SECOND);
            builder.put(state, second ? (top ? shapeTopSecond : shapeBotSecond) : (top ? shapeTopFirst : shapeBotFirst));
        }
        return ShapeProvider.of(builder.build());
    }
}
