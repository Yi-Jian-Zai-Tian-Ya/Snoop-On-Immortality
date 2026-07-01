package soi.network.xiu_xian;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soi.api.CountDistance;

import java.util.HashMap;
import java.util.Map;

public class MessageItemShowing implements IMessage, IMessageHandler<MessageItemShowing, IMessage>
{
    private static final long COOLDOWN_TIME = 3000;
    private static final Map<String, Long> lastTriggerTime = new HashMap<>();

    public MessageItemShowing() { }

    @Override
    public void toBytes(ByteBuf buffer) { }

    @Override
    public void fromBytes(ByteBuf buffer) { }

    @Override
    public IMessage onMessage(MessageItemShowing message, MessageContext ctx)
    {
        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(new Runnable()
        {
            public void run()
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (shouldTrigger(player)) sendMessage(player);
            }
        });
        return null;
    }

    private boolean shouldTrigger(EntityPlayerMP player)
    {
        if (player == null) return false;
        String playerName = player.getName();
        long currentTime = System.currentTimeMillis();
        if (!lastTriggerTime.containsKey(playerName) || currentTime - lastTriggerTime.get(playerName) >= COOLDOWN_TIME)
        {
            lastTriggerTime.put(playerName, currentTime);
            return true;
        }
        return false;
    }

    public void sendMessage(EntityPlayerMP sender)
    {
        ItemStack item = sender.getHeldItemMainhand();
        if (item.isEmpty()) return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        int count = item.getCount();
        ITextComponent component = new TextComponentTranslation("message.show", sender.getName(), item.getTextComponent(), (count == 1) ? "" : TextFormatting.AQUA + "x" + count);

        for (EntityPlayerMP target : server.getPlayerList().getPlayers())
        {
            if (target == null) continue;
            if (sender.isCreative()) { target.sendMessage(component); continue; }
            if (target.isCreative()) { target.sendMessage(component); continue; }
            if (sender.dimension != target.dimension) continue;

            CountDistance.countDistance(sender, target);
            double distance = CountDistance.getDistance();

            if (distance > 200 + 50) continue;
            else if (distance > 100 + 50) target.sendMessage(new TextComponentTranslation("message.chat.far", (TextFormatting.YELLOW + sender.getName())));
            else if (distance > 50) target.sendMessage(new TextComponentTranslation("message.chat.near", (TextFormatting.YELLOW + sender.getName()), (TextFormatting.GOLD + TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + distance)));
            else target.sendMessage(component);
        }
    }
}