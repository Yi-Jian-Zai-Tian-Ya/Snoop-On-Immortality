package soi.common.event;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import soi.SOI;
import soi.api.dao_zai.*;
import soi.api.dao_zai.cap.DaoZaiCapabilities;
import soi.api.dao_zai.storage_bag.StorageBagType;
import soi.client.gui.inventory.GuiDaoZaiInventory;
import soi.client.render.entity.*;
import soi.client.render.entity.rune.RenderRuneHorizontal;
import soi.client.render.entity.shu.*;
import soi.common.block.*;
import soi.common.entity.*;
import soi.common.entity.corpse.EntityCorpse;
import soi.common.entity.rune.*;
import soi.client.gui.inventory.*;
import soi.common.entity.shu.*;
import soi.common.item.dao_zai.gong_fa.*;
import soi.common.item.extra.*;
import soi.common.item.ordinary.*;
import soi.common.item.ban.ItemBanSlot;
import soi.common.item.rune.*;
import soi.common.item.dao_shu.*;
import soi.common.item.dao_zai.ItemStorageBag;
import soi.client.model.entity.ModelTeamUp;
import soi.client.model.entity.shu.ModelFirePellet;
import soi.common.potion.*;
import soi.client.render.entity.corpse.RenderCorpse;
import soi.client.render.entity.rune.RenderTeleportVoiceRune;
import soi.common.sound.AllSound;

@EventBusSubscriber
public class EventRegisterHandler
{
    @SubscribeEvent
    public static void registerItems(Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        //Items - 物品
        registry.register(ItemBrokenSword.BROKEN_SWORD.setUnlocalizedName("brokenSword").setRegistryName("broken_sword"));

        registry.register(ItemStorageBag.STORAGE_BAG.setUnlocalizedName("storageBag").setRegistryName("storage_bag"));
        registry.register(ItemChangChunGong.CHANG_CHUN.setUnlocalizedName("changChun").setRegistryName("chang_chun"));
        registry.register(ItemQingYuanSwordJue.QING_YUAN_SWORD.setUnlocalizedName("qingYuanSword").setRegistryName("qing_yuan_sword"));

        registry.register(ItemWoodenToken.WOODEN_TOKEN.setUnlocalizedName("woodenToken").setRegistryName("wooden_token"));
        registry.register(ItemRune.RUNE.setUnlocalizedName("rune").setRegistryName("rune"));
        registry.register(ItemTeleportVoiceRune.TELEPORT_VOICE_RUNE.setUnlocalizedName("teleportVoiceRune").setRegistryName("teleport_voice_rune"));

        registry.register(ItemIronEssence.IRON_ESSENCE.setUnlocalizedName("ironEssence").setRegistryName("iron_essence"));
        registry.register(ItemElectrum.ELECTRUM.setUnlocalizedName("electrum").setRegistryName("electrum"));
        registry.register(ItemSilverNugget.SILVER_NUGGET.setUnlocalizedName("silverNugget").setRegistryName("silver_nugget"));
        registry.register(ItemSilverIngot.SILVER_INGOT.setUnlocalizedName("silverIngot").setRegistryName("silver_ingot"));
        registry.register(ItemSilverEssence.SILVER_ESSENCE.setUnlocalizedName("silverEssence").setRegistryName("silver_essence"));
        registry.register(ItemJade.JADE.setUnlocalizedName("jade").setRegistryName("jade"));
        registry.register(ItemJadeBowl.JADE_BOWL.setUnlocalizedName("jadeBowl").setRegistryName("jade_bowl"));
        registry.register(ItemSnowSpiritualityWater.SNOW_SPIRITUALITY_WATER.setUnlocalizedName("snowSpiritualityWater").setRegistryName("snow_spirituality_water"));
        registry.register(ItemSkyFireLiquid.SKY_FIRE_LIQUID.setUnlocalizedName("skyFireLiquid").setRegistryName("sky_fire_liquid"));

        registry.register(ItemQingQiJue.QING_QI.setUnlocalizedName("qingQi").setRegistryName("qing_qi"));
        registry.register(ItemTaiYangJue.TAI_YANG.setUnlocalizedName("taiYang").setRegistryName("tai_yang"));
        registry.register(ItemZhenYangJue.ZHEN_YANG.setUnlocalizedName("zhenYang").setRegistryName("zhen_yang"));
        registry.register(ItemFirePellet.FIRE_PELLET.setUnlocalizedName("firePellet").setRegistryName("fire_pellet"));
        registry.register(ItemLingGuang.LING_GUANG.setUnlocalizedName("lingGuang").setRegistryName("ling_guang"));

        registry.register(ItemBanSlot.BAN_SLOT.setUnlocalizedName("banSlot").setRegistryName("ban_slot"));
        //Blocks - 方块
        registry.register(BlockSilverGoldOre.SILVER_GOLD_ORE.setUnlocalizedName("silverGoldOre").setRegistryName("silver_gold_ore"));
        registry.register(BlockJadeOre.JADE_ORE.setUnlocalizedName("jadeOre").setRegistryName("jade_ore"));
        registry.register(BlockNeonDressHerbs.NEON_DRESS_HERBS.setUnlocalizedName("neonDressHerbs").setRegistryName("neon_dress_herbs"));
        registry.register(BlockStructureMarker.STRUCTURE_MARKER.setUnlocalizedName("structureMarker").setRegistryName("structure_marker"));
        registry.register(BlockLunHui.LUN_HUI.setUnlocalizedName("lunHui").setRegistryName("lun_hui"));
    }

