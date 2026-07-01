package soi.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.SOI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PotionFanRen extends Potion
{
    public static final Potion FAN_REN = new PotionFanRen();
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/gui/potions.png");

    private static final Potion Slowness = Potion.getPotionById(2);
    private static final Potion MiningFatigue = Potion.getPotionById(4);
    private static final Potion Weakness = Potion.getPotionById(18);

    public PotionFanRen() { super(true, 0xA2B2B2); }
    @SideOnly(Side.CLIENT) @Override public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) { renderIcon(x + 6, y + 7, mc); }
    @SideOnly(Side.CLIENT) @Override public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) { renderIcon(x + 3, y + 3, mc); }

    @SideOnly(Side.CLIENT)
    private void renderIcon(int x, int y, Minecraft mc)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(TEXTURE);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 18, 18, 256, 256);
        GlStateManager.popMatrix();
    }

    @Override public List<ItemStack> getCurativeItems() { return new ArrayList<ItemStack>(); }
    @Override public boolean isReady(int duration, int amplifier) { return true; }
    @Override public void performEffect(EntityLivingBase player, int amplifier) { if (player.ticksExisted % 20 == 0) applyDeBuffs((EntityPlayer) player, amplifier); }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase player, AbstractAttributeMap attributeMapIn, int amplifier)
    {
        super.applyAttributesModifiersToEntity(player, attributeMapIn, amplifier);
        if (!(player instanceof EntityPlayer)) return; applyDeBuffs((EntityPlayer) player, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase player, AbstractAttributeMap attributeMapIn, int amplifier)
    {
        if (!(player instanceof EntityPlayer)) return;
        if (player.isPotionActive(Slowness)) player.removePotionEffect(Slowness);
        if (player.isPotionActive(MiningFatigue)) player.removePotionEffect(MiningFatigue);
        if (player.isPotionActive(Weakness)) player.removePotionEffect(Weakness);
        super.removeAttributesModifiersFromEntity(player, attributeMapIn, amplifier);
    }

    private static void applyDeBuffs(EntityPlayer player, int amplifier)
    {
        if (!player.isPotionActive(Slowness)) player.addPotionEffect(new PotionEffect(Slowness, Integer.MAX_VALUE, Math.max(0, amplifier - 2), false, false));
        if (!player.isPotionActive(MiningFatigue)) player.addPotionEffect(new PotionEffect(MiningFatigue, Integer.MAX_VALUE, amplifier + 1, false, false));
        if (!player.isPotionActive(Weakness)) player.addPotionEffect(new PotionEffect(Weakness, Integer.MAX_VALUE, Math.max(0, amplifier - 1), false, false));
    }
}