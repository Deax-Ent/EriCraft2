package com.deaxent.ec2.items;

import com.deaxent.ec2.blocks.MachineFrame;
import com.deaxent.ec2.creativetabs.MCreativeTabs;
import com.deaxent.ec2.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MItems {

    // Stuffz
    public static Item energy_core;
    public static Item itemBattery;

    // Ingots
    public static Item ingot_tin;
    public static Item ingot_copper;
    public static Item ingot_silver;
    public static Item ingot_phenium;

    // Dusts
    public static Item dust_tin;
    public static Item dust_copper;
    public static Item dust_silver;
    public static Item dust_phenium;
    public static Item dust_iron;
    public static Item dust_gold;

    // Parts

    public static void MItems() {
        init();
        register();
    }

    public static void init() {
        // Stuffz
        energy_core = new Item().setUnlocalizedName("energy_core").setCreativeTab(MCreativeTabs.tabItems);
        itemBattery = new ItemBattery().setUnlocalizedName("itemBattery").setCreativeTab(MCreativeTabs.tabItems);

        // Ingots
        ingot_tin = new Item().setUnlocalizedName("ingot_tin").setCreativeTab(MCreativeTabs.tabItems);
        ingot_copper = new Item().setUnlocalizedName("ingot_copper").setCreativeTab(MCreativeTabs.tabItems);
        ingot_silver = new Item().setUnlocalizedName("ingot_silver").setCreativeTab(MCreativeTabs.tabItems);
        ingot_phenium = new Item().setUnlocalizedName("ingot_phenium").setCreativeTab(MCreativeTabs.tabItems);

        // Dusts
        dust_tin = new ItemDust().setUnlocalizedName("dust_tin").setCreativeTab(MCreativeTabs.tabItems);
        dust_copper = new ItemDust().setUnlocalizedName("dust_copper").setCreativeTab(MCreativeTabs.tabItems);
        dust_silver= new ItemDust().setUnlocalizedName("dust_silver").setCreativeTab(MCreativeTabs.tabItems);
        dust_phenium = new ItemDust().setUnlocalizedName("dust_phenium").setCreativeTab(MCreativeTabs.tabItems);
        dust_iron = new ItemDust().setUnlocalizedName("dust_iron").setCreativeTab(MCreativeTabs.tabItems);
        dust_gold = new ItemDust().setUnlocalizedName("dust_gold").setCreativeTab(MCreativeTabs.tabItems);

        // Parts
    }

    public static void register() {
        // Stuffz
        GameRegistry.registerItem(energy_core, energy_core.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(itemBattery, itemBattery.getUnlocalizedName().substring(5));

        // Ingots
        GameRegistry.registerItem(ingot_tin, ingot_tin.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ingot_copper, ingot_copper.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ingot_silver, ingot_silver.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ingot_phenium, ingot_phenium.getUnlocalizedName().substring(5));

        // Dusts
        GameRegistry.registerItem(dust_tin, dust_tin.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(dust_copper, dust_copper.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(dust_silver, dust_silver.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(dust_phenium, dust_phenium.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(dust_iron, dust_iron.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(dust_gold, dust_gold.getUnlocalizedName().substring(5));

        // Parts
    }

    public static void registerRenders() {
        // Stuffz
        registerRender(energy_core);
        registerRender(itemBattery);

        // Ingots
        registerRender(ingot_tin);
        registerRender(ingot_copper);
        registerRender(ingot_silver);
        registerRender(ingot_phenium);

        // Dusts
        registerRender(dust_tin);
        registerRender(dust_copper);
        registerRender(dust_silver);
        registerRender(dust_phenium);
        registerRender(dust_iron);
        registerRender(dust_gold);

        // Parts
    }

    public static void registerRender(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }

}
