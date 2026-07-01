package soi.common.item.ordinary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemJadeBowl extends Item
{
    public static final Item JADE_BOWL = new ItemJadeBowl();

    public ItemJadeBowl()
    {
        super();
        this.setMaxStackSize(8);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}