package soi;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = SOI.MODID)
@Config.LangKey("config.soi")
public class SOIConfig
{
    @Config.LangKey("config.soi.category.ban_slot")
    public static CategoryBan Ban_Slot = new CategoryBan();
    @Config.LangKey("config.soi.category.ui")
    public static CategoryUI UI = new CategoryUI();

    //Ban BagSlot
    public static class CategoryBan
    {
        @Config.LangKey("config.soi.ban_slot.overlay")
        public boolean BanSlotOverlay = true;
        @Config.LangKey("config.soi.ban_slot.overlay_color")
        public String BanSlotOverlayColor = "0xC6C6C6";
    }

    //UI UI
    public static class CategoryUI
    {
        @Config.LangKey("config.soi.health_ui.disabled")
        public boolean HealthUIDisabled = true;
        @Config.LangKey("config.soi.status_ui.scale")
        public float StatusUIScale = 0.5F;
        @Config.LangKey("config.soi.status_ui.shen_shi.offsetX")
        public float ShenShiUIOffsetX = -26F;
        @Config.LangKey("config.soi.status_ui.shen_shi.offsetY")
        public float ShenShiUIOffsetY = -26F;
        @Config.LangKey("config.soi.direction_bar.scale")
        public float DirectionBarScale = 0.5F;
        @Config.LangKey("config.soi.gong_fa.item.activation")
        public boolean GongFaItemActivation = true;
    }

    @EventBusSubscriber(modid = SOI.MODID)
    public static class ConfigSyncHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(OnConfigChangedEvent event)
        {
            if (event.getModID().equals(SOI.MODID))
            {
                ConfigManager.sync(SOI.MODID, Config.Type.INSTANCE);
            }
        }
    }
}