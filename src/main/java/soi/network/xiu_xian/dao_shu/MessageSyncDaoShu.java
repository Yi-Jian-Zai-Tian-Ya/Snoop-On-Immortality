package soi.network.xiu_xian.dao_shu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.client.event.EventDaoShuHandler;
import soi.api.dao_shu.IDaoShu;

import java.io.IOException;

public class MessageSyncDaoShu implements IMessage, IMessageHandler<MessageSyncDaoShu, IMessage>
{
    private NBTTagCompound nbtData;

    public MessageSyncDaoShu() { }
    public MessageSyncDaoShu(NBTTagCompound nbt) { this.nbtData = nbt; }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try (ByteBufInputStream input = new ByteBufInputStream(buf))
        {
            this.nbtData = CompressedStreamTools.readCompressed(input);
        }
        catch (IOException e)
        {
            this.nbtData = new NBTTagCompound();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        try (ByteBufOutputStream output = new ByteBufOutputStream(buf))
        {
            CompressedStreamTools.writeCompressed(nbtData, output);
        }
        catch (IOException ignored) { }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageSyncDaoShu message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            IDaoShu DaoShu = Minecraft.getMinecraft().player.getCapability(DaoShuCapabilities.DAO_SHU, null);
            if (DaoShu != null && message.nbtData != null)
            {
                DaoShu.loadNBTData(message.nbtData);
                DaoShu.getDaoShus().forEach(name -> EventDaoShuHandler.updateBinding(name, DaoShu.getKeyBinding(name)));
            }
        });
        return null;
    }
}