package com.deaxent.ec2.blocks;

import com.deaxent.ec2.creativetabs.MCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCore extends Block {

    public BlockCore(Material material) {
        super(material);
        this.setCreativeTab(MCreativeTabs.tabBlock);
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(10f);
        this.setResistance(10f);
        this.setLightLevel(0f);
    }

}
