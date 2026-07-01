/*
  Source from TinyInv
 */

package soi.common.event.inv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import soi.SOIConfig;
import soi.client.event.EventClient;
import soi.client.render.player.inv.RenderUtils;

import static soi.common.item.ban.ItemBanSlot.BAN_SLOT;

public class EventTickHandler
{
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;

        EntityPlayer player = event.player;
        if (player == null || player.world == null) return;

        if (!player.world.isRemote)
        {
            InventoryPlayer inv = player.inventory;

            for (int i = 9; i < 36; i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getItem() == BAN_SLOT) continue;

                if (!stack.isEmpty())
                {
                    player.captureDrops = true;
                    EntityItem entity = player.dropItem(stack, false, false);
                    player.capturedDrops.clear();
                    player.captureDrops = false;

                    if (entity != null) player.world.spawnEntity(entity);
                }
                inv.setInventorySlotContents(i, new ItemStack(BAN_SLOT));
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;
        if (!SOIConfig.Ban_Slot.BanSlotOverlay) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if (player == null || player.isSpectator()) return;
        if (!(mc.currentScreen instanceof GuiContainer)) return;
        GuiContainer gui = (GuiContainer) mc.currentScreen;
        int baseColor = Integer.decode(SOIConfig.Ban_Slot.BanSlotOverlayColor);
        gui.inventorySlots.inventorySlots.stream()
                .filter(slot -> slot.getSlotIndex() >= 9 && slot.getSlotIndex() <= 35)
                .filter(slot -> slot.inventory == player.inventory)
                .forEach(slot -> {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableDepth();
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.translate(0, 0, -1F);
                    RenderUtils.drawColoredRect(baseColor,
                            gui.getGuiLeft() + slot.xPos - 1,
                            gui.getGuiTop() + slot.yPos - 1,
                            18, 18, 128);
                    GlStateManager.popMatrix();
                });
        if (!EventClient.TOOLTIPS.isEmpty())
        {
            EventClient.TOOLTIPS.forEach(e ->
                    RenderUtils.drawHoveringText(e.getStack(), e.getLines(), e.getX(), e.getY(),
                            e.getScreenWidth(), e.getScreenHeight(), e.getMaxWidth(), e.getFontRenderer()));
            EventClient.TOOLTIPS.clear();
        }
    }
}