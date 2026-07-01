/*
  Source from Baubles
 */

package soi.network.xiu_xian.dao_zai.inventory;

import io.netty.buffer.ByteBuf;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import soi.SOI;
import soi.common.CommonProxy;

public class MessageOpenDaoZaiInventory implements IMessage, IMessageHandler<MessageOpenDaoZaiInventory, IMessage>
{
    public MessageOpenDaoZaiInventory() { }

    @Override
    public void toBytes(ByteBuf buffer) { }

    @Override
    public void fromBytes(ByteBuf buffer) { }

    @Override
    public IMessage onMessage(MessageOpenDaoZaiInventory message, MessageContext ctx)
    {
        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(new Runnable() { public void run() {
                ctx.getServerHandler().player.openContainer.onContainerClosed(ctx.getServerHandler().player);
                ctx.getServerHandler().player.openGui(SOI.instance, CommonProxy.GUI_DAO_ZAI, ctx.getServerHandler().player.world, 0, 0, 0);
        }});
        return null;
    }
}