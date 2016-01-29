package com.deaxent.ec2.utils;

import com.deaxent.ec2.blocks.MBlock;
import net.minecraftforge.oredict.OreDictionary;

public class MOres {

    public static void init() {
        OreDictionary.registerOre("oreTin", MBlock.ore_tin);
        OreDictionary.registerOre("oreCopper", MBlock.ore_copper);
        OreDictionary.registerOre("oreSilver", MBlock.ore_silver);
        OreDictionary.registerOre("orePhenium", MBlock.ore_phenium);
    }

}
