package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

import javax.annotation.Nullable;

public class FramedVerticalHalfStairsBlock extends FramedBlock
{
    public FramedVerticalHalfStairsBlock() { super(BlockType.FRAMED_VERTICAL_HALF_STAIRS); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection());
        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = VoxelShapes.join(
                Block.box(0, 0, 8, 16, 8, 16),
                Block.box(8, 0, 0, 16, 8,  8),
                IBooleanFunction.OR
        );

        VoxelShape shapeTop = VoxelShapes.join(
                Block.box(0, 8, 8, 16, 16, 16),
                Block.box(8, 8, 0, 16, 16,  8),
                IBooleanFunction.OR
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR).getOpposite();
            boolean top = state.getValue(PropertyHolder.TOP);

            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, top ? shapeTop : shapeBottom));
        }

        return builder.build();
    }
}
