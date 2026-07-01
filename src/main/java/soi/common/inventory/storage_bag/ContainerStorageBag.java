/*
  Source from Iron Backpacks
 */

package soi.common.inventory.storage_bag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import soi.api.dao_zai.storage_bag.IStorageBag;
import soi.api.dao_zai.storage_bag.StorageBagInfo;
import soi.api.dao_zai.storage_bag.StorageBagType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerStorageBag extends Container
{
    @Nonnull
    private final StorageBagInfo info;
    @Nonnull
    private final StorageBagType type;
    @Nonnull
    private final ItemStack stack;
    @Nonnull
    private final int barIndex;

    private int blocked = -1;
    private final ItemStack blockedStack = ItemStack.EMPTY;

    public ContainerStorageBag(@Nonnull ItemStack stack, @Nonnull InventoryPlayer inventoryPlayer, @Nullable EnumHand hand, @Nullable int barIndex)
    {
        StorageBagInfo info = StorageBagInfo.fromStack(stack);
        IItemHandler itemHandler = info.getInventory();

        this.info = info;
        this.type = info.getType();
        this.stack = stack;
        this.barIndex = barIndex;

        setupSlots(inventoryPlayer, itemHandler, hand);
    }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return true; }
    @Override public void putStackInSlot(int slotID, ItemStack stack) { super.putStackInSlot(slotID, stack); }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        Slot slot = this.getSlot(slotIndex);
        if (!slot.canTakeStack(player)) return slot.getStack();

        if (slotIndex == blocked) return ItemStack.EMPTY;
        if (!slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        if (!stack.isEmpty() && ItemStack.areItemStacksEqual(stack, blockedStack)) return ItemStack.EMPTY;

        ItemStack newStack = stack.copy();

        if (slotIndex < type.getTotalSize()) { if (!this.mergeItemStack(stack, type.getTotalSize(), this.inventorySlots.size(), true)) return ItemStack.EMPTY; slot.onSlotChanged(); }
        else if (!this.mergeItemStack(stack, 0, type.getTotalSize(), false)) return ItemStack.EMPTY;

        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        return slot.onTake(player, newStack);
    }

    @Nonnull
    @Override
    public ItemStack slotClick(int slotId, int button, ClickType flag, EntityPlayer player)
    {
        if (slotId < 0 || slotId > inventorySlots.size()) return super.slotClick(slotId, button, flag, player);

        Slot slot = inventorySlots.get(slotId);
        if (!canTake(slotId, slot, button, player, flag)) return slot.getStack();

        return super.slotClick(slotId, button, flag, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        if (!(stack.getItem() instanceof IStorageBag)) return;
        ((IStorageBag) stack.getItem()).updateStorageBag(stack, info);
    }

    public boolean canTake(int slotId, Slot slot, int button, EntityPlayer player, ClickType clickType)
    {
        if (slotId == blocked) return false;

        ItemStack stack = slot.getStack();
        if (!stack.isEmpty() && ItemStack.areItemStacksEqual(stack, blockedStack)) return false;

        if (slotId <= type.getTotalSize() - 1) if (player.inventory.getItemStack().getItem() instanceof IStorageBag) return false;

        if (clickType == ClickType.QUICK_MOVE) if (stack.getItem() instanceof IStorageBag) return false;

        if (clickType == ClickType.SWAP)
        {
            int hotbarId = type.getTotalSize() + button;
            if (blocked == hotbarId) return false;

            Slot hotbarSlot = getSlot(hotbarId);
            ItemStack hotbarStack = hotbarSlot.getStack();

            if (!hotbarStack.isEmpty() && ItemStack.areItemStacksEqual(hotbarStack, blockedStack)) return false;

            if (slotId <= type.getTotalSize() - 1) return !(stack.getItem() instanceof IStorageBag || hotbarStack.getItem() instanceof IStorageBag);
        }
        return true;
    }

    @Nonnull public String getName() { return stack.getDisplayName(); }
    @Nonnull public int getBarIndex() { return barIndex; }

    private void setupSlots(@Nonnull InventoryPlayer player, @Nonnull IItemHandler handler, @Nullable EnumHand hand)
    {
        setupStorageBagSlots(handler);
        setupPlayerSlots(player, hand);
        setupPlayerSlots(player, hand);
    }

    private void setupStorageBagSlots(@Nonnull IItemHandler handler)
    {
        for (int y = 0; y < type.getRows(); y++) for (int x = 0; x < type.getCols(); x++)
                addSlotToContainer(new SlotItemHandler(handler, x + y * type.getCols(), 8 + x * 18  + getBagX(), 19 + y * 18));
    }

    private void setupPlayerSlots(@Nonnull InventoryPlayer inventory, @Nullable EnumHand hand)
    {
        for (int x = 0; x < 9; x++)
        {
            Slot slot = addSlotToContainer(new Slot(inventory, x, 8 + x * 18 + getHotbarX(), getHeight() - 24)
            {
                @Override
                public boolean canTakeStack(final EntityPlayer playerIn)
                {
                    ItemStack slotStack = getStack();
                    return slotNumber != blocked && (!slotStack.isEmpty() && !ItemStack.areItemStacksEqual(slotStack, blockedStack));
                }
            });

            if (x == inventory.currentItem && hand == EnumHand.MAIN_HAND) blocked = slot.slotNumber;
        }
    }

    public int getWidth() { return Math.max(type.getCols(), 9) * 18 + 14; }
    public int getHeight() { return (type.getRows() + 1) * 18 + 40; }
    public int getTexture() { return type.getMeta(); }
    public int getBagX() { if (type.getMeta() == 3) return 9; if (type.getMeta() == 4) return 36; return 0; }
    public int getHotbarX() { if (type.getMeta() == 2) return 18; return 0; }
}