package com.deaxent.ec2.blocks.Charger;

import com.deaxent.ec2.crafting.ChargerRecipes;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SlotChargerOutput extends Slot {

    private final EntityPlayer thePlayer;
    private int numChargerOutput;

    public SlotChargerOutput(EntityPlayer player, IInventory iinventory, int slotIndex, int xpos, int ypos) {
        super(iinventory, slotIndex, xpos, ypos);
        thePlayer = player;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if(getHasStack()) {
            numChargerOutput += Math.min(amount, getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        onCrafting(stack);
        super.onPickupFromSlot(player, stack);
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        numChargerOutput += amount;
        onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        if(!thePlayer.worldObj.isRemote) {
            int expEarned = numChargerOutput;
            float expFactor = ChargerRecipes.instance().getChargingExperience(stack);

            if(expFactor == 0.0F) {
                expEarned = 0;
            } else if(expFactor < 1.0F) {
                int possibleExpEarned = MathHelper.floor_float(expEarned * expFactor);

                if(possibleExpEarned < MathHelper.ceiling_float_int(expEarned * expFactor) && Math.random() < expEarned * expFactor - possibleExpEarned) {
                    possibleExpEarned++;
                }
                expEarned = possibleExpEarned;
            }

            int expInOrb;
            while(expEarned > 0) {
                expInOrb = EntityXPOrb.getXPSplit(expEarned);
                expEarned -= expInOrb;
                thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(thePlayer.worldObj, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, expInOrb));
            }
        }

        numChargerOutput = 0;
    }

}
