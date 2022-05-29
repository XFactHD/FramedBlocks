package xfacthd.framedblocks.common.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class DoubleBlockSoundType extends SoundType
{
    private final FramedDoubleBlockEntity be;

    public DoubleBlockSoundType(FramedDoubleBlockEntity be)
    {
        //noinspection deprecation, ConstantConditions
        super(0, 0, null, null, null, null, null);
        this.be = be;
    }

    @Override
    public float getVolume() { return getSoundType(be.getSoundMode()).getVolume(); }

    @Override
    public float getPitch() { return getSoundType(be.getSoundMode()).getPitch(); }

    @Override
    public SoundEvent getBreakSound() { return be.getBlockState().getSoundType().getBreakSound(); }

    @Override
    public SoundEvent getStepSound() { return getSoundType(be.getSoundMode()).getStepSound(); }

    @Override
    public SoundEvent getPlaceSound()
    {
        return FBContent.blockFramedCube.get().defaultBlockState().getSoundType().getPlaceSound();
    }

    @Override
    public SoundEvent getHitSound() { return getEitherSoundType().getHitSound(); }

    @Override
    public SoundEvent getFallSound() { return getSoundType(be.getSoundMode()).getFallSound(); }



    private SoundType getSoundType(DoubleSoundMode mode)
    {
        if (mode == DoubleSoundMode.EITHER) { return getEitherSoundType(); }

        CamoContainer camo = mode == DoubleSoundMode.SECOND ? be.getCamoTwo() : be.getCamo();
        if (!camo.isEmpty())
        {
            return camo.getState().getSoundType(be.getLevel(), be.getBlockPos(), null);
        }

        return be.getBlockState().getSoundType();
    }

    private SoundType getEitherSoundType()
    {
        CamoContainer camo = be.getCamo();
        if (!camo.isEmpty())
        {
            return camo.getState().getSoundType(be.getLevel(), be.getBlockPos(), null);
        }

        camo = be.getCamoTwo();
        if (!camo.isEmpty())
        {
            return camo.getState().getSoundType(be.getLevel(), be.getBlockPos(), null);
        }

        return be.getBlockState().getSoundType();
    }
}
