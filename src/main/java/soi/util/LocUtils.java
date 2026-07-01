package soi.util;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;

import soi.api.dao_zai.storage_bag.IStorageBag;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class LocUtils
{
    /*
      Source from Mantle
     */
    public static List<String> getTooltips(String text)
    {
        List<String> list = Lists.newLinkedList();
        if (text == null) return list;
        int j = 0;
        int k;
        while ((k = text.indexOf("\\n", j)) >= 0)
        { list.add(text.substring(j, k)); j = k + 2; }

        list.add(text.substring(j, text.length()));

        return list;
    }

    public static String translateRecursive(String key, Object... params)
    {
        return I18n.translateToLocal(I18n.translateToLocalFormatted(key, params));
    }

    /*
      Source from TinyInv
     */
    public static void fixContainer(Container container, EntityPlayer player)
    {
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (shouldBeRemoved(slot, player))
            {
                container.inventorySlots.set(i, new BanSlot(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos));
            }
        }
    }

    public static boolean shouldBeRemoved(Slot slot, EntityPlayer player)
    {
        if (slot.inventory != player.inventory)
        {
            return false;
        }
        return shouldBeRemoved(slot.getSlotIndex(), player);
    }

    public static boolean shouldBeRemoved(int id, EntityPlayer player)
    {
        return (id < 36) && (id >= 9 && id <= 35);
    }

    /*
      Source from Iron Backpacks
     */
    @Nonnull
    public static ItemStack getNonEquippedStorageBagFromInventory(EntityPlayer player, EnumHand hand)
    {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() instanceof IStorageBag) return held;
        else
        {
            ItemStack offhand = player.getHeldItem(EnumHand.OFF_HAND);
            if (!offhand.isEmpty() && offhand.getItem() instanceof IStorageBag) return offhand;

            return ItemStack.EMPTY;
        }
    }
}