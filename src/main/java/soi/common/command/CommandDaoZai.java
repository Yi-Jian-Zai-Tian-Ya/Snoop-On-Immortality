package soi.common.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.DaoZaiType;
import soi.api.dao_zai.IDaoZai;
import soi.api.dao_zai.cap.DaoZaiCapabilities;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandDaoZai extends CommandBase
{
    @Nonnull @Override public String getName() { return "daoZai"; }
    @Override public int getRequiredPermissionLevel() { return 2; }
    @Override public boolean isUsernameIndex(String[] args, int i) { return i == 1 && !"view".equalsIgnoreCase(args[0]); }
    @Nonnull @Override public String getUsage(@Nonnull ICommandSender sender) { return "commands.daoZai.usage"; }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1 || "help".equalsIgnoreCase(args[0]))
        {
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.help.view"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.view.usage"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.help.take"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.take.usage"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.help.put"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.put.usage"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.help.replace"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.replace.usage"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.help.clear"));
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.clear.usage"));
            throw new WrongUsageException("commands.daoZai.usage", new Object[0]);
        }
        if (args.length == 1) throw this.getTabCompletions(server, sender, args, null).contains(args[0])
                ? new WrongUsageException("commands.daoZai." + args[0] + ".usage", new Object[0]) : new WrongUsageException("commands.daoZai.usage", new Object[0]);

        Entity living = getEntity(server, sender, args[1]);
        if (!(living instanceof EntityLivingBase)) throw new CommandException("commands.generic.entity.invalidType", living.getName());
        EntityLivingBase entity = (EntityLivingBase) living;
        EntityPlayer player = getCommandSenderAsPlayer(sender);

        IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(entity);

        if ("view".equalsIgnoreCase(args[0]))
        {
            sender.sendMessage(new TextComponentTranslation("commands.daoZai.view", TextFormatting.GOLD + entity.getName()));
            for (int i = 0; i < DaoZais.getSlots(); i++)
            {
                ItemStack stack = DaoZais.getStackInSlot(i);
                if (!stack.isEmpty() && stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
                {
                    IDaoZai DaoZai = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
                    if (DaoZai == null) return;
                    DaoZaiType type = DaoZai.getDaoType(stack);
                    sender.sendMessage(new TextComponentTranslation(TextFormatting.GREEN + " [Slot " + i + "] " + type + " %s", stack.getTextComponent()));
                }
            }
        }
        else if ("take".equalsIgnoreCase(args[0]))
        {
            if (args.length == 2) throw new WrongUsageException("commands.daoZai.take.usage", new Object[0]);
            int slot = parseInt(args[2], 0, DaoZais.getSlots() - 1);

            ItemStack stack = DaoZais.getStackInSlot(slot).copy();
            if (stack.isEmpty()) throw new CommandException("commands.generic.slot.isEmpty", slot);
            DaoZais.setStackInSlot(slot, ItemStack.EMPTY);
            takeItem(stack, stack.getCount(), player, sender);

            notifyCommandListener(sender, this, "commands.daoZai.take.success", new Object[] {entity.getName(), slot, stack.getTextComponent(), stack.getCount()});
        }
        else if ("put".equalsIgnoreCase(args[0]))
        {
            if (args.length == 2) throw new WrongUsageException("commands.daoZai.put.usage", new Object[0]);
            int slot = parseInt(args[2], 0, DaoZais.getSlots() - 1);
            if (!DaoZais.getStackInSlot(slot).isEmpty()) throw new CommandException("commands.generic.slot.hasItem", slot);

            ItemStack stack = args.length >= 4 ? player.inventory.getStackInSlot(parseInt(args[3], 0, 8)) : player.getHeldItemMainhand();
            checkValidity(stack, slot, entity);

            DaoZais.setStackInSlot(slot, stack);
            notifyCommandListener(sender, this, "commands.daoZai.put.success", new Object[] {stack.getTextComponent(), entity.getName(), slot});
        }
        else if ("replace".equalsIgnoreCase(args[0]))
        {
            if (args.length == 2) throw new WrongUsageException("commands.daoZai.replace.usage", new Object[0]);
            int slot = parseInt(args[2], 0, DaoZais.getSlots() - 1);

            ItemStack held = args.length >= 4 ? player.inventory.getStackInSlot(parseInt(args[3], 0, 8)) : player.getHeldItemMainhand();
            checkValidity(held, slot, entity);

            ItemStack stack = DaoZais.getStackInSlot(slot).copy();
            if (!stack.isEmpty()) takeItem(stack, stack.getCount(), player, sender);

            DaoZais.setStackInSlot(slot, held);
            notifyCommandListener(sender, this, "commands.daoZai.replace.success", new Object[] {entity.getName(), slot, held.getTextComponent()});
        }
        else if ("clear".equalsIgnoreCase(args[0]))
        {
            if (args.length >= 3)
            {
                int slot = parseInt(args[2], 0, DaoZais.getSlots() - 1);
                DaoZais.setStackInSlot(slot, ItemStack.EMPTY);
                notifyCommandListener(sender, this, "commands.daoZai.clear.only.success", new Object[] {entity.getName(), slot});
            }
            else
            {
                for (int i = 0; i < DaoZais.getSlots(); i++) DaoZais.setStackInSlot(i, ItemStack.EMPTY);
                notifyCommandListener(sender, this, "commands.daoZai.clear.success", new Object[] {entity.getName()});
            }
        }
        else throw new WrongUsageException("commands.daoZai.usage", new Object[0]);
    }

    private void checkValidity(ItemStack stack, int slot, EntityLivingBase entity) throws CommandException
    {
        IDaoZai Dao = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
        if (Dao == null) throw new CommandException("commands.generic.slot.invalidItem", stack.getDisplayName());
        if (!Dao.getDaoType(stack).hasSlot(slot)) throw new CommandException("commands.generic.slot.notSameType", Dao.getDaoType(stack).toString(), slot);
        if (Dao.getDaoType(stack).onlyForPlayer())
        {
            if (!(entity instanceof EntityPlayer)) throw new CommandException("commands.generic.slot.onlyForPlayer", Dao.getDaoType(stack).toString());
            EntityPlayer target = (EntityPlayer) entity;
            if (Dao.hasSameItem(target)) throw new CommandException("commands.generic.slot.sameItem", stack.getDisplayName());
            if (!Dao.isSuitableLingGen(stack, target)) throw new CommandException("commands.generic.lingGen.unsuitable", new Object[] {entity.getName(), stack.getTextComponent()});
        }
        if (!Dao.canEquip(stack, entity)) throw new CommandException("commands.generic.slot.canNotEquip", entity.getName(), stack.getDisplayName());

    }
    private void takeItem(ItemStack stack, int i, EntityPlayer player, ICommandSender sender)
    {
        boolean flag = player.inventory.addItemStackToInventory(stack);
        if (flag) { player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F); player.inventoryContainer.detectAndSendChanges(); }
        if (flag && stack.isEmpty()) { stack.setCount(1); sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i); EntityItem eItem = player.dropItem(stack, false); if (eItem != null) eItem.makeFakeItem(); }
        else { sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i - stack.getCount()); EntityItem eItem = player.dropItem(stack, false); if (eItem != null) { eItem.setNoPickupDelay(); eItem.setOwner(player.getName()); } }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "help", "view", "take", "put", "replace", "clear");
        else if (args.length == 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        return Collections.emptyList();
    }
}