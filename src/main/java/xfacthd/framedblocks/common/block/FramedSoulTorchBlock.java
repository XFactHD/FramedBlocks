package xfacthd.framedblocks.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
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
    public BlockItem createItemBlock()
    {
        BlockItem item = new WallOrFloorItem(
                FBContent.blockFramedSoulTorch.get(),
                FBContent.blockFramedSoulWallTorch.get(),
                new Item.Properties().tab(FramedBlocks.FRAMED_GROUP)
        );
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }
}