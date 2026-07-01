package soi.network.xiu_xian.dao_zai.gong_fa;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.SOIConfig;

public class MessageGongFaWorn implements IMessage
{
    public boolean valid;
    public ItemStack stack;
    public MessageGongFaWorn() { }
    public MessageGongFaWorn(boolean valid, ItemStack stack) { this.valid = valid; this.stack = stack; }

    @Override public void toBytes(ByteBuf buf) { buf.writeBoolean(valid); ByteBufUtils.writeItemStack(buf, stack); }
    @Override public void fromBytes(ByteBuf buf) { valid = buf.readBoolean(); stack = ByteBufUtils.readItemStack(buf);}

    public static class Handler implements IMessageHandler<MessageGongFaWorn, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageGongFaWorn msg, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.player == null || !msg.valid) return;
                mc.player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,.50F,1F);
                if (SOIConfig.UI.GongFaItemActivation) mc.entityRenderer.displayItemActivation(msg.stack);
            });
            return null;
        }
    }
}