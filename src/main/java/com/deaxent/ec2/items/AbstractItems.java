package com.deaxent.ec2.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class AbstractItems extends Item {

    protected int currentPower;
    protected int maxPower;

    public AbstractItems(int stackSize, int maxDamage) {
        this.maxStackSize = stackSize;
        this.setMaxDamage(maxDamage);
    }

    public boolean charge(ItemStack itemStack) {
        return false;
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getMaxPower() {
        return maxPower;
    }

}
