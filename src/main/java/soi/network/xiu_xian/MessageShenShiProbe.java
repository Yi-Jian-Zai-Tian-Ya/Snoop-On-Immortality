package soi.network.xiu_xian;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class MessageShenShiProbe implements IMessage
{
    private List<Integer> entityIds;

    public MessageShenShiProbe() { }

    public MessageShenShiProbe(List<Integer> ids) { this.entityIds = ids; }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int size = buf.readInt();
        entityIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) entityIds.add(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityIds.size());
        for (int id : entityIds) buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<MessageShenShiProbe, IMessage>
    {
        @Override
        public IMessage onMessage(MessageShenShiProbe message, MessageContext ctx)
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(()->
            {
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                if (player == null) return;
                WorldClient world = (WorldClient) player.world;

                for (Entity entity : world.loadedEntityList) entity.setGlowing(false);
                if (message.entityIds.isEmpty()) return;
                for (int id : message.entityIds) { Entity entity = world.getEntityByID(id); if (entity != null) entity.setGlowing(true); }
            });
            return null;
        }
    }
}