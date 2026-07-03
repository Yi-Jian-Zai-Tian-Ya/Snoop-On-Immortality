package soi.api.dao_zai.gong_fa;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IGongFa
{
    void loadGongFa(String id, int max, float LL, float HD, float SS);
    NBTTagCompound getGongFas();
    NBTTagCompound getGongFaTag(NBTTagCompound root, String id);

    void addProgress(String id, double value, EntityPlayerMP player);

    int getJingJie(String id);
    int getMaxJingJie(String id);
    double getProgress(String id);

    boolean isMaxAllJingJie(String id);
    double getBaseNum(int JingJie);

    void syncGongFa(EntityPlayerMP player);
    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
}