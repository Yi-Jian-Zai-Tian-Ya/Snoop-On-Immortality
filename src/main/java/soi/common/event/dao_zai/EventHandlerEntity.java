/*
  Source from Baubles
 */

package soi.common.event.dao_zai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import soi.api.dao_zai.*;
import soi.api.dao_zai.cap.*;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.inventory.MessagePacketSync;

import java.util.*;

public class EventHandlerEntity
{
    private final HashMap<Integer, ItemStack[]> DaoZaiSync = new HashMap<Integer, ItemStack[]>();

    @SubscribeEvent
    public void cloneCapabilitiesEvent(Clone event)
    {
        DaoZaiContainer bco = (DaoZaiContainer) DaoZaiAPI.getDaoZaiHandler(event.getOriginal());
        NBTTagCompound nbt = bco.serializeNBT();
        DaoZaiContainer bcn = (DaoZaiContainer) DaoZaiAPI.getDaoZaiHandler(event.getEntityPlayer());
        bcn.deserializeNBT(nbt);
    }

    @SubscribeEvent public void EntityJoin(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote) return; Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) entity;
        syncSlots(player, Collections.singletonList(player)); }
    @SubscribeEvent public void onStartTracking(StartTracking event)
    {
        EntityPlayerMP watcher = (EntityPlayerMP) event.getEntityPlayer();
        Entity target = event.getTarget();
        if (target instanceof EntityPlayerMP) syncSlots((EntityPlayer) target, Collections.singletonList(watcher));
        else if (target instanceof EntityLivingBase) syncLivingSlots(watcher, Collections.singletonList(target));
    }

    @SubscribeEvent public void onPlayerLoggedOut(PlayerLoggedOutEvent event) { DaoZaiSync.remove(event.player.getEntityId()); }
    @SubscribeEvent public void LivingDeath(LivingDeathEvent event) { EntityLivingBase living = event.getEntityLiving(); if (!(living instanceof EntityPlayer)) DaoZaiSync.remove(living.getEntityId()); }

    @SubscribeEvent
    public void livingTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;

        for (Entity entity : event.world.loadedEntityList)
        {
            if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer))
            {
                EntityLivingBase living = (EntityLivingBase) entity;
                IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(living);
                if (DaoZais == null) continue;

                boolean changed = false;
                for (int i = 0; i < DaoZais.getSlots(); i++)
                {
                    ItemStack stack = DaoZais.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    changed = true;
                    IDaoZai DaoZai = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
                    if (DaoZai != null) DaoZai.onWornTick(stack, living);
                }
                if (changed) syncLivingDaoZai(living, DaoZais);
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;

        EntityPlayer player = event.player;
        IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(player);
        for (int i = 0; i < DaoZais.getSlots(); i++)
        {
            ItemStack stack = DaoZais.getStackInSlot(i);
            IDaoZai DaoZai = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);
            if (DaoZai != null) DaoZai.onWornTick(stack, player, i);
        }
        syncLivingDaoZai(player, DaoZais);
    }

    private void syncLivingDaoZai(EntityLivingBase living, IDaoZaiItemHandler DaoZais)
    {
        int id = living.getEntityId();
        ItemStack[] items = DaoZaiSync.get(id);

        if (items == null) { items = new ItemStack[DaoZais.getSlots()]; Arrays.fill(items, ItemStack.EMPTY); DaoZaiSync.put(id, items); }
        if (items.length != DaoZais.getSlots()) { ItemStack[] old = items; items = new ItemStack[DaoZais.getSlots()]; System.arraycopy(old, 0, items, 0, Math.min(old.length, items.length)); DaoZaiSync.put(id, items); }

        Set<EntityPlayer> players = null;

        for (int slot = 0; slot < DaoZais.getSlots(); slot++)
        {
            ItemStack stack = DaoZais.getStackInSlot(slot);
            IDaoZai DaoZai = stack.getCapability(DaoZaiCapabilities.ITEM_DAO_ZAI, null);

            if (DaoZais.isChanged(slot) || (DaoZai != null && DaoZai.willAutoSync(stack, living) && !ItemStack.areItemStacksEqual(stack, items[slot])))
            {
                if (players == null) { players = new HashSet<>(((WorldServer) living.world).getEntityTracker().getTrackingPlayers(living)); if (living instanceof EntityPlayer) players.add((EntityPlayer) living); }

                syncSlot(living, slot, stack, players);
                DaoZais.setChanged(slot, false);
                items[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            }
        }
    }

    private void syncLivingSlots(EntityPlayerMP receiver, List<Entity> entities)
    {
        for (Entity entity : entities)
        {
            if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer))
            {
                EntityLivingBase living = (EntityLivingBase) entity;
                IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(living);

                if (DaoZais == null) continue;

                for (int i = 0; i < DaoZais.getSlots(); i++)
                {
                    ItemStack stack = DaoZais.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    PacketHandler.INSTANCE.sendTo(new MessagePacketSync(living, i, stack), receiver);
                }
            }
        }
    }

    private void syncLivingSlots(List<EntityPlayer> players , Entity entity)
    {
        EntityLivingBase living = (EntityLivingBase) entity;
        IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(living);
        if (DaoZais == null) return;

        for (int i = 0; i < DaoZais.getSlots(); i++)
        {
            ItemStack stack = DaoZais.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            for (EntityPlayer receiver : players) PacketHandler.INSTANCE.sendTo(new MessagePacketSync(living, i, stack), (EntityPlayerMP) receiver);
        }
    }

    private void syncSlots(EntityPlayer player, Collection<? extends EntityPlayer> receivers)
    {
        IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(player);
        for (int i = 0; i < DaoZais.getSlots(); i++) syncSlot(player, i, DaoZais.getStackInSlot(i), receivers);
    }

    private void syncSlot(EntityLivingBase living, int slot, ItemStack stack, Collection<? extends EntityPlayer> players)
    {
        MessagePacketSync pkt = new MessagePacketSync(living, slot, stack);
        for (EntityPlayer receiver : players) PacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
    }

    @SubscribeEvent public void LivingDrops(LivingDropsEvent event) { if (event.getEntity() instanceof EntityLivingBase && !event.getEntity().world.isRemote) dropItemsAt((EntityLivingBase) event.getEntity(), event.getDrops(), event.getEntity()); }

    public void dropItemsAt(EntityLivingBase entity, List<EntityItem> drops, Entity e)
    {
        IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(entity); if (DaoZais == null) return;

        for (int i = 0; i < DaoZais.getSlots(); i++)
            if (DaoZais.getStackInSlot(i) != null && !DaoZais.getStackInSlot(i).isEmpty())
            {
                if (entity instanceof EntityPlayer) { DaoZais.setStackInSlot(i, ItemStack.EMPTY); continue;}
                EntityItem item = new EntityItem(e.world, e.posX, e.posY + e.getEyeHeight(), e.posZ, DaoZais.getStackInSlot(i).copy());
                item.setPickupDelay(40);
                float f1 = e.world.rand.nextFloat() * 0.5F;
                float f2 = e.world.rand.nextFloat() * (float) Math.PI * 2.0F;
                item.motionX = (double) (-MathHelper.sin(f2) * f1);
                item.motionZ = (double) (MathHelper.cos(f2) * f1);
                item.motionY = 0.20000000298023224D;
                drops.add(item);
                DaoZais.setStackInSlot(i, ItemStack.EMPTY);
            }
    }
}