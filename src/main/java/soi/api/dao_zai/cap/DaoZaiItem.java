/*
  Source from Baubles
 */

package soi.api.dao_zai.cap;

import net.minecraft.item.ItemStack;

import soi.api.dao_zai.*;

public class DaoZaiItem implements IDaoZai
{
    private DaoZaiType DaoType;
    public DaoZaiItem(DaoZaiType type) { DaoType = type; }
    @Override public DaoZaiType getDaoType(ItemStack stack) { return DaoType; }
}