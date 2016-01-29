package com.deaxent.ec2.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemBattery extends AbstractItems {

    private int currentPower = 0;
    private int maxPower = 1000;

    public ItemBattery() {
        super(1, 1000);
        this.maxStackSize = 1;
        this.setMaxDamage(1000);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean bool) {
        if(itemStack.getTagCompound() != null) {
            System.out.println("Battery did have compound!");
            if(itemStack.getTagCompound().hasKey("BatteryPower")) {
                System.out.println("Battery did have key BatteryPower..");
                list.add("CurrentPower: " + itemStack.getTagCompound().getInteger("CurrentPower"));
            } else {
                System.out.println("Battery did not have key BatteryPower..");
                list.add("Current Power: 0 / " + maxPower);
            }
        }
    }

    @Override
    public boolean charge(ItemStack itemStack) {
        currentPower++;
        System.out.println("Charging Battery");

        if (currentPower > maxPower) {
            currentPower = maxPower;
            return false;
        }

        if(itemStack.getTagCompound() != null) {
            itemStack.getTagCompound().setInteger("CurrentPower", currentPower);
        }

        return true;
    }

    @Override
    public int getCurrentPower() {
        return currentPower;
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
        if(itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
            System.out.println("Battery did not have compound! Creating...");
        }
        System.out.println("Setting compound info for Battery..");
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("CurrentPower", 0);
        itemStack.getTagCompound().setTag("BatteryPower", nbt);
        itemStack.setStackDisplayName(EnumChatFormatting.DARK_PURPLE + "Battery");
    }

}
