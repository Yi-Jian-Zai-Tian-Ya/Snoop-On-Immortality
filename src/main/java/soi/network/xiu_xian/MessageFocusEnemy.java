package soi.network.xiu_xian;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;

public class MessageFocusEnemy implements IMessage
{
    public MessageFocusEnemy() { }
    @Override public void fromBytes(ByteBuf buf) { }
    @Override public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<MessageFocusEnemy, IMessage>
    {
        @Override
        public IMessage onMessage(MessageFocusEnemy message, MessageContext ctx)
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player; if (player == null || player.world.isRemote) return;
                IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.XIU_XIAN_PLAYER, null); if (XiuXianPlayer == null) return;

                if (player.getCapability(XiuXianCapabilities.XIU_XIAN, null).getLingLi() >= 0.15D || XiuXianPlayer.getFocusEnemyActive())
                    XiuXianPlayer.setFocusEnemyActive(!XiuXianPlayer.getFocusEnemyActive());

                XiuXianPlayer.syncXiuXian(player);
            });
            return null;
        }
    }
}