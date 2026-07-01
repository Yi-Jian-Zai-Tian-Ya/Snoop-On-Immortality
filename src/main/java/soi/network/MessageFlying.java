package soi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageFlying implements IMessage, IMessageHandler<MessageFlying, IMessage>
{
    @Override
    public IMessage onMessage(MessageFlying message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().player;

        NBTTagCompound persistentData = player.getEntityData();
        boolean isFlying = persistentData.getBoolean("flying");

        player.capabilities.isFlying = !isFlying;
        player.sendPlayerAbilities();

        player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }
}
