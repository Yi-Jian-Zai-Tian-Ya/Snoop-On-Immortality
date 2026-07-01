/*
  Source from Iron Backpacks - 源自特殊背包(Iron Backpacks)
 */

package soi.client.gui.storage_bag;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import soi.SOI;
import soi.common.inventory.storage_bag.ContainerStorageBag;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GuiStorageBag extends GuiContainer
{
    private static final List<ResourceLocation> STORAGE_BAG_TEXTURES = Arrays.asList(
            new ResourceLocation(SOI.MODID, "textures/gui/bag/inferior.png"),
            new ResourceLocation(SOI.MODID, "textures/gui/bag/moderate.png"),
            new ResourceLocation(SOI.MODID, "textures/gui/bag/excellent.png"),
            new ResourceLocation(SOI.MODID, "textures/gui/bag/chaotic.png"),
            new ResourceLocation(SOI.MODID, "textures/gui/bag/leather.png")
    );

    @Nonnull private final ContainerStorageBag container;

    public GuiStorageBag(@Nonnull ContainerStorageBag container)
    {
        super(container);

        this.container = container;
        xSize = this.container.getWidth();
        ySize = this.container.getHeight();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString(container.getName(), 7 + container.getBagX(), 7 - getFontY(), getFontColor());
        fontRenderer.drawString(I18n.format("container.inventory"),  7 + container.getHotbarX(), ySize - 36 - getFontY(), getFontColor());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float a, int b, int c)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(STORAGE_BAG_TEXTURES.get(container.getTexture()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    public int getFontY()
    {
        if (container.getTexture() == 2) return 1;
        return 0;
    }

    public int getFontColor()
    {
        switch (container.getTexture())
        {
            case 0 : return 0x001822;
            case 1 : return 0x095A4B;
            case 2 : return 0x576351;
            case 3 : return 0x330C0D;
            case 4 : return 0x542716;
        }
        return 0x404040;
    }
}