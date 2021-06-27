package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;

public class FramedGhostBlock extends Block
{
    public FramedGhostBlock()
    {
        super(Properties.create(Material.WOOD)
                .notSolid()
                .doesNotBlockMovement()
                .noDrops()
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) { return null; }
}