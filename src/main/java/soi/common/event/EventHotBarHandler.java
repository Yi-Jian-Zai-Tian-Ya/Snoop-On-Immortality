package soi.common.event;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;
import soi.common.item.extra.ItemBrokenSword;

import java.util.UUID;

@EventBusSubscriber
public class EventHotBarHandler
{
    private static final int[] CHECK_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 40};
    private final static UUID LUCK_UUID = UUID.fromString("72C7E169-3781-BEEB-3A50-92F706BB8224");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;

        EntityPlayer player = event.player;
        boolean hasTargetItem = false;

        for (int slot : CHECK_SLOTS)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == ItemBrokenSword.BROKEN_SWORD)
            {
                hasTargetItem = true;
                break;
            }
        }

        IAttributeInstance luckAttr = player.getEntityAttribute(SharedMonsterAttributes.LUCK);
        boolean hasModifier = luckAttr.getModifier(LUCK_UUID) != null;

        if (hasTargetItem)
        {
            if (hasModifier) return;
            IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
            if (MingGe == null) return;

            AttributeModifier luck = new AttributeModifier(
                    LUCK_UUID, "broken_sword_luck",
                    MingGe.getVIIINum() == 25073605 || MingGe.getVIIINum() == 37033009 ? 4.20D : -4.20D,
                    0);

            luckAttr.applyModifier(luck);
        }
        else if (hasModifier) luckAttr.removeModifier(LUCK_UUID);
    }
}