package com.deaxent.ec2.power;

public interface IEnergy {

    int getEnergyStored();

    int getMaxEnergyStored();

    int getMaxExtract();

    int getMaxReceive();

    boolean hasEnergy();

    int getEnergyUsePerTick();

    void setMaxExtract(int maxEnergyExtracted);

    void setMaxReceive(int maxEnergyReceived);

    void setEnergyStored(int stored);

}
