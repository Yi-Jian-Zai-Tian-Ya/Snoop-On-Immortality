package soi.network.corpse;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import soi.api.dao_zai.APIDaoZai;
import soi.api.dao_zai.cap.DaoZaiInv;
import soi.api.dao_zai.cap.DaoZaiType;
import soi.common.entity.corpse.EntityCorpse;

public class MessageGetItem implements IMessage, IMessageHandler<MessageGetItem, IMessage>
{
    private int corpseId;

    public MessageGetItem() { }
    public MessageGetItem(int entityId) { this.corpseId = entityId; }

    @Override public void fromBytes(ByteBuf buf) { this.corpseId = buf.readInt(); }
    @Override public void toBytes(ByteBuf buf) { buf.writeInt(this.corpseId); }

    @Override
    public IMessage onMessage(MessageGetItem message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().player;
        EntityCorpse corpse = (EntityCorpse) ctx.getServerHandler().player.world.getEntityByID(message.corpseId);

        MinecraftServer server = ctx.getServerHandler().player.getServer();
        server.addScheduledTask(new Runnable()
        {
            public void run()
            {
                GetItemLogic(corpse, player);
            }
        });
        return null;
    }

    public void GetItemLogic(EntityCorpse corpse, EntityPlayer player)
    {
        DaoZaiInv inv = APIDaoZai.getDaoZaiInv(player).getInv(DaoZaiType.Bag);

        for (int i = 0; i < corpse.getSizeInventory(); i++)
        {
            ItemStack item = corpse.getStackInSlot(i);
            if (item == ItemStack.EMPTY) continue;

            boolean moved = false;

            if (i < 4) { if (handleArmorGet(player, corpse, item, i)) continue; }
            else if (i == 4) { if (handleOffHandGet(player, corpse, item, i)) continue; }
            else if (i < 8) { if (handleBagGet(inv, corpse, item, i)) continue; }

            if (ToHotBar(player, item)) moved = true;
            if (moved) break;
            else corpse.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    private boolean handleArmorGet(EntityPlayer player, EntityCorpse corpse, ItemStack item, int i)
    {
        if (player.inventory.armorInventory.get(3 - i) == ItemStack.EMPTY)
        {
            player.inventory.armorInventory.set(3 - i, item);
            corpse.setInventorySlotContents(i, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    private boolean handleOffHandGet(EntityPlayer player, EntityCorpse corpse, ItemStack item, int i)
    {
        if (player.inventory.offHandInventory.get(0) == ItemStack.EMPTY)
        {
            player.setHeldItem(EnumHand.OFF_HAND, item);
            corpse.setInventorySlotContents(i, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    private boolean handleBagGet(DaoZaiInv inv, EntityCorpse corpse, ItemStack item, int i)
    {
        if (inv.getStackInSlot(i - 5) == ItemStack.EMPTY)
        {
            inv.setStackInSlot(i - 5, item);
            corpse.setInventorySlotContents(i, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    private boolean ToHotBar(EntityPlayer player, ItemStack item)
    {
        for (int i = 0; i < 9; i++) if (player.inventory.mainInventory.get(i) == ItemStack.EMPTY) { player.inventory.mainInventory.set(i, item); return false; }
        return true;
    }
}