/*
  Source from Baubles
 */

package soi.common.inventory.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import soi.api.dao_zai.IDaoZai;
import soi.api.dao_zai.cap.*;

public class SlotDao extends SlotItemHandler
{
    int DaoSlot;
    EntityPlayer player;
    int type;

    public SlotDao(EntityPlayer player, IDaoZaiItemHandler itemHandler, int slot, int par4, int par5, int type)
    {
        super(itemHandler, slot, par4, par5);
        this.DaoSlot = slot;
        this.player = player;
        this.type = type;
    }

    @Override public boolean isItemValid(ItemStack stack) { return ((IDaoZaiItemHandler)getItemHandler()).isItemValidForSlot(DaoSlot, stack, player); }

    @Override
    public String getSlotTexture()
    {
        switch (type)
        {
            case 0 : return "soi:items/empty_bag_slot";
            case 1 : return "soi:items/empty_gong_fa_slot";
            case 2 : return "soi:items/empty_fa_bao_slot";
        }
        return "soi:items/empty_bag_slot";
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        ItemStack stack = getStack();
        if (stack.isEmpty()) return false;


        IDaoZai Dao = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
        return Dao.canUnEquip(stack, player);
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack)
    {
        if (!getHasStack() && stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
            stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null).onUnequipped(stack, player);
        super.onTake(player, stack);
        return stack;
    }

    @Override
    public void putStack(ItemStack stack)
    {
        if (getHasStack() && !ItemStack.areItemStacksEqual(stack, getStack()) &&
                getStack().hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
            getStack().getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null).onUnequipped(getStack(), player);

        ItemStack oldStack = getStack().copy();
        super.putStack(stack);

        if (getHasStack() && !ItemStack.areItemStacksEqual(oldStack, getStack()) &&
                getStack().hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
            getStack().getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null).onEquipped(getStack(), player);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}