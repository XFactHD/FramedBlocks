package xfacthd.framedblocks.common.capability;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.EnergyStorage;

public class EntityAwareEnergyStorage extends EnergyStorage
{
    private final BlockEntity be;

    public EntityAwareEnergyStorage(int capacity, int maxReceive, int maxExtract, BlockEntity be)
    {
        super(capacity, maxReceive, maxExtract);
        this.be = be;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        int received = super.receiveEnergy(maxReceive, simulate);
        onContentsChanged(received, simulate);
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        int extracted = super.extractEnergy(maxExtract, simulate);
        onContentsChanged(extracted, simulate);
        return extracted;
    }

    public void extractEnergyInternal(int maxExtract)
    {
        energy -= maxExtract;
        onContentsChanged(maxExtract, false);
    }

    private void onContentsChanged(int diff, boolean simulate)
    {
        if (!simulate && diff > 0)
        {
            be.setChanged();
        }
    }
}
