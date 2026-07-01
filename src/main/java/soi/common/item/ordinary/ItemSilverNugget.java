package soi.common.item.ordinary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemSilverNugget extends Item
{
    public static final Item SILVER_NUGGET = new ItemSilverNugget();

    public ItemSilverNugget()
    {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}