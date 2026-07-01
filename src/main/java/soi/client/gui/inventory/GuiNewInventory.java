package soi.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import soi.SOI;
import soi.common.inventory.inventory.ContainerNewInventory;

import java.io.IOException;

public class GuiNewInventory extends InventoryEffectRenderer
{
    public static final ResourceLocation inv = new ResourceLocation(SOI.MODID,"textures/gui/container/new_inventory.png");

    private float oldMouseX;
    private float oldMouseY;

    public GuiNewInventory(EntityPlayer player)
    {
        super(new ContainerNewInventory(player.inventory, !player.getEntityWorld().isRemote, player));
        this.allowUserInput = true;
        this.ySize = 130;
    }

    private void resetGuiLeft()
    {
        this.guiLeft = (this.width - this.xSize) / 2;
    }

    @Override
    public void updateScreen()
    {
        updateActivePotionEffects();
        resetGuiLeft();
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        super.initGui();
        resetGuiLeft();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b)
    {
        this.fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 97, 19, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(inv);
        int l = this.guiLeft;
        int t = this.guiTop;
        this.drawTexturedModalRect(l, t, 0, 0, this.xSize, this.ySize);
        GuiInventory.drawEntityOnScreen(l + 51, t + 86, 30, (float)(l + 51) - this.oldMouseX, (float)(t + 25) - this.oldMouseY, this.mc.player);
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException
    {
        super.keyTyped(par1, par2);
    }
}