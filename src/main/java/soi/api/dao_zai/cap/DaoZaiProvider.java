package soi.api.dao_zai.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class DaoZaiProvider implements ICapabilitySerializable<NBTTagCompound>
{
    IDaoZai instance;
    public DaoZaiProvider() { this.instance = new DaoZai(); }

    @Override public <T> T getCapability(Capability<T> cap, EnumFacing side) { return cap == DaoZaiCapability.INV_DAO_ZAI ? DaoZaiCapability.INV_DAO_ZAI.cast(instance) : null; }
    @Override public boolean hasCapability(Capability<?> cap, EnumFacing side) { return cap == DaoZaiCapability.INV_DAO_ZAI; }

    @Override public NBTTagCompound serializeNBT() { return instance.serializeNBT(); }
    @Override public void deserializeNBT(NBTTagCompound nbt) { instance.deserializeNBT(nbt); }
}