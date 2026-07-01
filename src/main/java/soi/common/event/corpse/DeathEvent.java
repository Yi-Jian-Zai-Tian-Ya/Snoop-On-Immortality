/*
  Source from Corpse
 */

package soi.common.event.corpse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;
import soi.common.entity.corpse.*;

import java.util.Collection;
import java.util.Collections;

@EventBusSubscriber
public class DeathEvent
{
    private NonNullList<ItemStack> armor;
    private NonNullList<ItemStack> offHand;
    private NonNullList<ItemStack> bag;
    private NonNullList<ItemStack> hotBar;
    private NonNullList<ItemStack> daoZai;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        if (!entity.world.isRemote && entity instanceof EntityPlayer && !entity.isDead)
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            player.getInventoryEnderChest().clear();

            armor = NonNullList.create();
            offHand = NonNullList.create();
            bag = NonNullList.create();
            hotBar = NonNullList.create();
            daoZai = NonNullList.create();
            IDaoZaiItemHandler inv = DaoZaiAPI.getDaoZaiHandler(player);

            armor.addAll(player.inventory.armorInventory);
            Collections.reverse(armor);
            offHand.addAll(player.inventory.offHandInventory);
            for (int i = 0; i < 3; i++) bag.add(inv.getStackInSlot(i));
            for (int i = 0; i < 9; i++) hotBar.add(player.inventory.mainInventory.get(i));
            for (int i = 3; i < inv.getSlots(); i++) daoZai.add(inv.getStackInSlot(i));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeath(PlayerDropsEvent event)
    {
        if (event.isCanceled()) return; if (event.getEntity().world.isRemote) return;
        EntityPlayer entity = event.getEntityPlayer(); if (entity == null) return;

        if (!(entity instanceof EntityPlayerMP)) return;
        Collection<EntityItem> drops = event.getDrops();
        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        NonNullList<ItemStack> stacks = NonNullList.create();

        for (ItemStack item : armor) stacks.add((!item.isEmpty()) ? item : ItemStack.EMPTY);
        for (ItemStack item : offHand) stacks.add((!item.isEmpty()) ? item : ItemStack.EMPTY);
        for (ItemStack item : bag) stacks.add((!item.isEmpty()) ? item : ItemStack.EMPTY);
        for (ItemStack item : hotBar) stacks.add((!item.isEmpty()) ? item : ItemStack.EMPTY);
        for (ItemStack item : daoZai) stacks.add((!item.isEmpty()) ? item : ItemStack.EMPTY);
        for (EntityItem item : drops) stacks.add((!item.getItem().isEmpty()) ? item.getItem() : ItemStack.EMPTY);

        drops.clear();
        Death death = Death.fromPlayer(player, stacks);
        player.world.spawnEntity(EntityCorpse.createFromDeath(player, death));
    }
}