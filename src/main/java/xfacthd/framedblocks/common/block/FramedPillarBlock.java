package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;

public class FramedPillarBlock extends FramedBlock
{
    public FramedPillarBlock(BlockType blockType) { super(blockType); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.AXIS, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        state = state.setValue(BlockStateProperties.AXIS, context.getClickedFace().getAxis());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot != Rotation.NONE)
        {
            return state.cycle(BlockStateProperties.AXIS);
        }
        return state;
    }



    public static ImmutableMap<BlockState, VoxelShape> generatePillarShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeX = box(0, 4, 4, 16, 12, 12);
        VoxelShape shapeY = box(4, 0, 4, 12, 16, 12);
        VoxelShape shapeZ = box(4, 4, 0, 12, 12, 16);

        for (BlockState state : states)
        {
            builder.put(state, switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X -> shapeX;
                case Y -> shapeY;
                case Z -> shapeZ;
            });
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
            builder.put(state, switch (state.getValue(BlockStateProperties.AXIS))
                    {
                        case X -> shapeX;
                        case Y -> shapeY;
                        case Z -> shapeZ;
                    });
        }

        return builder.build();
    }
}