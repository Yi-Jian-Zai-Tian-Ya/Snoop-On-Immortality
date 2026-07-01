package soi.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRune extends ModelBase
{
	private final ModelRenderer main;

	public ModelRune()
	{
		this.textureWidth = 188;
		this.textureHeight = 256;

		this.main = new ModelRenderer(this, 0, 0);
		this.main.addBox(-47, -128, 0, 94, 256, 0);
		this.main.rotateAngleX = (float) Math.toRadians(180F);
	}

	@Override public void render(Entity entityIn, float a, float b, float c, float d, float e, float scale) { main.render(scale); }
}