package soi.network.xiu_xian.dao_shu;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.api.dao_shu.IDaoShu;

public class MessageUpdateKeyBinding implements IMessage, IMessageHandler<MessageUpdateKeyBinding, IMessage>
{
    private String name;
    private String keyCode;

    public MessageUpdateKeyBinding() { }
    public MessageUpdateKeyBinding(String name, String key)
    {
        this.name = name;
        this.keyCode = key;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int nameLen = buf.readInt();
        byte[] nameBytes = new byte[nameLen];
        buf.readBytes(nameBytes);
        this.name = new String(nameBytes);

        int keyLen = buf.readInt();
        byte[] keyBytes = new byte[keyLen];
        buf.readBytes(keyBytes);
        this.keyCode = new String(keyBytes);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(name.getBytes().length);
        buf.writeBytes(name.getBytes());
        buf.writeInt(keyCode.getBytes().length);
        buf.writeBytes(keyCode.getBytes());
    }

    @Override
    public IMessage onMessage(MessageUpdateKeyBinding message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            IDaoShu DaoShu = player.getCapability(DaoShuCapabilities.DAO_SHU, null);
            if (DaoShu != null)
            {
                DaoShu.setKeyBinding(message.name, message.keyCode);
                DaoShu.syncDaoShu(player);
            }
        });
        return null;
    }
}