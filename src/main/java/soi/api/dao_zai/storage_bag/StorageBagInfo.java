/*
  Source from Iron Backpacks
 */

package soi.api.dao_zai.storage_bag;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StorageBagInfo implements INBTSerializable<NBTTagCompound>
{
    @Nonnull private StorageBagType type;
    private IItemHandlerModifiable inventory;

    public StorageBagInfo(@Nonnull StorageBagType type) { this.type = type; }
    private StorageBagInfo() { this(new StorageBagType()); }

    @Nonnull public StorageBagType getType() { return type; }
    @Nonnull public StorageBagInfo setType(@Nonnull StorageBagType type) { this.type = type; return this; }

    public IItemHandlerModifiable getInventory() { return inventory; }
    public StorageBagInfo setInventory(@Nonnull IItemHandlerModifiable inventory) { this.inventory = inventory; return this; }

    @Override public NBTTagCompound serializeNBT() { NBTTagCompound tag = new NBTTagCompound(); tag.setString("type", type.getType()); return tag; }
    @Override public void deserializeNBT(NBTTagCompound nbt) { type = StorageBagAPI.getStorageBagType(nbt.getString("type")); }

    @Nonnull
    public static StorageBagInfo fromStack(@Nonnull ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("BagInfo"))
        { StorageBagInfo info = new StorageBagInfo(StorageBagAPI.getStorageBagType(stack.getItemDamage()));
            info.setInventory(new ItemStackHandler(info.type.getTotalSize())); return info; }

        StorageBagInfo tagged = fromTag(stack.getTagCompound().getCompoundTag("BagInfo"));
        ItemStackHandler handler = new ItemStackHandler(tagged.type.getTotalSize());
        NBTTagList list = stack.getTagCompound().getTagList("BagInv", 10);
        for (int i = 0; i < list.tagCount(); i++) { NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getByte("Slot") & 0xFF; handler.setStackInSlot(slot, new ItemStack(tag)); }
        return tagged.setInventory(handler);
    }

    @Nonnull
    public static StorageBagInfo fromTag(@Nullable NBTTagCompound tag)
    {
        StorageBagInfo info = new StorageBagInfo();
        if (tag == null || tag.hasNoTags()) return info;
        info.deserializeNBT(tag); return info;
    }
}