package soi.common.item.ordinary;

import net.minecraft.item.Item;

import soi.SOI;

public class ItemSilverEssence extends Item
{
    public static final Item SILVER_ESSENCE = new ItemSilverEssence();

    public ItemSilverEssence()
    {
        super();
        this.maxStackSize = 1;
        this.setCreativeTab(SOI.TAB);
    }
}