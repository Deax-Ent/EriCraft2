package com.deaxent.ec2.blocks.Charger;

import com.deaxent.ec2.EriCraft2;
import com.deaxent.ec2.blocks.MBlock;
import com.deaxent.ec2.creativetabs.MCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockCharger extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static boolean hasTileEntity;

    public BlockCharger() {
        super(Material.rock);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setCreativeTab(MCreativeTabs.tabBlock);
        stepSound = soundTypeMetal;
        blockParticleGravity = 1.0f;
        slipperiness = 0.6f;
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        lightOpacity = 20;
        setTickRandomly(false);
        useNeighborBrightness = false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return Item.getItemFromBlock(MBlock.blockCharger);
    }

    @Override
    public void onBlockAdded(World world, BlockPos blockPos, IBlockState blockState) {
        if(!world.isRemote) {
            Block blockToNorth = world.getBlockState(blockPos.offset(EnumFacing.NORTH)).getBlock();
            Block blockToSouth = world.getBlockState(blockPos.offset(EnumFacing.SOUTH)).getBlock();
            Block blockToWest = world.getBlockState(blockPos.offset(EnumFacing.WEST)).getBlock();
            Block blockToEast = world.getBlockState(blockPos.offset(EnumFacing.EAST)).getBlock();
            EnumFacing enumfacing = (EnumFacing) blockState.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH
                    && blockToNorth.isFullBlock()
                    && !blockToSouth.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH
                    && blockToSouth.isFullBlock()
                    && !blockToNorth.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST
                    && blockToWest.isFullBlock()
                    && !blockToEast.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST
                    && blockToEast.isFullBlock()
                    && !blockToWest.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            world.setBlockState(blockPos, blockState
                    .withProperty(FACING, enumfacing), 2);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            player.openGui(EriCraft2.instance, MBlock.GUI_ENUM.CHARGER.ordinal(), world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        System.out.println("BlockCharger createNewTileEntity()");
        return new TileEntityCharger();
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if(!hasTileEntity) {
            TileEntity tileentity = world.getTileEntity(pos);

            if(tileentity instanceof TileEntityCharger) {
                InventoryHelper.dropInventoryItems(world, pos, (TileEntityCharger)tileentity);
                world.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, BlockPos pos) {
        return Item.getItemFromBlock(MBlock.blockCharger);
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state) {
        return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(meta);
        if(facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    @Override
    public BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {FACING});
    }

    public static void changeBlockBasedOnChargingStatus(boolean b, World worldObj, BlockPos pos) {
    }

    @SideOnly(Side.CLIENT)
    static final class SwitchEnumFacing {
        static final int[] enumFacingArray = new int[EnumFacing.values().length];

        static {
            try {
                enumFacingArray[EnumFacing.WEST.ordinal()] = 1;
            } catch(NoSuchFieldError var4) {
                ;
            }

            try {
                enumFacingArray[EnumFacing.EAST.ordinal()] = 2;
            } catch(NoSuchFieldError var3) {
                ;
            }

            try {
                enumFacingArray[EnumFacing.NORTH.ordinal()] = 3;
            } catch(NoSuchFieldError var2) {
                ;
            }

            try {
                enumFacingArray[EnumFacing.SOUTH.ordinal()] = 4;
            } catch(NoSuchFieldError var1) {
                ;
            }
        }
    }

}
