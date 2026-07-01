package soi.common.item.ordinary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemElectrum extends Item
{
    public static final Item ELECTRUM = new ItemElectrum();

    public ItemElectrum()
    {
        super();
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MISC);
    }
}