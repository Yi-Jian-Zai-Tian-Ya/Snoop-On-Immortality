package soi.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.api.dao_shu.IDaoShu;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_shu.MessageCastDaoShu;
import soi.network.xiu_xian.dao_shu.MessageUpdateKeyBinding;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class EventDaoShuHandler
{
    private static final Map<String, KeyBinding> BINDINGS = new HashMap<>();

    public static void updateBinding(String name, String keyCode)
    {
        int key = Keyboard.getKeyIndex(keyCode);

        KeyBinding binding = BINDINGS.get(name);
        if (binding == null)
        {
            binding = new KeyBinding("item." + name + ".name", key, "key.categories.dao_shu");
            ClientRegistry.registerKeyBinding(binding);
            BINDINGS.put(name, binding);
        }
        else binding.setKeyCode(key);
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    public static KeyBinding getKeyBinding(String name) { return BINDINGS.get(name); }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        BINDINGS.forEach((name, binding) -> { if (binding.isPressed()) PacketHandler.INSTANCE.sendToServer(new MessageCastDaoShu(name)); });
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.KeyboardInputEvent event)
    {
        if (event.getGui() instanceof GuiControls)
        {
            BINDINGS.forEach((name, binding) -> {
                String current = binding.getKeyCode() == Keyboard.KEY_NONE ? "NONE" : Keyboard.getKeyName(binding.getKeyCode());

                IDaoShu cap = Minecraft.getMinecraft().player.getCapability(DaoShuCapabilities.DAO_SHU, null);
                if (cap != null && !cap.getKeyBinding(name).equals(current))
                {
                    cap.setKeyBinding(name, current);
                    PacketHandler.INSTANCE.sendToServer(new MessageUpdateKeyBinding(name, current));
                }
            });

        }
    }
}