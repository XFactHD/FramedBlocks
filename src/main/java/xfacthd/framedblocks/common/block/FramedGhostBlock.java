package xfacthd.framedblocks.common.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class FramedGhostBlock extends Block
{
    public FramedGhostBlock()
    {
        super(Properties.of(Material.WOOD)
                .noOcclusion()
                .noCollission()
                .noDrops()
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { return null; }
}