package soi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.client.gui.xiu_xian.lun_hui.GuiLunHuiMenu;

public class MessageResponseGameRule implements IMessage
{
    private String ruleName;
    private boolean value;

    public MessageResponseGameRule() { }

    public MessageResponseGameRule(String ruleName, boolean value) { this.ruleName = ruleName; this.value = value; }

    @Override
    public void toBytes(ByteBuf buf)
    {
        byte[] bytes = ruleName.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.writeBoolean(value);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.ruleName = new String(bytes);
        this.value = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<MessageResponseGameRule, IMessage>
    {
        @Override
        public IMessage onMessage(MessageResponseGameRule message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiLunHuiMenu)
                {
                    GuiLunHuiMenu gui = (GuiLunHuiMenu) Minecraft.getMinecraft().currentScreen;
                    gui.updateRuleValue(message.getRuleName(), message.getValue());
                }
            });
            return null;
        }
    }

    public String getRuleName() { return ruleName; }
    public boolean getValue() { return value; }
}