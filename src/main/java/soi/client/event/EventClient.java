package soi.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.SOIConfig;
import soi.client.ClientProxy;
import soi.client.gui.xiu_xian.GuiDaoShuConfig;
import soi.network.xiu_xian.*;
import soi.util.LocUtils;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.storage_bag.MessageOnPickStorageBag;

import java.util.ArrayList;

@EventBusSubscriber(Side.CLIENT)
public class EventClient
{
    public static final ArrayList<RenderTooltipEvent.Pre> TOOLTIPS = new ArrayList<RenderTooltipEvent.Pre>();

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event)
    {
        if (!event.getWorld().isRemote) return;
        if (event.getEntity() instanceof EntityPlayerSP && event.getEntity().dimension == 2)
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage(new TextComponentTranslation("message.join", (TextFormatting.GOLD + event.getEntity().getName())), false);
    }

    @SubscribeEvent
    public void onRenderNameTag(RenderLivingEvent.Specials.Pre event)
    {
        if (event.getEntity() instanceof EntityPlayer) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        if (SOIConfig.UI.HealthUIDisabled)
        {
            if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.START)
        {
            if (ClientProxy.KEY_OPEN_BAG_1.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageOnPickStorageBag(0));
            if (ClientProxy.KEY_OPEN_BAG_2.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageOnPickStorageBag(1));
            if (ClientProxy.KEY_OPEN_BAG_3.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageOnPickStorageBag(2));

            if (ClientProxy.KEY_ITEM_SHOWING.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageItemShowing());
            if (ClientProxy.KEY_YUN_QI.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageTriggerSit());
            if (ClientProxy.KEY_DAO_SHU.isPressed()) Minecraft.getMinecraft().displayGuiScreen(new GuiDaoShuConfig());

            if (ClientProxy.KEY_SHEN_SHI_PROBE.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageRequestShenShiProbe());
            if (ClientProxy.KEY_FOCUS_ENEMY.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageFocusEnemy());
            if (ClientProxy.KEY_FOCUS_ENEMY.isKeyDown()) PacketHandler.INSTANCE.sendToServer(new MessageMaxFocusEnemy());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onGuiOpen(GuiOpenEvent event)
    {
        Gui gui = event.getGui();
        if (gui instanceof GuiContainer)
        {
            GuiContainer guiContainer = (GuiContainer) gui;
            Container container = guiContainer.inventorySlots;
            LocUtils.fixContainer(container, Minecraft.getMinecraft().player);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltipPre(RenderTooltipEvent.Pre event)
    {
        if (!SOIConfig.Ban_Slot.BanSlotOverlay) return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || player.isSpectator()) return;

        if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer)
        {
            event.setCanceled(true);
            TOOLTIPS.add(event);
        }
    }
}