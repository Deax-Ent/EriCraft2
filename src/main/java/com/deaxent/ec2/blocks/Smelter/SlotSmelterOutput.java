package com.deaxent.ec2.blocks.Smelter;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SlotSmelterOutput extends Slot {

    private final EntityPlayer thePlayer;
    private int numSmeltingOutput;

    public SlotSmelterOutput(EntityPlayer player, IInventory iinventory, int slot, int xDisPos, int yDisPos) {
        super(iinventory, slot, xDisPos, yDisPos);
        thePlayer = player;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if(getHasStack()) {
            numSmeltingOutput += Math.min(amount, getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        onCrafting(stack);
        super.onPickupFromSlot(player, stack);
    }

    @Override
    protected void onCrafting(ItemStack stack, int amountGround) {
        numSmeltingOutput += amountGround;
        onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack parItemStack)
    {
        if (!thePlayer.worldObj.isRemote)
        {
            int expEarned = numSmeltingOutput;
            float expFactor = SmelterRecipes.instance()
                    .getSmeltingExperience(parItemStack);

            if (expFactor == 0.0F)
            {
                expEarned = 0;
            }
            else if (expFactor < 1.0F)
            {
                int possibleExpEarned = MathHelper.floor_float(
                        expEarned*expFactor);

                if (possibleExpEarned < MathHelper.ceiling_float_int(
                        expEarned*expFactor)
                        && Math.random() < expEarned*expFactor-possibleExpEarned)
                {
                    ++possibleExpEarned;
                }

                expEarned = possibleExpEarned;
            }

            // create experience orbs
            int expInOrb;
            while (expEarned > 0)
            {
                expInOrb = EntityXPOrb.getXPSplit(expEarned);
                expEarned -= expInOrb;
                thePlayer.worldObj.spawnEntityInWorld(
                        new EntityXPOrb(thePlayer.worldObj, thePlayer.posX,
                                thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D,
                                expInOrb));
            }
        }

        numSmeltingOutput= 0;

        // You can trigger achievements here based on output
        // E.g. if (parItemStack.getItem() == Items.grinded_fish)
        //      {
        //          thePlayer.triggerAchievement(AchievementList.grindFish);
        //      }
    }

}
