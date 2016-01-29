package com.deaxent.ec2.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabArmor extends CreativeTabs {
    public CreativeTabArmor(String armorTab) {
        super(armorTab);
    }

    @Override
    public Item getTabIconItem() {
        return Items.diamond_chestplate;
    }
}
