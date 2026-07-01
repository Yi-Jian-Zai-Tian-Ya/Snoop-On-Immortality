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

import soi.api.dao_zai.gong_fa.GongFaCapabilities;
import soi.api.dao_zai.gong_fa.IGongFa;

public class MessageSyncGongFa implements IMessage, IMessageHandler<MessageSyncGongFa, IMessage>
{
    private NBTTagCompound nbt;

    public MessageSyncGongFa() { }
    public MessageSyncGongFa(NBTTagCompound nbt) { this.nbt = nbt; }

    @Override public void fromBytes(ByteBuf buf) { this.nbt = ByteBufUtils.readTag(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeTag(buf, nbt); }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageSyncGongFa message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IGongFa GongFa = Minecraft.getMinecraft().player.getCapability(GongFaCapabilities.GONG_FA, null);
            if (GongFa != null) GongFa.loadNBTData(message.nbt);
        });
        return null;
    }
}