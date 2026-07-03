package soi.api.dao_zai.gong_fa;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GongFaProvider implements ICapabilitySerializable<NBTTagCompound>
{
    private final IGongFa instance;

    public GongFaProvider() { this.instance = new GongFa(); }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == GongFaCapabilities.GONG_FA; }
    @Override public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) { return hasCapability(capability, facing) ? GongFaCapabilities.GONG_FA.cast(instance) : null; }

    @Override public NBTTagCompound serializeNBT() { NBTTagCompound nbt = new NBTTagCompound(); instance.saveNBTData(nbt); return nbt; }
    @Override public void deserializeNBT(NBTTagCompound nbt) { instance.loadNBTData(nbt); }
}