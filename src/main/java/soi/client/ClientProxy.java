package soi.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.lwjgl.input.Keyboard;

import soi.client.event.EventDaoShuHandler;
import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;
import soi.client.event.EventClient;
import soi.client.gui.xiu_xian.GuiDirection;
import soi.client.gui.storage_bag.GuiDisplay;
import soi.client.gui.inventory.GuiDaoZaiInventory;
import soi.client.gui.inventory.GuiInwardObserve;
import soi.client.gui.inventory.GuiNewInventory;
import soi.client.gui.xiu_xian.GuiStatusUI;
import soi.client.gui.rune.GuiTeleportVoice;
import soi.client.gui.GuiCorpse;
import soi.client.gui.storage_bag.GuiStorageBag;
import soi.client.render.layer.RegisterLayer;
import soi.common.CommonProxy;
import soi.common.entity.corpse.EntityCorpse;
import soi.common.entity.rune.EntityTeleportVoiceRune;
import soi.common.inventory.rune.ContainerTeleportVoiceRune;
import soi.common.inventory.storage_bag.ContainerStorageBag;
import soi.util.LocUtils;

public class ClientProxy extends CommonProxy
{
    public static final KeyBinding KEY_PICK_STORAGE_BAG = new KeyBinding("keybind.pick_storage_bag", KeyConflictContext.IN_GAME, Keyboard.KEY_V, "key.categories.gameplay");
    public static final KeyBinding KEY_OPEN_BAG_1 = new KeyBinding("keybind.open_bag_1", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, "key.categories.soi");
    public static final KeyBinding KEY_OPEN_BAG_2 = new KeyBinding("keybind.open_bag_2", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, "key.categories.soi");
    public static final KeyBinding KEY_OPEN_BAG_3 = new KeyBinding("keybind.open_bag_3", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, "key.categories.soi");

    public static final KeyBinding KEY_ITEM_SHOWING = new KeyBinding("keybind.item_showing", KeyConflictContext.IN_GAME, Keyboard.KEY_N, "key.categories.gameplay");
    public static final KeyBinding KEY_YUN_QI = new KeyBinding("keybind.run_ling_qi", KeyConflictContext.IN_GAME, Keyboard.KEY_NUMPAD1, "key.categories.gameplay");
    public static final KeyBinding KEY_DAO_SHU = new KeyBinding("keybind.dao_shu", KeyConflictContext.IN_GAME, Keyboard.KEY_NUMPAD2, "key.categories.gameplay");

    public static final KeyBinding KEY_SHEN_SHI_PROBE = new KeyBinding("keybind.shen_shi_probe", KeyConflictContext.IN_GAME, Keyboard.KEY_G, "key.categories.soi");
    public static final KeyBinding KEY_FOCUS_ENEMY = new KeyBinding("keybind.focus_enemy", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, "key.categories.soi");

    @Override
    public void registerEventHandlers()
    {
        super.registerEventHandlers();

        ClientRegistry.registerKeyBinding(KEY_PICK_STORAGE_BAG);
        ClientRegistry.registerKeyBinding(KEY_OPEN_BAG_1);
        ClientRegistry.registerKeyBinding(KEY_OPEN_BAG_2);
        ClientRegistry.registerKeyBinding(KEY_OPEN_BAG_3);

        ClientRegistry.registerKeyBinding(KEY_ITEM_SHOWING);
        ClientRegistry.registerKeyBinding(KEY_YUN_QI);
        ClientRegistry.registerKeyBinding(KEY_DAO_SHU);

        ClientRegistry.registerKeyBinding(KEY_SHEN_SHI_PROBE);
        ClientRegistry.registerKeyBinding(KEY_FOCUS_ENEMY);

        MinecraftForge.EVENT_BUS.register(new EventClient());
        MinecraftForge.EVENT_BUS.register(new GuiDisplay());
        MinecraftForge.EVENT_BUS.register(new GuiStatusUI());
        MinecraftForge.EVENT_BUS.register(new GuiDirection());
        MinecraftForge.EVENT_BUS.register(new EventDaoShuHandler());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world instanceof WorldClient)
        {
            switch (ID)
            {
                case GUI_INVENTORY: return new GuiNewInventory(player);

                case GUI_INWARD_OBSERVE: return new GuiInwardObserve(player);

                case GUI_DAO_ZAI: return new GuiDaoZaiInventory(player);

                case GUI_CORPSE:
                {
                    EntityCorpse corpse = EntityCorpse.getCorpse(world, x, y, z);
                    if (corpse != null) return new GuiCorpse(player.inventory, corpse, true);
                }

                case GUI_STORAGE_BAG:
                {
                    EnumHand hand = x == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                    IDaoZaiItemHandler bag = DaoZaiAPI.getDaoZaiHandler(player);

                    ItemStack selected = LocUtils.getNonEquippedStorageBagFromInventory(player, hand);
                    if (selected.isEmpty() || y != -1) return new GuiStorageBag(new ContainerStorageBag(bag.getStackInSlot(y), player.inventory, null, y));
                    return new GuiStorageBag(new ContainerStorageBag(selected, player.inventory, hand, y));
                }

                case GUI_TELEPORT_VOICE_RUNE:
                {
                    if (y == 42000)
                    {
                        EnumHand hand = x == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                        ItemStack stack = player.getHeldItem(hand);
                        return new GuiTeleportVoice(new ContainerTeleportVoiceRune(player, stack, hand));
                    }
                    else
                    {
                        EntityTeleportVoiceRune rune = EntityTeleportVoiceRune.getRune(world, x, y, z);
                        return new GuiTeleportVoice(new ContainerTeleportVoiceRune(player, rune, x, y, z));
                    }
                }
            }
        }
        return null;
    }

    @Override public World getClientWorld() { return FMLClientHandler.instance().getClient().world; }

    @Override public void init()
    {
        super.init();
        RegisterLayer.registerAllLivingDaoZaiLayers();
    }

    public void preInit(FMLPreInitializationEvent event) { super.preInit(event); }

    public void postInit(FMLPostInitializationEvent event) { super.postInit(event); }
}