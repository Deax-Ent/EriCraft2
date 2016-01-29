package com.deaxent.ec2.blocks.Smelter;

import com.deaxent.ec2.power.AbstractEnergyMachine;
import com.deaxent.ec2.utils.PowerEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySmelter extends AbstractEnergyMachine implements ITickable, ISidedInventory {

    public enum slotEnum {
        INPUT_SLOT, OUTPUT_SLOT, ENERGY_SLOT
    }

    private static final int[] slotsTop = new int[] {
            slotEnum.INPUT_SLOT.ordinal()};
    private static final int[] slotsBottom = new int[] {
            slotEnum.OUTPUT_SLOT.ordinal()};
    private static final int[] slotsSide = new int[] {};
    private ItemStack[] smelterItemStackArray = new ItemStack[3];
    private int timeCanSmelt;
    private int currentItemSmeltTime;
    private int ticksSmeltingItemSoFar;
    private int ticksPerItem;
    private String smelterCustomName;

    private int energy = 0;
    private int maxEnergy = 10000;
    private int ticksGivingEnergySoFar;
    private int maxEnergyExtracted = 20;
    private int maxEnergyReceived = 80;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }

    @Override
    public int getSizeInventory() {
        return smelterItemStackArray.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return smelterItemStackArray[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if(smelterItemStackArray[index] != null) {

            if(smelterItemStackArray[index].stackSize <= count) {
                ItemStack itemstack = smelterItemStackArray[index];
                smelterItemStackArray[index] = null;
                return itemstack;
            } else {
                ItemStack itemstack1 = smelterItemStackArray[index].splitStack(count);

                if(smelterItemStackArray[index].stackSize == 0) {
                    smelterItemStackArray[index] = null;
                }

                return itemstack1;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) { return null; }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        boolean isSameItemStackAlreadyInSlot = stack != null && stack.isItemEqual(smelterItemStackArray[index]) && ItemStack.areItemStacksEqual(stack, smelterItemStackArray[index]);
        smelterItemStackArray[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == slotEnum.INPUT_SLOT.ordinal() && !isSameItemStackAlreadyInSlot) {
            ticksPerItem = timeToSmeltOneItem(stack);
            ticksSmeltingItemSoFar = 0;
            this.markDirty();
        }
    }

    @Override
    public String getName() {
        return hasCustomName() ? smelterCustomName : "container.smelter";
    }

    @Override
    public boolean hasCustomName() {
        return smelterCustomName != null && smelterCustomName.length() > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        smelterItemStackArray = new ItemStack[getSizeInventory()];

        for(int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbt2 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbt2.getByte("Slot");

            if(b0 >= 0 && b0 < smelterItemStackArray.length) {
                smelterItemStackArray[b0] = ItemStack.loadItemStackFromNBT(nbt2);
            }
        }

        energy = nbt.getShort("Energy");
        timeCanSmelt = nbt.getShort("SmeltTime");
        ticksSmeltingItemSoFar = nbt.getShort("CookTime");
        ticksPerItem = nbt.getShort("CookTimeTotal");

        if(nbt.hasKey("CustomName", 8)) {
            smelterCustomName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("Energy", (short) energy);
        nbt.setShort("SmeltTime", (short) timeCanSmelt);
        nbt.setShort("CookTime", (short) ticksSmeltingItemSoFar);
        nbt.setShort("CookTimeTotal", (short) ticksPerItem);
        NBTTagList taglist = new NBTTagList();

        for(int i = 0; i < smelterItemStackArray.length; i++) {
            if(smelterItemStackArray[i] != null) {
                NBTTagCompound nbt2 = new NBTTagCompound();
                nbt2.setByte("Slot", (byte) i);
                smelterItemStackArray[i].writeToNBT(nbt2);
                taglist.appendTag(nbt2);
            }
        }

        nbt.setTag("Items", taglist);

        if(hasCustomName()) {
            nbt.setString("CustomName", smelterCustomName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean smeltingSomething() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_174903_a(IInventory inv) {
        return true;
    }

    @Override
    public void update() {
        boolean hasBeenSmelting = smeltingSomething();
        boolean changedSmeltingState = false;

        if(smeltingSomething()) {
            --timeCanSmelt;
        }

        if(!worldObj.isRemote) {
            if(smelterItemStackArray[slotEnum.ENERGY_SLOT.ordinal()] != null && energy < maxEnergy) {
                int powerPerItem = PowerEnum.getPowerLevel(smelterItemStackArray[slotEnum.ENERGY_SLOT.ordinal()].getItem());
                if(powerPerItem > 0 && energy < maxEnergy) {
                    setEnergyStored(getEnergyStored() + powerPerItem);

                    smelterItemStackArray[slotEnum.ENERGY_SLOT.ordinal()].stackSize--;
                    if(smelterItemStackArray[slotEnum.ENERGY_SLOT.ordinal()].stackSize <= 0) {
                        smelterItemStackArray[slotEnum.ENERGY_SLOT.ordinal()] = null;
                    }
                }
            }
            if(smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()] != null) {
                if(!smeltingSomething() && canSmelt()) {
                    timeCanSmelt = 150;

                    if(smeltingSomething()) {
                        changedSmeltingState = true;
                    }
                }

                if(smeltingSomething() && canSmelt()) {
                    ++ticksSmeltingItemSoFar;
                    energy -= getEnergyUsePerTick();

                    if(ticksSmeltingItemSoFar == ticksPerItem) {
                        ticksSmeltingItemSoFar = 0;
                        ticksPerItem = timeToSmeltOneItem(smelterItemStackArray[0]);
                        smeltItem();
                        changedSmeltingState = true;
                    }
                } else {
                    ticksSmeltingItemSoFar = 0;
                }
            }
            if(hasBeenSmelting != smeltingSomething()) {
                changedSmeltingState = true;
                Smelter.changeBlockBasedOnSmeltingStatus(smeltingSomething(), worldObj, pos);
            }
        }

        if(smelterItemStackArray[0] == null) {
            ticksSmeltingItemSoFar = 0;
            ticksPerItem = 0;
        }

        if(changedSmeltingState) {
            this.markDirty();
        }
    }

    public int timeToSmeltOneItem(ItemStack stack) {
        return 80;
    }

    private boolean canSmelt() {
        if (smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()] == null) {
            return false;
        } else {
            ItemStack itemStackToOutput = SmelterRecipes.instance().getSmeltingResult(smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);

            if (itemStackToOutput == null) return false;

            if(!hasEnergy()) return false;

            if (smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] == null) {
                return true;
            }
            if (!smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].isItemEqual(itemStackToOutput)) return false;

            int result = smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].stackSize + itemStackToOutput.stackSize;
            return result <= getInventoryStackLimit() && result <= smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].getMaxStackSize();
        }
    }

    public void smeltItem()
    {
        if (canSmelt()) {
            ItemStack itemstack = SmelterRecipes.instance().getSmeltingResult(smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);

            // check if output slot is empty
            if (smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] == null) {
                smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] = itemstack.copy();
            } else if (smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].getItem() == itemstack.getItem()) {
                smelterItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].stackSize += itemstack.stackSize;
            }

            --smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize;

            if (smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize <= 0) {
                smelterItemStackArray[slotEnum.INPUT_SLOT.ordinal()] = null;
            }
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == slotEnum.INPUT_SLOT.ordinal() ? (index == slotEnum.ENERGY_SLOT.ordinal() ? isEnergyItem(stack) : true) : false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSide);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing facing) {
        return true;
    }

    @Override
    public String getGuiID() {
        return "ec2:Smelter";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInv, EntityPlayer player) {
        System.out.println("TileEntitySmelter createContainer()");
        return new ContainerSmelter(playerInv, this);
    }

    @Override
    public int getField(int id) {
        switch(id) {
            case 0:
                return timeCanSmelt;
            case 1:
                return currentItemSmeltTime;
            case 2:
                return ticksSmeltingItemSoFar;
            case 3:
                return ticksPerItem;
            case 4:
                return energy;
            //case 5:
            //    return energyRequiredPerItem;
            case 5:
                return ticksGivingEnergySoFar;
            case 6:
                return maxEnergy;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch(id) {
            case 0:
                timeCanSmelt = value;
                break;
            case 1:
                currentItemSmeltTime = value;
                break;
            case 2:
                ticksSmeltingItemSoFar = value;
                break;
            case 3:
                ticksPerItem = value;
                break;
            case 4:
                energy = value;
                break;
            //case 5:
            //    energyRequiredPerItem = value;
            //    break;
            case 5:
                ticksGivingEnergySoFar = value;
                break;
            case 6:
                maxEnergy = value;
                break;
            default:
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 7;
    }

    @Override
    public void clear() {
        for(int i = 0; i < smelterItemStackArray.length; i++) {
            smelterItemStackArray[i] = null;
        }
    }

    /**
     *
     * ENERGY FUNCTIONS
     *
     */

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    @Override
    public int getMaxExtract() {
        return maxEnergyExtracted;
    }

    @Override
    public int getMaxReceive() {
        return maxEnergyReceived;
    }

    @Override
    public boolean hasEnergy() {
        return this.energy != 0 ? true : false;
    }

    @Override
    public int getEnergyUsePerTick() {
        return this.maxEnergyExtracted;
    }

    @Override
    public void setMaxExtract(int maxEnergyExtracted) {
        this.maxEnergyExtracted = maxEnergyExtracted;
    }

    @Override
    public void setMaxReceive(int maxEnergyReceived) {
        this.maxEnergyReceived = maxEnergyReceived;
    }

    @Override
    public void setEnergyStored(int stored) {
        energy = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
    }

    public static int getItemEnergy(ItemStack stack) {
        if(stack != null) {
            Item item = stack.getItem();
            if(item == Items.redstone) {
                return 250;
            }
        }

        return 0;
    }

    public static boolean isEnergyItem(ItemStack stack) {
        return getItemEnergy(stack) > 0;
    }

}
