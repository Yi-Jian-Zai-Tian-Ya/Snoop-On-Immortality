package soi.common.item.ban;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import soi.util.LocUtils;

public class ItemBanSlot extends Item
{
    public static final Item BAN_SLOT = new ItemBanSlot();

    public ItemBanSlot()
    {
        super();
        this.maxStackSize = 1;
    }

    /*
      Source from TinyInv
     */
    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        entityItem.setDead();
        return true;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (entityIn instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entityIn;
            if (!LocUtils.shouldBeRemoved(itemSlot, player)) player.inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
        }
    }
}