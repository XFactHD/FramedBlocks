package xfacthd.framedblocks.common.block.torch;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedSoulWallTorchBlock extends FramedWallTorchBlock
{
    public FramedSoulWallTorchBlock()
    {
        super(Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .lightLevel(state -> 14)
                .noOcclusion(),
                ParticleTypes.SOUL_FIRE_FLAME
        );
    }

    @Override
    public BlockType getBlockType()
    {
        return BlockType.FRAMED_SOUL_WALL_TORCH;
    }
}