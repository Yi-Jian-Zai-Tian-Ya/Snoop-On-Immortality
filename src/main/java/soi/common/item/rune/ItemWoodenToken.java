package soi.common.item.rune;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import soi.SOI;
import soi.api.token.IToken;
import soi.api.token.TokenProvider;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.common.sound.AllSound;
import soi.util.LocUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemWoodenToken extends Item
{
    public static final Item WOODEN_TOKEN = new ItemWoodenToken();
    public static final List<Item> sword = Arrays.asList
            (
                    Items.WOODEN_SWORD,
                    Items.STONE_SWORD,
                    Items.IRON_SWORD,
                    Items.GOLDEN_SWORD,
                    Items.DIAMOND_SWORD
            );

    public ItemWoodenToken()
    {
        super();
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setCreativeTab(SOI.RUNE);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int metadata = stack.getMetadata();
        String tokenName = getTokenName(metadata);
        return super.getUnlocalizedName(stack) + "." + tokenName;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return stack.getMetadata() != 0;
    }

    public String getTokenName(int metadata)
    {
        switch (metadata)
        {
            case 0:
                return "unCarved";
            case 1:
                return "carved";
            default:
                return "";
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (isInCreativeTab(tab))
        {
            subItems.add(new ItemStack(this, 1, 0));
            subItems.add(new ItemStack(this, 1, 1));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack held = player.getHeldItem(hand);
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        if (!world.isRemote)
        {
            if (held.getMetadata() == 0)
            {
                if (mainHand.getItem() instanceof ItemSword || offHand.getItem() instanceof ItemSword)
                {
                    ItemStack sword = (mainHand.getItem() instanceof ItemSword) ? mainHand : offHand;
                    if (sword.getMaxDamage() - 5 >= sword.getItemDamage() || !sword.getItem().isDamageable() || player.isCreative())
                    {
                        if (sword.getItem().isDamageable() && !player.isCreative()) sword.setItemDamage(sword.getItemDamage() + 5);
                        world.playSound(null, player.getPosition(), AllSound.SWORD_CARVE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.inventory.addItemStackToInventory(new ItemStack(WOODEN_TOKEN, 1, 1));
                        held.setCount(held.getCount() - 1);
                    }
                    else player.sendMessage(new TextComponentTranslation("message.token.notDamage"));
                }
                else player.sendMessage(new TextComponentTranslation("message.token.notSword"));
            }
            if (held.getMetadata() == 1)
            {
                IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
                if (XiuXian == null) return new ActionResult<>(EnumActionResult.PASS, held);

                if (held.getTagCompound() == null)
                {
                    if (XiuXian.getLingLi() < 5.0D * held.getCount() && !player.isCreative())
                    {
                        Minecraft.getMinecraft().ingameGUI.setOverlayMessage(new TextComponentTranslation("message.ling_li.exhausted"), false);
                        return new ActionResult<>(EnumActionResult.FAIL, held);
                    }
                    if (!player.isCreative()) { XiuXian.addLingLi(-5.0D  * held.getCount()); XiuXian.syncXiuXian((EntityPlayerMP) player); }

                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setString("playerName", player.getName());
                    held.setTagCompound(nbt);
                    return new ActionResult<>(EnumActionResult.SUCCESS, held);
                }

                IToken Token = player.getCapability(TokenProvider.TOKEN, null);
                if (Token == null) return new ActionResult<>(EnumActionResult.PASS, held);

                String target = held.getTagCompound().getString("playerName");

                if (target.equals(player.getName()))
                {
                    player.sendMessage(new TextComponentTranslation("message.token.namesake"));
                    return new ActionResult<>(EnumActionResult.SUCCESS, held);
                }

                if (Token.getTokens().containsValue(target))
                {
                    player.sendMessage(new TextComponentTranslation("message.token.alreadyFriend", target));
                    return new ActionResult<>(EnumActionResult.SUCCESS, held);
                }

                if (Token.addToken(target))
                {
                    Token.markDirty(player);
                    player.sendMessage(new TextComponentTranslation("message.token.identify", target));
                    held.shrink(1);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    /*
      Source from Tinkers' Construct
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (I18n.canTranslate(getUnlocalizedName(stack) + ".tooltip"))
        {
            if (stack.getMetadata() == 0) tooltip.addAll(LocUtils.getTooltips(LocUtils.translateRecursive(getUnlocalizedName(stack) + ".tooltip", TextFormatting.GOLD + getSwordName(sword))));
            else
            {
                if (stack.getTagCompound() != null) tooltip.add(TextFormatting.GRAY + LocUtils.translateRecursive(getUnlocalizedName(stack) + ".bind.tooltip", TextFormatting.YELLOW + stack.getTagCompound().getString("playerName")));
                else tooltip.add(LocUtils.translateRecursive(getUnlocalizedName(stack) + ".tooltip"));
            }
        }
    }

    public String getSwordName(List<Item> sword)
    {
        StringBuilder swordNames = new StringBuilder();
        for (Item item : sword) swordNames.append("\n-").append(item.getItemStackDisplayName(new ItemStack(item)));
        return swordNames.toString();
    }
}