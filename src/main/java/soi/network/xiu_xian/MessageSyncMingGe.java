package soi.network.xiu_xian;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;

public class MessageSyncMingGe implements IMessage, IMessageHandler<MessageSyncMingGe, IMessage>
{
    private NBTTagCompound nbt;

    public MessageSyncMingGe() { }
    public MessageSyncMingGe(NBTTagCompound nbt) { this.nbt = nbt; }

    @Override public void fromBytes(ByteBuf buf) { this.nbt = ByteBufUtils.readTag(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeTag(buf, nbt); }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageSyncMingGe message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IMingGe MingGe = Minecraft.getMinecraft().player.getCapability(MingGeCapabilities.MING_GE, null);
            if (MingGe != null) MingGe.loadNBTData(message.nbt);
        });
        return null;
    }
}