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

import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;

public class MessageSyncXiuXian implements IMessage, IMessageHandler<MessageSyncXiuXian, IMessage>
{
    private NBTTagCompound nbt;

    public MessageSyncXiuXian() { }
    public MessageSyncXiuXian(NBTTagCompound nbt) { this.nbt = nbt; }

    @Override public void fromBytes(ByteBuf buf) { this.nbt = ByteBufUtils.readTag(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeTag(buf, nbt); }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageSyncXiuXian message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IXiuXian XiuXian = Minecraft.getMinecraft().player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
            if (XiuXian != null) XiuXian.loadNBTData(message.nbt);
        });
        return null;
    }
}