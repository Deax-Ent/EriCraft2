package com.deaxent.ec2.blocks.Grinder;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.deaxent.ec2.blocks.MBlock;
import com.deaxent.ec2.items.MItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Maps;

public class GrinderRecipes
{
    private static final GrinderRecipes grindingBase = new GrinderRecipes();
    /** The list of grinding results. */
    private final Map grindingList = Maps.newHashMap();
    private final Map experienceList = Maps.newHashMap();

    public static GrinderRecipes instance()
    {
        return grindingBase;
    }

    private GrinderRecipes()
    {
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(MBlock.ore_tin)),
                new ItemStack(MItems.dust_tin, 2), 0.0F);
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(MBlock.ore_copper)),
                new ItemStack(MItems.dust_copper, 2), 0.0F);
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(MBlock.ore_silver)),
                new ItemStack(MItems.dust_silver, 2), 0.0F);
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(MBlock.ore_phenium)),
                new ItemStack(MItems.dust_phenium, 2), 0.0F);
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(Blocks.cobblestone)),
                new ItemStack(Item.getItemFromBlock(Blocks.sand)), 0.0F);
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(Blocks.iron_ore)),
                new ItemStack(MItems.dust_iron, 2), 0.0F);
        addGrindingRecipe(
                new ItemStack(Item.getItemFromBlock(Blocks.gold_ore)),
                new ItemStack(MItems.dust_gold, 2), 0.0F);
    }

    public void addGrindingRecipe(ItemStack parItemStackIn,
                                  ItemStack parItemStackOut, float parExperience)
    {
        grindingList.put(parItemStackIn, parItemStackOut);
        experienceList.put(parItemStackOut, Float.valueOf(parExperience));
    }

    /**
     * Returns the grinding result of an item.
     */
    public ItemStack getGrindingResult(ItemStack parItemStack)
    {
        Iterator iterator = grindingList.entrySet().iterator();
        Entry entry;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entry = (Map.Entry)iterator.next();
        }
        while (!areItemStacksEqual(parItemStack, (ItemStack)entry.getKey()));

        return (ItemStack)entry.getValue();
    }

    private boolean areItemStacksEqual(ItemStack parItemStack1,
                                       ItemStack parItemStack2)
    {
        return parItemStack2.getItem() == parItemStack1.getItem() && (parItemStack2.getMetadata() == 32767 || parItemStack2.getMetadata() == parItemStack1.getMetadata());
    }

    public Map getGrindingList()
    {
        return grindingList;
    }

    public float getGrindingExperience(ItemStack parItemStack)
    {
        Iterator iterator = experienceList.entrySet().iterator();
        Map.Entry entry;

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
