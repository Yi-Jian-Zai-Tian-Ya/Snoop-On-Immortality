package soi.common.command;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class PreciseTeleporter extends Teleporter
{
    private final double x;
    private final double y;
    private final double z;

    public PreciseTeleporter(WorldServer world, double x, double y, double z)
    {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw)
    {
        entity.setPositionAndRotation(x, y, z, rotationYaw, entity.rotationPitch);
        entity.motionX = entity.motionY = entity.motionZ = 0.0F;
    }
}