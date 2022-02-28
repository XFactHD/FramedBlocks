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

public class FramedHalfPillarBlock extends FramedBlock
{
    public FramedHalfPillarBlock(BlockType blockType) { super(blockType); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        state = state.setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeNorth = box(4, 4, 0, 12, 12,  8);
        VoxelShape shapeSouth = box(4, 4, 8, 12, 12, 16);
        VoxelShape shapeEast =  box(8, 4, 4, 16, 12, 12);
        VoxelShape shapeWest =  box(0, 4, 4,  8, 12, 12);
        VoxelShape shapeUp =    box(4, 8, 4, 12, 16, 12);
        VoxelShape shapeDown =  box(4, 0, 4, 12,  8, 12);

        for (BlockState state : states)
        {
            switch (state.getValue(BlockStateProperties.FACING))
            {
                case NORTH:
                {
                    builder.put(state, shapeNorth);
                    break;
                }
                case EAST:
                {
                    builder.put(state, shapeEast);
                    break;
                }
                case SOUTH:
                {
                    builder.put(state, shapeSouth);
                    break;
                }
                case WEST:
                {
                    builder.put(state, shapeWest);
                    break;
                }
                case UP:
                {
                    builder.put(state, shapeUp);
                    break;
                }
                case DOWN:
                {
                    builder.put(state, shapeDown);
                    break;
                }
            }
        }

        return builder.build();
    }
}