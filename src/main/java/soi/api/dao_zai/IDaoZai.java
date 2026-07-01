/*
  Source from Baubles
 */

package soi.api.dao_zai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IDaoZai
{
    DaoZaiType getDaoType(ItemStack stack);

    default void onWornTick(ItemStack stack, EntityLivingBase player) { }
    default void onWornTick(ItemStack stack, EntityLivingBase player, int slot) { }
    default void onEquipped(ItemStack stack, EntityLivingBase player) { }
    default void onUnequipped(ItemStack stack, EntityLivingBase player) { }
    default boolean canEquip(ItemStack stack, EntityLivingBase player) { return true; }
    default boolean canUnEquip(ItemStack stack, EntityLivingBase player) { return true; }
    default boolean hasSameItem(EntityPlayer player) { return false; }
    default boolean isSuitableLingGen(ItemStack stack, EntityPlayer player) { return false; }
    default boolean willAutoSync(ItemStack stack, EntityLivingBase player) { return false; }
}