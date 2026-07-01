package soi.common.item.ordinary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemJade extends Item
{
    public static final Item JADE = new ItemJade();

    public ItemJade()
    {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}