package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;

public class FramedPillarBlock extends FramedBlock
{
    public FramedPillarBlock(BlockType blockType) { super(blockType); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.AXIS, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        state = state.setValue(BlockStateProperties.AXIS, context.getClickedFace().getAxis());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generatePillarShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeX = box(0, 4, 4, 16, 12, 12);
        VoxelShape shapeY = box(4, 0, 4, 12, 16, 12);
        VoxelShape shapeZ = box(4, 4, 0, 12, 12, 16);

        for (BlockState state : states)
        {
            switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X:
                {
                    builder.put(state, shapeX);
                    break;
                }
                case Y:
                {
                    builder.put(state, shapeY);
                    break;
                }
                case Z:
                {
                    builder.put(state, shapeZ);
                    break;
                }
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generatePostShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeX = box(0, 6, 6, 16, 10, 10);
        VoxelShape shapeY = box(6, 0, 6, 10, 16, 10);
        VoxelShape shapeZ = box(6, 6, 0, 10, 10, 16);

        for (BlockState state : states)
        {
            switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X:
                {
                    builder.put(state, shapeX);
                    break;
                }
                case Y:
                {
                    builder.put(state, shapeY);
                    break;
                }
                case Z:
                {
                    builder.put(state, shapeZ);
                    break;
                }
            }
        }

        return builder.build();
    }
}