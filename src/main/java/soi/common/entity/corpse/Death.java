/*
  Source from Corpse
 */

package soi.common.entity.corpse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.*;

public class Death
{
    private UUID playerUUID;
    private String playerName;
    private NonNullList<ItemStack> items;
    private double posX;
    private double posY;
    private double posZ;

    private Death() { }

    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
    public NonNullList<ItemStack> getItems() { return items; }

    public double getPosX() { return posX; }
    public double getPosY() { return posY; }
    public double getPosZ() { return posZ; }

    public static Death fromPlayer(EntityPlayer player, NonNullList<ItemStack> items)
    {
        Death death = new Death();
        death.playerUUID = player.getUniqueID();
        death.playerName = player.getName();
        death.items = items;

        death.posX = player.posX;
        death.posY = player.posY;
        death.posZ = player.posZ;

        return death;
    }
}