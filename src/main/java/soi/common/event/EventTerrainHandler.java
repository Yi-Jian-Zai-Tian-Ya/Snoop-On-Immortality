package soi.common.event;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soi.common.world.structure.StructureLaiFu;

public class EventTerrainHandler
{
    @SubscribeEvent
    public void onInitMapGen(InitMapGenEvent event)
    {
        if (event.getType() == InitMapGenEvent.EventType.CUSTOM) {
            event.setNewGen(new StructureLaiFu());
        }
    }
}