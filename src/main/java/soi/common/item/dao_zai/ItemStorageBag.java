package soi.common.item.dao_zai;

import com.google.common.collect.Lists;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import soi.SOI;
import soi.api.dao_zai.*;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;
import soi.api.dao_zai.storage_bag.*;
import soi.common.CommonProxy;
import soi.util.LocUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class ItemStorageBag extends Item implements IDaoZai, IStorageBag
{
    public static final Item STORAGE_BAG = new ItemStorageBag();

    public ItemStorageBag()
    {
        super();
        this.maxStackSize = 1;
        this.setHasSubtypes(true);
        this.setCreativeTab(SOI.ZAI);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        StorageBagInfo info = getStorageBagInfo(stack);
        return super.getUnlocalizedName(stack) + info.getType().getType();
    }

    /*
      Source from Baubles
        DaoShuStorage Bag
     */
    @Override public DaoZaiType getDaoType(ItemStack stack) { return DaoZaiType.BAG; }
    @Override public boolean hasEffect(ItemStack stack) { return stack.getMetadata() != 4; }
    @Override public void onEquipped(ItemStack stack, EntityLivingBase entity) { entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 2F); }
    @Override public void onUnequipped(ItemStack stack, EntityLivingBase entity) { entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 1.9F); }
    /*
      Source from Iron Backpacks
     */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (!this.isInCreativeTab(tab)) return;

        List<StorageBagType> types = Lists.newArrayList(StorageBagAPI.getStorageBagTypes());
        types.sort(Comparator.comparingInt(StorageBagType::getMeta));

        for (StorageBagType type : types) { subItems.add(StorageBagAPI.getStack(type)); }
    }

    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) { return slotChanged; }

    /*
      Source from Tinkers' Construct
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (I18n.canTranslate(getUnlocalizedName(stack) + ".tooltip")) tooltip.addAll(LocUtils.getTooltips(TextFormatting.YELLOW + LocUtils.translateRecursive(getUnlocalizedName(stack) + ".tooltip")));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack held = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<ItemStack>(EnumActionResult.FAIL, held);

        IDaoZaiItemHandler bag = DaoZaiAPI.getDaoZaiHandler(player);

        if (player.isSneaking()) for (int i = 0; i < 3; i++)
        {
            if (bag.getStackInSlot(i).isEmpty() && bag.isItemValidForSlot(i, held, player))
            {
                bag.setStackInSlot(i, held.copy());
                if (!player.isCreative()) player.setHeldItem(hand, ItemStack.EMPTY);
                world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.PLAYERS, .75F, 2F);
                this.onEquipped(held, player); break;
            }
            else if (i == 2) player.openGui(SOI.instance, CommonProxy.GUI_STORAGE_BAG, world, hand == EnumHand.OFF_HAND ? 1 : 0, -1, 0);
        }
        else player.openGui(SOI.instance, CommonProxy.GUI_STORAGE_BAG, world, hand == EnumHand.OFF_HAND ? 1 : 0, -1, 0);

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
    }
}