package com.deaxent.ec2.utils;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class PowerEnum {

    public static int getPowerLevel(Item item) {
        if(item == Items.redstone) {
            return 250;
        }

        return 0;
    }

}