    @SubscribeEvent
    public static void registryBlocks(Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(BlockSilverGoldOre.BLOCK_SILVER_GOLD_ORE.setUnlocalizedName("silverGoldOre").setRegistryName("silver_gold_ore"));
        registry.register(BlockJadeOre.BLOCK_JADE_ORE.setUnlocalizedName("jadeOre").setRegistryName("jade_ore"));
        registry.register(BlockNeonDressHerbs.BLOCK_NEON_DRESS_HERBS.setUnlocalizedName("neonDressHerbs").setRegistryName("neon_dress_herbs"));
        registry.register(BlockStructureMarker.BLOCK_STRUCTURE_MARKER.setUnlocalizedName("structureMarker").setRegistryName("structure_marker"));
        registry.register(BlockLunHui.BLOCK_LUN_HUI.setUnlocalizedName("lunHui").setRegistryName("lun_hui"));
    }

    @SubscribeEvent
    public static void registryEntities(Register<EntityEntry> event)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(SOI.MODID, "corpse"), EntityCorpse.class, SOI.MODID + ".corpse", 0, SOI.instance, 128, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(SOI.MODID, "teleport_voice_rune"), EntityTeleportVoiceRune.class, SOI.MODID + ".teleport_voice_rune", 1, SOI.instance, 128, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(SOI.MODID, "seat"), EntitySeat.class, SOI.MODID + ".seat", 2, SOI.instance, 16, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation(SOI.MODID,"fire_pellet"), EntityFirePellet.class, SOI.MODID + ".fire_pellet", 3, SOI.instance, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(SOI.MODID, "team_up"), EntityTeamUp.class, SOI.MODID + ".team_up", 4, SOI.instance, 32, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation(SOI.MODID, "rune"), EntityRuneHorizontal.class, SOI.MODID + "rune", 5, SOI.instance, 32, 1, true);
    }

    @SubscribeEvent
    public static void registryPotions(Register<Potion> event)
    {
        IForgeRegistry<Potion> registry = event.getRegistry();

        registry.register(PotionFanRen.FAN_REN.setPotionName("effect.fanRen").setRegistryName("fan_ren"));
        registry.register(PotionLingGuang.LING_GUANG.setPotionName("effect.lingGuang").setRegistryName("ling_guang"));
        registry.register(PotionSkyFire.SKY_FIRE.setPotionName("effect.skyFire").setRegistryName("sky_fire"));
        registry.register(PotionSnowSpirituality.SNOW_SPIRITUALITY.setPotionName("effect.snowSpirituality").setRegistryName("snow_spirituality"));
    }

    @SubscribeEvent
    public static void registerSounds(Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();

        registry.register(AllSound.BU_FAN.setRegistryName("soi:music.bu_fan"));
        registry.register(AllSound.BU_FAN_PX.setRegistryName("soi:music.bu_fan_px"));
        registry.register(AllSound.SWORD_CARVE.setRegistryName("soi:item.sword_carve"));
        registry.register(AllSound.GONG_FA_IMPROVE.setRegistryName("soi:gong_fa.improve"));
    }

    @SubscribeEvent
    public void registerItemModels(ModelRegistryEvent event)
    {
        //Items - 物品
        ModelLoader.setCustomModelResourceLocation(ItemBrokenSword.BROKEN_SWORD, 0, new ModelResourceLocation("soi:broken_sword", "inventory"));

        ModelLoader.setCustomModelResourceLocation(ItemStorageBag.STORAGE_BAG, 0, new ModelResourceLocation("soi:storage_bag/inferior", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemStorageBag.STORAGE_BAG, 1, new ModelResourceLocation("soi:storage_bag/moderate", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemStorageBag.STORAGE_BAG, 2, new ModelResourceLocation("soi:storage_bag/excellent", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemStorageBag.STORAGE_BAG, 3, new ModelResourceLocation("soi:storage_bag/chaotic", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemStorageBag.STORAGE_BAG, 4, new ModelResourceLocation("soi:storage_bag/leather", "inventory"));

        ModelLoader.setCustomModelResourceLocation(ItemChangChunGong.CHANG_CHUN, 0, new ModelResourceLocation("soi:gong_fa/chang_chun", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemQingYuanSwordJue.QING_YUAN_SWORD, 0, new ModelResourceLocation("soi:gong_fa/qing_yuan_sword", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemQingYuanSwordJue.QING_YUAN_SWORD, 1, new ModelResourceLocation("soi:gong_fa/qing_yuan_sword", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemQingYuanSwordJue.QING_YUAN_SWORD, 2, new ModelResourceLocation("soi:gong_fa/qing_yuan_sword", "inventory"));

        ModelLoader.setCustomModelResourceLocation(ItemWoodenToken.WOODEN_TOKEN, 0, new ModelResourceLocation("soi:rune/wooden_token_uncarved", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemWoodenToken.WOODEN_TOKEN, 1, new ModelResourceLocation("soi:rune/wooden_token_carved", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemRune.RUNE, 0, new ModelResourceLocation("soi:rune/rune", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemTeleportVoiceRune.TELEPORT_VOICE_RUNE, 0, new ModelResourceLocation("soi:rune/teleport_voice_rune", "inventory"));

        ModelLoader.setCustomModelResourceLocation(ItemIronEssence.IRON_ESSENCE, 0, new ModelResourceLocation("soi:iron_essence", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemElectrum.ELECTRUM, 0, new ModelResourceLocation("soi:electrum", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemSilverNugget.SILVER_NUGGET, 0, new ModelResourceLocation("soi:silver_nugget", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemSilverIngot.SILVER_INGOT, 0, new ModelResourceLocation("soi:silver_ingot", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemSilverEssence.SILVER_ESSENCE, 0, new ModelResourceLocation("soi:silver_essence", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemJade.JADE, 0, new ModelResourceLocation("soi:jade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemJadeBowl.JADE_BOWL, 0, new ModelResourceLocation("soi:jade_bowl", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemSnowSpiritualityWater.SNOW_SPIRITUALITY_WATER, 0, new ModelResourceLocation("soi:snow_spirituality_water", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemSkyFireLiquid.SKY_FIRE_LIQUID, 0, new ModelResourceLocation("soi:sky_fire_liquid", "inventory"));

        ModelLoader.setCustomModelResourceLocation(ItemQingQiJue.QING_QI, 0, new ModelResourceLocation("soi:gong_fa/qing_qi", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemTaiYangJue.TAI_YANG, 0, new ModelResourceLocation("soi:gong_fa/tai_yang", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemZhenYangJue.ZHEN_YANG, 0, new ModelResourceLocation("soi:gong_fa/zhen_yang", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemFirePellet.FIRE_PELLET, 0, new ModelResourceLocation("soi:dao_shu/fire_pellet", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemLingGuang.LING_GUANG, 0, new ModelResourceLocation("soi:dao_shu/ling_guang", "inventory"));

        ModelLoader.setCustomModelResourceLocation(ItemBanSlot.BAN_SLOT, 0, new ModelResourceLocation("soi:ban_slot", "inventory"));
        //Blocks - 方块
        ModelLoader.setCustomModelResourceLocation(BlockSilverGoldOre.SILVER_GOLD_ORE, 0, new ModelResourceLocation("soi:blocks/silver_gold_ore", "inventory"));
        ModelLoader.setCustomModelResourceLocation(BlockJadeOre.JADE_ORE, 0, new ModelResourceLocation("soi:blocks/jade_ore", "inventory"));
        ModelLoader.setCustomModelResourceLocation(BlockNeonDressHerbs.NEON_DRESS_HERBS, 0, new ModelResourceLocation("soi:blocks/neon_dress_herbs", "inventory"));
        ModelLoader.setCustomModelResourceLocation(BlockStructureMarker.STRUCTURE_MARKER, 0, new ModelResourceLocation("soi:blocks/structure_marker", "inventory"));
        ModelLoader.setCustomModelResourceLocation(BlockLunHui.LUN_HUI, 0, new ModelResourceLocation("soi:blocks/lun_hui", "inventory"));
        //Entities - 实体
        RenderingRegistry.registerEntityRenderingHandler(EntityCorpse.class, RenderCorpse::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTeleportVoiceRune.class, RenderTeleportVoiceRune::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFirePellet.class, manager -> new RenderFirePellet(manager, new ModelFirePellet(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTeamUp.class, manager -> new RenderTeamUp(manager, new ModelTeamUp()));
        RenderingRegistry.registerEntityRenderingHandler(EntityRuneHorizontal.class, RenderRuneHorizontal::new);
    }

    @SubscribeEvent
    public static void registerTexturePath(TextureStitchEvent.Pre event)
    {
        TextureMap map = event.getMap();
        //map.registerSprite(new ResourceLocation(SOI.MODID, "models/storage_bag/inferior"));
        map.registerSprite(new ResourceLocation(SOI.MODID, "items/empty_bag_slot"));
        map.registerSprite(new ResourceLocation(SOI.MODID, "items/empty_fa_bao_slot"));
        map.registerSprite(new ResourceLocation(SOI.MODID, "items/empty_gong_fa_slot"));
    }

    @SubscribeEvent
    public static void registerGameRules(WorldEvent.Load event)
    {
        if (event.getWorld().isRemote) return;

        GameRules rules = event.getWorld().getGameRules();
        if (!rules.hasRule("enableOptionalIVZhu")) rules.addGameRule("enableOptionalIVZhu", "false", GameRules.ValueType.BOOLEAN_VALUE);
    }

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {

        EntityPlayer player = event.getEntityPlayer();
        NBTTagCompound data = player.getEntityData();

        if (data.getBoolean("isSitting"))
        {
            event.getRenderer().getMainModel().isSneak = true;
            GlStateManager.translate(0, 0.5F, 0);
        }
    }

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event)
    {
        if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null))
        {
            IDaoZai Dao = event.getItemStack().getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
            DaoZaiType type = Dao.getDaoType(event.getItemStack());
            event.getToolTip().add(TextFormatting.GOLD + I18n.format("name." + type));
        }
    }

    @SubscribeEvent
    public static void registerStorageBags(RegistryEvent.Register<StorageBagType> event)
    {
        IForgeRegistry<StorageBagType> registry = event.getRegistry();

        registry.register(new StorageBagType("Inferior", 0, 9, 4));
        registry.register(new StorageBagType("Moderate", 1, 9, 7));
        registry.register(new StorageBagType("Excellent", 2, 11, 9));
        registry.register(new StorageBagType("Chaotic", 3, 8, 8));
        registry.register(new StorageBagType("Leather", 4, 5, 1));
    }

    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event)
    {
        @SuppressWarnings("unused") IForgeRegistry<StorageBagType> REGISTRY_TYPES = new RegistryBuilder<StorageBagType>()
                .setName(new ResourceLocation(SOI.MODID, "types"))
                .setDefaultKey(new ResourceLocation("Null"))
                .setType(StorageBagType.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .create();
    }

    @SubscribeEvent
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof GuiNewInventory || event.getGui() instanceof GuiInwardObserve || event.getGui() instanceof GuiDaoZaiInventory)
        {
            GuiContainer inv = (GuiContainer) event.getGui();
            event.getButtonList().add(new GuiTabButton(0, inv, 0, -28, 28, 32,
                    I18n.format( "button.inventory"), (event.getGui() instanceof GuiNewInventory), new ItemStack(Blocks.CHEST)));
            GuiContainer inward = (GuiContainer) event.getGui();
            event.getButtonList().add(new GuiTabButton(1, inward, 30, -28, 28, 32,
                    I18n.format("button.inward_observe"), (event.getGui() instanceof GuiInwardObserve), new ItemStack(ItemJade.JADE)));
            GuiContainer fa_bao = (GuiContainer) event.getGui();
            event.getButtonList().add(new GuiTabButton(2, fa_bao, 60, -28, 28, 32,
                    I18n.format("button.dao_zai"), (event.getGui() instanceof GuiDaoZaiInventory), new ItemStack(Items.GOLDEN_SWORD)));
        }
    }
}