/*
  Source from Corpse
 */

package soi.common.inventory.corpse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public abstract class ContainerBase extends Container
{
    protected IInventory inventory;
    protected IInventory playerInventory;

    public ContainerBase(IInventory playerInventory, IInventory inventory) { this.playerInventory = playerInventory; this.inventory = inventory; }
    protected void addInvSlots() { if (playerInventory != null) for (int x = 0; x < 9; x++) addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 23 + getInvOffset())); }

    public int getInvOffset() { return 0; }

    public abstract int getInventorySize();

    @Override public boolean canInteractWith(EntityPlayer playerIn) { return true; }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            if (index < getInventorySize()) if (!this.mergeItemStack(stack, getInventorySize(), inventorySlots.size(), true)) return ItemStack.EMPTY;
            else if (!this.mergeItemStack(stack, 0, getInventorySize(), false)) return ItemStack.EMPTY;

            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return itemstack;
    }
}