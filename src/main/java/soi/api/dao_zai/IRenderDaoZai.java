/*
  Source from Baubles
 */

package soi.api.dao_zai;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public interface IRenderDaoZai
{
    public void onEntityDaoZaiRender(ItemStack stack, EntityLivingBase entity, RenderType type, float partialTicks, int index, float zOffset);

    final class Helper
    {
        public static void rotateIfSneaking(EntityPlayer player) { if (player.isSneaking()) applySneakingRotation(); }

        public static void applySneakingRotation()
        {
            GlStateManager.translate(0F, 0.2F, 0F);
            GlStateManager.rotate(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
        }

        public static void translateToHeadLevel(EntityPlayer player)
        {
            GlStateManager.translate(0, -player.getDefaultEyeHeight(), 0);
            if (player.isSneaking()) GlStateManager.translate(0.25F * MathHelper.sin(player.rotationPitch * (float) Math.PI / 180), 0.25F * MathHelper.cos(player.rotationPitch * (float) Math.PI / 180), 0F);
        }

        public static void translateToFace()
        {
            GlStateManager.rotate(90F, 0F, 1F, 0F);
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            GlStateManager.translate(0f, -4.35f, -1.27f);
        }

        public static void defaultTransforms()
        {
            GlStateManager.translate(0.0, 3.0, 1.0);
            GlStateManager.scale(0.55, 0.55, 0.55);
        }

        public static void translateToChest()
        {
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            GlStateManager.translate(0F, -3.2F, -0.85F);
        }
    }

    public enum RenderType
    {
        BODY,
        HEAD;
    }
}