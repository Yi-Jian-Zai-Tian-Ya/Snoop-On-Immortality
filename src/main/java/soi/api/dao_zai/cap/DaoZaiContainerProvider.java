/*
  Source from Baubles
 */

package soi.api.dao_zai.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class DaoZaiContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider
{
    private final DaoZaiContainer container;

    public DaoZaiContainerProvider(DaoZaiContainer container) { this.container = container; }

    @Override public boolean hasCapability(Capability<?> capability, EnumFacing facing) { return capability == DaoZaiCapabilities.DAO_ZAI; }
    @Override @SuppressWarnings("unchecked") public <T> T getCapability(Capability<T> capability, EnumFacing facing) { if (capability == DaoZaiCapabilities.DAO_ZAI) return (T) this.container; return null; }

    @Override public NBTTagCompound serializeNBT() { return this.container.serializeNBT(); }
    @Override public void deserializeNBT(NBTTagCompound nbt) { this.container.deserializeNBT(nbt); }
}