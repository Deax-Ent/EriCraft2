package com.deaxent.ec2.creativetabs;

import com.deaxent.ec2.blocks.MBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabBlock extends CreativeTabs {
    public CreativeTabBlock(String blockTab) {
        super(blockTab);
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(MBlock.block_core);
    }
}
