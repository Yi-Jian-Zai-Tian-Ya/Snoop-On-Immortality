package soi.client.render.entity.rune;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import soi.client.model.entity.ModelRune;
import soi.common.entity.rune.EntityRuneBase;

public abstract class RenderRuneBase<T extends EntityRuneBase> extends Render<T>
{
    protected final ModelRune model;

    public RenderRuneBase(RenderManager manager)
    {
        super(manager);
        this.model = new ModelRune();
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float yaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.48D, z);

        float smoothYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float smoothPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(-smoothYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(smoothPitch, 1.0F, 0.0F, 0.0F);

        this.bindTexture(getRuneTexture());
        model.render(entity, 0F, 0F, partialTicks, 0F, 0F, 0.00375F);
        GlStateManager.popMatrix();
    }

    protected abstract ResourceLocation getRuneTexture();
}