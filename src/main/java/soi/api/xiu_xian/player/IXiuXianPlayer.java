package soi.api.xiu_xian.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import soi.api.xiu_xian.IXiuXian;

public interface IXiuXianPlayer
{
    void addProbeTick(int value);
    void addFocusTick(int value);

    boolean getShenShiActive();
    boolean getFocusEnemyActive();
    int getProbeTick();
    int getFocusTick();
    double getProbeRange(double SS);
    double getFocusRange();
    int getYunQiTime();
    boolean hasProbed(EntityLivingBase player, EntityLivingBase target);

    void setShenShiActive(boolean bool);
    void setFocusEnemyActive(boolean bool);
    void updateYunQi(IXiuXian XiuXian);

    void update(EntityPlayerMP player, IXiuXian XiuXian);
    void syncXiuXian(EntityPlayerMP player);

    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
}