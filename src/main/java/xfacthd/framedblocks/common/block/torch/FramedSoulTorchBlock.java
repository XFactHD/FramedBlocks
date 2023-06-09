package xfacthd.framedblocks.common.block.torch;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedSoulTorchBlock extends FramedTorchBlock
{
    public FramedSoulTorchBlock()
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
    public BlockType getBlockType() { return BlockType.FRAMED_SOUL_TORCH; }

    @Override
    public BlockItem createBlockItem()
    {
        return new StandingAndWallBlockItem(
                FBContent.blockFramedSoulTorch.get(),
                FBContent.blockFramedSoulWallTorch.get(),
                new Item.Properties(),
                Direction.DOWN
        );
    }
}