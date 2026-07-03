package soi.network.xiu_xian.dao_shu;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.api.dao_shu.IDaoShu;
import soi.common.item.dao_shu.ItemDaoShuBase;

public class MessageCastDaoShu implements IMessage, IMessageHandler<MessageCastDaoShu, IMessage>
{
    private String name;

    public MessageCastDaoShu() { }
    public MessageCastDaoShu(String name) { this.name = name; }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        name = new String(bytes);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        byte[] bytes = name.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public IMessage onMessage(MessageCastDaoShu message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            IDaoShu DaoShu = player.getCapability(DaoShuCapabilities.DAO_SHU, null);
            if (DaoShu != null && DaoShu.getDaoShus().contains(message.name))
            {
                Item item = Item.getByNameOrId(message.name);
                if (item instanceof ItemDaoShuBase) ((ItemDaoShuBase) item).castOnServer(player.world, player);
            }
        });
        return null;
    }
}