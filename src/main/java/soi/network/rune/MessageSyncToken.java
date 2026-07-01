package soi.network.rune;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.api.token.IToken;
import soi.api.token.TokenProvider;

public class MessageSyncToken implements IMessage
{
    private NBTTagCompound tokenData;

    public MessageSyncToken() { }

    public MessageSyncToken(NBTTagCompound tokenData) { this.tokenData = tokenData; }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        tokenData = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, tokenData);
    }

    public static class Handler implements IMessageHandler<MessageSyncToken, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageSyncToken message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().player != null)
                {
                    IToken Token = Minecraft.getMinecraft().player.getCapability(TokenProvider.TOKEN, null);
                    if (Token != null) Token.loadNBTData(message.tokenData);
                }
            });
            return null;
        }
    }
}