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

public class PotionLingGuang extends Potion
{
    public static final Potion LING_GUANG = new PotionLingGuang();
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/gui/potions.png");

    public PotionLingGuang()
    {
        super(false, 0xCA962E);
        setBeneficial();
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
        Gui.drawModalRectWithCustomSizedTexture(x, y, 18, 0, 18, 18, 256, 256);
        GlStateManager.popMatrix();
    }
}