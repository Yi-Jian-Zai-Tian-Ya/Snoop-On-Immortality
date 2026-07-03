package soi.network.rune;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.common.entity.rune.EntityTeleportVoiceRune;
import soi.common.item.rune.ItemTeleportVoiceRune;

public class MessageUpdateTeleportVoiceRune implements IMessage
{
    private static final int MAX_STRING_LENGTH = 512;

    private boolean isEntity;
    private int posX, posY, posZ;
    private EnumHand hand;
    private String message;
    private String select;

    public MessageUpdateTeleportVoiceRune() { }

    public MessageUpdateTeleportVoiceRune(String message, String select, BlockPos pos)
    {
        this.isEntity = true;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
        this.hand = null;
        this.message = filterString(message);
        this.select = filterString(select);
    }

    public MessageUpdateTeleportVoiceRune(String message, String select, EnumHand hand)
    {
        this.isEntity = false;
        this.posX = this.posY = this.posZ = 0;
        this.hand = hand;
        this.message = filterString(message);
        this.select = filterString(select);
    }

    private String filterString(String str)
    {
        if (str == null) return "";
        if (str.length() > MAX_STRING_LENGTH) str = str.substring(0, MAX_STRING_LENGTH);
        return str;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        isEntity = buf.readBoolean();

        if (isEntity)
        {
            posX = buf.readInt();
            posY = buf.readInt();
            posZ = buf.readInt();
            hand = null;
        }
        else
        {
            byte handFlag = buf.readByte();
            hand = handFlag == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        }

        int msgLength = buf.readInt();
        if (msgLength > 0)
        {
            byte[] msgBytes = new byte[msgLength];
            buf.readBytes(msgBytes);
            message = new String(msgBytes);
        }
        else message = "";

        int selLength = buf.readInt();
        if (selLength > 0)
        {
            byte[] selBytes = new byte[selLength];
            buf.readBytes(selBytes);
            select = new String(selBytes);
        }
        else select = "";
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(isEntity);

        if (isEntity)
        {
            buf.writeInt(posX);
            buf.writeInt(posY);
            buf.writeInt(posZ);
        }
        else buf.writeByte(hand == EnumHand.MAIN_HAND ? 0 : 1);

        if (message != null)
        {
            byte[] msgBytes = message.getBytes();
            buf.writeInt(msgBytes.length);
            buf.writeBytes(msgBytes);
        }
        else buf.writeInt(0);

        if (select != null)
        {
            byte[] selBytes = select.getBytes();
            buf.writeInt(selBytes.length);
            buf.writeBytes(selBytes);
        }
        else buf.writeInt(0);
    }

    public static class Handler implements IMessageHandler<MessageUpdateTeleportVoiceRune, IMessage>
    {
        @Override
        public IMessage onMessage(MessageUpdateTeleportVoiceRune msg, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServer().addScheduledTask(() ->
            {
                String safeMsg = msg.filterString(msg.message);
                String safeSel = msg.filterString(msg.select);

                if (msg.isEntity)
                {
                    BlockPos targetPos = new BlockPos(msg.posX, msg.posY, msg.posZ);
                    EntityTeleportVoiceRune rune = EntityTeleportVoiceRune.getRune(player.world, msg.posX, msg.posY, msg.posZ);
                    if (rune != null && !rune.isDead && rune.getPosition().equals(targetPos))
                    {
                        rune.setMessage(safeMsg);
                        rune.setSelect(safeSel);
                    }
                }
                else
                {
                    ItemStack stack = player.getHeldItem(msg.hand);
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemTeleportVoiceRune)
                    {
                        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound nbt = stack.getTagCompound();
                        if (nbt == null) return;
                        nbt.setString("message", safeMsg);
                        nbt.setString("select", safeSel);
                    }
                }
            });
            return null;
        }
    }
}