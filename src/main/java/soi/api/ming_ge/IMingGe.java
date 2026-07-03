package soi.api.ming_ge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IMingGe
{
    String getIVZhu();
    String getLingGen();
    int getVIIINum();

    boolean getLunHui();
    int getVXing(String lingGen, boolean only);
    void setFateData(String mingGe, String lingGen);
    void IVZhuLunHui();
    void SelectIVZhu(int nian, int yue, int ri, int shi);

    void syncMingGe(EntityPlayerMP player);
    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
}