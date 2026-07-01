package soi.util.lun_hui;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.common.command.PreciseTeleporter;
import soi.common.item.extra.ItemBrokenSword;
import soi.network.PacketHandler;
import soi.network.xiu_xian.lun_hui.MessageSyncLunHuiTeam;

import java.util.*;

public class LunHuiManager
{
    public static void runLunHui(IMingGe MingGe, EntityPlayerMP player, WorldServer overworld)
    {
        int num = MingGe.getVIIINum();
        if (num == 25073605 || num == 37033009) player.addItemStackToInventory(new ItemStack(ItemBrokenSword.BROKEN_SWORD));

        player.sendStatusMessage(new TextComponentString(MingGe.getIVZhu()), false);

        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian != null) { XiuXian.addHuDun(5.0D); XiuXian.syncXiuXian(player); }
        BlockPos village = getVillagePos(overworld, player);
        if (village != null) player.setPositionAndUpdate(village.getX() + 0.5D, village.getY(), village.getZ() + 0.5D);
        EntityIronGolem GuardsA = new EntityIronGolem(overworld), GuardsB = new EntityIronGolem(overworld);
        GuardsA.setPosition(player.posX, player.posY, player.posZ); GuardsB.setPosition(player.posX, player.posY, player.posZ);
        overworld.spawnEntity(GuardsA); overworld.spawnEntity(GuardsB);

