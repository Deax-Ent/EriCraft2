package com.deaxent.ec2.blocks.Charger;

import com.deaxent.ec2.crafting.ChargerRecipes;
import com.deaxent.ec2.items.AbstractItems;
import com.deaxent.ec2.items.ItemBattery;
import com.deaxent.ec2.items.MItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCharger extends TileEntityLockable implements ITickable, ISidedInventory {

    public enum slotEnum {
        INPUT_SLOT, OUTPUT_SLOT
    }

    private static final int[] slotsTop = new int[]{
            slotEnum.INPUT_SLOT.ordinal()};
    private static final int[] slotsBottom = new int[]{
            slotEnum.OUTPUT_SLOT.ordinal()};
    private static final int[] slotsSide = new int[]{};
    private ItemStack[] chargerItemStackArray = new ItemStack[2];
    private int timeToCharge;
    private int currentItemChargerTime;
    private int ticksChargingItemSoFar;
    private int ticksPerItem;
    private String chargerCustomName;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState state, IBlockState newState) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return chargerItemStackArray.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return chargerItemStackArray[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (chargerItemStackArray[index] != null) {
            ItemStack itemstack;

            if (chargerItemStackArray[index].stackSize <= count) {
                itemstack = chargerItemStackArray[index];
                chargerItemStackArray[index] = null;
                return itemstack;
            } else {
                itemstack = chargerItemStackArray[index].splitStack(count);

                if (chargerItemStackArray[index].stackSize == 0) {
                    chargerItemStackArray[index] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        boolean isSameItemStackAlreadyInSlot = stack != null
                && stack.isItemEqual(chargerItemStackArray[index])
                && ItemStack.areItemStacksEqual(stack, chargerItemStackArray[index]);
        chargerItemStackArray[index] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

        if (index == slotEnum.INPUT_SLOT.ordinal() && !isSameItemStackAlreadyInSlot) {
            ticksPerItem = timeToChargeOneItem(stack);
            ticksChargingItemSoFar = 0;
            markDirty();
        }
    }

    @Override
    public String getName() {
        return hasCustomName() ? chargerCustomName : "container.charger";
    }

    @Override
    public boolean hasCustomName() {
        return chargerCustomName != null && chargerCustomName.length() > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        chargerItemStackArray = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound compound = nbttaglist.getCompoundTagAt(i);
            byte b0 = compound.getByte("Slot");

            if (b0 >= 0 && b0 < chargerItemStackArray.length) {
                chargerItemStackArray[b0] = ItemStack.loadItemStackFromNBT(compound);
            }
        }

        timeToCharge = nbt.getShort("ChargeTime");
        ticksChargingItemSoFar = nbt.getShort("CookTime");
        ticksPerItem = nbt.getShort("CookTimeTotal");

        if (nbt.hasKey("CustomName", 8)) {
            chargerCustomName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("ChargeTime", (short) timeToCharge);
        compound.setShort("CookTime", (short) ticksChargingItemSoFar);
        compound.setShort("CookTimeTotal", (short) ticksPerItem);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < chargerItemStackArray.length; i++) {
            if (chargerItemStackArray[i] != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setByte("Slot", (byte) i);
                chargerItemStackArray[i].writeToNBT(nbt);
                nbttaglist.appendTag(nbt);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (hasCustomName()) {
            compound.setString("CustomName", chargerCustomName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    public boolean chargingSomething() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_174903_a(IInventory inv) {
        return true;
    }

    @Override
    public void update() {
        boolean hasBeenCharging = chargingSomething();
        boolean changedChargingState = false;

        if (chargingSomething()) {
            timeToCharge--;
        }

        if (!worldObj.isRemote) {
            if (chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()] != null) {
                Item item = chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()].getItem();

                int currentItemTimeToCharge = ((AbstractItems) item).getMaxPower() - ((AbstractItems) item).getCurrentPower();
                System.out.println("itemTimeToCharge: " + currentItemTimeToCharge);

                if (!chargingSomething() && canCharge()) {
                    timeToCharge = currentItemTimeToCharge;

                    if (chargingSomething()) {
                        changedChargingState = true;
                    }
                }

                if (chargingSomething() && canCharge()) {
                    ticksChargingItemSoFar++;
                    ((AbstractItems) item).charge(chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);
                    System.out.println("Battery Power: " + ((AbstractItems) item).getCurrentPower());

                    if (ticksChargingItemSoFar == ticksPerItem) {
                    //if(((AbstractItems) item).charge()) {
                        ticksChargingItemSoFar = 0;
                        ticksPerItem = timeToChargeOneItem(chargerItemStackArray[0]);
                        chargeItem();
                        changedChargingState = true;
                    }
                } else {
                    ticksChargingItemSoFar = 0;
                }
            }

            if (hasBeenCharging != chargingSomething()) {
                changedChargingState = true;
                BlockCharger.changeBlockBasedOnChargingStatus(chargingSomething(), worldObj, pos);
            }
        }

        if (changedChargingState) {
            markDirty();
        }
    }

    public int timeToChargeOneItem(ItemStack stack) {
        if (chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()] != null) {
            Item item = stack.getItem();
            AbstractItems obj = (AbstractItems) item;

            return obj.getMaxPower() - obj.getCurrentPower();
        }
        return 0;
    }

    private boolean canCharge() {
        if (chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()] == null) {
            return false;
        } else {
            ItemStack itemStackToOutput = ChargerRecipes.instance().getChargingResult(chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);

            if (itemStackToOutput == null) return false;

            if (chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] == null) {
                return true;
            }

            if (!chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].isItemEqual(itemStackToOutput)) return false;

            int result = chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].stackSize + itemStackToOutput.stackSize;
            return result <= getInventoryStackLimit() && result <= chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].getMaxDamage();
        }
    }

    public void chargeItem() {
        if (canCharge()) {
            ItemStack itemstack = ChargerRecipes.instance().getChargingResult(chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);

            if (chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] == null) {
                chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] = itemstack.copy();
            } else if (chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].getItem() == itemstack.getItem()) {
                chargerItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].stackSize += itemstack.stackSize;
            }

            chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize--;

            if (chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize <= 0) {
                chargerItemStackArray[slotEnum.INPUT_SLOT.ordinal()] = null;
            }
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == slotEnum.INPUT_SLOT.ordinal() ? true : false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSide);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing facing) {
        return true;
    }

    @Override
    public String getGuiID() {
        return "ec2:charger";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
        System.out.println("TileEntityCharger createContainer()");
        return new ContainerCharger(playerInventory, this);
    }

    @Override
    public int getField(int id) {
        switch(id) {
            case 0:
                return timeToCharge;
            case 1:
                return currentItemChargerTime;
            case 2:
                return ticksChargingItemSoFar;
            case 3:
                return ticksPerItem;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch(id) {
            case 0:
                timeToCharge = value;
                break;
            case 1:
                currentItemChargerTime = value;
                break;
            case 2:
                ticksChargingItemSoFar = value;
                break;
            case 3:
                ticksPerItem = value;
                break;
            default:
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 4;
    }

    @Override
    public void clear() {
        for(int i = 0; i < chargerItemStackArray.length; i++) {
            chargerItemStackArray[i] = null;
        }
    }


}
