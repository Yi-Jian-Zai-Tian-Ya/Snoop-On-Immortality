/*
  Source from Corpse
 */

package soi.common.entity.corpse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public abstract class EntityCorpseInventoryBase extends Entity implements IInventory
{
    private static final DataParameter<Integer> INVENTORY_SIZE = EntityDataManager.createKey(EntityCorpse.class, DataSerializers.VARINT);

    protected IInventory inventory;

    public EntityCorpseInventoryBase(World worldIn) { super(worldIn); }

    private IInventory getInventory()
    {
        if (inventory == null) inventory = new InventoryBasic("", false, 25);
        return inventory;
    }

    public void setItems(NonNullList<ItemStack> items)
    {
        dataManager.set(INVENTORY_SIZE, items.size());
        int size = 0;
        for (int i = 0; i < getSizeInventory() && i < items.size(); i++)
        {
            if (i >= 8 && items.get(i).isEmpty()) continue;
            setInventorySlotContents(size, items.get(i));
            size++;
        }
    }

    @Override protected void entityInit() { dataManager.register(INVENTORY_SIZE, 25); }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        int size = compound.getInteger("InventorySize");

        NBTTagList list = compound.getTagList("Inventory", 10);

        inventory = new InventoryBasic("", false, size);

        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound c = list.getCompoundTagAt(i);
            int j = c.getInteger("BagSlot");

            if (j >= 0 && j < inventory.getSizeInventory()) inventory.setInventorySlotContents(j, new ItemStack(c));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < getSizeInventory(); i++)
        {
            if (!getStackInSlot(i).isEmpty())
            {
                NBTTagCompound c = new NBTTagCompound();
                c.setInteger("BagSlot", i);
                getStackInSlot(i).writeToNBT(c);
                list.appendTag(c);
            }
        }

        compound.setTag("Inventory", list);
        compound.setInteger("InventorySize", getSizeInventory());
    }

    @Override public int getSizeInventory() { return getInventory().getSizeInventory(); }
    @Override public boolean isEmpty() { return getInventory().isEmpty(); }
    @Override public ItemStack getStackInSlot(int index) { return getInventory().getStackInSlot(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return getInventory().decrStackSize(index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return getInventory().removeStackFromSlot(index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) { getInventory().setInventorySlotContents(index, stack); }
    @Override public int getInventoryStackLimit() { return getInventory().getInventoryStackLimit(); }
    @Override public void markDirty() { getInventory().markDirty(); }
    @Override public boolean isUsableByPlayer(EntityPlayer player) { return getInventory().isUsableByPlayer(player); }
    @Override public void openInventory(EntityPlayer player) { getInventory().openInventory(player); }
    @Override public void closeInventory(EntityPlayer player) { getInventory().closeInventory(player); }
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return getInventory().isItemValidForSlot(index, stack); }
    @Override public int getField(int id) { return 0; }
    @Override public void setField(int id, int value) { }
    @Override public int getFieldCount() { return 0; }
    @Override public void clear() { getInventory().clear(); }
}