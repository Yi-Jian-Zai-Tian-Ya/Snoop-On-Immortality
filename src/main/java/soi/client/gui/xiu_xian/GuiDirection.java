package soi.client.gui.xiu_xian;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.SOI;
import soi.SOIConfig;
import soi.util.GuiUtils;

@Mod.EventBusSubscriber(modid = "soi", value = Side.CLIENT)
public class GuiDirection
{
    private static final ResourceLocation Direction = new ResourceLocation(SOI.MODID, "textures/gui/direction.png");
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderUI(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.getType() != ElementType.HOTBAR) return;

        int width = event.getResolution().getScaledWidth();
        int LocX = width / 2;

        if (ElementType.HOTBAR.equals(event.getType()))
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            mc.getTextureManager().bindTexture(Direction);

            float RotY = MathHelper.wrapDegrees(mc.player.rotationYawHead);
            float scale = SOIConfig.UI.DirectionBarScale;
            int u = (int) ((RotY + 180) * 42 / 30) + 252;

            GuiUtils.drawScaledCustomSizeModalRect(LocX, 0, u, 0, 252, 48, 252.0F * scale, 48.0F * scale, 1024, 1024);
            GuiUtils.drawScaledCustomSizeModalRect(LocX - (252.0F * scale), 0, u - 252, 0, 252, 48, 252.0F * scale, 48.0F * scale, 1024, 1024);
            GuiUtils.drawScaledCustomSizeModalRect(LocX - (11.0F * scale), 0, 0, 48, 22, 64, 22.0F * scale, 64.0F * scale, 1024, 1024);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}