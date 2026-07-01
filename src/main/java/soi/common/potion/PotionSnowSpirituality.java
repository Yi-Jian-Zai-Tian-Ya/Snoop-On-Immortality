package soi.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.SOI;

public class PotionSnowSpirituality extends Potion
{
    public static final Potion SNOW_SPIRITUALITY = new PotionSnowSpirituality();
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/gui/potions.png");

    public PotionSnowSpirituality()
    {
        super(false, 0xFBFFFF);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        renderIcon(x + 6, y + 7, mc);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
    {
        renderIcon(x + 3, y + 3, mc);
    }

    @SideOnly(Side.CLIENT)
    private void renderIcon(int x, int y, Minecraft mc)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(TEXTURE);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 36, 18, 18, 18, 256, 256);
        GlStateManager.popMatrix();
    }
}