package com.deaxent.ec2.blocks.Smelter;

import com.deaxent.ec2.items.MItems;
import com.google.common.collect.Maps;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SmelterRecipes {

    private static final SmelterRecipes smeltingBase = new SmelterRecipes();

    private final Map smeltingList = Maps.newHashMap();
    private final Map experienceList = Maps.newHashMap();

    public static SmelterRecipes instance() { return smeltingBase; }

    private SmelterRecipes() {
        addSmeltingRecipe(
            new ItemStack(MItems.dust_tin),
            new ItemStack(MItems.ingot_tin), 1.0F);
        addSmeltingRecipe(
                new ItemStack(MItems.dust_copper),
                new ItemStack(MItems.ingot_copper), 1.0F);
        addSmeltingRecipe(
                new ItemStack(MItems.dust_silver),
                new ItemStack(MItems.ingot_silver), 1.0F);
        addSmeltingRecipe(
                new ItemStack(MItems.dust_phenium),
                new ItemStack(MItems.ingot_phenium), 1.0F);
        addSmeltingRecipe(
                new ItemStack(MItems.dust_iron),
                new ItemStack(Items.iron_ingot), 1.0F);
        addSmeltingRecipe(
                new ItemStack(MItems.dust_gold),
                new ItemStack(Items.gold_ingot), 1.0F);
    }

    public void addSmeltingRecipe(ItemStack stackIn, ItemStack stackOut, float exp) {
        smeltingList.put(stackIn, stackOut);
        experienceList.put(stackOut, Float.valueOf(exp));
    }

    public ItemStack getSmeltingResult(ItemStack parItemStack)
    {
        Iterator iterator = smeltingList.entrySet().iterator();
        Entry entry;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entry = (Entry)iterator.next();
        }
        while (!areItemStacksEqual(parItemStack, (ItemStack)entry.getKey()));

        return (ItemStack)entry.getValue();
    }

    private boolean areItemStacksEqual(ItemStack parItemStack1,
                                       ItemStack parItemStack2)
    {
        return parItemStack2.getItem() == parItemStack1.getItem() && (parItemStack2.getMetadata() == 32767 || parItemStack2.getMetadata() == parItemStack1.getMetadata());
    }

    public Map getSmeltingList()
    {
        return smeltingList;
    }

    public float getSmeltingExperience(ItemStack parItemStack)
    {
        Iterator iterator = experienceList.entrySet().iterator();
        Entry entry;

        do
        {
            if (!iterator.hasNext())
            {
                return 0.0F;
            }

            entry = (Entry)iterator.next();
        }
        while (!areItemStacksEqual(parItemStack, (ItemStack) entry.getKey()));

        return ((Float) entry.getValue()).floatValue();
    }

}
