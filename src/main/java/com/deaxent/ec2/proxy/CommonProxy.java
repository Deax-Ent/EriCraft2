package com.deaxent.ec2.proxy;

//import com.deaxent.ec2.blocks.Charger.TileEntityCharger;
import com.deaxent.ec2.blocks.FurnaceGenerator.TileFurnaceGenerator;
import com.deaxent.ec2.blocks.Grinder.TileEntityGrinder;
import com.deaxent.ec2.blocks.Smelter.TileEntitySmelter;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class CommonProxy implements IProxy {

    public void preInitTileEntities() {
        //GameRegistry.registerTileEntity(TileEntityCharger.class, "TileEntityCharger");
        GameRegistry.registerTileEntity(TileEntityGrinder.class, "TileEntityGrinder");
        GameRegistry.registerTileEntity(TileEntitySmelter.class, "TileEntitySmelter");
        GameRegistry.registerTileEntity(TileFurnaceGenerator.class, "TileFurnaceGenerator");
    }

    public void registerRenders() {

    }


}
