package soi.common.event;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soi.common.item.dao_zai.gong_fa.*;
import soi.common.item.rune.ItemWoodenToken;
import soi.common.item.dao_shu.ItemFirePellet;
import soi.common.item.dao_shu.ItemLingGuang;
import soi.common.item.dao_zai.ItemStorageBag;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class EventLootHandler
{
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        //LootChest - 战利品箱
        if (event.getName().toString().startsWith("minecraft:chests"))
        {
            List<LootEntry> entries = new ArrayList<>();

            LootFunction setCountA = new SetCount(new LootCondition[0], new RandomValueRange(1, 3));

            entries.add(new LootEntryEmpty(
                    90, 0,
                    new LootCondition[0],
                    "soi:inject_empty"
            ));
            entries.add(new LootEntryItem(
                    new ItemStack(ItemStorageBag.STORAGE_BAG, 1, 0).getItem(), 1, 2,
                    new LootFunction[0], new LootCondition[0], "soi:storage_bag_inferior"));
            entries.add(new LootEntryItem(
                    new ItemStack(ItemQingYuanSwordJue.QING_YUAN_SWORD, 1, 0).getItem(), 1, 1,
                    new LootFunction[0], new LootCondition[0], "soi:qing_yuan_sword"));
            entries.add(new LootEntryItem(
                    new ItemStack(ItemStorageBag.STORAGE_BAG, 1, 4).getItem(), 1, 5,
                    new LootFunction[0], new LootCondition[0], "soi:storage_bag_leather"));
            entries.add(new LootEntryItem(
                    ItemWoodenToken.WOODEN_TOKEN, 20, 1,
                    new LootFunction[] { setCountA }, new LootCondition[0], "soi:wooden_token"));
            entries.add(new LootEntryItem(
                    ItemChangChunGong.CHANG_CHUN, 3, 1,
                    new LootFunction[0], new LootCondition[0], "soi:chang_chun"));
            entries.add(new LootEntryItem(
                    ItemQingQiJue.QING_QI, 3, 1,
                    new LootFunction[0], new LootCondition[0], "soi:qing_qi"));
            entries.add(new LootEntryItem(
                    ItemTaiYangJue.TAI_YANG, 3, 1,
                    new LootFunction[0], new LootCondition[0], "soi:tai_yang"));
            entries.add(new LootEntryItem(
                    ItemZhenYangJue.ZHEN_YANG, 2, 1,
                    new LootFunction[0], new LootCondition[0], "soi:zhen_yang"));
            entries.add(new LootEntryItem(
                    ItemFirePellet.FIRE_PELLET, 5, 1,
                    new LootFunction[0], new LootCondition[0], "soi:fire_pellet"));
            entries.add(new LootEntryItem(
                    ItemLingGuang.LING_GUANG, 5, 1,
                    new LootFunction[0], new LootCondition[0], "soi:ling_guang"));

            LootPool injectPool = new LootPool(
                    entries.toArray(new LootEntry[0]),
                    new LootCondition[0],
                    new RandomValueRange(1, 2),         //Base
                    new RandomValueRange(0.4F),         //Luck
                    "soi:inject_pool"
            );
            event.getTable().addPool(injectPool);
        }
    }
}