        MingGe.syncMingGe(player);
    }

    public static BlockPos getOverworldPos(EntityPlayerMP player)
    {
        WorldServer overworld = Objects.requireNonNull(player.getServer()).getWorld(0);
        int x = player.getRNG().nextInt(20000) - 10000;
        int z = player.getRNG().nextInt(20000) - 10000;
        BlockPos block = overworld.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        int y = (overworld.getBlockState(block).getBlock() instanceof BlockLiquid) ? overworld.getSeaLevel() : block.getY();

        return new BlockPos(x + 0.5D, y, z + 0.5D);
    }

    public static BlockPos getVillagePos(WorldServer overworld, EntityPlayerMP player)
    {
        BlockPos village = overworld.findNearestStructure("Village", player.getPosition(), true);
        //BlockPos village = world.findNearestStructure("LaiFu", player.getPosition(), true);
        if (village == null) return null;
        BlockPos block = overworld.getTopSolidOrLiquidBlock(new BlockPos(village.getX(), 0, village.getZ()));
        int y = (overworld.getBlockState(village).getBlock() instanceof BlockLiquid) ? overworld.getSeaLevel() : block.getY();
        return new BlockPos(village.getX(), y + 1.0D, village.getZ());
    }

    public static void createOrJoinTeam(LunHuiTeamData data, EntityPlayerMP inviter, EntityPlayerMP target)
    {
        Map<UUID, LunHuiTeam> teamMap = data.teamMap;
        UUID uuid = inviter.getUniqueID();
        LunHuiTeam team = teamMap.get(uuid);
        ArrayList<LunHuiTeam.TeamMember> members;
        if (team == null)
        {
            members = new ArrayList<>();
            members.add(new LunHuiTeam.TeamMember(uuid, inviter.getName(), false, -1,-1,-1,-1));
            team = new LunHuiTeam(uuid, members);
            teamMap.put(uuid, team);
        }
        else members = team.members;
        boolean exist = members.stream().anyMatch(m -> m.uuid.equals(target.getUniqueID()));
        if (!exist) members.add(new LunHuiTeam.TeamMember(target.getUniqueID(), target.getName(), false, -1,-1,-1,-1));
        data.markDirty();
    }

    public static void playerSetReady(LunHuiTeamData data, EntityPlayerMP player, int n, int y, int r, int s)
    {
        UUID uuid = player.getUniqueID();
        IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
        if (MingGe == null) return;

        Map<UUID, LunHuiTeam> teamMap = data.teamMap;
        for (LunHuiTeam team : teamMap.values()) for (LunHuiTeam.TeamMember m : team.members)
                if (m.uuid.equals(uuid))
                {
                    m.isReady = true;
                    if (n == -1 || y == -1 || r == -1 || s == -1)
                    {
                        MingGe.IVZhuLunHui();

                        int num = MingGe.getVIIINum();
                        m.nian = num / 1000000;
                        m.yue = (num / 10000) % 100;
                        m.ri = (num / 100) % 100;
                        m.shi = num % 100;
                    }
                    else { m.nian = n; m.yue = y; m.ri = r; m.shi = s; }

                    data.markDirty(); return;
                }
    }

    public static void kickMember(LunHuiTeamData data, UUID owner, UUID beKicked, MinecraftServer server)
    {
        LunHuiTeam team = data.teamMap.get(owner);
        if (team == null) return;

        PlayerList list = server.getPlayerList();

        if (beKicked.equals(owner))
        {
            IMessage emptySync = new MessageSyncLunHuiTeam(null);
            for (LunHuiTeam.TeamMember m : team.members) { EntityPlayerMP mp = list.getPlayerByUUID(m.uuid); if (list.getPlayerByUsername(m.name) != null)
            { PacketHandler.INSTANCE.sendTo(emptySync, mp); mp.sendMessage(new TextComponentTranslation("message.lun_hui.team.remove",  TextFormatting.BLUE + list.getPlayerByUUID(owner).getName())); } }
            data.teamMap.remove(owner);
            data.markDirty();
            return;
        }
        team.members.removeIf(m -> m.uuid.equals(beKicked));

        boolean onlyOwner = team.members.size() == 1 && team.members.get(0).uuid.equals(owner);
        if (onlyOwner)
        {
            LunHuiTeam.TeamMember m = team.members.get(0);
            IMessage emptySync = new MessageSyncLunHuiTeam(null);
            EntityPlayerMP mp = list.getPlayerByUUID(m.uuid);
            if (list.getPlayerByUsername(m.name) != null) { PacketHandler.INSTANCE.sendTo(emptySync, mp);  mp.sendMessage(new TextComponentTranslation("message.lun_hui.team.remove",  TextFormatting.BLUE + list.getPlayerByUUID(owner).getName())); }
            data.teamMap.remove(owner);
        }
        data.markDirty();
    }

    public static void leaveTeam(LunHuiTeamData data, UUID owner, UUID leaveUUID, MinecraftServer server)
    {
        LunHuiTeam team = data.teamMap.get(owner);
        if (team == null) return;

        PlayerList list = server.getPlayerList();

        if (leaveUUID.equals(owner))
        {
            IMessage emptySync = new MessageSyncLunHuiTeam(null);
            for (LunHuiTeam.TeamMember m : team.members) { EntityPlayerMP mp = list.getPlayerByUUID(m.uuid); if (list.getPlayerByUsername(m.name) != null)
            { PacketHandler.INSTANCE.sendTo(emptySync, mp);  mp.sendMessage(new TextComponentTranslation("message.lun_hui.team.remove",  TextFormatting.BLUE + list.getPlayerByUUID(owner).getName())); } }
            data.teamMap.remove(owner);
            data.markDirty();
            return;
        }

        team.members.removeIf(m -> m.uuid.equals(leaveUUID));

        boolean onlyOwner = team.members.size() == 1 && team.members.get(0).uuid.equals(owner);
        if (onlyOwner)
        {
            IMessage emptySync = new MessageSyncLunHuiTeam(null);
            for (LunHuiTeam.TeamMember m : team.members) { EntityPlayerMP mp = list.getPlayerByUUID(m.uuid); if (list.getPlayerByUsername(m.name) != null)
            { PacketHandler.INSTANCE.sendTo(emptySync, mp); mp.sendMessage(new TextComponentTranslation("message.lun_hui.team.remove",  TextFormatting.BLUE + list.getPlayerByUUID(owner).getName())); } }
            data.teamMap.remove(owner);
        }
        data.markDirty();
    }

    public static void syncTeamToAll(LunHuiTeamData data, MinecraftServer server, UUID triggerPlayer)
    {
        LunHuiTeam team = null;
        for(Map.Entry<UUID, LunHuiTeam> entry : data.teamMap.entrySet())
        {
            LunHuiTeam r = entry.getValue();
            boolean inTeam = r.members.stream().anyMatch(m -> m.uuid.equals(triggerPlayer));
            if (inTeam) { team = r; break; }
        }
        if (team == null) return;
        IMessage syncPacket = new MessageSyncLunHuiTeam(team);
        PlayerList list = server.getPlayerList();
        for (LunHuiTeam.TeamMember m : team.members) { EntityPlayerMP mp = list.getPlayerByUUID(m.uuid); if (list.getPlayerByUsername(m.name) != null) PacketHandler.INSTANCE.sendTo(syncPacket, mp); }
    }

    public static void checkAllReadyAndRunLunHui(LunHuiTeamData data, MinecraftServer server, EntityPlayerMP triggerPlayer)
    {
        UUID uuid = triggerPlayer.getUniqueID();
        LunHuiTeam team = null;
        UUID ownerKey = null;

        for (Map.Entry<UUID, LunHuiTeam> entry : data.teamMap.entrySet())
        {
            LunHuiTeam r = entry.getValue();
            boolean contains = r.members.stream().anyMatch(m -> m.uuid.equals(uuid));
            if (contains) { team = r; ownerKey = entry.getKey(); break; }
        }
        if (team == null || ownerKey == null) return;

        boolean allReady = team.members.stream().allMatch(m -> m.isReady);
        if(!allReady) return;

        BlockPos pos = getOverworldPos(triggerPlayer);

        for (LunHuiTeam.TeamMember member : team.members)
        {
            EntityPlayerMP mp = server.getPlayerList().getPlayerByUUID(member.uuid);
            runFullRebirthLogic(mp, member.nian, member.yue, member.ri, member.shi, pos);
        }

        IMessage emptySync = new MessageSyncLunHuiTeam(null);
        for (LunHuiTeam.TeamMember m : team.members)
        {
            EntityPlayerMP mp = server.getPlayerList().getPlayerByUUID(m.uuid);
            PacketHandler.INSTANCE.sendTo(emptySync, mp);
        }

        for (LunHuiTeam.TeamMember self : team.members) for (LunHuiTeam.TeamMember other : team.members)
                if (!self.equals(other))
                {
                    EntityPlayerMP sender = server.getPlayerList().getPlayerByUUID(self.uuid);
                    EntityPlayerMP target = server.getPlayerList().getPlayerByUUID(other.uuid);
                    Float Yaw = getYaw(sender, target);
                    target.sendMessage(new TextComponentTranslation("message.lun_hui.team.pos", TextFormatting.DARK_GREEN + self.name, TextFormatting.DARK_GREEN + getDirection(Yaw), TextFormatting.DARK_GREEN + String.format("%.2f", Yaw)));
                }

        data.teamMap.remove(ownerKey);
        data.markDirty();
    }

    private static void runFullRebirthLogic(EntityPlayerMP player, int n, int y, int r, int s, BlockPos pos)
    {
        IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
        if (MingGe == null) return;
        MingGe.SelectIVZhu(n, y, r, s);

        WorldServer overworld = Objects.requireNonNull(player.getServer()).getWorld(0);
        player.changeDimension(0, new PreciseTeleporter(overworld, pos.getX(), pos.getY(), pos.getZ()));

        runLunHui(MingGe, player, overworld);
    }

    private static Float getYaw(EntityPlayerMP sender, EntityPlayerMP target)
    {
        double dx = sender.posX - target.posX;
        double dz = sender.posZ - target.posZ;

        double rad = Math.atan2(dz, dx);
        float yaw = (float) Math.toDegrees(rad) - 90F;

        while (yaw > 180) yaw -= 360;
        while (yaw < -180) yaw += 360;

        return yaw;
    }

    private static String getDirection(Float yaw)
    {
        int index;
        if (yaw >= 172.5F || yaw < -172.5F) index = 0;
        else { float shifted = yaw + 172.5F; index = 1 + (int) Math.floor(shifted / 15F); }

        if (index % 2 == 0) return new TextComponentTranslation(DI_ZHI[index / 2]).getUnformattedText();
        else return new TextComponentTranslation(DI_ZHI[index / 2]).getUnformattedText() + new TextComponentTranslation(DI_ZHI[(index / 2 + 1) % 12]).getUnformattedText();
    }

    private static final String[] DI_ZHI = {
            "diZhi.zi", "diZhi.chou", "diZhi.yin", "diZhi.mao",
            "diZhi.chen", "diZhi.si", "diZhi.wu", "diZhi.wei",
            "diZhi.shen", "diZhi.you", "diZhi.xu", "diZhi.hai"
    };
}