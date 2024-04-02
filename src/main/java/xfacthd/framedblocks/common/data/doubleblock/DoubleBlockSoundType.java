package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public final class DoubleBlockSoundType extends SoundType
{
    private final FramedDoubleBlockEntity be;

    public DoubleBlockSoundType(FramedDoubleBlockEntity be)
    {
        //noinspection deprecation, ConstantConditions
        super(0, 0, null, null, null, null, null);
        this.be = be;
    }

    @Override
    public float getVolume()
    {
        return getSoundType(be.getTopInteractionMode()).getVolume();
    }

    @Override
    public float getPitch()
    {
        return getSoundType(be.getTopInteractionMode()).getPitch();
    }

    // This is only called when no camo is applied and should therefore return the default break sound
    @Override
    public SoundEvent getBreakSound()
    {
        return be.getBlockState().getSoundType().getBreakSound();
    }

    @Override
    public SoundEvent getStepSound()
    {
        return getSoundType(be.getTopInteractionMode()).getStepSound();
    }

    @Override
    public SoundEvent getPlaceSound()
    {
        return FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState().getSoundType().getPlaceSound();
    }

    @Override
    public SoundEvent getHitSound()
    {
        return getEitherSoundType().getHitSound();
    }

    @Override
    public SoundEvent getFallSound()
    {
        return getSoundType(be.getTopInteractionMode()).getFallSound();
    }



    private SoundType getSoundType(DoubleBlockTopInteractionMode mode)
    {
        if (mode == DoubleBlockTopInteractionMode.EITHER)
        {
            return getEitherSoundType();
        }

        CamoContainer<?, ?> camo = mode == DoubleBlockTopInteractionMode.SECOND ? be.getCamoTwo() : be.getCamo();
        if (!camo.isEmpty())
        {
            return camo.getContent().getSoundType();
        }

        // Default to no-camo sound
        return be.getBlockState().getSoundType();
    }

    private SoundType getEitherSoundType()
    {
        CamoContainer<?, ?> camo = be.getCamo();
        if (!camo.isEmpty())
        {
            return camo.getContent().getSoundType();
        }

        camo = be.getCamoTwo();
        if (!camo.isEmpty())
        {
            return camo.getContent().getSoundType();
        }

        // Default to no-camo sound
        return be.getBlockState().getSoundType();
    }
}
