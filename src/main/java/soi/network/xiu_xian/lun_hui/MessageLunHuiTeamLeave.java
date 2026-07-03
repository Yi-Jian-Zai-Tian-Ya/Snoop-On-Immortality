package soi.network.xiu_xian.lun_hui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
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

public class MessageLunHuiTeamLeave implements IMessage
{
    public MessageLunHuiTeamLeave() { }

    @Override public void toBytes(ByteBuf buf) { }
    @Override public void fromBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<MessageLunHuiTeamLeave, IMessage>
    {
        @Override
        public IMessage onMessage(MessageLunHuiTeamLeave msg, MessageContext ctx)
        {
            EntityPlayerMP self = ctx.getServerHandler().player;
            self.getServerWorld().addScheduledTask(() ->
            {
                WorldServer world = self.getServerWorld();
                LunHuiTeamData worldData = LunHuiTeamData.getWorldData(world);
                if (worldData == null) return;

                MinecraftServer server = self.getServer();
                UUID selfUUID = self.getUniqueID();
                UUID targetTeamOwner = null;
                LunHuiTeam targetTeam = null;

                for (Map.Entry<UUID, LunHuiTeam> entry : worldData.teamMap.entrySet())
                {
                    LunHuiTeam team = entry.getValue();
                    boolean inside = team.members.stream().anyMatch(m -> m.uuid.equals(selfUUID));
                    if (inside) { targetTeamOwner = entry.getKey(); targetTeam = team; break; }
                }

                if (targetTeam == null || targetTeamOwner == null) return;

                self.sendMessage(new TextComponentTranslation("message.lun_hui.team.leave"));

                LunHuiManager.leaveTeam(worldData, targetTeamOwner, selfUUID, server);
                LunHuiManager.syncTeamToAll(worldData, server, targetTeamOwner);

                MessageSyncLunHuiTeam emptySync = new MessageSyncLunHuiTeam(null);
                PacketHandler.INSTANCE.sendTo(emptySync, self);
            });
            return null;
        }
    }
}