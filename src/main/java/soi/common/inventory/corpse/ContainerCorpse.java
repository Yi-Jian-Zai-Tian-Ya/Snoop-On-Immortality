/*
  Source from Corpse
 */

package soi.common.inventory.corpse;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.NonNullList;

import soi.common.entity.corpse.EntityCorpse;

public class ContainerCorpse extends ContainerBase
{
    private EntityCorpse corpse;
    private boolean editable;

    public ContainerCorpse(IInventory playerInventory, EntityCorpse corpse, boolean editable)
    {
        super(playerInventory, corpse);
        this.corpse = corpse;
        this.editable = editable;

        setSlots(0);
    }

    public void setSlots(int start)
    {
        inventorySlots = Lists.newArrayList();
        inventoryItemStacks = NonNullList.create();
        for (int x = 0; x < 9; x++)
        {
            if (x < 4)
            {
                ContainerSlot slot = new ContainerSlot(corpse, x, 8 + x * 18, 19, editable);
                slot.setBackgroundName(emptySlots[3 - x]);
                addSlotToContainer(slot);
            }
            if (x > 4)
            {
                ContainerSlot slot = new ContainerSlot(corpse, x - 1, 8 + x * 18, 19, editable);
                slot.setBackgroundName(emptySlots[x - 1]);
                addSlotToContainer(slot);
            }
        }
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new ContainerSlot(corpse, x + 8, 8 + x * 18, 41, editable));
        }

        addInvSlots();
        listeners.stream().forEach(listener -> listener.sendAllContents(this, getInventory()));
    }

    public EntityCorpse getCorpse()
    {
        return corpse;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public int getInvOffset()
    {
        return 81;
    }

    @Override
    public int getInventorySize()
    {
        return 17;
    }

    private static final String[] emptySlots = new String[]
            {
                    "minecraft:items/empty_armor_slot_boots",
                    "minecraft:items/empty_armor_slot_leggings",
                    "minecraft:items/empty_armor_slot_chestplate",
                    "minecraft:items/empty_armor_slot_helmet",
                    "minecraft:items/empty_armor_slot_shield",
                    "soi:items/empty_bag_slot",
                    "soi:items/empty_bag_slot",
                    "soi:items/empty_bag_slot"
            };
}