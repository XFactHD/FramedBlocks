package xfacthd.framedblocks.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedCube extends FramedBlock
{
    private FramedCube(BlockType type) { super(type); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING);
    }



    public static FramedCube cube() { return new FramedCube(BlockType.FRAMED_CUBE); }

    public static FramedCube glowingCube() { return new FramedCube(BlockType.FRAMED_GLOWING_CUBE); }
}
