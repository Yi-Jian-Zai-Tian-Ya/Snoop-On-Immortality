package soi.api.xiu_xian;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import soi.network.PacketHandler;
import soi.network.xiu_xian.MessageSyncXiuXian;

public class XiuXian implements IXiuXian
{
    private int JingJie = 0;
    private double XiuWei = 0.0D;
    private double LingLi = 0.0D;
    private double HuDun = 0.0D;
    private double ShenShi = 0.0D;
    private double MaxXiuWei = 1000.0D;
    private double MaxLingLi = 0.0D;
    private double MaxHuDun = 5.0D;
    private boolean AutoLianQi = false;
    private double XWRate = 0.05D;
    private double CDLL = 0.025D;
    private double HDCostLL = 0.125D;

    private static final double[][] REWARDS = { new double[]{9.0, 0.5, 1.8}, new double[]{75.0, 6.0, 30.0}, new double[]{200.0, 25.0, 62.5},
            new double[]{300.0, 40.0, 75.0}, new double[]{400.0, 50.0, 80.0}, new double[]{600.0, 75.0, 100.0}, new double[]{800.0, 100.0, 125.0} };


    @Override public void addJingJie(int value) { if (value == 0 || Math.max(value, -JingJie) == 0) return; onJingJieChanged(Math.max(value, -JingJie), (value > 0)); }
    @Override public void addXiuWei(double value) { XiuWei += value; checkJingJieImprove(); }
    @Override public void addLingLi(double value) { LingLi = Math.max(0, Math.min(LingLi + value, MaxLingLi)); }
    @Override public void addHuDun(double value) { HuDun = Math.max(0, Math.min(HuDun + value, MaxHuDun)); }
    @Override public void addShenShi(double value) { ShenShi = Math.max(0, ShenShi + value); }
    @Override public void addMaxXiuWei(double value) { MaxXiuWei = Math.max(0, MaxXiuWei + value); }
    @Override public void addMaxLingLi(double value) { MaxLingLi = Math.max(0, MaxLingLi + value); LingLi = Math.min(LingLi, MaxLingLi); }
    @Override public void addMaxHuDun(double value) { MaxHuDun = Math.max(0, MaxHuDun + value); HuDun = Math.min(HuDun, MaxHuDun); }
    @Override public void addHDCostLingLi(double value) { HDCostLL = Math.max(0, value); }

    @Override public int getJingJie() { return JingJie; }
    @Override public String getJingJieName() { return new TextComponentTranslation("jingJie." + JingJie).getUnformattedText(); }
    @Override public double getXiuWei() { return XiuWei; }
    @Override public double getLingLi() { return LingLi; }
    @Override public double getHuDun() { return HuDun; }
    @Override public double getShenShi() { return ShenShi; }

    @Override public void setJingJie(int value) { if (value == JingJie) return; onJingJieChanged(Math.max(0, value) - JingJie, (value > JingJie)); }
    @Override public void setXiuWei(double value) { XiuWei = value; checkJingJieImprove(); }
    @Override public void setLingLi(double value) { LingLi = Math.max(0, value); if (LingLi > MaxLingLi) LingLi = MaxLingLi; }
    @Override public void setHuDun(double value) { HuDun = Math.max(0, value); if (HuDun > MaxHuDun) HuDun = MaxHuDun; }
    @Override public void setShenShi(double value) { ShenShi = Math.max(0, value); }
    @Override public void setMaxXiuWei(double value) { MaxXiuWei = Math.max(0, value); }
    @Override public void setAutoLianQi(boolean bool) { AutoLianQi = bool; }

    @Override public double getMaxXiuWei() { return MaxXiuWei; }
    @Override public double getMaxHuDun() { return MaxHuDun; }
    @Override public double getMaxLingLi() { return MaxLingLi; }
    @Override public boolean canAutoLianQi() { return AutoLianQi; }

    @Override public void effuseXiuWei(int value, double percent, boolean isEffuse)
    {
        if (JingJie - value < 14 && isEffuse) return;
        int t = value; double Effused = 0.0D;
        while (t-- > 0) { onJingJieChanged(-1, false); //Effused += (XiuWei + MaxXiuWei) * (1.0D - percent);
            Effused += MaxXiuWei * (1.0D - percent); XiuWei += MaxXiuWei; XiuWei *= percent; }
        if (isEffuse) XWRate += Effused * Math.pow(1.2, value) * (1.0D - percent) * 0.000015D - 2.0D;
    }

