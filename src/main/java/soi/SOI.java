package soi;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import soi.common.command.*;
import soi.common.item.dao_shu.ItemFirePellet;
import soi.common.item.dao_zai.ItemStorageBag;
import soi.common.item.ordinary.*;
import soi.common.item.rune.ItemRune;
import soi.common.pattern.PatternHandler;
import soi.common.CommonProxy;
import soi.network.PacketHandler;

import java.io.File;

@Mod(
        modid = SOI.MODID,
        name = SOI.NAME,
        version = SOI.VERSION,

        guiFactory = "soi.SOIGuiFactory",
        acceptedMinecraftVersions = "[1.12,1.12.2]")
public class SOI
{
    public static final String MODID = "soi";
    public static final String NAME = "Snoop On Immortality";
    public static final String VERSION = "1.0.17";
    public static final CreativeTabs TAB = new CreativeTabs("tab") {@Override public ItemStack getTabIconItem() {return new ItemStack(ItemElectrum.ELECTRUM);}};
    public static final CreativeTabs ZAI = new CreativeTabs("dao_zai") {@Override public ItemStack getTabIconItem() { return new ItemStack(ItemStorageBag.STORAGE_BAG);}};
    public static final CreativeTabs SHU = new CreativeTabs("dao_shu") {@Override public ItemStack getTabIconItem() {return new ItemStack(ItemFirePellet.FIRE_PELLET);}};
    public static final CreativeTabs RUNE = new CreativeTabs("rune") {@Override public ItemStack getTabIconItem() {return new ItemStack(ItemRune.RUNE);}};

    @SidedProxy(clientSide = "soi.client.ClientProxy", serverSide = "soi.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(value=SOI.MODID)
    public static SOI instance;

    public File modDir;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);

        modDir = event.getModConfigurationDirectory();

        proxy.registerEventHandlers();
        PacketHandler.init();
        PatternHandler.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }

    @EventHandler
    public static void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandXiuXian());
        event.registerServerCommand(new CommandMingGe());
        event.registerServerCommand(new CommandTpDIM());
        event.registerServerCommand(new CommandDaoZai());
    }
}