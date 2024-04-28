package xfacthd.framedblocks.common.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.EnergyStorage;

public class EntityAwareEnergyStorage extends EnergyStorage
{
    private final Runnable changeNotifier;

    public EntityAwareEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable changeNotifier)
    {
        super(capacity, maxReceive, maxExtract);
        this.changeNotifier = changeNotifier;
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
            changeNotifier.run();
        }
    }

    public int getMaxReceive()
    {
        return maxReceive;
    }

    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", energy);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag)
    {
        energy = tag instanceof CompoundTag cmpTag ? cmpTag.getInt("energy") : 0;
        energy = Math.min(energy, capacity);
    }
}
