package soi.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTeamUp extends ModelBase
{
	private final ModelRenderer main;
	private final ModelRenderer armL1;
	private final ModelRenderer armL2;
	private final ModelRenderer armR1;
	private final ModelRenderer armR2;

	public ModelTeamUp()
	{
		textureWidth = 64;
		textureHeight = 32;

		main = new ModelRenderer(this);
		main.rotateAngleX = (float) Math.toRadians(180F);
		main.cubeList.add(new ModelBox(main, 40, 16, -3.5F, -12.0F, -1.5F, 3, 12, 3, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 40, 16, 0.5F, -12.0F, -1.5F, 3, 12, 3, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 16, 16, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 0, 0, -4.0F, -32.0F, -4.0F, 8, 8, 8, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 52, 0, -1.5F, -23.5F, -6.0F, 3, 3, 3, 0.0F, false));

		armL1 = new ModelRenderer(this);
		armL1.setRotationPoint(4.0F, -24.0F, -2.0F);
		main.addChild(armL1);
		setRotationAngle(armL1, -2.1588F, 0.4478F, -0.281F);
		armL1.cubeList.add(new ModelBox(armL1, 0, 16, -1.5F, -4.0F, -1.5F, 3, 8, 3, 0.0F, true));

		armL2 = new ModelRenderer(this);
		armL2.setRotationPoint(0.5F, -24.433F, -2.75F);
		main.addChild(armL2);
		setRotationAngle(armL2, -2.7202F, 0.9909F, -1.0788F);
		armL2.cubeList.add(new ModelBox(armL2, 0, 16, 0.5F, -3.0F, -1.5F, 3, 6, 3, 0.0F, true));

		armR2 = new ModelRenderer(this);
		armR2.setRotationPoint(-1.0F, -18.3876F, -3.6124F);
		main.addChild(armR2);
		setRotationAngle(armR2, -0.2533F, -0.7519F, -1.2086F);
		armR2.cubeList.add(new ModelBox(armR2, 0, 16, -2.0F, -3.0F, -1.5F, 3, 6, 3, 0.0F, false));

		armR1 = new ModelRenderer(this);
		armR1.setRotationPoint(-4.0F, -20.0F, -2.0F);
		main.addChild(armR1);
		setRotationAngle(armR1, -0.7137F, -0.3614F, -0.3876F);
		armR1.cubeList.add(new ModelBox(armR1, 0, 16, -1.5F, -4.0F, -1.5F, 3, 8, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { main.render(f5); }

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}