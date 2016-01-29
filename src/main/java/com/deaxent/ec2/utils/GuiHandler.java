package com.deaxent.ec2.utils;

//import com.deaxent.ec2.blocks.Charger.ContainerCharger;
//import com.deaxent.ec2.blocks.Charger.GuiCharger;
import com.deaxent.ec2.blocks.FurnaceGenerator.ContainerFurnaceGenerator;
import com.deaxent.ec2.blocks.FurnaceGenerator.GuiFurnaceGenerator;
import com.deaxent.ec2.blocks.Grinder.ContainerGrinder;
import com.deaxent.ec2.blocks.Grinder.GuiGrinder;
import com.deaxent.ec2.blocks.MBlock;
import com.deaxent.ec2.blocks.Smelter.ContainerSmelter;
import com.deaxent.ec2.blocks.Smelter.GuiSmelter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if(tileEntity != null) {
            //if(ID == MBlock.GUI_ENUM.CHARGER.ordinal()) {
            //    return new ContainerCharger(player.inventory, (IInventory) tileEntity);
            //}
            if(ID == MBlock.GUI_ENUM.GRINDER.ordinal()) {
                return new ContainerGrinder(player.inventory, (IInventory) tileEntity);
            }
            if(ID == MBlock.GUI_ENUM.SMELTER.ordinal()) {
                return new ContainerSmelter(player.inventory, (IInventory) tileEntity);
            }
            if(ID == MBlock.GUI_ENUM.FURNACEGENERATOR.ordinal()) {
                return new ContainerFurnaceGenerator(player.inventory, (IInventory) tileEntity);
            }
            // if blablabla
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if(tileEntity != null) {
            //if(ID == MBlock.GUI_ENUM.CHARGER.ordinal()) {
            //    return new GuiCharger(player.inventory, (IInventory) tileEntity);
            //}
            if(ID == MBlock.GUI_ENUM.GRINDER.ordinal()) {
                return new GuiGrinder(player.inventory, (IInventory) tileEntity);
            }
            if(ID == MBlock.GUI_ENUM.SMELTER.ordinal()) {
                return new GuiSmelter(player.inventory, (IInventory) tileEntity);
            }
            if(ID == MBlock.GUI_ENUM.FURNACEGENERATOR.ordinal()) {
                return new GuiFurnaceGenerator(player.inventory, (IInventory) tileEntity);
            }
            // if blablabla
        }

        return null;
    }
}
