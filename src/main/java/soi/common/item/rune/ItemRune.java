package soi.common.item.rune;

import net.minecraft.item.Item;
import soi.SOI;

public class ItemRune extends Item
{
    public static final Item RUNE = new ItemRune();

    public ItemRune()
    {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(SOI.RUNE);
    }
}