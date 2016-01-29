package com.deaxent.ec2.blocks.FurnaceGenerator;

import com.deaxent.ec2.power.AbstractEnergyMachine;
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

public class TileFurnaceGenerator extends AbstractEnergyMachine implements ITickable, ISidedInventory {

    public enum slotEnum {
        INPUT_SLOT, ENERGY_SLOT
    }

    private static final int[] slotsTop = new int[] {
            slotEnum.INPUT_SLOT.ordinal()};
    private static final int[] slotsBottom = new int[] {};
    private static final int[] slotsSide = new int[] {};
    private ItemStack[] generatorItemStackArray = new ItemStack[2];
    private int timeCanGenerate;
    private int currentItemGenerateTime;
    private int ticksGeneratingSoFar;
    private int ticksPerItem;
    private String furnaceCustomName;

    private int energy = 0;
    private int maxEnergy = 100000;
    private int ticksGivingEnergySoFar;
    private int maxEnergyExtracted = 80;
    private int maxEnergyReceived = 80;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }

    @Override
    public int getSizeInventory() {
        return generatorItemStackArray.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return generatorItemStackArray[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if(generatorItemStackArray[index] != null) {

            if(generatorItemStackArray[index].stackSize <= count) {
                ItemStack itemstack = generatorItemStackArray[index];
                generatorItemStackArray[index] = null;
                return itemstack;
            } else {
                ItemStack itemstack1 = generatorItemStackArray[index].splitStack(count);

                if(generatorItemStackArray[index].stackSize == 0) {
                    generatorItemStackArray[index] = null;
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
        boolean isSameItemStackAlreadyInSlot = stack != null && stack.isItemEqual(generatorItemStackArray[index]) && ItemStack.areItemStacksEqual(stack, generatorItemStackArray[index]);
        generatorItemStackArray[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == slotEnum.INPUT_SLOT.ordinal() && !isSameItemStackAlreadyInSlot) {
            ticksPerItem = timeToGenerateEnergyPerItem(stack);
            ticksGeneratingSoFar = 0;
            this.markDirty();
        }
    }

    @Override
    public String getName() {
        return hasCustomName() ? furnaceCustomName : "container.furnaceGenerator";
    }

    @Override
    public boolean hasCustomName() {
        return furnaceCustomName != null && furnaceCustomName.length() > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        generatorItemStackArray = new ItemStack[getSizeInventory()];

        for(int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbt2 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbt2.getByte("Slot");

            if(b0 >= 0 && b0 < generatorItemStackArray.length) {
                generatorItemStackArray[b0] = ItemStack.loadItemStackFromNBT(nbt2);
            }
        }

        energy = nbt.getShort("Energy");
        timeCanGenerate = nbt.getShort("GenerateTime");
        ticksGeneratingSoFar = nbt.getShort("CookTime");
        ticksPerItem = nbt.getShort("CookTimeTotal");

        if(nbt.hasKey("CustomName", 8)) {
            furnaceCustomName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("Energy", (short) energy);
        nbt.setShort("SmeltTime", (short) timeCanGenerate);
        nbt.setShort("CookTime", (short) ticksGeneratingSoFar);
        nbt.setShort("CookTimeTotal", (short) ticksPerItem);
        NBTTagList taglist = new NBTTagList();

        for(int i = 0; i < generatorItemStackArray.length; i++) {
            if(generatorItemStackArray[i] != null) {
                NBTTagCompound nbt2 = new NBTTagCompound();
                nbt2.setByte("Slot", (byte) i);
                generatorItemStackArray[i].writeToNBT(nbt2);
                taglist.appendTag(nbt2);
            }
        }

        nbt.setTag("Items", taglist);

        if(hasCustomName()) {
            nbt.setString("CustomName", furnaceCustomName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isGenerating() { return true; }

    @SideOnly(Side.CLIENT)
    public static boolean func_174903_a(IInventory inv) { return true; }

    @Override
    public void update() {
        boolean hasBeenGenerating = isGenerating();
        boolean changedGenerationState = false;

        if(isGenerating()) {
            --timeCanGenerate;
        }

        if(!worldObj.isRemote) {
            if(generatorItemStackArray[slotEnum.INPUT_SLOT.ordinal()] != null) {
                if(!isGenerating() && canGenerate()) {
                    timeCanGenerate = 150;

                    if(isGenerating()) {
                        changedGenerationState = true;
                    }
                }

                if(isGenerating() && canGenerate()) {
                    ++ticksGeneratingSoFar;
                    energy += getEnergyUsePerTick();

                    if(ticksGeneratingSoFar == ticksPerItem) {
                        ticksGeneratingSoFar = 0;
                        ticksPerItem = timeToGenerateEnergyPerItem(generatorItemStackArray[0]);
                        generateEnergy();
                        changedGenerationState = true;
                    }
                } else {
                    ticksGeneratingSoFar = 0;
                }
            }
            if(hasBeenGenerating != isGenerating()) {
                changedGenerationState = true;
                FurnaceGenerator.changeBlockBasedOnSmeltingStatus(isGenerating(), worldObj, pos);
            }
        }

        if(generatorItemStackArray[0] == null) {
            ticksGeneratingSoFar = 0;
            ticksPerItem = 0;
        }

        if(changedGenerationState) {
            this.markDirty();
        }
    }

    public int timeToGenerateEnergyPerItem(ItemStack stack) { return 80; }

    private boolean canGenerate() {
        if(generatorItemStackArray[slotEnum.INPUT_SLOT.ordinal()] == null) {
            return false;
        } else {
            return getEnergyStored() < getMaxEnergyStored();
        }
    }

    public void generateEnergy() {
        if(canGenerate()) {
            --generatorItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize;

            if(generatorItemStackArray[slotEnum.INPUT_SLOT.ordinal()].stackSize <= 0) {
                generatorItemStackArray[slotEnum.INPUT_SLOT.ordinal()] = null;
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
        return "ec2:FurnaceGenerator";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInv, EntityPlayer player) {
        System.out.println("TileFurnaceGenerator createContainer()");
        return new ContainerFurnaceGenerator(playerInv, this);
    }

    @Override
    public int getField(int id) {
        switch(id) {
            case 0:
                return timeCanGenerate;
            case 1:
                return currentItemGenerateTime;
            case 2:
                return ticksGeneratingSoFar;
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
                timeCanGenerate = value;
                break;
            case 1:
                currentItemGenerateTime = value;
                break;
            case 2:
                ticksGeneratingSoFar = value;
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
        for(int i = 0; i < generatorItemStackArray.length; i++) {
            generatorItemStackArray[i] = null;
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
