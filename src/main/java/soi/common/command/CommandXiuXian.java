package soi.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandXiuXian extends CommandBase
{
    @Nonnull @Override public String getName() { return "xiuXian"; }
    @Override public int getRequiredPermissionLevel() { return 2; }
    @Override public boolean isUsernameIndex(@Nonnull String[] args, int index) { if ("get".equalsIgnoreCase(args[0])) return index == 2; else return index == 3; }
    @Nonnull @Override public String getUsage(@Nonnull ICommandSender sender) { return "commands.xiuXing.usage"; }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1) throw new WrongUsageException("commands.xiuXing.usage", new Object[0]);

        Entity entity = ("get".equalsIgnoreCase(args[0])) ?
                (args.length < 3) ? getCommandSenderAsPlayer(sender) : getEntity(server, sender, args[2]) :
                (args.length < 4) ? getCommandSenderAsPlayer(sender) : getEntity(server, sender, args[3]);
        IXiuXian XiuXian = entity.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null) return;

        if ("add".equalsIgnoreCase(args[0]))
        {
            if (args.length <= 2) throw new WrongUsageException("commands.xiuXing.add.usage", new Object[0]);

            if ("JingJie".equalsIgnoreCase(args[1]))
            {
                int value = parseInt(args[2]);
                int oldValue = XiuXian.getJingJie();
                XiuXian.addJingJie(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], oldValue, XiuXian.getJingJieName(), XiuXian.getJingJie()});
            }
            else if ("XiuWei".equalsIgnoreCase(args[1]))
            {
                double value = ("Max".equalsIgnoreCase(args[2])) ? XiuXian.getMaxXiuWei() : parseDouble(args[2]);
                double oldValue = XiuXian.getXiuWei();
                XiuXian.addXiuWei(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], oldValue, XiuXian.getXiuWei(), XiuXian.getMaxXiuWei()});
            }
            else if ("LingLi".equalsIgnoreCase(args[1]))
            {
                double value = ("Max".equalsIgnoreCase(args[2])) ? XiuXian.getMaxLingLi() : parseInt(args[2]);
                double oldValue = XiuXian.getLingLi();
                XiuXian.addLingLi(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], oldValue, XiuXian.getLingLi(), XiuXian.getMaxLingLi()});
            }
            else if ("HuDun".equalsIgnoreCase(args[1]))
            {
                double value = ("Max".equalsIgnoreCase(args[2])) ? XiuXian.getMaxHuDun() : parseInt(args[2]);
                double oldValue = XiuXian.getHuDun();
                XiuXian.addHuDun(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], oldValue, XiuXian.getHuDun(), XiuXian.getMaxHuDun()});
            }
            else if ("ShenShi".equalsIgnoreCase(args[1]))
            {
                double value = parseDouble(args[2]);
                double oldValue = XiuXian.getShenShi();
                XiuXian.addShenShi(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getShenShi(), TextFormatting.DARK_RED + "None"});
            }
            else if ("LingLiMax".equalsIgnoreCase(args[1]))
            {
                double value = parseDouble(args[2]);
                double oldValue = XiuXian.getMaxLingLi();
                XiuXian.addMaxLingLi(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getMaxLingLi(), TextFormatting.DARK_RED + "None"});
            }
            else if ("HuDunMax".equalsIgnoreCase(args[1]))
            {
                double value = parseDouble(args[2]);
                double oldValue = XiuXian.getMaxHuDun();
                XiuXian.addMaxHuDun(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getMaxHuDun(), TextFormatting.DARK_RED + "None"});
            }
            else throw new CommandException("commands.generic.parameter.invalid", args[1]);
        }
        else if ("get".equalsIgnoreCase(args[0]))
        {
            if (args.length == 1) throw new WrongUsageException("commands.xiuXing.get.usage", new Object[0]);
            if ("JingJie".equalsIgnoreCase(args[1]))
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.only", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], XiuXian.getJingJieName(), XiuXian.getJingJie()}));
            else if ("XiuWei".equalsIgnoreCase(args[1]))
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.only", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], XiuXian.getXiuWei(), XiuXian.getMaxXiuWei()}));
            else if ("LingLi".equalsIgnoreCase(args[1]))
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.only", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], XiuXian.getLingLi(), XiuXian.getMaxLingLi()}));
            else if ("HuDun".equalsIgnoreCase(args[1]))
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.only", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], XiuXian.getHuDun(), XiuXian.getMaxHuDun()}));
            else if ("ShenShi".equalsIgnoreCase(args[1]))
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.only", new Object[] {TextFormatting.GOLD + entity.getName(), args[1], XiuXian.getShenShi(), TextFormatting.DARK_RED + "None"}));
            else if ("All".equalsIgnoreCase(args[1]))
            {
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.all", new Object[] {TextFormatting.GOLD + entity.getName()}));
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.all.entry", new Object[] {"JingJie", XiuXian.getJingJieName(), XiuXian.getJingJie()}));
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.all.entry", new Object[] {"XiuWei", XiuXian.getXiuWei(), XiuXian.getMaxXiuWei()}));
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.all.entry", new Object[] {"LingLi", XiuXian.getLingLi(), XiuXian.getMaxLingLi()}));
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.all.entry", new Object[] {"HuDun", XiuXian.getHuDun(), XiuXian.getMaxHuDun()}));
                sender.sendMessage(new TextComponentTranslation("commands.xiuXing.get.all.entry", new Object[] {"ShenShi", XiuXian.getShenShi(), TextFormatting.DARK_RED + "None"}));
            }
            else throw new CommandException("commands.generic.parameter.invalid", args[1]);
        }
        else if ("set".equalsIgnoreCase(args[0]))
        {
            if (args.length <= 2) throw new WrongUsageException("commands.xiuXing.set.usage", new Object[0]);

            if ("JingJie".equalsIgnoreCase(args[1]))
            {
                int value = parseInt(args[2]);
                int oldValue = XiuXian.getJingJie();
                XiuXian.setJingJie(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getJingJieName(), XiuXian.getJingJie()});
            }
            else if ("XiuWei".equalsIgnoreCase(args[1]))
            {
                double value = ("Max".equalsIgnoreCase(args[2])) ? XiuXian.getMaxXiuWei() : parseDouble(args[2]);
                double oldValue = XiuXian.getXiuWei();
                XiuXian.setXiuWei(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getXiuWei(), XiuXian.getMaxXiuWei()});
            }
            else if ("LingLi".equalsIgnoreCase(args[1]))
            {
                double value = ("Max".equalsIgnoreCase(args[2])) ? XiuXian.getMaxLingLi() : parseInt(args[2]);
                double oldValue = XiuXian.getLingLi();
                XiuXian.setLingLi(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getLingLi(), XiuXian.getMaxLingLi()});
            }
            else if ("HuDun".equalsIgnoreCase(args[1]))
            {
                double value = ("Max".equalsIgnoreCase(args[2])) ? XiuXian.getMaxHuDun() : parseInt(args[2]);
                double oldValue = XiuXian.getHuDun();
                XiuXian.setHuDun(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getHuDun(), XiuXian.getMaxHuDun()});
            }
            else if ("ShenShi".equalsIgnoreCase(args[1]))
            {
                double value = parseDouble(args[2]);
                double oldValue = XiuXian.getShenShi();
                XiuXian.setShenShi(value);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getShenShi(), TextFormatting.DARK_RED + "None"});
            }
            else if ("LingLiMax".equalsIgnoreCase(args[1]))
            {
                double value = parseDouble(args[2]);
                double oldValue = XiuXian.getMaxLingLi();
                XiuXian.addMaxLingLi(value - oldValue);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getMaxLingLi(), TextFormatting.DARK_RED + "None"});
            }
            else if ("HuDunMax".equalsIgnoreCase(args[1]))
            {
                double value = parseDouble(args[2]);
                double oldValue = XiuXian.getMaxHuDun();
                XiuXian.addMaxHuDun(value - oldValue);
                notifyCommandListener(sender, this, "commands.xiuXing.success", new Object[] {TextFormatting.GOLD + entity.getName(), args[1],oldValue, XiuXian.getMaxHuDun(), TextFormatting.DARK_RED + "None"});
            }
            else throw new CommandException("commands.generic.parameter.invalid", args[1]);
        }
        else throw new WrongUsageException("commands.xiuXing.usage", new Object[0]);

        if (entity instanceof EntityPlayerMP) XiuXian.syncXiuXian((EntityPlayerMP) entity);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "add", "get", "set");
        else if (args.length == 2)
        {
            if ("get".equalsIgnoreCase(args[0])) return getListOfStringsMatchingLastWord(args, "JingJie", "XiuWei", "LingLi", "HuDun", "ShenShi", "All");
            return getListOfStringsMatchingLastWord(args, "JingJie", "XiuWei", "LingLi", "HuDun", "ShenShi");
        }
        else if (args.length == 3)
        {
            if ("get".equalsIgnoreCase(args[0])) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            return getListOfStringsMatchingLastWord(args, "<value>", ("XiuWei".equalsIgnoreCase(args[1]) || "LingLi".equalsIgnoreCase(args[1]) || "HuDun".equalsIgnoreCase(args[1])) ? "Max" : null);
        }
        else if (args.length == 4) return ("get".equalsIgnoreCase(args[0])) ?
                Collections.emptyList() : getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        return Collections.emptyList();
    }
}