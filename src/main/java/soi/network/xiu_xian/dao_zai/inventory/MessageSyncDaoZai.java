package soi.network.xiu_xian.dao_zai.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.api.dao_zai.APIDaoZai;
import soi.api.dao_zai.cap.DaoZaiCapability;
import soi.api.dao_zai.cap.IDaoZai;

public class MessageSyncDaoZai implements IMessage, IMessageHandler<MessageSyncDaoZai, IMessage>
{
    private NBTTagCompound nbt;

    public MessageSyncDaoZai() { }
    public MessageSyncDaoZai(NBTTagCompound nbt) { this.nbt = nbt; }

    @Override public void fromBytes(ByteBuf buf) { this.nbt = ByteBufUtils.readTag(buf); }
    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeTag(buf, nbt); }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageSyncDaoZai message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IDaoZai Invs = APIDaoZai.getDaoZaiInv(Minecraft.getMinecraft().player);
            if (Invs != null) Invs.deserializeNBT(message.nbt);
        });
        return null;
    }
}