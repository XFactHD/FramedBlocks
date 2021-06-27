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
        super(Properties.create(Material.MISCELLANEOUS)
                .doesNotBlockMovement()
                .hardnessAndResistance(0.5F)
                .sound(SoundType.WOOD)
                .setLightLevel(state -> 14)
                .notSolid(),
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
                new Item.Properties().group(FramedBlocks.FRAMED_GROUP)
        );
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }
}