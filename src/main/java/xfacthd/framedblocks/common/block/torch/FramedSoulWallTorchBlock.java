package xfacthd.framedblocks.common.block.torch;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedSoulWallTorchBlock extends FramedWallTorchBlock
{
    public FramedSoulWallTorchBlock()
    {
        super(ParticleTypes.SOUL_FIRE_FLAME, Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .lightLevel(state -> 14)
                .noOcclusion()
        );
    }

    @Override
    public BlockType getBlockType()
    {
        return BlockType.FRAMED_SOUL_WALL_TORCH;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return ((IFramedBlock) FBContent.BLOCK_FRAMED_SOUL_TORCH.value()).getJadeRenderState(state);
    }

    @Override
    public float getJadeRenderScale(BlockState state)
    {
        return ((IFramedBlock) FBContent.BLOCK_FRAMED_SOUL_TORCH.value()).getJadeRenderScale(state);
    }
}
