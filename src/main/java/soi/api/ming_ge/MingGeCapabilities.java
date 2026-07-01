package soi.api.ming_ge;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class MingGeCapabilities
{
    @CapabilityInject(IMingGe.class)
    public static Capability<IMingGe> MING_GE;

    public static class MingGeStorage implements Capability.IStorage<IMingGe>
    {
        @Override
        public NBTTagCompound writeNBT(Capability<IMingGe> capability, IMingGe instance, EnumFacing side)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            instance.saveNBTData(nbt);
            return nbt;
        }

        @Override
        public void readNBT(Capability<IMingGe> capability, IMingGe instance, EnumFacing side, NBTBase nbt) { instance.loadNBTData((NBTTagCompound) nbt); }
    }
}