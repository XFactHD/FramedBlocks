package xfacthd.framedblocks.common.block.pane;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedFloorBlock extends FramedBlock
{
    public FramedFloorBlock()
    {
        super(BlockType.FRAMED_FLOOR_BOARD);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = box(0, 0, 0, 16, 1, 16);
        VoxelShape shapeTop = box(0, 15, 0, 16, 16, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, top ? shapeTop : shapeBottom);
        }

        return ShapeProvider.of(builder.build());
    }
}