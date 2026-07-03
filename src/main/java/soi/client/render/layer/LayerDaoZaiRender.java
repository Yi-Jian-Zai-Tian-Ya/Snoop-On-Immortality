package soi.client.render.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import soi.api.dao_zai.IRenderDaoZai;
import soi.api.dao_zai.APIDaoZai;
import soi.api.dao_zai.cap.DaoZaiInv;
import soi.api.dao_zai.cap.DaoZaiType;
import soi.common.item.dao_zai.ItemStorageBag;

public class LayerDaoZaiRender<T extends EntityLivingBase> implements LayerRenderer<T>
{
    private final RenderLivingBase<T> renderer;
    private final float zOffset;
    private static final LayerStorageBag BAG_RENDER = LayerStorageBag.INST;

    public LayerDaoZaiRender(RenderLivingBase<T> renderer) { this.renderer = renderer; this.zOffset = 0.0F; }
    public LayerDaoZaiRender(RenderLivingBase<T> renderer, float zOffset) { this.renderer = renderer; this.zOffset = zOffset; }

    @Override
    public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        DaoZaiInv inv = APIDaoZai.getDaoZaiInv(entity).getInv(DaoZaiType.Bag);

        for (int i = 0; i < inv.getSlots(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemStorageBag) BAG_RENDER.onEntityDaoZaiRender(stack, entity, IRenderDaoZai.RenderType.BODY, partialTicks, i, zOffset);
        }
    }

    @Override public boolean shouldCombineTextures() { return false; }
}