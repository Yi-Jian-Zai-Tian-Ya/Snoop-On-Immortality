package soi.common.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CommandTpDIM extends CommandBase
{
    @Nonnull @Override public String getName() { return "tpDIM"; }
    @Override public int getRequiredPermissionLevel() { return 2; }
    @Override public boolean isUsernameIndex(@Nonnull String[] args, int index) { return index == 0; }

    @Nonnull @Override public String getUsage(@Nonnull ICommandSender sender) { return "commands.tpDIM.usage"; }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2) throw new WrongUsageException("commands.tpDIM.usage", new Object[0]);

        Entity entity = getEntity(server, sender, args[0]);

        int dimensionID = parseInt(args[1]);
        if (!DimensionManager.isDimensionRegistered(dimensionID)) throw new CommandException("commands.generic.dimension.notFound", dimensionID);

        if (entity.world != null)
        {
            CommandBase.CoordinateArg x;
            CommandBase.CoordinateArg y;
            CommandBase.CoordinateArg z;
            if (args.length == 3)
            {
                Entity target = getEntity(server, sender, args[2]);
                if (target.dimension != dimensionID) throw new CommandException("commands.tpDIM.notInDimension", entity.getName(), dimensionID);
                x = parseCoordinate(target.posX, "~", false);
                y = parseCoordinate(target.posY, "~", false);
                z = parseCoordinate(target.posZ, "~", false);
                notifyCommandListener(sender, this, "commands.tpDIM.success", new Object[] { entity.getName(), dimensionID, target.getName() });
            }
            else
            {
                Vec3d vec3d = sender.getPositionVector();
                x = parseCoordinate(vec3d.x, args.length > 2 ? args[2] : args[1].equals("2") ? "0" : "~", true);
                y = parseCoordinate(vec3d.y, args.length > 3 ? args[3] : args[1].equals("2") ? "2" : "~", false);
                z = parseCoordinate(vec3d.z, args.length > 4 ? args[4] : args[1].equals("2") ? "0" : "~", true);
                notifyCommandListener(sender, this, "commands.tpDIM.success.coordinates", new Object[] { entity.getName(), dimensionID, x.getResult(), y.getResult(), z.getResult() });
            }

            doTeleport(entity, dimensionID, x, y, z, server.getWorld(dimensionID));
        }
    }

    private static void doTeleport(Entity entity, int dimensionID, CommandBase.CoordinateArg argX, CommandBase.CoordinateArg argY, CommandBase.CoordinateArg argZ, WorldServer world)
    {
        if (entity.dimension == dimensionID)
        {
            entity.setPositionAndUpdate(argX.getResult(), argY.getResult(), argZ.getResult());
            return;
        }

        if (entity instanceof EntityPlayerMP)
        {
            entity.dismountRidingEntity();
            entity.changeDimension(dimensionID, new PreciseTeleporter(world, argX.getResult(), argY.getResult(), argZ.getResult()));
        }
        else
        {
            entity.setPositionAndUpdate(argX.getResult(), argY.getResult(), argZ.getResult());
            entity.changeDimension(dimensionID);
        }

        if (!(entity instanceof EntityLivingBase) || !((EntityLivingBase)entity).isElytraFlying())
        {
            entity.motionY = 0.0D;
            entity.onGround = true;
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        else if (args.length <= 2) return getListOfStringsMatchingLastWord(args, Arrays.stream(DimensionManager.getStaticDimensionIDs()).map(String::valueOf).collect(Collectors.toList()));
        else if (args.length == 3) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        else return args.length <= 5 ? getTabCompletionCoordinate(args, 2, targetPos) : Collections.emptyList();
    }
}