package soi.api.dao_zai.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class DaoZaiCapability
{
    @CapabilityInject(IDaoZai.class)
    public static final Capability<IDaoZai> INV_DAO_ZAI = null;
    @CapabilityInject(IDaoZaiItem.class)
    public static final Capability<IDaoZaiItem> ITEM_DAO_ZAI = null;

    public static class Storage implements Capability.IStorage<IDaoZai>
    {
        @Override public NBTBase writeNBT(Capability<IDaoZai> cap, IDaoZai inst, EnumFacing side) { return inst.serializeNBT(); }
        @Override public void readNBT(Capability<IDaoZai> cap, IDaoZai inst, EnumFacing side, NBTBase nbt) { if(nbt instanceof NBTTagCompound) inst.deserializeNBT((NBTTagCompound)nbt); }
    }

    public static class ItemStorage implements Capability.IStorage<IDaoZaiItem>
    {
        @Override public NBTBase writeNBT(Capability<IDaoZaiItem> cap, IDaoZaiItem inst, EnumFacing side) { return null; }
        @Override public void readNBT(Capability<IDaoZaiItem> cap, IDaoZaiItem inst, EnumFacing side, NBTBase nbt) { }
    }
}