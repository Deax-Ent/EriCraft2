package com.deaxent.ec2.blocks.Grinder;

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
public class GuiGrinder  extends GuiContainer
{
    private static final ResourceLocation GrinderGuiTextures = new ResourceLocation(Reference.MOD_ID + ":textures/gui/container/Grinder.png");
    private final InventoryPlayer inventoryPlayer;
    private final IInventory tileGrinder;

    public GuiGrinder(InventoryPlayer parInventoryPlayer,
                      IInventory parInventoryGrinder)
    {
        super(new ContainerGrinder(parInventoryPlayer, parInventoryGrinder));
        inventoryPlayer = parInventoryPlayer;
        tileGrinder = parInventoryGrinder;

        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = tileGrinder.getDisplayName().getUnformattedText();
        fontRendererObj.drawString(s, xSize/2-fontRendererObj.getStringWidth(s)/2, 6, 4210752);
        //fontRendererObj.drawString(inventoryPlayer.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks,
                                                   int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GrinderGuiTextures);
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
                list.add("Power: " + tileGrinder.getField(4));
                this.drawHoveringText(list, (int) mouseX, (int) mouseY, fontRendererObj);
            }
        }
    }

    private int getProgressLevel(int progressIndicatorPixelWidth)
    {
        int ticksGrindingItemSoFar = tileGrinder.getField(2);
        int ticksPerItem = tileGrinder.getField(3);
        return ticksPerItem != 0 && ticksGrindingItemSoFar != 0 ? ticksGrindingItemSoFar*progressIndicatorPixelWidth/ticksPerItem : 0;
    }

    private int getEnergyLevel(int progressIndicatorPixelHeight) {
        int energy = tileGrinder.getField(4);
        int maxEnergy = tileGrinder.getField(6);
        //return energy <= maxEnergy ? energy * progressIndicatorPixelHeight / maxEnergy : 0;
        return energy * progressIndicatorPixelHeight / maxEnergy;
    }
}