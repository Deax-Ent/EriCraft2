package com.deaxent.ec2.blocks;

import com.deaxent.ec2.creativetabs.MCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class MachineFrame extends Block {

    private int tier;

    public MachineFrame(int tier) {
        super(Material.iron);
        this.setCreativeTab(MCreativeTabs.tabBlock);
        this.setHardness(15F);
        this.setResistance(15F);
        this.tier = tier;
    }

}
