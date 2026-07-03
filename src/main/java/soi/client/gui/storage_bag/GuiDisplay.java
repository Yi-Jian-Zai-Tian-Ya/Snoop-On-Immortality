package soi.client.gui.storage_bag;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import soi.SOI;
import soi.api.dao_zai.APIDaoZai;
import soi.api.dao_zai.cap.DaoZaiInv;
import soi.api.dao_zai.cap.DaoZaiType;
import soi.network.PacketHandler;
import soi.client.ClientProxy;
import soi.network.xiu_xian.dao_zai.storage_bag.MessageGetStorageBag;
import soi.network.xiu_xian.dao_zai.storage_bag.MessageOnPickStorageBag;

@EventBusSubscriber(modid = "soi", value = Side.CLIENT)
public class GuiDisplay
{
    private static final ResourceLocation HOTBAR = new ResourceLocation(SOI.MODID, "textures/gui/hotbar.png");
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static int LocX;
    private static int LocY;
    public static int pick = 0;
    public static int time = 0;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.getType() != ElementType.HOTBAR) return;

        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();
        LocX = width / 2 + 111;
        LocY = height - 22;

        if (ElementType.HOTBAR.equals(event.getType()))
        {
            GlStateManager.enableBlend();
            mc.getTextureManager().bindTexture(HOTBAR);
            mc.ingameGUI.drawTexturedModalRect(LocX, LocY, 0, 0, 62, 22);
            drawBagHUDIcons(event.getResolution());
            pickStorageBag();
        }
    }

    /*
      Source from BaublesHud
     */
    public void renderItemStack(ItemStack stack, int x, int y)
    {
        if (stack.isEmpty())
        {
            mc.getTextureManager().bindTexture(new ResourceLocation(SOI.MODID, "textures/items/empty_bag_slot.png"));
            GlStateManager.color(1, 1, 1, 1);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
        }
        if (!stack.isEmpty())
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glPushMatrix();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.enableGUIStandardItemLighting();

            RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
            itemRender.renderItemAndEffectIntoGUI(stack, x, y);
            itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, stack, x, y, null);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    public void drawBagHUDIcons(ScaledResolution resolution)
    {
        EntityPlayer player = mc.player;
        DaoZaiInv inv = APIDaoZai.getDaoZaiInv(player).getInv(DaoZaiType.Bag);

        for (int i = 0; i < inv.getSlots(); i++) this.renderItemStack(inv.getStackInSlot(i), LocX + i * 20 + 3, LocY + 3);
    }

    public void pickStorageBag()
    {
        mc.getTextureManager().bindTexture(HOTBAR);

        if (ClientProxy.KEY_PICK_STORAGE_BAG.isPressed())
        {
            if (time <= 0) mc.ingameGUI.setOverlayMessage(new TextComponentTranslation("message.pick"), false);
            time = time <= 0 ? 100 : 0;
        }

        if (time > 0)
        {
            onPickStorageBag();
            mc.ingameGUI.drawTexturedModalRect(LocX + pick * 20 - 1, LocY - 1, 0, 22, 24, 24);
            time--;
        }
        else mc.ingameGUI.drawTexturedModalRect(LocX + pick * 20 - 1, LocY - 1, 24, 22, 24, 24);
    }

    public void onPickStorageBag()
    {
        EntityPlayer player = mc.player;
        DaoZaiInv bag = APIDaoZai.getDaoZaiInv(player).getInv(DaoZaiType.Bag);

        if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && !player.isSpectator())
        {
            time = 0;
            ItemStack held = bag.getStackInSlot(pick);
            if (held.isEmpty()) return;

            PacketHandler.INSTANCE.sendToServer(new MessageOnPickStorageBag(pick));
        }

        if (mc.gameSettings.keyBindSwapHands.isKeyDown())
        {
            time = 0;
            PacketHandler.INSTANCE.sendToServer(new MessageGetStorageBag());
        }
    }

    @SubscribeEvent
    public void mouseWheel(MouseEvent event)
    {
        int wheel = event.getDwheel();
        if (wheel == 0) return;

        if (time > 0)
        {
            event.setCanceled(true);
            time = 100;
            pick = (pick + (wheel > 0 ? -1 : 1) + 3) % 3;
        }
    }
}