/*
  Source from Baubles
 */

package soi.common.event.dao_zai;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import soi.SOI;
import soi.api.dao_zai.IDaoZai;
import soi.api.dao_zai.cap.DaoZaiCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventHandlerItem
{
    private static ResourceLocation capability = new ResourceLocation(SOI.MODID, "Dao_Zai_Cap");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (stack.isEmpty() || !(stack.getItem() instanceof IDaoZai) || stack.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null)
                || event.getCapabilities().values().stream().anyMatch(c -> c.hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null)))
            return;

        event.addCapability(capability, new ICapabilityProvider()
        {
            @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == DaoZaiCapabilities.ITEM_DAO_ZAI; }
            @Nullable @Override public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) { return capability == DaoZaiCapabilities.ITEM_DAO_ZAI ? DaoZaiCapabilities.ITEM_DAO_ZAI.cast((IDaoZai) stack.getItem()) : null; }
        });
    }
}
