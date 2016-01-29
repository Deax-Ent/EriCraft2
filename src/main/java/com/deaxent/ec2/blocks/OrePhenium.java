package com.deaxent.ec2.blocks;

import com.deaxent.ec2.creativetabs.MCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class OrePhenium extends Block {

    public OrePhenium(Material rock) {
        super(rock);
        this.setCreativeTab(MCreativeTabs.tabBlock);
        this.setHarvestLevel("pickaxe", 2);
        this.setResistance(15f);
        this.setHardness(15f);
    }

}
