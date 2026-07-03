package soi.network.xiu_xian.dao_zai.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.common.inventory.inventory.ContainerDaoZaiInventory;

public class MessageRefreshDaoZai implements IMessage
{
    @Override public void fromBytes(ByteBuf buf) { }
    @Override public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<MessageRefreshDaoZai, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRefreshDaoZai msg, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.player.openContainer instanceof ContainerDaoZaiInventory)
                {
                    ContainerDaoZaiInventory container = (ContainerDaoZaiInventory) mc.player.openContainer;
                    container.refreshSlots();
                }
            });
            return null;
        }
    }
}