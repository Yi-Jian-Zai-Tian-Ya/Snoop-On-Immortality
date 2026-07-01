package soi.common.event.xiu_xian;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.minecraftforge.fml.relauncher.Side;
import soi.api.CountDistance;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;
import soi.client.gui.inventory.GuiNewInventory;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.inventory.MessageOpenInventory;
import soi.network.xiu_xian.MessageShenShiProbe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventXiuXian
{
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null); if (XiuXian == null) return;
        IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.XIU_XIAN_PLAYER, null); if (XiuXianPlayer == null) return;

        if (player.isRiding())
        {
            XiuXianPlayer.updateYunQi(XiuXian);
            XiuXian.syncXiuXian(player);
            XiuXianPlayer.syncXiuXian(player);
            if (player.ticksExisted % 20 == 0) player.sendStatusMessage(new TextComponentTranslation("message.yun_qi", XiuXianPlayer.getYunQiTime()), true);
        }

        if (XiuXianPlayer.getShenShiActive() || XiuXianPlayer.getFocusEnemyActive()) XiuXianPlayer.update(player, XiuXian);
        if (XiuXianPlayer.getShenShiActive())
        {
            int tick = XiuXianPlayer.getProbeTick();
            double range;

            if (tick < 200) { range = XiuXian.getShenShi() * (float) tick / 200; XiuXianPlayer.syncXiuXian(player); }
            else range = XiuXian.getShenShi(); Vec3d pos = new Vec3d(player.posX, player.posY + 0.9D, player.posZ);

            AxisAlignedBB aabb = new AxisAlignedBB(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range);

            List<EntityLivingBase> list = player.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
            List<Integer> idList = new ArrayList<>();
            for (EntityLivingBase living : list) { IXiuXian cap = living.getCapability(XiuXianCapabilities.XIU_XIAN, null); if (cap != null && cap.getShenShi() >= XiuXian.getShenShi()) continue; idList.add(living.getEntityId()); }

            PacketHandler.INSTANCE.sendTo(new MessageShenShiProbe(idList), player);
        }
        else if (XiuXianPlayer.getProbeTick() > 0) { XiuXianPlayer.addProbeTick(-2); XiuXianPlayer.syncXiuXian(player); }
        if (!XiuXianPlayer.getFocusEnemyActive() && XiuXianPlayer.getFocusTick() > 0) { XiuXianPlayer.addFocusTick(-2); XiuXianPlayer.syncXiuXian(player); }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END) return;

        for (Entity entity : event.world.loadedEntityList)
        {
            if (!(entity instanceof EntityLivingBase)) continue;
            IXiuXian XiuXian = entity.getCapability(XiuXianCapabilities.XIU_XIAN, null);
            if (XiuXian == null) continue;
            if (XiuXian.getHuDun() < XiuXian.getMaxHuDun() || XiuXian.getLingLi() < XiuXian.getMaxLingLi() || XiuXian.canAutoLianQi()) XiuXian.update(entity);
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiInventory)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player.capabilities.isCreativeMode) return;
            event.setCanceled(true);

            PacketHandler.INSTANCE.sendToServer(new MessageOpenInventory());

            Minecraft.getMinecraft().addScheduledTask(() -> {if (player.world.isRemote) Minecraft.getMinecraft().displayGuiScreen(new GuiNewInventory(player));});
        }
    }

    @SubscribeEvent
    public void ReceiveChat(ServerChatEvent event)
    {
        EntityPlayerMP sender = event.getPlayer();
        if (sender == null || sender.isCreative()) return;
        ITextComponent component = event.getComponent();

        event.setCanceled(true);

        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        for (EntityPlayerMP target : players.getPlayers())
        {
            if (target == null) continue;
            if (target.isCreative()) { target.sendMessage(component); continue; }
            if (sender.dimension != target.dimension) continue;
            IXiuXian XiuXian = target.getCapability(XiuXianCapabilities.XIU_XIAN, null);
            if (XiuXian == null) continue;

            CountDistance.countDistance(sender, target);
            double distance = CountDistance.getDistance();

            if (distance > 200 + XiuXian.getShenShi()) continue;
            else if (distance > 100 + XiuXian.getShenShi()) target.sendMessage(new TextComponentTranslation("message.chat.far", (TextFormatting.YELLOW + sender.getName())));
            else if (distance > 25 + XiuXian.getShenShi()) target.sendMessage(new TextComponentTranslation("message.chat.near", (TextFormatting.YELLOW + sender.getName()), (TextFormatting.GOLD + TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + distance)));
            else target.sendMessage(component);
        }
    }

    @SubscribeEvent
    public void SendWhisper(CommandEvent event)
    {
        if (event.getSender() instanceof EntityPlayerMP)
        {
            EntityPlayerMP sender = (EntityPlayerMP) event.getSender();
            if (sender == null || sender.isCreative()) return;
            String command = event.getCommand().getName();
            String[] args = event.getParameters();

            if (command.equalsIgnoreCase("tell"))
            {
                String targetName = args.length > 0 ? args[0] : "";
                EntityPlayerMP target = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(targetName);
                if (target == null || target.isCreative()) return;
                if (sender.dimension != target.dimension) return;

                IXiuXian XiuXian = target.getCapability(XiuXianCapabilities.XIU_XIAN, null);
                if (XiuXian == null) return;

                CountDistance.countDistance(sender, target);
                double distance = CountDistance.getDistance();
                if (distance > XiuXian.getShenShi()) event.setCanceled(true);

                if (distance > 50 + XiuXian.getShenShi()) return;
                else if (distance > 20 + XiuXian.getShenShi()) target.sendMessage(new TextComponentTranslation("message.chat.far", (TextFormatting.YELLOW + sender.getName())));
                else if (distance > 5 + XiuXian.getShenShi()) sender.sendMessage(new TextComponentTranslation("message.chat.whisper", (TextFormatting.YELLOW + targetName)));
            }

            if (command.equalsIgnoreCase("me"))
            {
                String message = String.join(" ", event.getParameters());
                event.setCanceled(true);

                ITextComponent component = new TextComponentString("* " + sender.getName() + " " + message);

                for (EntityPlayerMP target : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                {
                    if (target == null) continue;
                    if (target.isCreative()) { target.sendMessage(component); continue; }
                    if (sender.dimension != target.dimension) continue;

                    IXiuXian XiuXian = target.getCapability(XiuXianCapabilities.XIU_XIAN, null);
                    if (XiuXian == null) continue;

                    CountDistance.countDistance(sender, target);
                    double distance = CountDistance.getDistance();

                    if (distance > 200 + XiuXian.getShenShi()) continue;
                    else if (distance > 100 + XiuXian.getShenShi()) target.sendMessage(new TextComponentTranslation("message.chat.far", (TextFormatting.YELLOW + sender.getName())));
                    else if (distance > 25 + XiuXian.getShenShi()) target.sendMessage(new TextComponentTranslation("message.chat.near", (TextFormatting.YELLOW + sender.getName()), (TextFormatting.GOLD + TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + distance)));
                    else target.sendMessage(component);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityKilled(LivingDeathEvent event)
    {
        EntityLivingBase deadEntity = event.getEntityLiving(); if (!(deadEntity instanceof EntityMob)) return;
        Entity killer = event.getSource().getTrueSource(); if (killer == null) return;

        IXiuXian XiuXian = killer.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null) return; XiuXian.addXiuWei(new Random().nextInt(73) + 8);

        if (killer instanceof EntityPlayerMP) XiuXian.syncXiuXian((EntityPlayerMP) killer);
    }
}