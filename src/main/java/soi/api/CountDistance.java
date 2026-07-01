package soi.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

public class CountDistance
{
    public static double distance;

    public static void countDistance(EntityPlayerMP playerA, EntityPlayerMP playerB)
    {
        distance = 0;

        Vec3d pos1 = playerA.getPositionVector();
        Vec3d pos2 = playerB.getPositionVector();

        distance = Math.round(pos1.distanceTo(pos2) * 100.0) / 100.0;
    }

    public static double getDistance()
    {
        return distance;
    }
}