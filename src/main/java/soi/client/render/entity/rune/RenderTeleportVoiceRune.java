package soi.client.render.entity.rune;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import soi.SOI;
import soi.common.entity.rune.EntityTeleportVoiceRune;

import javax.annotation.Nullable;

public class RenderTeleportVoiceRune extends RenderRuneBase<EntityTeleportVoiceRune>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/entity/rune/teleport_voice_rune.png");
    public RenderTeleportVoiceRune(RenderManager renderManager) { super(renderManager); }

    @Nullable @Override protected ResourceLocation getEntityTexture(EntityTeleportVoiceRune entity) { return TEXTURE; }
    @Override protected ResourceLocation getRuneTexture() { return TEXTURE; }
}