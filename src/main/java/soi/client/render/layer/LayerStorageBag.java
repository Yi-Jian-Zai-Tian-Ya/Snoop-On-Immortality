package soi.client.render.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.SOI;
import soi.api.dao_zai.IRenderDaoZai;
import soi.client.model.layer.ModelStorageBag;

@SideOnly(Side.CLIENT)
public class LayerStorageBag implements IRenderDaoZai
{
    public static final LayerStorageBag INST = new LayerStorageBag();
    private static final float[][] SLOT_OFFSETS = { {0.1F, 2.5F}, {0.18F, 2.6F}, {0.26F, 2.55F} };
    private static final String[] TEXTURE_NAMES = {"inferior", "moderate", "excellent", "chaotic", "leather"};

    private ResourceLocation getTextureLoc(int meta)
    {
        String type = meta >= 0 && meta < TEXTURE_NAMES.length ? TEXTURE_NAMES[meta] : TEXTURE_NAMES[0];
        return new ResourceLocation(SOI.MODID, "textures/items/storage_bag/" + type + ".png");
    }

    @Override
    public void onEntityDaoZaiRender(ItemStack stack, EntityLivingBase entity, RenderType type, float partialTicks, int index, float zOffset)
    {
        if (type != RenderType.BODY) return;

        GlStateManager.pushMatrix();

        if (entity instanceof EntityPlayer) Helper.rotateIfSneaking((EntityPlayer) entity);
        Helper.translateToChest();

        float[] offset = SLOT_OFFSETS[index];
        GlStateManager.translate(offset[0], offset[1], 0.985F + zOffset);

        ResourceLocation texture = getTextureLoc(stack.getMetadata());
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        manager.bindTexture(texture);

        GlStateManager.disableCull();
        ModelStorageBag.INST.render(entity, 0, 0, 0, 0, 0, 0.015F);
        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }
}