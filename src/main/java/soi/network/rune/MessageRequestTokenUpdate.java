package soi.network.rune;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.token.IToken;
import soi.api.token.TokenProvider;

public class MessageRequestTokenUpdate implements IMessage
{
    public MessageRequestTokenUpdate() { }

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<MessageRequestTokenUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRequestTokenUpdate message, MessageContext ctx)
        {
            if (ctx.side.isServer())
            {
                IToken Token = ctx.getServerHandler().player.getCapability(TokenProvider.TOKEN, null);
                if (Token != null)
                {
                    NBTTagCompound nbt = new NBTTagCompound();
                    Token.saveNBTData(nbt);
                    return new MessageSyncToken(nbt);
                }
            }
            return null;
        }
    }
}