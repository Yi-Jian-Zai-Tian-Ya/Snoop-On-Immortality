package soi.api.xiu_xian.player;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class XiuXianPlayerCapabilities
{
    @CapabilityInject(IXiuXianPlayer.class)
    public static Capability<IXiuXianPlayer> XIU_XIAN_PLAYER;

    public static class XiuXianPlayerStorage implements Capability.IStorage<IXiuXianPlayer>
    {
        @Override
        public NBTTagCompound writeNBT(Capability<IXiuXianPlayer> capability, IXiuXianPlayer instance, EnumFacing side)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            instance.saveNBTData(nbt);
            return nbt;
        }

        @Override
        public void readNBT(Capability<IXiuXianPlayer> capability, IXiuXianPlayer instance, EnumFacing side, NBTBase nbt) { instance.loadNBTData((NBTTagCompound) nbt); }
    }
}