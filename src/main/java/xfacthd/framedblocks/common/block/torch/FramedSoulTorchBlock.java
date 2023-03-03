package xfacthd.framedblocks.common.block.torch;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedSoulTorchBlock extends FramedTorchBlock
{
    public FramedSoulTorchBlock()
    {
        super(Properties.of(Material.DECORATION)
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .lightLevel(state -> 14)
                .noOcclusion(),
                ParticleTypes.SOUL_FIRE_FLAME
        );
    }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_SOUL_TORCH; }

    @Override
    public BlockItem createBlockItem()
    {
        return new StandingAndWallBlockItem(
                FBContent.blockFramedSoulTorch.get(),
                FBContent.blockFramedSoulWallTorch.get(),
                new Item.Properties().tab(FramedBlocks.FRAMED_TAB)
        );
    }
}