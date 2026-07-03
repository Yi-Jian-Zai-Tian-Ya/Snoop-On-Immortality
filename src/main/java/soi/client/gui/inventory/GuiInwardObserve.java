package soi.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import org.lwjgl.input.Mouse;
import soi.SOI;
import soi.common.inventory.inventory.ContainerInwardObserve;

public class GuiInwardObserve extends InventoryEffectRenderer
{
    public static final ResourceLocation inward = new ResourceLocation(SOI.MODID,"textures/gui/container/inward_observe.png");

    private float oldMouseX;
    private float oldMouseY;

    public GuiInwardObserve(EntityPlayer player)
    {
        super(new ContainerInwardObserve());
        this.allowUserInput = true;
        this.ySize = 176;
    }

    private void resetGuiLeft() { this.guiLeft = (this.width - this.xSize) / 2; }

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
        if (!Mouse.isGrabbed()) Mouse.setGrabbed(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0);
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);

        drawLightSpotOnTop(mouseX, mouseY);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float a, int b, int c)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(inward);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    private void drawLightSpotOnTop(int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();

        GlStateManager.translate(0, 0, 300);

        this.mc.getTextureManager().bindTexture(inward);

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(oldMouseX - 5, oldMouseY - 5, 176, 0, 10, 10);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GuiInventory.drawEntityOnScreen(this.guiLeft + 88, this.guiTop + 130, 30, (float)(this.guiLeft + 88) - this.oldMouseX, (float)(this.guiTop + 69) - this.oldMouseY, this.mc.player);


        GlStateManager.popMatrix();
    }

    public void displayInventory()
    {
        GuiInventory gui = new GuiInventory(this.mc.player);
        ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseX, "oldMouseX", "field_147048_u");
        ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseY, "oldMouseY", "field_147047_v");
        Mouse.setGrabbed(false);
        this.mc.displayGuiScreen(gui);
    }
}