package soi.common.inventory.corpse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSlot extends Slot
{
    private boolean editable;
    private int index;

    public ContainerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
        this.index = index;
    }

    public ContainerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean editable)
    {
        super(inventoryIn, index, xPosition, yPosition);
        this.editable = editable;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return editable ? super.canTakeStack(playerIn) : false;
    }
}