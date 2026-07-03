package soi.network.xiu_xian.dao_zai.storage_bag;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import soi.SOI;
import soi.api.dao_zai.APIDaoZai;
import soi.api.dao_zai.cap.DaoZaiInv;
import soi.api.dao_zai.cap.DaoZaiType;
import soi.common.CommonProxy;

public class MessageOnPickStorageBag implements IMessage, IMessageHandler<MessageOnPickStorageBag, IMessage>
{
    private static int Pick = -1;

    public MessageOnPickStorageBag() { }

    public MessageOnPickStorageBag(int pick) { Pick = pick; }

    @Override
    public void toBytes(ByteBuf buffer) { }

    @Override
    public void fromBytes(ByteBuf buffer) { }

    @Override
    public IMessage onMessage(MessageOnPickStorageBag message, MessageContext ctx)
    {
        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(new Runnable()
        {
            public void run()
            {
                EntityPlayer player = ctx.getServerHandler().player;
                DaoZaiInv inv = APIDaoZai.getDaoZaiInv(player).getInv(DaoZaiType.Bag);
                ItemStack held = inv.getStackInSlot(Pick);
                if (held.isEmpty()) return;

                ctx.getServerHandler().player.openContainer.onContainerClosed(ctx.getServerHandler().player);
                ctx.getServerHandler().player.openGui(SOI.instance, CommonProxy.GUI_STORAGE_BAG, ctx.getServerHandler().player.world, 0, Pick, 0);
            }
        });
        return null;
    }
}