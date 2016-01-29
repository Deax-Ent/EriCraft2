package com.deaxent.ec2.blocks.Smelter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSmelter extends Container {

    private final IInventory tileSmelter;
    private final int sizeInventory;
    private int ticksSmeltingItemSoFar;
    private int ticksPerItem;
    private int timeCanSmelt;
    private int energy;

    public ContainerSmelter(InventoryPlayer invPlayer, IInventory iinv) {
        tileSmelter = iinv;
        sizeInventory = tileSmelter.getSizeInventory();
        // Input slot
        addSlotToContainer(new Slot(tileSmelter, TileEntitySmelter.slotEnum.INPUT_SLOT.ordinal(), 59, 35));

        // Energy Slot
        addSlotToContainer(new Slot(tileSmelter, TileEntitySmelter.slotEnum.ENERGY_SLOT.ordinal(), 8, 58));

        // Output slot
        addSlotToContainer(new SlotSmelterOutput(invPlayer.player, tileSmelter, TileEntitySmelter.slotEnum.OUTPUT_SLOT.ordinal(), 116, 35));

        int i;
        for(i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting listener) {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileSmelter);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for(int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting)crafters.get(i);

            if(ticksSmeltingItemSoFar != tileSmelter.getField(2)) {
                icrafting.sendProgressBarUpdate(this, 2, tileSmelter.getField(2));
            }

            if(timeCanSmelt != tileSmelter.getField(0)) {
                icrafting.sendProgressBarUpdate(this, 0, tileSmelter.getField(0));
            }

            if(ticksPerItem != tileSmelter.getField(3)) {
                icrafting.sendProgressBarUpdate(this, 3, tileSmelter.getField(3));
            }

            if(energy != tileSmelter.getField(4)) {
                icrafting.sendProgressBarUpdate(this, 4, tileSmelter.getField(4));
            }
        }

        ticksSmeltingItemSoFar = tileSmelter.getField(2);
        timeCanSmelt = tileSmelter.getField(0);
        ticksPerItem = tileSmelter.getField(3);
        energy = tileSmelter.getField(4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        tileSmelter.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileSmelter.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
        ItemStack itemStack1 = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack1 = itemStack2.copy();

            if (slotIndex == TileEntitySmelter.slotEnum.OUTPUT_SLOT.ordinal()) {
                if (!mergeItemStack(itemStack2, sizeInventory, sizeInventory+36, true)) {
                    return null;
                }

                slot.onSlotChange(itemStack2, itemStack1);
            } else if (slotIndex != TileEntitySmelter.slotEnum.INPUT_SLOT.ordinal()) {
                // check if there is a grinding recipe for the stack
                if (SmelterRecipes.instance().getSmeltingResult(itemStack2) != null) {
                    if (!mergeItemStack(itemStack2, 0, 1, false)) {
                        return null;
                    }
                } else if (slotIndex >= sizeInventory && slotIndex < sizeInventory+27) {
                    if (!mergeItemStack(itemStack2, sizeInventory+27, sizeInventory+36, false)) {
                        return null;
                    }
                } else if (slotIndex >= sizeInventory+27 && slotIndex < sizeInventory+36 && !mergeItemStack(itemStack2, sizeInventory+1, sizeInventory+27, false)) {
                    return null;
                }
            } else if (!mergeItemStack(itemStack2, sizeInventory, sizeInventory+36, false)) {
                return null;
            }

            if (itemStack2.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack2.stackSize == itemStack1.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemStack2);
        }

        return itemStack1;
    }

}
