package soi.client.render.entity.rune;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import soi.SOI;
import soi.client.model.entity.ModelRuneHorizontal;
import soi.common.entity.rune.EntityRuneHorizontal;

import javax.annotation.Nullable;

public class RenderRuneHorizontal extends Render<EntityRuneHorizontal>
{
    protected final ModelRuneHorizontal model;
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/entity/rune/teleport_voice_rune.png");

    public RenderRuneHorizontal(RenderManager manager)
    {
        super(manager);
        this.model = new ModelRuneHorizontal();
    }

    @Override
    public void doRender(EntityRuneHorizontal entity, double x, double y, double z, float yaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.1D, z);

        float smoothYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float smoothPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(-smoothYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(smoothPitch, 1.0F, 0.0F, 0.0F);

        this.bindTexture(TEXTURE);
        model.render(entity, 0F, 0F, partialTicks, 0F, 0F, 0.00375F);
        GlStateManager.popMatrix();
    }

    @Nullable @Override protected ResourceLocation getEntityTexture(EntityRuneHorizontal entity) { return TEXTURE; }
}