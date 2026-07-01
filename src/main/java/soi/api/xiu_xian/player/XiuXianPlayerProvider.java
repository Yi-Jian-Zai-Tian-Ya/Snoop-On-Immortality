package soi.api.xiu_xian.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class XiuXianPlayerProvider implements ICapabilitySerializable<NBTTagCompound>
{
    private final IXiuXianPlayer instance;

    public XiuXianPlayerProvider() { this.instance = new XiuXianPlayer(); }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == XiuXianPlayerCapabilities.XIU_XIAN_PLAYER;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return hasCapability(capability, facing) ? XiuXianPlayerCapabilities.XIU_XIAN_PLAYER.cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        instance.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) { instance.loadNBTData(nbt); }
}