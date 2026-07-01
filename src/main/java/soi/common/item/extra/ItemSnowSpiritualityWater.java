package soi.common.item.extra;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import static soi.common.item.ordinary.ItemJadeBowl.JADE_BOWL;
import static soi.common.potion.PotionSnowSpirituality.SNOW_SPIRITUALITY;

public class ItemSnowSpiritualityWater extends ItemFood
{
    public static final ItemFood SNOW_SPIRITUALITY_WATER = new ItemSnowSpiritualityWater(0, 0.0f, false);

    public ItemSnowSpiritualityWater(int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, isWolfFood);
        this.maxStackSize = 1;
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabs.FOOD);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) { return 60; }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) { return EnumAction.DRINK; }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity)
    {
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;

        if (!worldIn.isRemote) entity.addPotionEffect(new PotionEffect(SNOW_SPIRITUALITY, 3600, 0, true, true));
        ItemStack result = super.onItemUseFinish(stack, worldIn, entity);

        if (player != null)
        {
            ItemStack bowl = new ItemStack(JADE_BOWL);
            if (!player.inventory.addItemStackToInventory(bowl)) player.dropItem(bowl, false);
        }

        return result;
    }
}