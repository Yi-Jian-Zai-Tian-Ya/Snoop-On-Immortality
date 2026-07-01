package soi.client.model.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelStorageBag extends ModelBase
{
	private final ModelRenderer main;

	public ModelStorageBag()
    {
		this.textureWidth = 16;
		this.textureHeight = 16;

		this.main = new ModelRenderer(this);
        this.main.addBox(-8.0F, -8.0F, 0.0F, 16, 16, 0);
		this.main.rotateAngleX = (float) Math.toRadians(180F);
		this.main.rotateAngleY = (float) Math.toRadians(-5F);
		this.main.rotateAngleZ = (float) Math.toRadians(22.5F);
	}

	@Override public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { main.render(f5); }

    public static ModelStorageBag INST = new ModelStorageBag();

}