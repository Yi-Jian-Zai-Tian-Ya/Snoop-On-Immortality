/*
  Source from Baubles
 */

package soi.api.dao_zai.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IDaoZaiItemHandler extends IItemHandlerModifiable
{
    public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase entity);

    boolean isChanged(int slot);
    void setChanged(int slot, boolean changed);

    public void setEntity(EntityLivingBase entity);
}