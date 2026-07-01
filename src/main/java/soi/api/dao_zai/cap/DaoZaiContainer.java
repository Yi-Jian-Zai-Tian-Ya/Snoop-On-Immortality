/*
  Source from Baubles
 */

package soi.api.dao_zai.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import soi.api.dao_zai.IDaoZai;

public class DaoZaiContainer extends ItemStackHandler implements IDaoZaiItemHandler
{
    private final static int DAO_SLOTS = 11;
    private boolean[] changed = new boolean[DAO_SLOTS];
    private EntityLivingBase entity;

    public DaoZaiContainer() { super(DAO_SLOTS); }

    @Override
    public void setSize(int size)
    {
        if (size < DAO_SLOTS) size = DAO_SLOTS;
        super.setSize(size);
        boolean[] old = changed;
        changed = new boolean[size];
        for (int i = 0; i < old.length && i < changed.length; i++) changed[i] = old[i];
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase entity)
    {
        if (stack == null || stack.isEmpty() || !stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null)) return false;
        IDaoZai Dao = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
        if (Dao == null) return false;
        return Dao.canEquip(stack, entity) && Dao.getDaoType(stack).hasSlot(slot);
    }

    @Override public void setStackInSlot(int slot, ItemStack stack) { if (this.isItemValidForSlot(slot, stack, entity)) super.setStackInSlot(slot, stack); }
    @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { if (!this.isItemValidForSlot(slot, stack, entity)) return stack; return super.insertItem(slot, stack, simulate); }
    @Override protected void onContentsChanged(int slot)  { setChanged(slot, true); }

    @Override public boolean isChanged(int slot) { if (changed == null) changed = new boolean[this.getSlots()]; return changed[slot]; }
    @Override public void setChanged(int slot, boolean change) { if (changed == null) changed = new boolean[this.getSlots()]; this.changed[slot] = change; }
    @Override public void setEntity(EntityLivingBase entity) { this.entity = entity; }
}