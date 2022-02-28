package xfacthd.framedblocks.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedSoulWallTorchBlock extends FramedWallTorchBlock
{
    public FramedSoulWallTorchBlock()
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
    public BlockType getBlockType() { return BlockType.FRAMED_SOUL_WALL_TORCH; }
}