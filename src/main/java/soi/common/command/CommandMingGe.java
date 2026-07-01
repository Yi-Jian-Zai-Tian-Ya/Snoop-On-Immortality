package soi.common.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandMingGe extends CommandBase
{
    @Nonnull @Override public String getName() { return "mingGe"; }
    @Override public int getRequiredPermissionLevel() { return 2; }
    @Override public boolean isUsernameIndex(@Nonnull String[] args, int index) { return index == 0; }
    @Nonnull @Override public String getUsage(@Nonnull ICommandSender sender) { return "commands.mingGe.usage"; }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayer player = (args.length < 1) ? getCommandSenderAsPlayer(sender) : getPlayer(server, sender, args[0]);
        IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
        if (player.world != null && MingGe != null)
        {
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage(MingGe.getIVZhu(), false);
            notifyCommandListener(sender, this, "commands.mingGe.success", new Object[] {TextFormatting.GOLD + player.getName(), MingGe.getIVZhu(), MingGe.getLingGen()});
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        return Collections.emptyList();
    }
}