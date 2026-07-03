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

public class MessageMaxFocusEnemy implements IMessage
{
    public MessageMaxFocusEnemy() { }
    @Override public void fromBytes(ByteBuf buf) { }
    @Override public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<MessageMaxFocusEnemy, IMessage>
    {
        @Override
        public IMessage onMessage(MessageMaxFocusEnemy message, MessageContext ctx)
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player; if (player == null || player.world.isRemote) return;
                IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null); if (XiuXian == null) return;
                IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.PLAYER, null); if (XiuXianPlayer == null) return;

                if (XiuXianPlayer.getFocusEnemyActive())
                {
                    if (XiuXian.getLingLi() <= 0.05D) { XiuXianPlayer.setFocusEnemyActive(false); return; }
                    if (!player.isCreative()) XiuXian.addLingLi(-0.05D);
                    if (XiuXianPlayer.getFocusRange() < XiuXian.getShenShi() * 1.25F) XiuXianPlayer.addFocusTick(1);
                }

                XiuXian.syncXiuXian(player);
                XiuXianPlayer.syncXiuXian(player);
            });
            return null;
        }
    }
}