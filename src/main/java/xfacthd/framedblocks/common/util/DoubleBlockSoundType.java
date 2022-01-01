package xfacthd.framedblocks.common.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
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

        BlockState camo = mode == DoubleSoundMode.SECOND ? be.getCamoStateTwo() : be.getCamoState();
        if (!camo.isAir())
        {
            return camo.getSoundType(be.getLevel(), be.getBlockPos(), null);
        }

        return be.getBlockState().getSoundType();
    }

    private SoundType getEitherSoundType()
    {
        BlockState camo = be.getCamoState();
        if (!camo.isAir())
        {
            return camo.getSoundType(be.getLevel(), be.getBlockPos(), null);
        }

        camo = be.getCamoStateTwo();
        if (!camo.isAir())
        {
            return camo.getSoundType(be.getLevel(), be.getBlockPos(), null);
        }

        return be.getBlockState().getSoundType();
    }
}
