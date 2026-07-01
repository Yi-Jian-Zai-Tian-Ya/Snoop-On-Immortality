/*
  Source from Baubles
 */

package soi.network.xiu_xian.dao_zai.inventory;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import soi.SOI;
import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;

public class MessagePacketSync implements IMessage
{
    int id;
    byte slot = 0;
    ItemStack stack;

    public MessagePacketSync() { }

    public MessagePacketSync(EntityLivingBase living, int slot, ItemStack stack) { this.slot = (byte) slot; this.stack = stack.copy(); this.id = living.getEntityId(); }

    @Override public void toBytes(ByteBuf buffer) { buffer.writeInt(id); buffer.writeByte(slot); ByteBufUtils.writeItemStack(buffer, stack); }
    @Override public void fromBytes(ByteBuf buffer) { id = buffer.readInt(); slot = buffer.readByte(); stack = ByteBufUtils.readItemStack(buffer); }

    public static class Handler implements IMessageHandler<MessagePacketSync, IMessage>
    {
        @Override
        public IMessage onMessage(MessagePacketSync message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() { public void run()
                {
                    World world = SOI.proxy.getClientWorld();
                    if (world == null) return;
                    Entity target = world.getEntityByID(message.id);
                    if (target == null) return;
                    if (target instanceof EntityLivingBase)
                    {
                        EntityLivingBase living = (EntityLivingBase) target;
                        IDaoZaiItemHandler dao = DaoZaiAPI.getDaoZaiHandler(living);
                        if (dao != null) { dao.setStackInSlot(message.slot, message.stack); dao.setChanged(message.slot, true); }
                    }
                }
            });
            return null;
        }
    }
}