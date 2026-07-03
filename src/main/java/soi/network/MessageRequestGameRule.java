package soi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestGameRule implements IMessage
{
    private String ruleName;

    public MessageRequestGameRule() { }

    public MessageRequestGameRule(String ruleName)
    {
        this.ruleName = ruleName;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        byte[] bytes = ruleName.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.ruleName = new String(bytes);
    }

    public String getRuleName() { return ruleName; }

    public static class Handler implements IMessageHandler<MessageRequestGameRule, MessageResponseGameRule>
    {
        @Override
        public MessageResponseGameRule onMessage(MessageRequestGameRule message, MessageContext ctx)
        {
            String ruleName = message.getRuleName();
            GameRules rules = ctx.getServerHandler().player.world.getGameRules();
            boolean value = rules.getBoolean(ruleName);
            return new MessageResponseGameRule(ruleName, value);
        }
    }
}