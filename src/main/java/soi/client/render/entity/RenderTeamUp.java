package soi.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import soi.SOI;
import soi.common.entity.EntityTeamUp;
import soi.client.model.entity.ModelTeamUp;

import javax.annotation.Nullable;

public class RenderTeamUp extends Render<EntityTeamUp>
{
    private final ModelTeamUp model;
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/entity/team_up.png");

    public RenderTeamUp(RenderManager renderManager, ModelTeamUp model) { super(renderManager); this.model = model; }

    @Override
    public void doRender(EntityTeamUp entity, double x, double y, double z, float yaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        bindEntityTexture(entity);
        model.render(entity, 0, 0, 0, 0, 0, 0.0625F);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTeamUp entity) { return TEXTURE; }
}