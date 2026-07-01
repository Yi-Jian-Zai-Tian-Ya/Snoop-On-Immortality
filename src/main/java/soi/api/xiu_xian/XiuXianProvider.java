package soi.api.xiu_xian;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class XiuXianProvider implements ICapabilitySerializable<NBTTagCompound>
{
    private final IXiuXian instance;

    public XiuXianProvider() { this.instance = new XiuXian(); }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == XiuXianCapabilities.XIU_XIAN; }
    @Override public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) { return hasCapability(capability, facing) ? XiuXianCapabilities.XIU_XIAN.cast(instance) : null; }

    @Override public NBTTagCompound serializeNBT() { NBTTagCompound nbt = new NBTTagCompound(); instance.saveNBTData(nbt); return nbt; }
    @Override public void deserializeNBT(NBTTagCompound nbt) { instance.loadNBTData(nbt); }
}