package xfacthd.framedblocks.common.block.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedCenteredPanelBlock extends FramedBlock
{
    public FramedCenteredPanelBlock()
    {
        super(BlockType.FRAMED_CENTERED_PANEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_NE, FramedProperties.SOLID, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                {
                    Direction dir = modCtx.getHorizontalDirection();
                    if (dir == Direction.SOUTH || dir == Direction.WEST)
                    {
                        dir = dir.getOpposite();
                    }
                    return state.setValue(FramedProperties.FACING_NE, dir);
                })
                .withWater()
                .build();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot == Rotation.NONE || rot == Rotation.CLOCKWISE_180)
        {
            return state;
        }
        return state.cycle(FramedProperties.FACING_NE);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeNorth = box(0, 0, 4, 16, 16, 12);
        VoxelShape shapeEast = box(4, 0, 0, 12, 16, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> shapes = ImmutableMap.builder();
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_NE);
            shapes.put(state, dir == Direction.NORTH ? shapeNorth : shapeEast);
        }
        return ShapeProvider.of(shapes.build());
    }
}
