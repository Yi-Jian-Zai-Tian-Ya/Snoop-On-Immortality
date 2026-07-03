package soi.api.dao_shu;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_shu.MessageSyncDaoShu;

import java.util.*;

public class DaoShu implements IDaoShu
{
    private final NBTTagCompound keys = new NBTTagCompound();

    @Override public Set<String> getDaoShus() { return keys.getKeySet(); }
    @Override public void learnDaoShu(String name) { keys.setString(name, "NONE"); }
    @Override public String getKeyBinding(String name) { return keys.getString(name); }
    @Override public void setKeyBinding(String name, String key) { if (keys.hasKey(name)) keys.setString(name, key); }

    @Override
    public void syncDaoShu(EntityPlayerMP player)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        saveNBTData(nbt);
        PacketHandler.INSTANCE.sendTo(new MessageSyncDaoShu(nbt), player);
    }

    @Override public void saveNBTData(NBTTagCompound compound) { compound.setTag("LearnedDaoShu", keys.copy()); }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        keys.getKeySet().clear();
        if (compound.hasKey("LearnedDaoShu"))
        {
            NBTTagCompound bindings = compound.getCompoundTag("LearnedDaoShu");
            for (String key : bindings.getKeySet()) keys.setString(key, bindings.getString(key));
        }
    }
}