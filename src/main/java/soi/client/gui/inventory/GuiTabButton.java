/*
  Source from Baubles
 */

package soi.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import soi.SOI;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.inventory.MessageInwardObserve;
import soi.network.xiu_xian.dao_zai.inventory.MessageOpenDaoZaiInventory;
import soi.network.xiu_xian.dao_zai.inventory.MessageOpenInventory;
import soi.network.xiu_xian.MessageTriggerSit;

public class GuiTabButton extends GuiButton
{
    private final GuiContainer parentGui;
    private int index;
    private boolean enabled;
    private final ItemStack iconStack;

    public GuiTabButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height, String buttonText, boolean enabled, ItemStack iconStack)
    {
        super(buttonId, x, parentGui.getGuiTop() + y, width, height, buttonText);
        this.index = buttonId;
        this.parentGui = parentGui;
        this.enabled = enabled;
        this.iconStack = iconStack;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        boolean pressed = super.mousePressed(mc, mouseX - this.parentGui.getGuiLeft(), mouseY);
        if (pressed) ArriveGUI();
        return pressed;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.visible) return;

        int x = this.x + this.parentGui.getGuiLeft();
        mc.getTextureManager().bindTexture(new ResourceLocation(SOI.MODID, "textures/gui/container/tabs.png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;

        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0,  200);

        if (!enabled) this.drawTexturedModalRect(x, this.y, 28 * index, 0, 28, 32);
        else this.drawTexturedModalRect(x, this.y, 28 * index, 32, 28, 32);
        if (this.getHoverState(this.hovered) == 2) mc.fontRenderer.drawString(I18n.format(this.displayString), 8, 6, 0xFFFFFF);

        if (!iconStack.isEmpty())
        {
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(iconStack, x + 6, this.y + 9);
            RenderHelper.disableStandardItemLighting();
        }

        GlStateManager.popMatrix();

        this.mouseDragged(mc, mouseX, mouseY);
    }

    private void ArriveGUI()
    {
        if (index == 0 && !(parentGui instanceof GuiNewInventory))
        {
            if (parentGui instanceof GuiInwardObserve) { ((GuiInwardObserve) parentGui).displayInventory(); if (Minecraft.getMinecraft().player.isRiding()) PacketHandler.INSTANCE.sendToServer(new MessageTriggerSit());}
            if (parentGui instanceof GuiDaoZaiInventory) ((GuiDaoZaiInventory) parentGui).displayInventory();
            PacketHandler.INSTANCE.sendToServer(new MessageOpenInventory());
        }
        if (index == 1 && !(parentGui instanceof GuiInwardObserve))
        {
            if (parentGui instanceof GuiDaoZaiInventory) ((GuiDaoZaiInventory) parentGui).displayInventory();
            if (!Minecraft.getMinecraft().player.isRiding()) PacketHandler.INSTANCE.sendToServer(new MessageTriggerSit());
            PacketHandler.INSTANCE.sendToServer(new MessageInwardObserve());
        }
        if (index == 2 && !(parentGui instanceof GuiDaoZaiInventory))
        {
            if (parentGui instanceof GuiInwardObserve) { ((GuiInwardObserve) parentGui).displayInventory(); if (Minecraft.getMinecraft().player.isRiding()) PacketHandler.INSTANCE.sendToServer(new MessageTriggerSit()); }
            PacketHandler.INSTANCE.sendToServer(new MessageOpenDaoZaiInventory());
        }
    }
}