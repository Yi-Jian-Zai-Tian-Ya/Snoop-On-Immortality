/*
  Source from Corpse - 源自遗体(Corpse)
 */

package soi.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import org.lwjgl.input.Mouse;
import soi.SOI;
import soi.common.inventory.corpse.ContainerCorpse;
import soi.common.entity.corpse.EntityCorpse;
import soi.network.PacketHandler;
import soi.network.corpse.MessageGetItem;

import java.io.IOException;

public class GuiCorpse extends GuiContainer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/gui/corpse/inventory_corpse.png");

    private IInventory playerInventory;
    private EntityCorpse corpse;
    private GuiButton getItem;

    public GuiCorpse(IInventory playerInventory, EntityCorpse corpse, boolean editable)
    {
        super(new ContainerCorpse(playerInventory, corpse, editable));
        this.playerInventory = playerInventory;
        this.corpse = corpse;
        xSize = 176; ySize = 128;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString(corpse.getDisplayName().getFormattedText(), 7, 7, 0x404040);
        fontRenderer.drawString(playerInventory.getDisplayName().getFormattedText(), 7, ySize - 36, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.clear();
        getItem = addButton(new GuiButton(11, (width - 72) / 2, (height + 2) / 2, 72, 20, new TextComponentTranslation("button.get_item").getFormattedText()));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        if (button.id == 11) PacketHandler.INSTANCE.sendToServer(new MessageGetItem(corpse.getEntityId()));
    }

    @Override public void updateScreen() { getItem.enabled = !corpse.isEmpty(); Mouse.setGrabbed(false); }
}