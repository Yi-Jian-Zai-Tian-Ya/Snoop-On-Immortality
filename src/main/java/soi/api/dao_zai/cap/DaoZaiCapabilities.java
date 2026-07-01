package soi.api.dao_zai.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

import soi.api.dao_zai.IDaoZai;

public class DaoZaiCapabilities
{
    @CapabilityInject(IDaoZaiItemHandler.class)
    public static final Capability<IDaoZaiItemHandler> DAO_ZAI = null;
    @CapabilityInject(IDaoZai.class)
    public static final Capability<IDaoZai> ITEM_DAO_ZAI = null;

    public static class CapabilityDaoZai implements IStorage<IDaoZaiItemHandler>
    {
        @Override public NBTBase writeNBT(Capability<IDaoZaiItemHandler> capability, IDaoZaiItemHandler instance, EnumFacing side) { return null; }
        @Override public void readNBT(Capability<IDaoZaiItemHandler> capability, IDaoZaiItemHandler instance, EnumFacing side, NBTBase nbt) { }
    }

    public static class CapabilityItemDaoZaiStorage implements IStorage<IDaoZai>
    {
        @Override public NBTBase writeNBT(Capability<IDaoZai> capability, IDaoZai instance, EnumFacing side) { return null; }
        @Override public void readNBT(Capability<IDaoZai> capability, IDaoZai instance, EnumFacing side, NBTBase nbt) { }
    }
}