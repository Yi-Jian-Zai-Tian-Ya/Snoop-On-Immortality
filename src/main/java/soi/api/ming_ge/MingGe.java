package soi.api.ming_ge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import soi.network.PacketHandler;
import soi.network.xiu_xian.MessageSyncMingGe;

import java.util.ArrayList;
import java.util.List;

public class MingGe implements IMingGe
{
    private String IVZhu = "";
    private String LingGen = "";
    private int VIIINum = 0;
    private int Jin, Mu, Shui, Huo, Tu;
    private boolean LunHui = false;

    @Override public String getIVZhu() { return IVZhu; }
    @Override public String getLingGen() { return LingGen; }
    @Override public int getVIIINum() { return VIIINum; }
    @Override public boolean getLunHui() { return LunHui; }
    @Override public int getVXing(String lingGen, boolean only)
    {
        if (lingGen.equals("无")) return (Jin + Mu + Shui + Huo + Tu) % 6 + 1;
        if (only) switch (lingGen) { case "金" : return Jin; case "木" : return Mu; case "水" : return Shui; case "火" : return Huo; case "土" : return Tu; }
        else
        {
            int VX = 0;
            for (char c : lingGen.toCharArray()) switch (c) { case '金' : VX += Jin; break;  case '木' : VX += Mu; break;  case '水' : VX += Shui; break;  case '火' : VX += Huo; break;  case '土' : VX += Tu; break; }
            return VX / lingGen.length();
        }
        return 0;
    }

    @Override
    public void IVZhuLunHui()
    {
        LunHui = true;
        World world = DimensionManager.getWorld(0);
        if (world != null)
        {
            long worldTime = world.getTotalWorldTime();

            int nian = (int)((worldTime / 570_960L) % 60);
            int yue = (int)((worldTime % 570_960) / 47_580L);
            int ri = (int)((worldTime / 1_560L) % 60);
            int shi = (int)(((worldTime + 65) % 1_560L) / 65) / 2;

            CountIVZhu(nian, yue, ri, shi);
        }
    }

    @Override
    public void SelectIVZhu(int nian, int yue, int ri, int shi)
    {
        LunHui = true;
        CountIVZhu(nian, yue, ri, shi);
    }

    @Override public void setFateData(String ivZhu, String lingGen) { this.IVZhu = ivZhu; this.LingGen = lingGen; }

    @Override
    public void syncMingGe(EntityPlayerMP player)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        saveNBTData(nbt);
        PacketHandler.INSTANCE.sendTo(new MessageSyncMingGe(nbt), player);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setString("IVZhu", IVZhu);
        compound.setString("LingGen", LingGen);
        compound.setInteger("VIIINum", VIIINum);
        compound.setBoolean("LunHui", LunHui);
        compound.setInteger("Jin", Jin);
        compound.setInteger("Mu", Mu);
        compound.setInteger("Shui", Shui);
        compound.setInteger("Huo", Huo);
        compound.setInteger("Tu", Tu);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        IVZhu = compound.getString("IVZhu");
        LingGen = compound.getString("LingGen");
        VIIINum = compound.getInteger("VIIINum");
        LunHui = compound.getBoolean("LunHui");
        Jin = compound.getInteger("Jin");
        Mu = compound.getInteger("Mu");
        Shui = compound.getInteger("Shui");
        Huo = compound.getInteger("Huo");
        Tu = compound.getInteger("Tu");
    }

    private static final String[] TIAN_GAN = {
            "tianGan.jia", "tianGan.yi", "tianGan.bing", "tianGan.ding", "tianGan.wu",
            "tianGan.ji", "tianGan.geng", "tianGan.xin", "tianGan.ren", "tianGan.gui"
    };
    private static final String[] DI_ZHI = {
            "diZhi.zi", "diZhi.chou", "diZhi.yin", "diZhi.mao",
            "diZhi.chen", "diZhi.si", "diZhi.wu", "diZhi.wei",
            "diZhi.shen", "diZhi.you", "diZhi.xu", "diZhi.hai"
    };
    private static final int[] WU_HU = {
            2, 4, 6, 8, 0,
            2, 4, 6, 8, 0
    };
    private static final int[] WU_SHU = {
            0, 2, 4, 6, 8,
            0, 2, 4, 6, 8
    };

    private void CountIVZhu(int nian, int yue, int ri, int shi)
    {
        String yearGanZhi = translate(TIAN_GAN[nian % 10]) + translate(DI_ZHI[nian % 12]);
        String monthGanZhi = translate(TIAN_GAN[(WU_HU[nian % 10] + yue) % 10]) + translate(DI_ZHI[(yue + 2) % 12]);
        String dayGanZhi = translate(TIAN_GAN[ri % 10]) + translate(DI_ZHI[ri % 12]);
        String hourGanZhi = translate(TIAN_GAN[(WU_SHU[ri % 10] + shi) % 10]) + translate(DI_ZHI[shi]);

        this.IVZhu = yearGanZhi + translate("siZhu.nian") + " - "
                + monthGanZhi + translate("siZhu.yue") + " - "
                + dayGanZhi + translate("siZhu.ri") + " - "
                + hourGanZhi + translate("siZhu.shi");
        this.VIIINum = (nian + 1) * 1000000 + (yue + 1) * 10000 + (ri + 1) * 100 + (shi + 1);

        Jin = Mu = Shui = Huo = Tu = 0;
        countWuXing(nian % 10, nian % 12);
        countWuXing((WU_HU[nian % 10] + yue) % 10, (yue + 2) % 12);
        countWuXing(ri % 10, ri % 12);
        countWuXing((WU_SHU[ri % 10] + shi) % 10, shi);

        this.LingGen = deterLingGen();
    }

    private String translate(String key) { return new TextComponentTranslation(key).getUnformattedText(); }

    private void countWuXing(int ganIndex, int zhiIndex)
    {
        int random = 1 + (int)(Math.random() * 3);
        switch (ganIndex)
        {
            case 0: case 1: Mu += random; break;
            case 2: case 3: Huo += random; break;
            case 4: case 5: Tu += random; break;
            case 6: case 7: Jin += random; break;
            case 8: case 9: Shui += random; break;
        }

        random = 1 + (int)(Math.random() * 3);
        switch (zhiIndex) {
            case 0: case 11: Shui += random; break;
            case 1: case 4: case 7: case 10: Tu += random; break;
            case 2: case 3: Mu += random; break;
            case 5: case 6: Huo += random; break;
            case 8: case 9: Jin += random; break;
        }
    }

    private String deterLingGen()
    {
        Jin %= 6;
        Mu %= 6;
        Shui %= 6;
        Huo %= 6;
        Tu %= 6;
        List<String> lingGens = new ArrayList<>();
        if (Jin != 0) lingGens.add(translate("wuXing.jin"));
        if (Mu != 0) lingGens.add(translate("wuXing.mu"));
        if (Shui != 0) lingGens.add(translate("wuXing.shui"));
        if (Huo != 0) lingGens.add(translate("wuXing.huo"));
        if (Tu != 0) lingGens.add(translate("wuXing.tu"));

        switch (lingGens.size())
        {
            case 0: return translate("wuXing.none") + translate("wuXing.lingGen");
            case 1: return lingGens.get(0) + translate("wuXing.lingGen");
            default: return String.join("", lingGens) + translate("wuXing.lingGen");
        }
    }
}