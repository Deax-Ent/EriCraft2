package com.deaxent.ec2.power;

import net.minecraft.util.MathHelper;

public class AbstractEnergyMachine extends AbstractEnergyTileEntityLockable implements IEnergy {

    private int energy = 0;
    private int maxEnergy = 10000;
    private int maxEnergyExtracted = 20;
    private int maxEnergyReceived = 40;

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    @Override
    public int getMaxExtract() {
        return maxEnergyExtracted;
    }

    @Override
    public int getMaxReceive() {
        return maxEnergyReceived;
    }

    @Override
    public boolean hasEnergy() {
        return this.energy != 0 ? true : false;
    }

    @Override
    public int getEnergyUsePerTick() {
        return 0;
    }

    @Override
    public void setMaxExtract(int maxEnergyExtracted) {
        this.maxEnergyExtracted = maxEnergyExtracted;
    }

    @Override
    public void setMaxReceive(int maxEnergyReceived) {
        this.maxEnergyReceived = maxEnergyReceived;
    }

    @Override
    public void setEnergyStored(int stored) {
        energy = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
    }
}
