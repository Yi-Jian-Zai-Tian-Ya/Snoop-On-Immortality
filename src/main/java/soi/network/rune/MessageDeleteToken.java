package soi.network.rune;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.token.IToken;
import soi.api.token.TokenProvider;

public class MessageDeleteToken implements IMessage
{
    private String playerName;

    public MessageDeleteToken() { }

    public MessageDeleteToken(String playerName) { this.playerName = playerName; }

    @Override
    public void fromBytes(ByteBuf buf) { playerName = ByteBufUtils.readUTF8String(buf); }

    @Override
    public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, playerName); }

    public static class Handler implements IMessageHandler<MessageDeleteToken, IMessage>
    {
        @Override
        public IMessage onMessage(MessageDeleteToken message, MessageContext ctx)
        {
            if (ctx.side.isServer())
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                IToken Token = player.getCapability(TokenProvider.TOKEN, null);
                if (Token != null && Token.removeToken(message.playerName))
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