package soi.common.item.ordinary;

import net.minecraft.item.Item;

import soi.SOI;

public class ItemIronEssence extends Item
{
    public static final Item IRON_ESSENCE = new ItemIronEssence();

    public ItemIronEssence()
    {
        super();
        this.maxStackSize = 1;
        this.setCreativeTab(SOI.TAB);
    }
}