package soi.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRuneHorizontal extends ModelBase
{
	private final ModelRenderer main;

	public ModelRuneHorizontal() {
		textureWidth = 188;
		textureHeight = 256;

		main = new ModelRenderer(this, -256, 0);
		main.addBox( -47.0F, 0.0F, -128.0F, 94, 0, 256);
		this.main.rotateAngleZ = (float) Math.toRadians(180F);
	}

	@Override
	public void render(Entity entity, float a, float b, float c, float d, float e, float scale) { main.render(scale); }
}