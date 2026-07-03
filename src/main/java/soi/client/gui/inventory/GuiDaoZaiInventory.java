package soi.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import soi.SOI;
import soi.api.dao_zai.cap.DaoZaiType;
import soi.common.inventory.inventory.ContainerDaoZaiInventory;

public class GuiDaoZaiInventory extends InventoryEffectRenderer
{
    public static final ResourceLocation DAO = new ResourceLocation(SOI.MODID,"textures/gui/container/dao_zai.png");

    private float oldMouseX;
    private float oldMouseY;

    public GuiDaoZaiInventory(EntityPlayer player)
    {
        super(new ContainerDaoZaiInventory(player.inventory, !player.getEntityWorld().isRemote, player));
        this.allowUserInput = true;
        this.ySize = 108;
    }

    private void resetGuiLeft() { this.guiLeft = (this.width - this.xSize) / 2; }

    @Override
    public void updateScreen()
    {
        ((ContainerDaoZaiInventory)inventorySlots).invs.getInv(DaoZaiType.GongFa).setBlockEvent(false);
        ((ContainerDaoZaiInventory)inventorySlots).invs.getInv(DaoZaiType.FaBao).setBlockEvent(false);
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float a, int b, int c)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(DAO);
        int l = this.guiLeft;
        int t = this.guiTop;
        this.drawTexturedModalRect(l, t, 0, 0, this.xSize, this.ySize);
        GuiInventory.drawEntityOnScreen(l + 33, t + 75, 30, (float)(l + 33) - this.oldMouseX, (float)(t + 14) - this.oldMouseY, this.mc.player);
    }

    public void displayInventory()
    {
        GuiInventory gui = new GuiInventory(this.mc.player);
        ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseX, "oldMouseX", "field_147048_u");
        ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseY, "oldMouseY", "field_147047_v");
        this.mc.displayGuiScreen(gui);
    }
}