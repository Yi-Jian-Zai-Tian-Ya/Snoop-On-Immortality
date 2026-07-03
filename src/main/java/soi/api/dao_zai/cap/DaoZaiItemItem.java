package soi.api.dao_zai.cap;

import net.minecraft.item.ItemStack;

public class DaoZaiItemItem implements IDaoZaiItem
{
    private final DaoZaiType type;

    public DaoZaiItemItem() { this.type = DaoZaiType.Bag; }
    public DaoZaiItemItem(DaoZaiType type) { this.type = type; }

    @Override public DaoZaiType getType(ItemStack stack) { return type; }
}