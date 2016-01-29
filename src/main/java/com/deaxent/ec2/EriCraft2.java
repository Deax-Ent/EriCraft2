package com.deaxent.ec2;

import com.deaxent.ec2.blocks.MBlock;
import com.deaxent.ec2.crafting.MCrafting;
import com.deaxent.ec2.creativetabs.MCreativeTabs;
import com.deaxent.ec2.items.MItems;
import com.deaxent.ec2.proxy.*;
import com.deaxent.ec2.utils.GuiHandler;
import com.deaxent.ec2.utils.MOres;
import com.deaxent.ec2.utils.Reference;
import com.deaxent.ec2.worldgen.WorldGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class EriCraft2 {

    @Mod.Instance(Reference.MOD_ID)
    public static EriCraft2 instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public static void preLoad(FMLPreInitializationEvent event) {
        MCreativeTabs.initializeTabs();
        MBlock.MBlock();
        MItems.MItems();

        MOres.init();
        proxy.preInitTileEntities();
        NetworkRegistry.INSTANCE.registerGuiHandler(EriCraft2.instance, new GuiHandler());
    }

    @Mod.EventHandler
    public static void load(FMLInitializationEvent event) {
        proxy.registerRenders();
        GameRegistry.registerWorldGenerator(new WorldGen(), 0);

        MCrafting.initCrafting();
    }

    @Mod.EventHandler
    public static void postLoad(FMLPostInitializationEvent event) {

    }

}
