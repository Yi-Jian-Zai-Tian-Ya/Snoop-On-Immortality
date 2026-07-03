package soi.api.dao_zai.gong_fa;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class GongFaCapabilities
{
    @CapabilityInject(IGongFa.class)
    public static Capability<IGongFa> GONG_FA;

    public static class GongFaStorage implements Capability.IStorage<IGongFa>
    {
        @Override public NBTTagCompound writeNBT(Capability<IGongFa> capability, IGongFa instance, EnumFacing side) { NBTTagCompound nbt = new NBTTagCompound(); instance.saveNBTData(nbt); return nbt; }
        @Override public void readNBT(Capability<IGongFa> capability, IGongFa instance, EnumFacing side, NBTBase nbt) { instance.loadNBTData((NBTTagCompound) nbt); }
    }
}