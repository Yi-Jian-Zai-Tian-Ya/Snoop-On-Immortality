/*
  Source from Baubles
 */

package soi.common.inventory.inventory;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import soi.api.dao_zai.IDaoZai;
import soi.api.dao_zai.cap.*;

public class ContainerDaoZaiInventory extends Container
{
    public IDaoZaiItemHandler dao;

    public boolean isLocalWorld;
    private final EntityPlayer player;

    public ContainerDaoZaiInventory(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
    {
        this.isLocalWorld = par2;
        this.player = player;
        dao = player.getCapability(DaoZaiCapabilities.DAO_ZAI, null);

        this.addSlotToContainer(new SlotDao(player, dao, 3, 62, 8, 1));
        this.addSlotToContainer(new SlotDao(player, dao, 4, 80, 8, 1));
        this.addSlotToContainer(new SlotDao(player, dao, 5, 98, 8, 1));
        this.addSlotToContainer(new SlotDao(player, dao, 6, 116, 8, 1));
        this.addSlotToContainer(new SlotDao(player, dao, 7, 62, 26, 2));
        this.addSlotToContainer(new SlotDao(player, dao, 8, 80, 26, 2));
        this.addSlotToContainer(new SlotDao(player, dao, 9, 98, 26, 2));
        this.addSlotToContainer(new SlotDao(player, dao, 10, 116, 26, 2));

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 84));
        }
    }

    @Override public void onContainerClosed(EntityPlayer player) { super.onContainerClosed(player); }

    @Override public boolean canInteractWith(EntityPlayer player) { return true; }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack()) return stack;

        ItemStack slotStack = slot.getStack();
        stack = slotStack.copy();

        int slotShift = dao.getSlots() - 3;

        if (index < slotShift)
        {
            if (!this.mergeItemStack(slotStack, slotShift, 9 + slotShift, true)) return ItemStack.EMPTY;
        }
        else if (stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
        {
            IDaoZai Dao = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
            for (int i : Dao.getDaoType(stack).getValidSlots())
            {
                i -= 3;
                if (Dao.canEquip(slotStack, this.player) && !(this.inventorySlots.get(i)).getHasStack() &&
                        !this.mergeItemStack(slotStack, i, i + 1, false)) return ItemStack.EMPTY;
                if (slotStack.getCount() == 0) break;
            }
        }

        if (slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();
        if (slotStack.getCount() == stack.getCount()) return ItemStack.EMPTY;
        if (slotStack.isEmpty() && slot instanceof SlotDao && stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
            stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null).onUnequipped(stack, player);

        return stack;
    }
}