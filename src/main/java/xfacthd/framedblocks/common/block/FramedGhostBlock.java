package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;

import net.minecraft.block.AbstractBlock.Properties;

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
    public BlockState getStateForPlacement(BlockItemUseContext context) { return null; }
}