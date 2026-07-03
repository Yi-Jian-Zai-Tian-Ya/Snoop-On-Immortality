package soi.api.dao_zai.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class DaoZaiInv extends ItemStackHandler
{
    private final DaoZaiType type;
    private int size;
    private boolean[] changes;
    private EntityLivingBase owner;
    private boolean blockEvent = false;

    public DaoZaiInv(DaoZaiType type, int base)
    {
        this.type = type;
        this.size = Math.max(1, base);
        this.updateSize();
    }

    public void updateSize()
    {
        int size = getSlots();
        int oldSize = this.stacks.size();
        NonNullList<ItemStack> newStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        int copyCount = Math.min(oldSize, size);
        for (int i = 0; i < copyCount; i++) newStacks.set(i, stacks.get(i));

        if (size < oldSize && owner != null)
            for (int i = size; i < oldSize; i++)
            {
                ItemStack overflow = stacks.get(i);
                if (!overflow.isEmpty()) if (owner instanceof EntityPlayer) ((EntityPlayer)owner).dropItem(overflow, false);
            }

        this.stacks = newStacks;

        boolean[] old = changes;
        changes = new boolean[size];
        if (old != null)
        {
            int copyLen = Math.min(old.length, size);
            System.arraycopy(old, 0, changes, 0, copyLen);
        }

        for (int i = size; i < oldSize; i++) { if (i >= this.changes.length) break; if (old != null && i < old.length) old[i] = false; }
    }

    public void addSlot(int num) { if (num <= 0) return; size += num; updateSize(); }

    public void removeSlot(int num) { if (num <= 0 || size <= 1) return; size = Math.max(1, size - num); updateSize(); }

    public int getSlots() { return size; }

    public DaoZaiType getType() { return type; }
    public void setOwner(EntityLivingBase entity) { this.owner = entity; }

    public boolean isValid(int slot, ItemStack stack) { return this.isValid(slot, stack, owner); }
    public boolean isValid(int slot, ItemStack stack, EntityLivingBase player)
    {
        if (stack.isEmpty() || slot < 0 || slot >= getSlots()) return false;
        if (!stack.hasCapability(DaoZaiCapability.ITEM_DAO_ZAI, null)) return false;
        IDaoZaiItem dao = stack.getCapability(DaoZaiCapability.ITEM_DAO_ZAI, null);
        if (dao == null) return false;
        return dao.getType(stack) == this.type && dao.canEquip(stack, player);
    }

    @Override public void setStackInSlot(int slot, ItemStack stack) { if (stack.isEmpty() || isValid(slot, stack)) super.setStackInSlot(slot, stack); }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (!isValid(slot, stack)) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override protected void onContentsChanged(int slot) { if (!blockEvent) setChanged(slot, true); }

    public void setChanged(int slot, boolean flag) { if (slot >= 0 && slot < changes.length) changes[slot] = flag; }

    public boolean isChanged(int slot) { return slot >= 0 && slot < changes.length && changes[slot]; }

    public void setBlockEvent(boolean b) { blockEvent = b; }
    public boolean isBlockEvent() { return blockEvent; }
    public void clear() { for (int i = 0; i < this.getSlots(); ++i) this.setStackInSlot(i, ItemStack.EMPTY); }
}