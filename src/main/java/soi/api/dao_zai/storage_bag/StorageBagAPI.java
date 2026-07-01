/*
  Source from Iron Backpacks
 */

package soi.api.dao_zai.storage_bag;

import com.google.common.collect.ImmutableSet;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import soi.SOI;

import javax.annotation.Nonnull;
import java.util.*;

public class StorageBagAPI
{
    @GameRegistry.ObjectHolder("soi:storage_bag")
    public static Item STORAGE_BAG_ITEM = Items.AIR;

    private static IForgeRegistry<StorageBagType> TypeRegistry = null;

    @Nonnull public static IForgeRegistry<StorageBagType> getStorageBagTypeRegistry() { return TypeRegistry == null ? TypeRegistry = GameRegistry.findRegistry(StorageBagType.class) : TypeRegistry; }
    @Nonnull public static StorageBagType getStorageBagType(@Nonnull String type) { return Objects.requireNonNull(getStorageBagTypeRegistry().getValue(new ResourceLocation(SOI.MODID, type))); }
    @Nonnull public static Set<StorageBagType> getStorageBagTypes() { return ImmutableSet.copyOf(getStorageBagTypeRegistry().getValues()); }

    @Nonnull public static ItemStack getStack(@Nonnull StorageBagType type)
    {
        ItemStack stack = new ItemStack(STORAGE_BAG_ITEM);
        StorageBagInfo info = new StorageBagInfo(type);

        return applyBagInfo(stack, info);
    }

    @Nonnull public static StorageBagType getStorageBagType(int meta)
    {
        List<StorageBagType> types = Lists.newArrayList(getStorageBagTypes());
        types.sort(Comparator.comparingInt(StorageBagType::getMeta));
        if (meta >= 1 && meta <= 4) return types.get(meta);
        else return new StorageBagType();
    }

    @Nonnull
    public static ItemStack applyBagInfo(@Nonnull ItemStack stack, @Nonnull StorageBagInfo info)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setTag("BagInfo", info.serializeNBT());

        if (info.getInventory() != null)
        {
            NBTTagList inv = new NBTTagList();
            for (int i = 0; i < info.getInventory().getSlots(); i++)
            { ItemStack item = info.getInventory().getStackInSlot(i); if (item.isEmpty()) continue;
                NBTTagCompound tag = item.serializeNBT(); tag.setByte("Slot", (byte) i); inv.appendTag(tag); }
            if (stack.getTagCompound() != null) stack.getTagCompound().setTag("BagInv", inv);
        }
        stack.setItemDamage(info.getType().getMeta());
        return stack;
    }
}