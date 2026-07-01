package soi.common.item.ordinary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemSilverIngot extends Item
{
    public static final Item SILVER_INGOT = new ItemSilverIngot();

    public ItemSilverIngot()
    {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}