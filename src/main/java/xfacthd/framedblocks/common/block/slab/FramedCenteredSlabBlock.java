package xfacthd.framedblocks.common.block.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedCenteredSlabBlock extends FramedBlock
{
    public FramedCenteredSlabBlock()
    {
        super(BlockType.FRAMED_CENTERED_SLAB);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, BlockStateProperties.WATERLOGGED);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = box(0, 4, 0, 16, 12, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> shapes = ImmutableMap.builder();
        for (BlockState state : states)
        {
            shapes.put(state, shape);
        }
        return ShapeProvider.of(shapes.build());
    }
}
