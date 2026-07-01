package soi.common.item.dao_zai.gong_fa;

public class ItemZhenYangJue extends ItemGongFaBase
{
    public static final ItemZhenYangJue ZHEN_YANG = new ItemZhenYangJue();

    public ItemZhenYangJue()
    {
        super();
        this.LingGen = "火";
        this.LingLi = 12.0F;
        this.HuDun = 1.0F;
        this.ShenShi = 3.0F;
        this.MaxJingJie = 26;
    }
}