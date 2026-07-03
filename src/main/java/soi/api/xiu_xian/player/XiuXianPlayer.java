package soi.api.xiu_xian.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.network.PacketHandler;
import soi.network.xiu_xian.MessageShenShiProbe;
import soi.network.xiu_xian.MessageSyncXiuXianPlayer;

import java.util.Collections;

public class XiuXianPlayer implements IXiuXianPlayer
{
    private boolean ShenShiActive = false;
    private boolean FocusEnemyActive = false;

    private int ProbeTick = 0;
    private int FocusTick = 0;
    private int YunQiTick = 1200;

    @Override public void addProbeTick(int value) { ProbeTick = Math.max(0, Math.min( ProbeTick + value, 200)); }
    @Override public void addFocusTick(int value) { FocusTick = Math.max(0, FocusTick + value); }

    @Override public boolean getShenShiActive() { return ShenShiActive; }
    @Override public boolean getFocusEnemyActive() { return FocusEnemyActive; }
    @Override public int getProbeTick() { return ProbeTick; }
    @Override public int getFocusTick() { return FocusTick; }
    @Override public double getProbeRange(double SS) { return ProbeTick == 200 ? SS : SS * (float) ProbeTick / 200;}
    @Override public double getFocusRange() { return FocusTick / 5D; }
    @Override public int getYunQiTime() { return YunQiTick / 20; }
    @Override public boolean hasProbed(EntityLivingBase player, EntityLivingBase target)
    {
        if (!(player instanceof EntityPlayerMP)) return false;
        if (!ShenShiActive) return false;
        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        IXiuXian TargetXX = target.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null || TargetXX == null) return false;
        if (TargetXX.getJingJie() >= XiuXian.getJingJie()) return false;
        return this.getProbeRange(XiuXian.getShenShi()) >= player.getDistance(target);
    }

    @Override public void setShenShiActive(boolean bool) { ShenShiActive = bool; }
    @Override public void setFocusEnemyActive(boolean bool) { FocusEnemyActive = bool; }

    @Override public void updateYunQi(IXiuXian XiuXian) { if (--YunQiTick <= 0) { XiuXian.addXiuWei(20.0D); YunQiTick = 1200;} }

    @Override
    public void update(EntityPlayerMP player, IXiuXian XiuXian)
    {
        if (ShenShiActive) {
            if (XiuXian.getLingLi() <= 0.05D) { ShenShiActive = false; PacketHandler.INSTANCE.sendTo(new MessageShenShiProbe(Collections.emptyList()), player); return; }
            if (!player.isCreative()) XiuXian.addLingLi(-0.05D); this.addProbeTick(1); }
        if (FocusEnemyActive) {
            if (XiuXian.getLingLi() <= 0.15D) { FocusEnemyActive = false; return; }
            if (!player.isCreative()) XiuXian.addLingLi(ShenShiActive ? -0.15D : -0.05D); if (ShenShiActive) this.addProbeTick(1); if (FocusTick < XiuXian.getShenShi() * 1.25F) this.addFocusTick(1); }

        XiuXian.syncXiuXian(player);
        syncXiuXian(player);
    }

    @Override
    public void syncXiuXian(EntityPlayerMP player)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        saveNBTData(nbt);
        PacketHandler.INSTANCE.sendTo(new MessageSyncXiuXianPlayer(nbt), player);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setBoolean("ShenShiActive", ShenShiActive);
        compound.setBoolean("FocusEnemyActive", FocusEnemyActive);
        compound.setInteger("ProbeTick", ProbeTick);
        compound.setInteger("FocusTick", FocusTick);
        compound.setInteger("YunQiTick", YunQiTick);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        ShenShiActive = compound.getBoolean("ShenShiActive");
        FocusEnemyActive = compound.getBoolean("FocusEnemyActive");
        ProbeTick = compound.getInteger("ProbeTick");
        FocusTick = compound.getInteger("FocusTick");
        YunQiTick = compound.getInteger("YunQiTick");
    }
}