package soi.common.item.dao_zai.gong_fa;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemQingYuanSwordJue extends ItemGongFaBase
{
    public static final ItemQingYuanSwordJue QING_YUAN_SWORD = new ItemQingYuanSwordJue();

    public ItemQingYuanSwordJue()
    {
        super();
        this.LingGen = "金木";
        this.AnyLingGen = true;
        this.LingLi = 15.0F;
        this.HuDun = 2.5F;
        this.ShenShi = 5.0F;
        this.MaxJingJie = 13;
        this.hasSubtypes = true;
    }

    @Override
    public int getMaxJingJie(ItemStack stack)
    {
        switch (stack.getMetadata())
        {
            case 0 : return 22;
            case 1 : return 27;
            case 2 : return 35;
        }
        return this.MaxJingJie;
    }

    @Override public boolean hasEffect(ItemStack stack) { return stack.getMetadata() != 0; }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (!this.isInCreativeTab(tab)) return;

        for (int i = 0; i < 3; i++)
        {
            ItemStack stack = new ItemStack(this);
            stack.setItemDamage(i);
            subItems.add(stack);
        }
    }
}