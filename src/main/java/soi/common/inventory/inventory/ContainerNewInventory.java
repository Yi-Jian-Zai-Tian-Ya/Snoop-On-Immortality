/*
  Source from Baubles
 */

package soi.common.inventory.inventory;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.api.dao_zai.IDaoZai;
import soi.api.dao_zai.cap.DaoZaiCapabilities;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;

import javax.annotation.Nullable;

public class ContainerNewInventory extends Container
{
    public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public boolean isLocalWorld;
    private final EntityPlayer player;
    public final IDaoZaiItemHandler dao;

    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[]
            {
                    EntityEquipmentSlot.HEAD,
                    EntityEquipmentSlot.CHEST,
                    EntityEquipmentSlot.LEGS,
                    EntityEquipmentSlot.FEET
            };

    public ContainerNewInventory(InventoryPlayer playerInv, boolean localWorld, EntityPlayer playerIn)
    {
        this.isLocalWorld = localWorld;
        this.player = playerIn;
        this.dao = player.getCapability(DaoZaiCapabilities.DAO_ZAI, null);

        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 39));

        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 2; ++j) this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 29 + i * 18));

        for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot slot = EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), 8, 19 + k * 18)
            {
                public int getSlotStackLimit() { return 1; }

                public boolean isItemValid(ItemStack stack) { return stack.getItem().isValidArmor(stack, slot, player); }

                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    ItemStack itemStack = this.getStack();
                    return (itemStack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeStack(playerIn);
                }

                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
                }
            });
        }

        this.addSlotToContainer(new SlotDao(player, dao, 0, 77, 19, 0));
        this.addSlotToContainer(new SlotDao(player, dao, 1, 77, 37, 0));
        this.addSlotToContainer(new SlotDao(player, dao, 2, 77, 55, 0));

        for (int i = 0; i < 9; ++i) this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 106));

        this.addSlotToContainer(new Slot(playerInv, 40, 77, 73)
        {
            @SideOnly(Side.CLIENT)
            public String getSlotTexture() { return "minecraft:items/empty_armor_slot_shield"; }
        });
    }

    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
    }

    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.craftResult.clear();

        if (!playerIn.world.isRemote) this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
    }

    public boolean canInteractWith(EntityPlayer playerIn) { return true; }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack()) return stack;

        ItemStack slotStack = slot.getStack();
        stack = slotStack.copy();
        EntityEquipmentSlot equipmentSlot = EntityLiving.getSlotForItemStack(stack);

        if (index == 0)
        {
            if (!this.mergeItemStack(slotStack, 12, 21, true)) return ItemStack.EMPTY;
            slot.onSlotChange(slotStack, stack);
        }
        else if (index < 12 || index == 21)
        {
            if (!this.mergeItemStack(slotStack, 12, 21, false)) return ItemStack.EMPTY;
        }
        else if (equipmentSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot) this.inventorySlots.get(8 - equipmentSlot.getIndex())).getHasStack())
        {
            int i = 8 - equipmentSlot.getIndex();
            if (!this.mergeItemStack(slotStack, i, i + 1, false)) return ItemStack.EMPTY;
        }
        else if (equipmentSlot == EntityEquipmentSlot.OFFHAND && !((Slot) this.inventorySlots.get(21)).getHasStack())
        {
            if (!this.mergeItemStack(slotStack, 21, 22, false)) return ItemStack.EMPTY;
        }
        else if (stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
        {
            IDaoZai Dao = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
            for (int bagSlot : Dao.getDaoType(stack).getValidSlots())
            {
                if (Dao.canEquip(slotStack, this.player) && !(this.inventorySlots.get(bagSlot + 9)).getHasStack() &&
                        !this.mergeItemStack(slotStack, bagSlot + 9, bagSlot + 10, false)) return ItemStack.EMPTY;
                if (slotStack.getCount() == 0) break;
            }
        }
        else if (index < 21) return ItemStack.EMPTY;
        else if (!this.mergeItemStack(slotStack, 12, 21, false)) return ItemStack.EMPTY;

        if (slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();
        if (slotStack.getCount() == stack.getCount()) return ItemStack.EMPTY;
        if (slotStack.isEmpty() && slot instanceof SlotDao && slotStack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
            stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null).onUnequipped(stack, player);

        ItemStack itemStack2 = slot.onTake(playerIn, slotStack);

        if (index == 0) playerIn.dropItem(itemStack2, false);

        return stack;
    }

    public boolean canMergeSlot(ItemStack stack, Slot slotIn) { return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn); }
}