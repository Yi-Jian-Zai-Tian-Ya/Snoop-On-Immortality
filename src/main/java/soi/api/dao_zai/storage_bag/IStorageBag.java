/*
  Source from Iron Backpacks
 */

package soi.api.dao_zai.storage_bag;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IStorageBag
{
    @Nonnull default StorageBagInfo getStorageBagInfo(@Nonnull ItemStack stack) { return StorageBagInfo.fromStack(stack); }
    default void updateStorageBag(@Nonnull ItemStack stack, @Nonnull StorageBagInfo info) { StorageBagAPI.applyBagInfo(stack, info); }
}