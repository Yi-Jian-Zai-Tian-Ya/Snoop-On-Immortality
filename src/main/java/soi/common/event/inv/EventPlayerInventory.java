/*
  Source from TinyInv
 */

package soi.common.event.inv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import soi.util.LocUtils;

@EventBusSubscriber
public class EventPlayerInventory
{
    @SubscribeEvent
    public static void onContainerEvent(PlayerContainerEvent.Open event)
    {
        Container container = event.getContainer();
        EntityPlayer player = event.getEntityPlayer();
        LocUtils.fixContainer(container, player);
    }
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity();
        LocUtils.fixContainer(player.inventoryContainer, player);
    }
}