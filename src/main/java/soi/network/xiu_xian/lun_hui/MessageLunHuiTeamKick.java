package soi.network.xiu_xian.lun_hui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.util.lun_hui.LunHuiManager;
import soi.util.lun_hui.LunHuiTeam;
import soi.util.lun_hui.LunHuiTeamData;
import soi.network.PacketHandler;

import java.util.Map;
import java.util.UUID;

public class MessageLunHuiTeamKick  implements IMessage
{
    private UUID kickTarget;
    public MessageLunHuiTeamKick() { }
    public MessageLunHuiTeamKick(UUID target) { kickTarget = target; }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(kickTarget.getMostSignificantBits());
        buf.writeLong(kickTarget.getLeastSignificantBits());
    }

    @Override
    public void fromBytes(ByteBuf buf) { kickTarget = new UUID(buf.readLong(), buf.readLong()); }

    public static class Handler implements IMessageHandler<MessageLunHuiTeamKick, IMessage>
    {
        @Override
        public IMessage onMessage(MessageLunHuiTeamKick msg, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() -> {
                WorldServer world = sender.getServerWorld();
                LunHuiTeamData worldData = LunHuiTeamData.getWorldData(world);
                if (worldData == null) return;

                MinecraftServer server = sender.getServer();
                if (server == null) return;
                UUID senderUUID = sender.getUniqueID();
                UUID kickTargetUuid = msg.kickTarget;
                EntityPlayerMP kickedPlayer = server.getPlayerList().getPlayerByUUID(kickTargetUuid);

                UUID owner = null;
                for (Map.Entry<UUID, LunHuiTeam> entry : worldData.teamMap.entrySet())
                {
                    LunHuiTeam team = entry.getValue();
                    boolean inTeam = team.members.stream().anyMatch(m -> m.uuid.equals(sender.getUniqueID()));
                    if (inTeam) { owner = entry.getKey(); break; }
                }
                if (owner == null || !owner.equals(sender.getUniqueID())) return;

                if (!kickTargetUuid.equals(owner)) kickedPlayer.sendMessage(new TextComponentTranslation("message.lun_hui.team.beKicked",TextFormatting.DARK_RED + sender.getName()));
                LunHuiManager.kickMember(worldData, owner, msg.kickTarget, server);
                LunHuiManager.syncTeamToAll(worldData, server, senderUUID);

                IMessage emptySync = new MessageSyncLunHuiTeam(null);
                PacketHandler.INSTANCE.sendTo(emptySync, kickedPlayer);
            });
            return null;
        }
    }
}