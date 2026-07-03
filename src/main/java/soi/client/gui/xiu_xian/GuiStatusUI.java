package soi.client.gui.xiu_xian;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.SOI;
import soi.SOIConfig;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;

import soi.util.GuiUtils;

@Mod.EventBusSubscriber(modid = "soi", value = Side.CLIENT)
public class GuiStatusUI
{
    private static final ResourceLocation UI = new ResourceLocation(SOI.MODID, "textures/gui/status_ui.png");
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int LocX, LocY, cX, cY, width, height;

    private static final float ANIMATION_SPEED = 1.0f / 0.8f;
    private double lastLingLi;
    private double lastHealth;
    private double lastHuDun;
    private double lastFood;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderUI(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.getType() != ElementType.HOTBAR) return;

        LocX = 10; LocY = 10;
        width = event.getResolution().getScaledWidth();
        height = event.getResolution().getScaledHeight();
        cX = width / 2; cY = height / 2;

        EntityPlayer player = mc.player;
        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null) return;
        IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.PLAYER, null);
        if (XiuXianPlayer == null) return;

        double LingLi = XiuXian.getLingLi() / XiuXian.getMaxLingLi();
        double Health = player.getHealth() / player.getMaxHealth();
        double HuDun = XiuXian.getHuDun() / XiuXian.getMaxHuDun();
        double Food = player.getFoodStats().getFoodLevel() / 20.0D;
        double Focus = XiuXianPlayer.getFocusRange() / (XiuXian.getShenShi() / 4.0D);

        float delta = Minecraft.getMinecraft().getRenderPartialTicks();
        lastLingLi = updateAnimation(lastLingLi, LingLi, delta);
        lastHealth = updateAnimation(lastHealth, Health, delta);
        lastHuDun = updateAnimation(lastHuDun, HuDun, delta);
        lastFood = updateAnimation(lastFood, Food, delta);

        if (ElementType.HOTBAR.equals(event.getType()))
        {
            float scale = SOIConfig.UI.StatusUIScale;

            mc.fontRenderer.drawString((float) XiuXian.getLingLi() + "/" + XiuXian.getMaxLingLi(), 8, 54, 0xFFFFFF);
            mc.fontRenderer.drawString((float) XiuXian.getHuDun() + "/" + XiuXian.getMaxHuDun(), 8, 62, 0xFFFFFF);
            mc.fontRenderer.drawString((float) XiuXian.getXiuWei() + "/" + (float) XiuXian.getMaxXiuWei(), 8, 70, 0xFFFFFF);
            if (XiuXianPlayer.getFocusRange() > 0.0D) { mc.fontRenderer.drawString(String.format("%.2f", XiuXianPlayer.getFocusRange()), (int) (width - 82 * scale), (int) (cY + 80 * scale), 0xA660FF); GlStateManager.color(1.0F, 1.0F, 1.0F);}

            GlStateManager.enableBlend();
            mc.getTextureManager().bindTexture(UI);

            //绘制底板（不含饱食度）
            GuiUtils.drawScaledCustomSizeModalRect(LocX, LocY, 0, 0, 80, 80, 80 * scale, 80 * scale, 256f, 256f);

            //绘制灵力条
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 26 * scale, LocY + 26 * scale + (28 * scale) * (1.0 - lastLingLi),
                    224, 32 + 28 * (1.0 - lastLingLi), 28, 28 * lastLingLi,
                    28 * scale, (28 * scale) * lastLingLi, 256f, 256f);
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 26 * scale, LocY + 26 * scale + (28 * scale) * (1.0 - LingLi),
                    224, 28 * (1.0 - LingLi), 28, 28 * LingLi,
                    28 * scale, (28 * scale) * LingLi, 256f, 256f);

            //绘制生命栏
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 12 * scale, LocY + 10 * scale,
                    160, 48, 58 * lastHealth, 44,
                    58 * lastHealth * scale, 44 * scale, 256f, 256f);
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 12 * scale, LocY + 10 * scale,
                    160, 0, 58 * Health, 44,
                    58 * Health * scale, 44 * scale, 256f, 256f);

            //绘制护盾栏
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 10 * scale + (58 * scale) * (1.0 - lastHuDun), LocY + 26 * scale,
                    224 - 58 * lastHuDun, 148, 58 * lastHuDun, 44,
                    58 * scale * lastHuDun, 44 * scale, 256f, 256f);
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 10 * scale + (58 * scale) * (1.0 - HuDun), LocY + 26 * scale,
                    224 - 58 * HuDun, 100, 58 * HuDun, 44,
                    58 * scale * HuDun, 44 * scale, 256f, 256f);
            //

            //绘制饱食栏
            GuiUtils.drawScaledCustomSizeModalRect(LocX, LocY, 80, 0, 80, 80, 80 * scale, 80 * scale, 256f, 256f);
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 2 * scale, LocY + 2 * scale + (76 * scale) * (1.0 - lastFood),
                    80, 80 + 76 * (1.0 - lastFood), 76, 76 * lastFood,
                    76 * scale, (76 * scale) * lastFood, 256f, 256f);
            GuiUtils.drawScaledCustomSizeModalRect(LocX + 2 * scale, LocY + 2 * scale + (76 * scale) * (1.0 - Food),
                    0, 80 + 76 * (1.0 - Food), 76, 76 * Food,
                    76 * scale, (76 * scale) * Food, 256f, 256f);


            if (XiuXianPlayer.getProbeTick() > 0 && XiuXianPlayer.getProbeTick() <= 200)
            {
                float offsetX = SOIConfig.UI.ShenShiUIOffsetX; float offsetY = SOIConfig.UI.ShenShiUIOffsetY;
                if (XiuXianPlayer.getProbeTick() < 200) GlStateManager.color(1.0F, 1.0F, 1.0F, XiuXianPlayer.getProbeTick() / 200.0F);
                GuiUtils.drawScaledCustomSizeModalRect(cX + offsetX * scale, cY + offsetY * scale, XiuXianPlayer.getShenShiActive() ? 13 : 0, 156, 13, 13, 52 * scale, 52 * scale, 256f, 256f);
                if (XiuXianPlayer.getProbeTick() < 200) GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (XiuXianPlayer.getFocusTick() > 0)
            {
                GuiUtils.drawScaledCustomSizeModalRect(width - 82 * scale, cY - 80 * scale, 0, 176, 40, 80, 80 * scale, 160 * scale, 256f, 256f);
                GuiUtils.drawScaledCustomSizeModalRect(width - 78 * scale, cY - 76 * scale + (152 * scale) * (1.0 - Focus),
                        40, 176 + 76 * (1.0 - Focus), 37, 76 * Focus,
                        74 * scale, (152 * scale) * Focus, 256f, 256f);
            }

            GlStateManager.disableBlend();
        }
    }

    private double updateAnimation(double current, double target, float delta)
    {
        if (Double.isNaN(current) || Double.isNaN(target)) return 0.0;
        if (target > current) return target;
        if (current - target < 0.002D) return target;

        double step = (target - current) * ANIMATION_SPEED * delta * 0.05f;
        return current + step;
    }
}