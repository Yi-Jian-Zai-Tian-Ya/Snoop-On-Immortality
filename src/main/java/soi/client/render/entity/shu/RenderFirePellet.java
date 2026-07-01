package soi.client.render.entity.shu;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.SOI;
import soi.common.entity.shu.EntityFirePellet;
import soi.client.model.entity.shu.ModelFirePellet;

@SideOnly(Side.CLIENT)
public class RenderFirePellet extends Render<EntityFirePellet>
{
    private final ModelFirePellet model;
    private final float scale;

    public RenderFirePellet(RenderManager manager, ModelFirePellet model, float scale)
    {
        super(manager);
        this.model = model;
        this.scale = scale;
    }

    @Override
    public void doRender(EntityFirePellet entity, double x, double y, double z, float yaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(scale, scale, scale);
        bindEntityTexture(entity);
        model.render(entity, 0, 0, 0, 0, 0, 0.0625F);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFirePellet entity)
    {
        return new ResourceLocation(SOI.MODID, "textures/entity/dao_shu/fire_pellet.png");
    }
}