package com.deaxent.ec2.proxy;

import com.deaxent.ec2.blocks.MBlock;
import com.deaxent.ec2.items.MItems;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenders() {
        MBlock.registerRenders();
        MItems.registerRenders();
    }

}
