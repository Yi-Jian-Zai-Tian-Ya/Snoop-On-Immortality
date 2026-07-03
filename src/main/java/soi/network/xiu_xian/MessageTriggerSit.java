package soi.network.xiu_xian;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.common.entity.EntitySeat;

public class MessageTriggerSit implements IMessage, IMessageHandler<MessageTriggerSit, IMessage>
{

    public MessageTriggerSit() { }

    @Override
    public IMessage onMessage(MessageTriggerSit message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().player;
        World world = player.getEntityWorld();

        if (player.world.provider.getDimension() == 2) return null;
        if (player.isRiding()) { player.dismountRidingEntity(); return null; }
        if (!player.onGround) return null;

        EntitySeat seat = new EntitySeat(world, player);
        seat.setPosition(player.posX, player.posY, player.posZ);
        player.startRiding(seat);
        world.spawnEntity(seat);

        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }
}
