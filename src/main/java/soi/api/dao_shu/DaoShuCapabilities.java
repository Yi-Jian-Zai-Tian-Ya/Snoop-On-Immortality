package soi.api.dao_shu;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public class DaoShuCapabilities
{
    @CapabilityInject(IDaoShu.class)
    public static Capability<IDaoShu> DAO_SHU;

    public static class DaoShuStorage implements Capability.IStorage<IDaoShu>
    {
        @Nullable @Override public NBTBase writeNBT(Capability<IDaoShu> capability, IDaoShu instance, EnumFacing side) { NBTTagCompound nbt = new NBTTagCompound(); instance.saveNBTData(nbt); return nbt; }
        @Override public void readNBT(Capability<IDaoShu> capability, IDaoShu instance, EnumFacing side, NBTBase nbt) { instance.loadNBTData((NBTTagCompound) nbt); }
    }
}