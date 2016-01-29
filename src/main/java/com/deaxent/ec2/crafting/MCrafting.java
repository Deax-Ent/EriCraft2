package com.deaxent.ec2.crafting;

import com.deaxent.ec2.blocks.MBlock;
import com.deaxent.ec2.items.MItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MCrafting {

    public static void initCrafting() {
        // Crafting
        GameRegistry.addRecipe(new ItemStack(MItems.itemBattery), new Object[] {" T ", "IRI", "III", 'T', MItems.ingot_tin, 'I', Items.iron_ingot, 'R', Items.redstone});

        GameRegistry.addRecipe(new ItemStack(MBlock.grinder), new Object[] {"IPI", "XMX", "IRI", 'X', MItems.ingot_silver, 'I', Items.iron_ingot, 'R', Items.redstone, 'P', Blocks.piston, 'M', MBlock.machine_frame});
        GameRegistry.addRecipe(new ItemStack(MBlock.smelter), new Object[] {"IPI", "XMX", "IRI", 'X', MItems.ingot_tin, 'I', Items.iron_ingot, 'R', Items.redstone, 'P', Blocks.piston, 'M', MBlock.machine_frame});

        // Smelting
        GameRegistry.addSmelting(MBlock.ore_tin, new ItemStack(MItems.ingot_tin, 1), 1.0f);
        GameRegistry.addSmelting(MBlock.ore_copper, new ItemStack(MItems.ingot_copper, 1), 1.0f);
        GameRegistry.addSmelting(MBlock.ore_silver, new ItemStack(MItems.ingot_silver, 1), 1.0f);
        GameRegistry.addSmelting(MBlock.ore_phenium, new ItemStack(MItems.ingot_phenium, 1), 1.0f);

        GameRegistry.addSmelting(MItems.dust_tin, new ItemStack(MItems.ingot_tin, 1), 1.0F);
        GameRegistry.addSmelting(MItems.dust_copper, new ItemStack(MItems.ingot_copper, 1), 1.0F);
        GameRegistry.addSmelting(MItems.dust_silver, new ItemStack(MItems.ingot_silver, 1), 1.0F);
        GameRegistry.addSmelting(MItems.dust_phenium, new ItemStack(MItems.ingot_phenium, 1), 1.0F);
    }

}
