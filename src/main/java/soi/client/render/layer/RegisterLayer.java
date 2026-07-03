package soi.client.render.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import soi.common.entity.corpse.EntityCorpse;

import java.util.Map;

public class RegisterLayer
{
    public static void registerAllLivingDaoZaiLayers()
    {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Map<String, RenderPlayer> skinMap = manager.getSkinMap();
        for (RenderPlayer render : skinMap.values()) render.addLayer(new LayerDaoZaiRender<>(render));

        addDaoZaiLayer(manager, EntityArmorStand.class, -0.0625F);
        addDaoZaiLayer(manager, EntityVillager.class, 0.09375F);
        addDaoZaiLayer(manager, EntityIronGolem.class, 0.0625F);
        addDaoZaiLayer(manager, EntitySnowman.class, 0.15625F);

        addDaoZaiLayer(manager, EntityZombie.class);
        addDaoZaiLayer(manager, EntitySkeleton.class);
        addDaoZaiLayer(manager, EntityCreeper.class);
        addDaoZaiLayer(manager, EntityWitch.class, 0.09375F);
        addDaoZaiLayer(manager, EntityEnderman.class, -0.0625F);
        addDaoZaiLayer(manager, EntityVindicator.class, 0.09375F);
        addDaoZaiLayer(manager, EntityEvoker.class, 0.09375F);
        addDaoZaiLayer(manager, EntityIllusionIllager.class, 0.09375F);
        addDaoZaiLayer(manager, EntityVex.class);

        addDaoZaiLayer(manager, EntityHusk.class);
        addDaoZaiLayer(manager, EntityZombieVillager.class, 0.0625F);
        addDaoZaiLayer(manager, EntityPigZombie.class);
        addDaoZaiLayer(manager, EntityGiantZombie.class);
        addDaoZaiLayer(manager, EntityWitherSkeleton.class);
        addDaoZaiLayer(manager, EntityStray.class);


    }

    private static  <T extends EntityLivingBase> void addDaoZaiLayer(RenderManager manager, Class<T> entity)
    {
        Render<T> render = manager.getEntityClassRenderObject(entity);
        if (render instanceof RenderLivingBase) ((RenderLivingBase<T>) render).addLayer(new LayerDaoZaiRender<>((RenderLivingBase<T>) render));
    }

    private static  <T extends EntityLivingBase> void addDaoZaiLayer(RenderManager manager, Class<T> entity, float zOffset)
    {
        Render<T> render = manager.getEntityClassRenderObject(entity);
        if (render instanceof RenderLivingBase) ((RenderLivingBase<T>) render).addLayer(new LayerDaoZaiRender<>((RenderLivingBase<T>) render, zOffset));
    }
}