package com.deaxent.ec2.blocks;

//import com.deaxent.ec2.blocks.Charger.BlockCharger;
import com.deaxent.ec2.blocks.Grinder.Grinder;
import com.deaxent.ec2.blocks.Smelter.Smelter;
import com.deaxent.ec2.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MBlock {

    public enum GUI_ENUM {
        CHARGER, GRINDER, SMELTER
    }

    //public static Block blockCharger;

    public static Block block_core;

    public static Block ore_copper;
    public static Block ore_tin;
    public static Block ore_silver;
    public static Block ore_phenium;

    // Machines
    public static Block grinder;
    public static Block smelter;

    // Parts
    public static Block machine_frame;

    public static void MBlock() {
        init();
        register();
    }

    public static void init() {
        //blockCharger = new BlockCharger().setUnlocalizedName("blockCharger");

        block_core = new BlockCore(Material.iron).setUnlocalizedName("block_core");

        // Ores
        ore_copper = new OreCopper(Material.rock).setUnlocalizedName("ore_copper");
        ore_tin = new OreTin(Material.rock).setUnlocalizedName("ore_tin");
        ore_silver = new OreSilver(Material.rock).setUnlocalizedName("ore_silver");
        ore_phenium = new OrePhenium(Material.rock).setUnlocalizedName("ore_phenium");

        // Machines
        grinder = new Grinder(Material.rock).setUnlocalizedName("grinder");
        smelter = new Smelter(Material.rock).setUnlocalizedName("smelter");

        // Parts
        machine_frame = new MachineFrame(1).setUnlocalizedName("machine_frame");
    }

    public static void register() {
        //GameRegistry.registerBlock(blockCharger, blockCharger.getUnlocalizedName().substring(5));

        GameRegistry.registerBlock(block_core, block_core.getUnlocalizedName().substring(5));

        GameRegistry.registerBlock(ore_copper, ore_copper.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(ore_tin, ore_tin.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(ore_silver, ore_silver.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(ore_phenium, ore_phenium.getUnlocalizedName().substring(5));

        // Machines
        GameRegistry.registerBlock(grinder, grinder.getUnlocalizedName().substring(5));
        GameRegistry.registerBlock(smelter, smelter.getUnlocalizedName().substring(5));

        // Parts
        GameRegistry.registerBlock(machine_frame, machine_frame.getUnlocalizedName().substring(5));
    }

    public static void registerRenders() {
        //registerRender(blockCharger);

        registerRender(block_core);
        registerRender(ore_copper);
        registerRender(ore_phenium);
        registerRender(ore_tin);
        registerRender(ore_silver);

        registerRender(grinder);
        registerRender(smelter);

        registerRender(machine_frame);
    }

    public static void registerRender(Block block) {
        Item item = Item.getItemFromBlock(block);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }

}
