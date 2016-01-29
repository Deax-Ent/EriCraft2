package com.deaxent.ec2.blocks.Charger;

import com.deaxent.ec2.crafting.ChargerRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCharger extends Container {

    private final IInventory tileCharger;
    private final int sizeInventory;
    private int ticksChargingItemSoFar;
    private int ticksPerItem;
    private int timeCanCharge;

    public ContainerCharger(InventoryPlayer playerInventory, IInventory iinventory) {
        System.out.println("ContainerCharger constructor()");

        tileCharger = iinventory;
        sizeInventory = tileCharger.getSizeInventory();
        addSlotToContainer(new Slot(tileCharger,
                TileEntityCharger.slotEnum.INPUT_SLOT.ordinal(), 41, 35));
        addSlotToContainer(new SlotChargerOutput(playerInventory.player, tileCharger, TileEntityCharger.slotEnum.OUTPUT_SLOT.ordinal(), 116, 35));

        int i;
        for(i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInventory, j+i*9+9, 8+j*18, 84+i*18));
            }
        }

        for(i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(playerInventory, i, 8+i*18, 182)); // TODO: CHANGE COORDINATES
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting listener) {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileCharger);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for(int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if(ticksChargingItemSoFar != tileCharger.getField(2)) {
                icrafting.sendProgressBarUpdate(this, 2, tileCharger.getField(2));
            }

            if(timeCanCharge != tileCharger.getField(0)) {
                icrafting.sendProgressBarUpdate(this, 0, tileCharger.getField(0));
            }

            if(ticksPerItem != tileCharger.getField(3)) {
                icrafting.sendProgressBarUpdate(this, 3, tileCharger.getField(3));
            }
        }

        ticksChargingItemSoFar = tileCharger.getField(2);
        timeCanCharge = tileCharger.getField(0);
        ticksPerItem = tileCharger.getField(3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        tileCharger.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileCharger.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack1 = null;
        Slot slot = (Slot) inventorySlots.get(index);

        if(slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack1 = itemstack2.copy();

            if(index == TileEntityCharger.slotEnum.OUTPUT_SLOT.ordinal()) {
                if(!mergeItemStack(itemstack2, sizeInventory, sizeInventory+36, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack2, itemstack1);
            } else if(index != TileEntityCharger.slotEnum.INPUT_SLOT.ordinal()) {
                if(ChargerRecipes.instance().getChargingResult(itemstack2) != null) {
                    if(!mergeItemStack(itemstack2, 0, 1, false)) {
                        return null;
                    }
                } else if(index >= sizeInventory && index < sizeInventory+27) {
                    if(!mergeItemStack(itemstack2, sizeInventory+27, sizeInventory+36, false)) {
                        return null;
                    }
                } else if(index >= sizeInventory+27 && index < sizeInventory+36 && !mergeItemStack(itemstack2, sizeInventory+1, sizeInventory+27, false)) {
                    return null;
                }
            } else if(!mergeItemStack(itemstack2, sizeInventory, sizeInventory+36, false)) {
                return null;
            }

            if(itemstack2.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if(itemstack2.stackSize == itemstack1.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack2);
        }

        return itemstack1;
    }

}
