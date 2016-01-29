package com.deaxent.ec2.crafting;

import java.util.Map;
import java.util.Map.Entry;

import com.deaxent.ec2.items.MItems;
import com.google.common.collect.Maps;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public class ChargerRecipes {

    private static final ChargerRecipes chargerBase = new ChargerRecipes();
    private final Map chargingList = Maps.newHashMap();
    private final Map experienceList = Maps.newHashMap();

    public static ChargerRecipes instance() {
        return chargerBase;
    }

    private ChargerRecipes() {
        addChargingRecipe(
                new ItemStack(MItems.itemBattery),
                new ItemStack(MItems.itemBattery), 0.7F
        );
        addChargingRecipe(
                new ItemStack(Items.coal),
                new ItemStack(Items.diamond), 0.7F
        );
    }

    public void addChargingRecipe(ItemStack itemstack, ItemStack itemstack2, float experience) {
        chargingList.put(itemstack, itemstack2);
        experienceList.put(itemstack2, Float.valueOf(experience));
    }

    public ItemStack getChargingResult(ItemStack itemstack) {
        Iterator iterator = chargingList.entrySet().iterator();
        Entry entry;

        do {
            if(!iterator.hasNext()) {
                return null;
            }
            entry = (Entry) iterator.next();
        }
        while(!areItemStacksEqual(itemstack, (ItemStack) entry.getKey()));
            return (ItemStack) entry.getValue();
    }

    private boolean areItemStacksEqual(ItemStack itemstack, ItemStack itemstack2) {
        return itemstack2.getItem() == itemstack.getItem() && (itemstack2.getMetadata() == 32767 || itemstack2.getMetadata() == itemstack.getMetadata());
    }

    public float getChargingExperience(ItemStack itemstack) {
        Iterator iterator = experienceList.entrySet().iterator();
        Entry entry;

        do {
            if(!iterator.hasNext()) {
                return 0.0F;
            }
            entry = (Entry) iterator.next();
        }
        while(!areItemStacksEqual(itemstack, (ItemStack) entry.getKey()));
            return ((Float) entry.getValue()).floatValue();
    }

}
