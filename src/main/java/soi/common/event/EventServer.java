package soi.common.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandGameRule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import soi.SOI;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.api.dao_shu.DaoShuProvider;
import soi.api.dao_shu.IDaoShu;
import soi.api.dao_zai.cap.DaoZaiContainer;
import soi.api.dao_zai.cap.DaoZaiContainerProvider;
import soi.api.dao_zai.gong_fa.GongFaCapabilities;
import soi.api.dao_zai.gong_fa.GongFaProvider;
import soi.api.dao_zai.gong_fa.IGongFa;
import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;
import soi.api.ming_ge.MingGeProvider;
import soi.api.token.TokenProvider;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.api.xiu_xian.XiuXianProvider;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;
import soi.api.xiu_xian.player.XiuXianPlayerProvider;
import soi.common.command.PreciseTeleporter;
import soi.common.entity.EntityTeamUp;
import soi.common.entity.corpse.EntityCorpse;
import soi.util.lun_hui.LunHuiTeamData;

import java.util.Objects;

@EventBusSubscriber(modid = "soi")
public class EventServer
{
    @SubscribeEvent
    public void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityLivingBase)
        {
            event.addCapability(new ResourceLocation(SOI.MODID, "Xiu_Xian"), new XiuXianProvider());
            event.addCapability(new ResourceLocation(SOI.MODID, "Dao_Zai"), new DaoZaiContainerProvider(new DaoZaiContainer()));
        }
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(SOI.MODID, "Ming_Ge"), new MingGeProvider());
            event.addCapability(new ResourceLocation(SOI.MODID, "Xiu_Xian_Player"), new XiuXianPlayerProvider());
            event.addCapability(new ResourceLocation(SOI.MODID, "Gong_Fa"), new GongFaProvider());
            event.addCapability(new ResourceLocation(SOI.MODID, "Dao_Shu"), new DaoShuProvider());
            event.addCapability(new ResourceLocation(SOI.MODID, "Token"), new TokenProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!player.world.isRemote)
        {
            IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
            IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
            IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.XIU_XIAN_PLAYER, null);
            IGongFa GongFa = player.getCapability(GongFaCapabilities.GONG_FA, null);
            IDaoShu DaoShu = player.getCapability(DaoShuCapabilities.DAO_SHU, null);
            if (MingGe != null) { MingGe.syncMingGe(player); if (!MingGe.getLunHui() && player.dimension != 2) player.changeDimension(2, new PreciseTeleporter(Objects.requireNonNull(player.getServer()).getWorld(2), 0.5, 2, 0.5)); }
            if (XiuXian != null) XiuXian.syncXiuXian(player);
            if (XiuXianPlayer != null) XiuXianPlayer.syncXiuXian(player);
            if (GongFa != null) GongFa.syncGongFa(player);
            if (DaoShu != null) DaoShu.syncDaoShu(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.world;

        if (world.isRemote) return;

        IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
        if (MingGe != null && !MingGe.getLunHui()) player.changeDimension(2, new PreciseTeleporter(Objects.requireNonNull(player.getServer()).getWorld(2), 0.5, 2, 0.5));
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity().world.isRemote) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity();
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        MinecraftServer server = playerMP.getServer(); if (server == null) return;
        for (Advancement adv : server.getAdvancementManager().getAdvancements())
        {
            AdvancementProgress progress = playerMP.getAdvancements().getProgress(adv);
            if (progress.hasProgress()) for (String s : progress.getCompletedCriteria()) playerMP.getAdvancements().revokeCriterion(adv, s);
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote) return;
        if (event.getEntity() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            World world = player.world;
            MinecraftServer server = player.getServer();

            if (!world.getGameRules().getBoolean("reducedDebugInfo"))
            {
                world.getGameRules().setOrCreateGameRule("reducedDebugInfo", "true");
                if (server != null) CommandGameRule.notifyGameRuleChange(server.getWorld(0).getGameRules(), "reducedDebugInfo", server);
            }
            if (world.getGameRules().getBoolean("announceAdvancements"))
            {
                world.getGameRules().setOrCreateGameRule("announceAdvancements", "false");
                if (server != null) CommandGameRule.notifyGameRuleChange(server.getWorld(0).getGameRules(), "announceAdvancements", server);
            }
        }
    }

    @SubscribeEvent
    public void onServerWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.world.isRemote || event.phase != TickEvent.Phase.END) return;
        if (event.world.provider.getDimension() != 2) return;

        WorldServer world = (WorldServer) event.world;
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) return;

        LunHuiTeamData data = LunHuiTeamData.getWorldData(world);
        if (data == null || data.teamMap.isEmpty()) return;

        data.clearExpiredTeams(Objects.requireNonNull(world.getMinecraftServer()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) { if (event.getWorld().provider.getDimension() == 2) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockPlace(BlockEvent.PlaceEvent event) { if (event.getWorld().provider.getDimension() == 2) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingAttackEvent event) { if (event.getEntity().getEntityWorld().provider.getDimension() == 2 && event.getEntity() instanceof EntityPlayer) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingSummon(EntityJoinWorldEvent event) { if (event.getWorld().provider.getDimension() == 2 && !(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityTeamUp)) event.setCanceled(true); }
}