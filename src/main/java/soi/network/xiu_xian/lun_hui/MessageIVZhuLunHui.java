package soi.network.xiu_xian.lun_hui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;
import soi.common.command.PreciseTeleporter;
import soi.util.lun_hui.LunHuiManager;

import java.util.Objects;

public class MessageIVZhuLunHui implements IMessage
{
    private int nian;
    private int yue;
    private int ri;
    private int shi;

    public MessageIVZhuLunHui() { }

    public MessageIVZhuLunHui(int nian, int yue, int ri, int shi)
    {
        this.nian = nian;
        this.yue = yue;
        this.ri = ri;
        this.shi = shi;
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
        this.nian = buf.readInt();
        this.yue = buf.readInt();
        this.ri = buf.readInt();
        this.shi = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageIVZhuLunHui, IMessage>
    {
        @Override
        public IMessage onMessage(MessageIVZhuLunHui message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = ctx.getServerHandler().player.world;

            player.getServerWorld().addScheduledTask(() ->
            {
                IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
                if (MingGe == null) return;
                int n = message.nian, y = message.yue, r = message.ri, s = message.shi;

                if (n == -1 || y == -1 || r == -1 || s == -1) MingGe.IVZhuLunHui();
                else { if (!world.getGameRules().getBoolean("enableOptionalIVZhu")) return; MingGe.SelectIVZhu(n, y, r, s); }

                WorldServer overworld = Objects.requireNonNull(player.getServer()).getWorld(0);
                BlockPos pos = LunHuiManager.getOverworldPos(player);
                player.changeDimension(0, new PreciseTeleporter(overworld, pos.getX(), pos.getY(), pos.getZ()));

                LunHuiManager.runLunHui(MingGe, player, Objects.requireNonNull(player.getServer()).getWorld(0));

                MingGe.syncMingGe(player);
            });

            return null;
        }
    }
}