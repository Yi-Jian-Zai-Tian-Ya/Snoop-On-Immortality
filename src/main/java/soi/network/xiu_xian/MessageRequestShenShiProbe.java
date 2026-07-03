package soi.network.xiu_xian;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;
import soi.network.PacketHandler;

import java.util.Collections;

public class MessageRequestShenShiProbe implements IMessage
{
    public MessageRequestShenShiProbe() { }

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<MessageRequestShenShiProbe, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRequestShenShiProbe message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (player == null || player.world.isRemote) return;

                IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
                if (XiuXian == null) return;
                IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.PLAYER, null);
                if (XiuXianPlayer == null) return;

                if (XiuXian.getLingLi() <= 10.05D && !XiuXianPlayer.getShenShiActive() && !player.isCreative()) { PacketHandler.INSTANCE.sendTo(new MessageShenShiProbe(Collections.emptyList()), player); return; }
                if (!player.isCreative() && !XiuXianPlayer.getShenShiActive()) XiuXian.addLingLi(-10.0D);
                XiuXianPlayer.setShenShiActive(!XiuXianPlayer.getShenShiActive());
                if (!XiuXianPlayer.getShenShiActive()) PacketHandler.INSTANCE.sendTo(new MessageShenShiProbe(Collections.emptyList()), player);

                XiuXian.syncXiuXian(player);
                XiuXianPlayer.syncXiuXian(player);
            });
            return null;
        }
    }
}