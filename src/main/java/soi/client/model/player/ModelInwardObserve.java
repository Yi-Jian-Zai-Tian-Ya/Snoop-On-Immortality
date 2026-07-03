package soi.client.model.player;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

public class ModelInwardObserve extends ModelPlayer
{

    public ModelInwardObserve(float modelSize, boolean smallArms)
    {
        super(modelSize, smallArms);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

        this.bipedBody.offsetY = -0.625f;

        this.bipedRightArm.rotateAngleX = 30 * (float)Math.PI / 180;
        this.bipedRightArm.rotateAngleY = -30 * (float)Math.PI / 180;
        this.bipedRightArm.rotateAngleZ = 30 * (float)Math.PI / 180;

        this.bipedLeftArm.rotateAngleX = 30 * (float)Math.PI / 180;
        this.bipedLeftArm.rotateAngleY = 30 * (float)Math.PI / 180;
        this.bipedLeftArm.rotateAngleZ = -30 * (float)Math.PI / 180;

        this.bipedRightLeg.rotateAngleX = 45 * (float)Math.PI / 180;
        this.bipedRightLeg.rotateAngleX += 120 * (float)Math.PI / 180;
        this.bipedRightLeg.rotateAngleZ = -90 * (float)Math.PI / 180;

        this.bipedLeftLeg.rotateAngleX = 45 * (float)Math.PI / 180;
        this.bipedLeftLeg.rotateAngleX += 120 * (float)Math.PI / 180;
        this.bipedLeftLeg.rotateAngleZ = 90 * (float)Math.PI / 180;
    }
}