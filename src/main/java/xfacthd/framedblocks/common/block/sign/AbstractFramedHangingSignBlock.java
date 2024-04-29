package xfacthd.framedblocks.common.block.sign;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public abstract class AbstractFramedHangingSignBlock extends AbstractFramedSignBlock
{
    protected AbstractFramedHangingSignBlock(BlockType type, Properties props)
    {
        super(type, props);
    }

    @Override
    public int getTextLineHeight()
    {
        return 9;
    }

    @Override
    public int getMaxTextLineWidth()
    {
        return 60;
    }

    @Override
    public SoundEvent getSignInteractionFailedSoundEvent()
    {
        return SoundEvents.WAXED_HANGING_SIGN_INTERACT_FAIL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return FramedSignBlockEntity.hangingSign(pos, state);
    }
}
