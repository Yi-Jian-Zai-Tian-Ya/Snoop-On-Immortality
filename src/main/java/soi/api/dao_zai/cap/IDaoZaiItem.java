package soi.api.dao_zai.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IDaoZaiItem
{
    DaoZaiType getType(ItemStack stack);

    default void onEquip(ItemStack stack, EntityLivingBase owner) { }
    default void onUnEquip(ItemStack stack, EntityLivingBase owner) { }
    default void onWorn(ItemStack stack, EntityLivingBase owner, int slot) { }
    default boolean canEquip(ItemStack stack, EntityLivingBase owner) { return true; }
    default boolean canUnEquip(ItemStack stack, EntityLivingBase owner) { return true; }
    default boolean hasSameItem(EntityPlayer player) { return false; }
    default boolean isSuitableLingGen(ItemStack stack, EntityPlayer player) { return false; }
    default boolean willAutoSync(ItemStack stack, EntityLivingBase player) { return false; }
}