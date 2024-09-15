package xfacthd.framedblocks.common.data.shapes.cube;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.common.block.cube.FramedChestBlock;

public final class ChestShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape shapeSingle = Block.box(1D, 0D, 1D, 15D, 14D, 15D);
        VoxelShape[] conShapes = new VoxelShape[] {
                Block.box(1D, 0D, 1D, 15D, 14D, 16D),
                Block.box(0D, 0D, 1D, 15D, 14D, 15D),
                Block.box(1D, 0D, 0D, 15D, 14D, 15D),
                Block.box(1D, 0D, 1D, 16D, 14D, 15D)
        };

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
            if (type == ChestType.SINGLE)
            {
                builder.put(state, shapeSingle);
            }
            else
            {
                Direction conDir = FramedChestBlock.getConnectionDirection(state);
                builder.put(state, conShapes[conDir.get2DDataValue()]);
            }
        }

        return ShapeProvider.of(builder.build());
    }



    private ChestShapes() { }
}
