package soi.network.xiu_xian.lun_hui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;
import soi.util.lun_hui.LunHuiManager;
import soi.util.lun_hui.LunHuiTeam;
import soi.util.lun_hui.LunHuiTeamData;

import java.util.UUID;

public class MessageLunHuiTeamInvite implements IMessage
{
    private String targetName;

    public MessageLunHuiTeamInvite() { }
    public MessageLunHuiTeamInvite(String target) { this.targetName = target; }

    @Override public void fromBytes(ByteBuf buf) { targetName = ByteBufUtils.readUTF8String(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, targetName); }

    public static class Handler implements IMessageHandler<MessageLunHuiTeamInvite, IMessage>
    {
        @Override
        public IMessage onMessage(MessageLunHuiTeamInvite msg, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                WorldServer world = sender.getServerWorld();
                LunHuiTeamData worldData = LunHuiTeamData.getWorldData(world);
                if (worldData == null) return;

                MinecraftServer server = sender.getServer();
                if (server == null) return;
                PlayerList list = server.getPlayerList();
                EntityPlayerMP target = list.getPlayerByUsername(msg.targetName);
                if (target == null) { sender.sendStatusMessage(new TextComponentTranslation("message.lun_hui.team.notFound", TextFormatting.DARK_RED + msg.targetName), true); return; }
                UUID senderUUID = sender.getUniqueID();
                UUID targetUUID = target.getUniqueID();
                if (senderUUID.equals(targetUUID)) { sender.sendStatusMessage(new TextComponentTranslation("message.lun_hui.team.sameTarget"), true); return; }
                IMingGe MingGe = target.getCapability(MingGeCapabilities.MING_GE, null);
                if (MingGe == null || MingGe.getLunHui() && target.dimension != 2) { sender.sendMessage(new TextComponentTranslation("message.lun_hui.team.lunHuied", TextFormatting.DARK_GREEN + target.getName())); return; }

                boolean senderIsTeamMember = false;
                UUID teamOwnerUUID = null;
                for (LunHuiTeam team : worldData.teamMap.values()) { boolean inside = team.members.stream().anyMatch(m -> m.uuid.equals(senderUUID)); if (inside) { senderIsTeamMember = true; teamOwnerUUID = team.owner; break; } }
                if (senderIsTeamMember && !senderUUID.equals(teamOwnerUUID))
                {
                    sender.sendMessage(new TextComponentTranslation("message.lun_hui.team.noPermission", TextFormatting.DARK_GREEN + list.getPlayerByUUID(teamOwnerUUID).getName()));
                    EntityPlayerMP owner = list.getPlayerByUUID(teamOwnerUUID);
                    if (list.getPlayerByUsername(owner.getName()) != null) owner.sendMessage(new TextComponentTranslation("message.lun_hui.team.apply", TextFormatting.BLUE + sender.getName(), TextFormatting.DARK_AQUA + list.getPlayerByUUID(targetUUID).getName()));
                    return;
                }

                boolean targetInAnyTeam = false;
                for (LunHuiTeam team : worldData.teamMap.values()) { if (team.members.stream().anyMatch(m -> m.uuid.equals(targetUUID))) { targetInAnyTeam = true; break; } }
                if (targetInAnyTeam) { sender.sendMessage(new TextComponentTranslation("message.lun_hui.team.joined", TextFormatting.DARK_RED + target.getName())); return; }

                LunHuiManager.createOrJoinTeam(worldData, sender, target);
                sender.sendMessage(new TextComponentTranslation("message.lun_hui.team.invite", TextFormatting.GOLD + target.getName()));
                target.sendMessage(new TextComponentTranslation("message.lun_hui.team.beInvited", TextFormatting.GOLD + sender.getName()));
                LunHuiManager.syncTeamToAll(worldData, server, sender.getUniqueID());
            });
            return null;
        }
    }
}