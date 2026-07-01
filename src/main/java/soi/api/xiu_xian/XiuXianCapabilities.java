package soi.api.xiu_xian;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class XiuXianCapabilities
{
    @CapabilityInject(IXiuXian.class)
    public static Capability<IXiuXian> XIU_XIAN;

    public static class XiuXianStorage implements Capability.IStorage<IXiuXian>
    {
        @Override public NBTTagCompound writeNBT(Capability<IXiuXian> capability, IXiuXian instance, EnumFacing side) { NBTTagCompound nbt = new NBTTagCompound(); instance.saveNBTData(nbt); return nbt; }
        @Override public void readNBT(Capability<IXiuXian> capability, IXiuXian instance, EnumFacing side, NBTBase nbt) { instance.loadNBTData((NBTTagCompound) nbt); }
    }
}