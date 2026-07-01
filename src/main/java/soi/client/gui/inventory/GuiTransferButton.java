package soi.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;

import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;
import soi.api.dao_zai.storage_bag.IStorageBag;
import soi.api.dao_zai.storage_bag.StorageBagInfo;
import soi.common.inventory.storage_bag.ContainerStorageBag;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.storage_bag.MessageTransferToStorageBag;

@EventBusSubscriber(Side.CLIENT)
public class GuiTransferButton extends GuiButton
{
    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (!(event.getGui() instanceof GuiContainer)) return;
        if (event.getGui() instanceof InventoryEffectRenderer) return;
        if (event.getGui() instanceof GuiCrafting || event.getGui() instanceof GuiFurnace || event.getGui() instanceof GuiEnchantment) return;
        if (event.getGui() instanceof GuiRepair || event.getGui() instanceof GuiBeacon) return;

        GuiContainer gui = (GuiContainer) event.getGui();

        for (GuiButton btn : event.getButtonList()) if (btn.id >= 20200420 && btn.id <= 20200422) return;

        int x = gui.getGuiLeft() + gui.getXSize();
        int y = gui.getGuiTop() + 5;

        event.getButtonList().add(new GuiTransferButton(20200420, x - 39, y, 0));
        event.getButtonList().add(new GuiTransferButton(20200421, x - 28, y, 1));
        event.getButtonList().add(new GuiTransferButton(20200422, x - 17, y, 2));
    }

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("soi", "textures/gui/container/new_inventory.png");
    private final int index;
    private boolean Disabled;

    public GuiTransferButton(int id, int x, int y, int index)
    {
        super(id, x, y, 10, 10, "");
        this.index = index;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(BUTTON_TEXTURE);

        IDaoZaiItemHandler inv = DaoZaiAPI.getDaoZaiHandler(mc.player);

        boolean IsSameBag = false;
        boolean IsFull = true;

        Container container = null;
        if (mc.currentScreen != null) container = ((GuiContainer) mc.currentScreen).inventorySlots;
        if (container instanceof ContainerStorageBag) IsSameBag = ((ContainerStorageBag) container).getBarIndex() == index;

        ItemStack bag = inv.getStackInSlot(index);
        if (!IsSameBag && !bag.isEmpty() && bag.getItem() instanceof IStorageBag)
        {
            IItemHandler bagInv = StorageBagInfo.fromStack(bag).getInventory();
            for (int i = 0; i < bagInv.getSlots(); i++) if (bagInv.getStackInSlot(i).isEmpty()) { IsFull = false; break; }
        }

        Disabled = IsSameBag || bag.isEmpty() || IsFull;

        if (IsSameBag) drawTexturedModalRect(x, y, 196, 10, width, height);
        else if (bag.isEmpty()) drawTexturedModalRect(x, y, 186, 10, width, height);
        else if (IsFull) drawTexturedModalRect(x, y, 176, 10, width, height);
        else drawTexturedModalRect(x, y, 176 + (isMouseOverButton(mouseX, mouseY) ? 10 : 0), 0, width, height);
    }

    protected boolean isMouseOverButton(int mouseX, int mouseY)
    {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (Disabled) return false;
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            if (mc.currentScreen instanceof GuiContainer) PacketHandler.INSTANCE.sendToServer(new MessageTransferToStorageBag(index));
            return true;
        }
        return false;
    }
}
