package com.deaxent.ec2.blocks.Smelter;

import com.deaxent.ec2.utils.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiSmelter extends GuiContainer {

    private static final ResourceLocation SmelterGuiTextures = new ResourceLocation(Reference.MOD_ID + ":textures/gui/container/Smelter.png");
    private final InventoryPlayer invPlayer;
    private final IInventory tileSmelter;

    public GuiSmelter(InventoryPlayer inventoryPlayer, IInventory tileSmelterInv) {
        super(new ContainerSmelter(inventoryPlayer, tileSmelterInv));
        invPlayer = inventoryPlayer;
        tileSmelter = tileSmelterInv;

        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = tileSmelter.getDisplayName().getUnformattedText();
        fontRendererObj.drawString(s, xSize/2-fontRendererObj.getStringWidth(s)/2, 6, 4210752);
        //fontRendererObj.drawString(invPlayer.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
        //fontRendererObj.drawString("Power: " + tileSmelter.getField(4) + " / " + tileSmelter.getField(7), 8, ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks,
                                                   int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(SmelterGuiTextures);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - ySize) / 2;
        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, xSize, ySize);

        // Draw progress indicator
        int progressLevel = getProgressLevel(17);
        drawTexturedModalRect(marginHorizontal + 84, marginVertical + 34, 176, 0, progressLevel + 1, 16);

        int energyLevel = getEnergyLevel(45);
        drawTexturedModalRect(marginHorizontal + 7, marginVertical + 8 + 45 - energyLevel, 176, 61 - energyLevel, 18, energyLevel + 1);

        if(mouseX > (marginHorizontal + 8) && mouseX < (marginHorizontal + 24)) {
            if(mouseY > (marginVertical + 9) && mouseY < (marginVertical + 53)) {
                List list = new ArrayList();
                list.add("Power: " + tileSmelter.getField(4));
                this.drawHoveringText(list, (int) mouseX, (int) mouseY, fontRendererObj);
            }
        }
    }

    private int getProgressLevel(int progressIndicatorPixelWidth)
    {
        int ticksSmeltingItemSoFar = tileSmelter.getField(2);
        int ticksPerItem = tileSmelter.getField(3);
        return ticksPerItem != 0 && ticksSmeltingItemSoFar != 0 ? ticksSmeltingItemSoFar*progressIndicatorPixelWidth/ticksPerItem : 0;
    }

    private int getEnergyLevel(int progressIndicatorPixelHeight) {
        int energy = tileSmelter.getField(4);
        int maxEnergy = tileSmelter.getField(6);
        //return energy <= maxEnergy ? energy * progressIndicatorPixelHeight / maxEnergy : 0;
        return energy * progressIndicatorPixelHeight / maxEnergy;
    }

}
