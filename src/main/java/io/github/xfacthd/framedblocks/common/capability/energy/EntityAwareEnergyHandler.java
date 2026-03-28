package io.github.xfacthd.framedblocks.common.capability.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public class EntityAwareEnergyHandler extends SimpleEnergyHandler {
    private final Runnable changeNotifier;

    public EntityAwareEnergyHandler(int capacity, int maxReceive, int maxExtract, Runnable changeNotifier) {
        super(capacity, maxReceive, maxExtract);
        this.changeNotifier = changeNotifier;
    }

    public void extractEnergyInternal(int maxExtract) {
        int prevEnergy = energy;
        energy -= maxExtract;
        onEnergyChanged(prevEnergy);
    }

    @Override
    protected void onEnergyChanged(int previousEnergy) {
        changeNotifier.run();
    }

    public int getMaxReceive() {
        return maxInsert;
    }

    public int getCapacity() {
        return capacity;
    }
}
