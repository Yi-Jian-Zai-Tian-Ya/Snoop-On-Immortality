/*
  Source from TinyInv
 */

package soi.client.render.player.inv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderUtils
{
    public static final ResourceLocation PATH = new ResourceLocation("textures/gui/widgets.png");

    public static void renderHotbar(ScaledResolution sr, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();
        float zLevel = 0;

        if (mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(PATH);
            EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
            ItemStack itemstack = entityplayer.getHeldItemOffhand();
            EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
            int i = sr.getScaledWidth() / 2;
            float f = zLevel;
            zLevel = -90.0F;

            int slots = 9;
            int width = 20 * slots + 2;
            int x = i - width / 2;

            for (int l = 0; l < slots; ++l)
            {
                int w = (l == 0 || l == slots - 1) ? 21 : 20;
                drawTexturedModalRect(x, sr.getScaledHeight() - 22, l == 0 ? 0 : (l == slots - 1 ? 161 : 21 + 20 * (l - 1)), 0, w, 22, zLevel);
                x += w;
            }

            drawTexturedModalRect(i - 92 + entityplayer.inventory.currentItem * 20 - (slots - 9) * 10, sr.getScaledHeight() - 23, 0, 22, 24, 22, zLevel);

            if (!itemstack.isEmpty())
            {
                if (enumhandside == EnumHandSide.LEFT)
                {
                    drawTexturedModalRect(i - 120, sr.getScaledHeight() - 23, 24, 22, 29, 24, zLevel);
                }
                else
                {
                    drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23, 53, 22, 29, 24, zLevel);
                }
            }

            zLevel = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (int l = 0; l < slots; ++l)
            {
                int i1 = i - 88 + l * 20 - (slots - 9) * 10;
                int j1 = sr.getScaledHeight() - 19;
                renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l), mc);
            }

            if (!itemstack.isEmpty())
            {
                int l1 = sr.getScaledHeight() - 19;

                if (enumhandside == EnumHandSide.LEFT)
                {
                    renderHotbarItem(i - 117, l1, partialTicks, entityplayer, itemstack, mc);
                }
                else
                {
                    renderHotbarItem(i + 101, l1, partialTicks, entityplayer, itemstack, mc);
                }
            }

            if (mc.gameSettings.attackIndicator == 2)
            {
                float f1 = mc.player.getCooledAttackStrength(0.0F);

                if (f1 < 1.0F)
                {
                    int i2 =sr.getScaledHeight() - 20;
                    int j2 = i + 97;

                    if (enumhandside == EnumHandSide.RIGHT)
                    {
                        j2 = i - 113;
                    }

                    mc.getTextureManager().bindTexture(Gui.ICONS);
                    int k1 = (int) (f1 * 19.0F);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    drawTexturedModalRect(j2, i2, 0, 94, 18, 18, zLevel);
                    drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1, zLevel);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public static void renderHotbarItem(int a, int b, float c, EntityPlayer player, ItemStack stack, Minecraft mc)
    {
        if (!stack.isEmpty())
        {
            float f = (float) stack.getAnimationsToGo() - c;

            if (f > 0.0F)
            {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float) (a + 8), (float) (b + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float) (-(a + 8)), (float) (-(b + 12)), 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(player, stack, a, b);

            if (f > 0.0F)
            {
                GlStateManager.popMatrix();
            }

            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, a, b);
        }
    }

    public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, double width, double height, double imageWidth, double imageHeight, double scale, double zLevel)
    {
        GL11.glPushMatrix();
        GlStateManager.color(1, 1, 1, 255.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        double minU = (double) u / (double) imageWidth;
        double maxU = (double) (u + width) / (double) imageWidth;
        double minV = (double) v / (double) imageHeight;
        double maxV = (double) (v + height) / (double) imageHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x + scale * (double) width, y + scale * (double) height, zLevel).tex(maxU, maxV).endVertex();
        worldrenderer.pos(x + scale * (double) width, y, zLevel).tex(maxU, minV).endVertex();
        worldrenderer.pos(x, y, zLevel).tex(minU, minV).endVertex();
        worldrenderer.pos(x, y + scale * (double) height, zLevel).tex(minU, maxV).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, double zLevel)
    {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) x, (double) (y + height), (double) zLevel).tex((double) ((float) textureX * f), (double) ((float) (textureY + height) * f)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) y, (double) zLevel).tex((double) ((float) (textureX + width) * f), (double) ((float) textureY * f)).endVertex();
        bufferbuilder.pos((double) x, (double) y, (double) zLevel).tex((double) ((float) textureX * f), (double) ((float) textureY * f)).endVertex();
        tessellator.draw();
    }

    public static void drawColoredRect(int color, double x, double y, double width, double height, double zLevel)
    {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        GL11.glPopMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) x, (double) y + height, zLevel).color(r, g, b, 255).endVertex();
        worldrenderer.pos((double) x + width, (double) y + height, zLevel).color(r, g, b, 255).endVertex();
        worldrenderer.pos((double) x + width, (double) y, zLevel).color(r, g, b, 255).endVertex();
        worldrenderer.pos((double) x, (double) y, zLevel).color(r, g, b, 255).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GL11.glPushMatrix();
    }

    public static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines)
            {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth)
                {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if ( tooltipX + tooltipTextWidth + 4 > screenWidth)
            {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4)
                {
                    if (mouseX > screenWidth / 2)
                    {
                        tooltipTextWidth = mouseX - 20;
                    }
                    else
                    {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
            {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap)
            {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0)
                    {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine)
                    {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth)
                        {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                }
                else
                {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1)
            {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount)
                {
                    tooltipHeight += 2;
                }
            }

            if (tooltipY < 4)
            {
                tooltipY = 4;
            }
            else
            {
                if (tooltipY + tooltipHeight + 4 > screenHeight)
                {
                    tooltipY = screenHeight - tooltipHeight - 4;
                }
            }

            final int zLevel = 300;
            int color = 0xF0100010;
            int start = 0x505000FF;
            int end = (start & 0xFEFEFE) >> 1 | start & 0xFF000000;
            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY, font, color, start, end);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            color = colorEvent.getBackground();
            start = colorEvent.getBorderStart();
            end = colorEvent.getBorderEnd();
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, color, color);
            GuiUtils.drawGradientRect(zLevel, tooltipX -3, tooltipY +  tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, color, color);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY -3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, color, color);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, color, color);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, color, color);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 2, tooltipX - 2, tooltipY + tooltipHeight + 2, start, end);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 2, start, end);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 2, start, start);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, end, end);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount)
                {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
}