package soi.api.dao_zai.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.inventory.MessageSyncDaoZai;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class DaoZai implements IDaoZai
{
    private final EnumMap<DaoZaiType, DaoZaiInv> map = new EnumMap<>(DaoZaiType.class);
    private EntityLivingBase owner;

    public DaoZai()
    {
        for (DaoZaiType type : DaoZaiType.values())
        {
            DaoZaiInv inv = new DaoZaiInv(type, type.getBaseSlots());
            map.put(type, inv);
        }
    }

    @Override public DaoZaiInv getInv(DaoZaiType type) { return map.get(type); }

    @Override
    public void addSlot(DaoZaiType type, int amount)
    {
        DaoZaiInv inv = getInv(type);
        if (inv != null) inv.addSlot(amount);
        map.remove(type); map.put(type, inv);
    }

    @Override
    public void removeSlot(DaoZaiType type, int amount)
    {
        DaoZaiInv inv = getInv(type);
        if (inv != null) inv.removeSlot(amount);
        map.remove(type); map.put(type, inv);
    }

    @Override
    public ItemStack getStack(DaoZaiType type, int slot)
    {
        DaoZaiInv g = getInv(type);
        if (g == null || slot >= g.getSlots()) return ItemStack.EMPTY;
        return g.getStackInSlot(slot);
    }

    @Override
    public void setStack(DaoZaiType type, int slot, ItemStack stack)
    {
        DaoZaiInv g = getInv(type);
        if (g != null) g.setStackInSlot(slot, stack);
    }

    @Override public Set<DaoZaiType> getTypes() { return map.keySet(); }
    @Override public Collection<DaoZaiInv> getInvs() { return map.values(); }

    @Override
    public void setOwner(EntityLivingBase entity)
    {
        this.owner = entity;
        map.values().forEach(h -> h.setOwner(entity));
    }

    @Override public EntityLivingBase getOwner() { return owner; }

    @Override public void syncDaoZai(EntityPlayerMP player)
    {
        if (owner == null) return;
        NBTTagCompound nbt = serializeNBT();
        PacketHandler.INSTANCE.sendTo(new MessageSyncDaoZai(nbt), player);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        for (Map.Entry<DaoZaiType, DaoZaiInv> entry : map.entrySet())
        {
            String name = entry.getKey().name();
            NBTTagCompound type = entry.getValue().serializeNBT();
            nbt.setTag(name, type);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        map.clear();
        for (String key : nbt.getKeySet())
        {
            DaoZaiType type = DaoZaiType.get(key); if (type == null) continue;

            NBTTagCompound tag = nbt.getCompoundTag(key);
            DaoZaiInv inv = new DaoZaiInv(type, tag.getInteger("Size"));
            inv.deserializeNBT(tag);
            inv.setOwner(owner);
            map.put(type, inv);
        }
    }
}