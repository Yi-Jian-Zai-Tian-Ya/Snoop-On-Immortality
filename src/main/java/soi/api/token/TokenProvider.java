package soi.api.token;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokenProvider implements ICapabilitySerializable<NBTTagCompound>
{
    @CapabilityInject(IToken.class)
    public static Capability<IToken> TOKEN;
    private final IToken instance;

    public TokenProvider() { this.instance = new Token(); }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == TOKEN; }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) { return hasCapability(capability, facing) ? TOKEN.cast(instance) : null; }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        instance.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) { instance.loadNBTData(nbt); }

    public static class TokenStorage implements Capability.IStorage<IToken>
    {
        @Nullable
        @Override
        public NBTTagCompound writeNBT(Capability<IToken> capability, IToken instance, EnumFacing side)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            instance.saveNBTData(nbt);
            return nbt;
        }

        @Override
        public void readNBT(Capability<IToken> capability, IToken instance, EnumFacing side, NBTBase nbt) { instance.loadNBTData((NBTTagCompound) nbt); }
    }
}