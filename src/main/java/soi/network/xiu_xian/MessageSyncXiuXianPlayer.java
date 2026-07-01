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

import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;

public class MessageSyncXiuXianPlayer implements IMessage, IMessageHandler<MessageSyncXiuXianPlayer, IMessage>
{
    private NBTTagCompound nbt;

    public MessageSyncXiuXianPlayer() { }
    public MessageSyncXiuXianPlayer(NBTTagCompound nbt) { this.nbt = nbt; }

    @Override public void fromBytes(ByteBuf buf) { this.nbt = ByteBufUtils.readTag(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeTag(buf, nbt); }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageSyncXiuXianPlayer message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IXiuXianPlayer XiuXianPlayer = Minecraft.getMinecraft().player.getCapability(XiuXianPlayerCapabilities.XIU_XIAN_PLAYER, null);
            if (XiuXianPlayer != null) XiuXianPlayer.loadNBTData(message.nbt);
        });
        return null;
    }
}