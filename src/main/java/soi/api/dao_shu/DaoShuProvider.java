package soi.api.dao_shu;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DaoShuProvider implements ICapabilitySerializable<NBTTagCompound>
{
    private final IDaoShu instance = DaoShuCapabilities.DAO_SHU.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == DaoShuCapabilities.DAO_SHU;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == DaoShuCapabilities.DAO_SHU ? DaoShuCapabilities.DAO_SHU.cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        instance.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        instance.loadNBTData(nbt);
    }
}