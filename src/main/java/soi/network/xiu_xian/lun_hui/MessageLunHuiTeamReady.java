package soi.network.xiu_xian.lun_hui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.util.lun_hui.LunHuiManager;
import soi.util.lun_hui.LunHuiTeamData;

public class MessageLunHuiTeamReady implements IMessage
{
    private int nian, yue, ri, shi;

    public MessageLunHuiTeamReady() { }
    public MessageLunHuiTeamReady(int n, int y, int r, int s)
    {
        nian = n; yue = y; ri = r; shi = s;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(nian);
        buf.writeInt(yue);
        buf.writeInt(ri);
        buf.writeInt(shi);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        nian = buf.readInt();
        yue = buf.readInt();
        ri = buf.readInt();
        shi = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageLunHuiTeamReady, IMessage>
    {
        @Override
        public IMessage onMessage(MessageLunHuiTeamReady msg, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {

                WorldServer world = player.getServerWorld();
                LunHuiTeamData worldData = LunHuiTeamData.getWorldData(world);
                if (worldData == null) return;
                MinecraftServer server = player.getServer();

                if (!ctx.getServerHandler().player.world.getGameRules().getBoolean("enableOptionalIVZhu")) LunHuiManager.playerSetReady(worldData, player, -1, -1, -1, -1);
                else LunHuiManager.playerSetReady(worldData, player, msg.nian, msg.yue, msg.ri, msg.shi);
                LunHuiManager.syncTeamToAll(worldData, server, player.getUniqueID());
                LunHuiManager.checkAllReadyAndRunLunHui(worldData, server, player);
            });
            return null;
        }
    }
}