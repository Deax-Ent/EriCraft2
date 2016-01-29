package com.deaxent.ec2.blocks.Grinder;

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
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityGrinder extends AbstractEnergyMachine implements ITickable, ISidedInventory {

    public enum slotEnum {
        INPUT_SLOT, OUTPUT_SLOT, ENERGY_SLOT
    }

    private static final int[] slotsTop = new int[] {
            slotEnum.INPUT_SLOT.ordinal()};
    private static final int[] slotsBottom = new int[] {
            slotEnum.OUTPUT_SLOT.ordinal()};
    private static final int[] slotsSide = new int[] {};
    private ItemStack[] grinderItemStackArray = new ItemStack[3];
    private int timeCanGrind;
    private int currentItemGrindTime;
    private int ticksGrindingItemSoFar;
    private int ticksPerItem;
    private String grinderCustomName;

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
        return grinderItemStackArray.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return grinderItemStackArray[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if(grinderItemStackArray[index] != null) {

            if(grinderItemStackArray[index].stackSize <= count) {
                ItemStack itemstack = grinderItemStackArray[index];
                grinderItemStackArray[index] = null;
                return itemstack;
            } else {
                ItemStack itemstack1 = grinderItemStackArray[index].splitStack(count);

                if(grinderItemStackArray[index].stackSize == 0) {
                    grinderItemStackArray[index] = null;
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
        boolean isSameItemStackAlreadyInSlot = stack != null && stack.isItemEqual(grinderItemStackArray[index]) && ItemStack.areItemStacksEqual(stack, grinderItemStackArray[index]);
        grinderItemStackArray[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == slotEnum.INPUT_SLOT.ordinal() && !isSameItemStackAlreadyInSlot) {
            ticksPerItem = timeToGrindOneItem(stack);
            ticksGrindingItemSoFar = 0;
            this.markDirty();
        }
    }

    @Override
    public String getName() {
        return hasCustomName() ? grinderCustomName : "container.grinder";
    }

    @Override
    public boolean hasCustomName() {
        return grinderCustomName != null && grinderCustomName.length() > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        grinderItemStackArray = new ItemStack[getSizeInventory()];

        for(int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbt2 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbt2.getByte("Slot");

            if(b0 >= 0 && b0 < grinderItemStackArray.length) {
                grinderItemStackArray[b0] = ItemStack.loadItemStackFromNBT(nbt2);
            }
        }

        energy = nbt.getShort("Energy");
        timeCanGrind = nbt.getShort("GrindTime");
        ticksGrindingItemSoFar = nbt.getShort("CookTime");
        ticksPerItem = nbt.getShort("CookTimeTotal");

        if(nbt.hasKey("CustomName", 8)) {
            grinderCustomName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("Energy", (short) energy);
        nbt.setShort("GrindTime", (short) timeCanGrind);
        nbt.setShort("CookTime", (short) ticksGrindingItemSoFar);
        nbt.setShort("CookTimeTotal", (short) ticksPerItem);
        NBTTagList taglist = new NBTTagList();

        for(int i = 0; i < grinderItemStackArray.length; i++) {
            if(grinderItemStackArray[i] != null) {
                NBTTagCompound nbt2 = new NBTTagCompound();
                nbt2.setByte("Slot", (byte) i);
                grinderItemStackArray[i].writeToNBT(nbt2);
                taglist.appendTag(nbt2);
            }
        }

        nbt.setTag("Items", taglist);

        if(hasCustomName()) {
            nbt.setString("CustomName", grinderCustomName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean grindingSomething() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_174903_a(IInventory inv) {
        return true;
    }

    @Override
    public void update() {
        boolean hasBeenGrinding = grindingSomething();
        boolean changedGrindingState = false;

        if(grindingSomething()) {
            --timeCanGrind;
        }

        if(!worldObj.isRemote) {
            if(grinderItemStackArray[slotEnum.ENERGY_SLOT.ordinal()] != null && energy < maxEnergy) {
                int powerPerItem = PowerEnum.getPowerLevel(grinderItemStackArray[slotEnum.ENERGY_SLOT.ordinal()].getItem());
                if(powerPerItem > 0 && energy < maxEnergy) {
                    setEnergyStored(getEnergyStored() + powerPerItem);

                    grinderItemStackArray[slotEnum.ENERGY_SLOT.ordinal()].stackSize--;
                    if(grinderItemStackArray[slotEnum.ENERGY_SLOT.ordinal()].stackSize <= 0) {
                        grinderItemStackArray[slotEnum.ENERGY_SLOT.ordinal()] = null;
                    }
                }
            }
            if(grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()] != null) {
                if(!grindingSomething() && canGrind()) {
                    timeCanGrind = 150;

                    if(grindingSomething()) {
                        changedGrindingState = true;
                    }
                }

                if(grindingSomething() && canGrind()) {
                    ++ticksGrindingItemSoFar;
                    energy -= getEnergyUsePerTick();

                    if(ticksGrindingItemSoFar == ticksPerItem) {
                        ticksGrindingItemSoFar = 0;
                        ticksPerItem = timeToGrindOneItem(grinderItemStackArray[0]);
                        grindItem();
                        changedGrindingState = true;
                    }
                } else {
                    ticksGrindingItemSoFar = 0;
                }
            }
            if(hasBeenGrinding != grindingSomething()) {
                changedGrindingState = true;
                Grinder.changeBlockBasedOnGrindingStatus(grindingSomething(), worldObj, pos);
            }
        }

        if(grinderItemStackArray[0] == null) {
            ticksGrindingItemSoFar = 0;
            ticksPerItem = 0;
        }

        if(changedGrindingState) {
            this.markDirty();
        }
    }

    String[] ores = OreDictionary.getOreNames();

    private boolean isOre(ItemStack itemstack) {
        for(int i = 0; i < ores.length; i++) {
            if(ores[i].contains("ore")) {
                for(int j = 0; j < OreDictionary.getOres(ores[i]).size(); j++) {
                    if(OreDictionary.getOres(ores[i]).get(j) == itemstack) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int timeToGrindOneItem(ItemStack stack) {
        return 80;
    }

    private boolean canGrind() {
        if (grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()] == null) {
            return false;
        } else {
            ItemStack itemStackToOutput = GrinderRecipes.instance().getGrindingResult(grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);

            if (itemStackToOutput == null) return false;

            if(!hasEnergy()) return false;

            if(this.isOre(grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()])) return false;

            if (grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] == null) {
                return true;
            }
            if (!grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].isItemEqual(itemStackToOutput)) return false;

            int result = grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].stackSize + itemStackToOutput.stackSize;
            return result <= getInventoryStackLimit() && result <= grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].getMaxStackSize();
        }
    }

    public void grindItem()
    {
        if (canGrind()) {
            ItemStack itemstack = GrinderRecipes.instance().getGrindingResult(grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()]);

            // check if output slot is empty
            if (grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] == null) {
                grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()] = itemstack.copy();
            } else if (grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].getItem() == itemstack.getItem()) {
                grinderItemStackArray[slotEnum.OUTPUT_SLOT.ordinal()].stackSize += itemstack.stackSize;
            }

            --grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize;

            if (grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize <= 0) {
                grinderItemStackArray[slotEnum.INPUT_SLOT.ordinal()] = null;
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
        return "ec2:Grinder";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInv, EntityPlayer player) {
        System.out.println("TileEntityGrinder createContainer()");
        return new ContainerGrinder(playerInv, this);
    }

    @Override
    public int getField(int id) {
        switch(id) {
            case 0:
                return timeCanGrind;
            case 1:
                return currentItemGrindTime;
            case 2:
                return ticksGrindingItemSoFar;
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
                timeCanGrind = value;
                break;
            case 1:
                currentItemGrindTime = value;
                break;
            case 2:
                ticksGrindingItemSoFar = value;
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
        for(int i = 0; i < grinderItemStackArray.length; i++) {
            grinderItemStackArray[i] = null;
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
