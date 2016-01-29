package com.deaxent.ec2.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabItems extends CreativeTabs {
    public CreativeTabItems(String itemsTab) {
        super(itemsTab);
    }

    @Override
    public Item getTabIconItem() {
        return Items.stick;
    }
}
