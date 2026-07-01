package soi.api.dao_shu;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

public interface IDaoShu
{
    Set<String> getDaoShus();
    void learnDaoShu(String name);
    String getKeyBinding(String name);
    void setKeyBinding(String name, String key);

    void syncDaoShu(EntityPlayerMP player);
    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
}