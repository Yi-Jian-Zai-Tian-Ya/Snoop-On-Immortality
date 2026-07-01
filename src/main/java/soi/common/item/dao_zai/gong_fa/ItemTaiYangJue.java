package soi.common.item.dao_zai.gong_fa;

public class ItemTaiYangJue extends ItemGongFaBase
{
    public static final ItemTaiYangJue TAI_YANG = new ItemTaiYangJue();

    public ItemTaiYangJue()
    {
        super();
        this.LingGen = "火";
        this.LingLi = 12.0F;
        this.HuDun = 1.0F;
        this.ShenShi = 2.0F;
        this.MaxJingJie = 22;
    }
}