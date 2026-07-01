package soi.network.xiu_xian.dao_zai.storage_bag;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;
import soi.client.gui.storage_bag.GuiDisplay;

public class MessageGetStorageBag implements IMessage, IMessageHandler<MessageGetStorageBag, IMessage>
{
    public MessageGetStorageBag() { }

    @Override
    public void toBytes(ByteBuf buffer) { }

    @Override
    public void fromBytes(ByteBuf buffer) { }

    @Override
    public IMessage onMessage(MessageGetStorageBag message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().player;

        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(new Runnable()
        {
            public void run() { GetBag(player); }
        });
        return null;
    }

    public void GetBag(EntityPlayer player)
    {
        IDaoZaiItemHandler bag = DaoZaiAPI.getDaoZaiHandler(player);
        if (bag.getStackInSlot(GuiDisplay.pick).isEmpty()) return;

        ItemStack offHand = player.getHeldItemOffhand();
        if (offHand.isEmpty())
        {
            ItemStack bagStack = bag.getStackInSlot(GuiDisplay.pick);
            player.setHeldItem(EnumHand.OFF_HAND, bagStack);
            bag.setStackInSlot(GuiDisplay.pick, offHand);
        }
    }
}