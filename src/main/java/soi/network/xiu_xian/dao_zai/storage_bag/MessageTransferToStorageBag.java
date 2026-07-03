package soi.network.xiu_xian.dao_zai.storage_bag;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import soi.api.dao_zai.cap.DaoZaiInv;
import soi.api.dao_zai.storage_bag.IStorageBag;
import soi.api.dao_zai.storage_bag.StorageBagInfo;
import soi.api.dao_zai.APIDaoZai;
import soi.api.dao_zai.cap.DaoZaiType;
import soi.common.inventory.corpse.ContainerCorpse;
import soi.common.inventory.storage_bag.ContainerStorageBag;

public class MessageTransferToStorageBag implements IMessage
{
    private int index;

    public MessageTransferToStorageBag() { }
    public MessageTransferToStorageBag(int bagIndex) { this.index = bagIndex; }

    @Override
    public void fromBytes(ByteBuf buf) { index = buf.readInt(); }
    @Override
    public void toBytes(ByteBuf buf) { buf.writeInt(index); }

    public static class Handler implements IMessageHandler<MessageTransferToStorageBag, IMessage>
    {
        @Override
        public IMessage onMessage(MessageTransferToStorageBag msg, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (!player.isEntityAlive()) return;

                DaoZaiInv inv = APIDaoZai.getDaoZaiInv(player).getInv(DaoZaiType.Bag);
                ItemStack bag = inv.getStackInSlot(msg.index);
                if (bag.isEmpty() || !(bag.getItem() instanceof IStorageBag)) return;

                Container container = player.openContainer;
                if (container instanceof ContainerStorageBag && ((ContainerStorageBag) container).getBarIndex() == msg.index) return;

                StorageBagInfo info = StorageBagInfo.fromStack(bag);
                IItemHandler inventory = info.getInventory();

                boolean NoInventory = container instanceof ContainerStorageBag || container instanceof ContainerCorpse;
                int chestSize = container.inventorySlots.size() - 9 - (NoInventory ? 0 : 27);
                boolean changed = false;

                for (int i = 0; i < chestSize; i++)
                {
                    Slot slot = container.getSlot(i);
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty()) continue;
                    if (stack.getItem() instanceof IStorageBag) continue;

                    ItemStack remaining = ItemHandlerHelper.insertItem(inventory, stack.copy(), false);
                    if (remaining.getCount() < stack.getCount())
                    {
                        changed = true;
                        stack.shrink(stack.getCount() - remaining.getCount());
                        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
                        else slot.putStack(stack);
                        slot.onSlotChanged();
                    }
                }

                if (changed) { ((IStorageBag) bag.getItem()).updateStorageBag(bag, info); inv.setChanged(msg.index, true); }
                player.sendContainerToPlayer(container);
            });
            return null;
        }
    }
}