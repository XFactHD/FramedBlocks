package xfacthd.framedblocks.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

public class DoubleBlockSoundType extends SoundType
{
    private final FramedDoubleTileEntity te;

    public DoubleBlockSoundType(FramedDoubleTileEntity te)
    {
        //noinspection deprecation, ConstantConditions
        super(0, 0, null, null, null, null, null);
        this.te = te;
    }

    @Override
    public float getVolume() { return getSoundType(te.getSoundMode()).getVolume(); }

    @Override
    public float getPitch() { return getSoundType(te.getSoundMode()).getPitch(); }

    @Override
    public SoundEvent getBreakSound() { return getEitherSoundType().getBreakSound(); }

    @Override
    public SoundEvent getStepSound() { return getSoundType(te.getSoundMode()).getStepSound(); }

    @Override
    public SoundEvent getPlaceSound()
    {
        return FBContent.blockFramedCube.get().getDefaultState().getSoundType().getPlaceSound();
    }

    @Override
    public SoundEvent getHitSound() { return getEitherSoundType().getHitSound(); }

    @Override
    public SoundEvent getFallSound() { return getSoundType(te.getSoundMode()).getFallSound(); }



    private SoundType getSoundType(DoubleSoundMode mode)
    {
        if (mode == DoubleSoundMode.EITHER) { return getEitherSoundType(); }

        BlockState camo = mode == DoubleSoundMode.SECOND ? te.getCamoStateTwo() : te.getCamoState();
        //noinspection deprecation
        if (!camo.isAir())
        {
            return camo.getSoundType(te.getWorld(), te.getPos(), null);
        }

        return FBContent.blockFramedCube.get().getDefaultState().getSoundType();
    }

    private SoundType getEitherSoundType()
    {
        BlockState camo = te.getCamoState();
        //noinspection deprecation
        if (!camo.isAir())
        {
            return camo.getSoundType(te.getWorld(), te.getPos(), null);
        }

        camo = te.getCamoStateTwo();
        //noinspection deprecation
        if (!camo.isAir())
        {
            return camo.getSoundType(te.getWorld(), te.getPos(), null);
        }

        return FBContent.blockFramedCube.get().getDefaultState().getSoundType();
    }
}