    @Override
    public void update(Entity entity)
    {
        if (HuDun < MaxHuDun && LingLi >= HDCostLL) { LingLi = Math.max(0, LingLi - HDCostLL); HuDun += 0.25; }
        if (LingLi < MaxLingLi) LingLi += CDLL;
        if (LingLi > MaxLingLi) LingLi = Math.min(LingLi, MaxLingLi);
        if (HuDun > MaxHuDun) HuDun = MaxHuDun;
        if (AutoLianQi) XiuWei += XWRate;

        if (entity instanceof EntityPlayerMP) syncXiuXian((EntityPlayerMP) entity);
    }

    @Override
    public void syncXiuXian(EntityPlayerMP player)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        saveNBTData(nbt);
        PacketHandler.INSTANCE.sendTo(new MessageSyncXiuXian(nbt), player);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setInteger("JingJie", JingJie);
        compound.setDouble("XiuWei", XiuWei);
        compound.setDouble("LingLi", LingLi);
        compound.setDouble("HuDun", HuDun);
        compound.setDouble("ShenShi", ShenShi);
        compound.setDouble("MaxXiuWei", MaxXiuWei);
        compound.setDouble("MaxLingLi", MaxLingLi);
        compound.setDouble("MaxHuDun", MaxHuDun);
        compound.setBoolean("AutoLianQi", AutoLianQi);
        compound.setDouble("XWRate", XWRate);
        compound.setDouble("CDLL", CDLL);
        compound.setDouble("HDCostLL", HDCostLL);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        JingJie = compound.getInteger("JingJie");
        XiuWei = compound.getDouble("XiuWei");
        LingLi = compound.getDouble("LingLi");
        HuDun = compound.getDouble("HuDun");
        ShenShi = compound.getDouble("ShenShi");
        MaxXiuWei = compound.getDouble("MaxXiuWei");
        MaxLingLi = compound.getDouble("MaxLingLi");
        MaxHuDun = compound.getDouble("MaxHuDun");
        AutoLianQi = compound.getBoolean("AutoLianQi");
        XWRate = compound.getDouble("XWRate");
        CDLL = compound.getDouble("CDLL");
        HDCostLL = compound.getDouble("HDCostLL");
    }

    private void checkJingJieImprove()
    {
        if (XiuWei >= MaxXiuWei)
        {
            double overage = XiuWei - MaxXiuWei;
            XiuWei = 0.0;
            onJingJieChanged(1, true);

            if (overage > 0) XiuWei += overage * 0.85F;
        }
        else if (XiuWei < 0 && JingJie >= 1) { effuseXiuWei(1, 0.95D, false); }
    }

    private void onJingJieChanged(int value, boolean improved)
    {
        int level = improved ? value : -value;
        while (level-- > 0)
        {
            if (improved) JingJie += 1;
            double[] rewards = REWARDS[Math.min(REWARDS.length - 1, getRewardLevel(JingJie))];
            int JJLevel = getRewardLevel(JingJie);
            int Length = getJingJieLength(JJLevel);
            double preXiuWei = 14000.0D * (Math.pow(2, JJLevel) + Math.pow(JJLevel, 3)) / Length;
            while (JJLevel-- > 0) preXiuWei += 14000.0D * (Math.pow(2, JJLevel) + Math.pow(JJLevel, 3)) / Length;

            addMaxLingLi(improved ? rewards[0] : -rewards[0]); addMaxHuDun(improved ? rewards[1] : -rewards[1]);
            addShenShi(improved ? rewards[2] : -rewards[2]); setMaxXiuWei(preXiuWei);
            if (!improved) JingJie -= 1;
        }
    }

    private int getRewardLevel(int JJ)
    {
        if (JJ < 0 && JJ > 35) return 0;
        if (JJ <= 13) return 0; else if (JJ <= 17) return 1; else if (JJ <= 21) return 2;
        else if (JJ == 22) return 3; else if (JJ <= 26) return 4; else if (JJ <= 30) return 5;
        else if (JJ <= 33) return 6; else if (JJ <= 35) return 7; else return 0;
    }

    private int getJingJieLength(int Level)
    {
        switch (Level) { case 0 : return 14; case 1 : case 2 : case 4 : case 5 : case 6 : case 7 : return 4; case 3 : return 1; }
        return 1;
    }
}