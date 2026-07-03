package soi.api.xiu_xian;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IXiuXian
{
    void addJingJie(int value);
    void addXiuWei(double value);
    void addLingLi(double value);
    void addHuDun(double value);
    void addMaxXiuWei(double value);
    void addMaxLingLi(double value);
    void addMaxHuDun(double value);
    void addHDCostLingLi(double value);
    void addShenShi(double value);

    int getJingJie();
    String getJingJieName();
    double getXiuWei();
    double getLingLi();
    double getHuDun();
    double getShenShi();

    void setJingJie(int value);
    void setXiuWei(double value);
    void setLingLi(double value);
    void setHuDun(double value);
    void setMaxXiuWei(double value);
    void setShenShi(double value);
    void setAutoLianQi(boolean bool);

    double getMaxXiuWei();
    double getMaxHuDun();
    double getMaxLingLi();
    boolean canAutoLianQi();

    void effuseXiuWei(int value, double percent, boolean isEffuse);

    void update(Entity entity);
    void syncXiuXian(EntityPlayerMP player);

    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
}