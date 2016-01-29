package com.deaxent.ec2.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabTools extends CreativeTabs {
    public CreativeTabTools(String toolsTab) {
        super(toolsTab);
    }

    public Item getTabIconItem() {
        return Items.diamond_pickaxe;
    }
}
