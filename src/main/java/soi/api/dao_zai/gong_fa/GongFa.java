package soi.api.dao_zai.gong_fa;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.common.sound.AllSound;
import soi.network.PacketHandler;
import soi.network.xiu_xian.MessageSyncGongFa;

public class GongFa implements IGongFa
{
    private final NBTTagCompound GongFas = new NBTTagCompound();

    @Override public void loadGongFa(String id, int max, float LL, float HD, float SS)
    {
        if (getGongFaTag(GongFas, id) != null) { if (max > this.getMaxJingJie(id)) getGongFaTag(GongFas, id).setInteger("MaxJingJie", max); return; }
        NBTTagCompound tag = new NBTTagCompound(); tag.setInteger("JingJie", 0); tag.setInteger("MaxJingJie", max); tag.setDouble("Progress", 0.0F);
        NBTTagCompound reward = new NBTTagCompound(); reward.setFloat("LL", LL); reward.setFloat("HD", HD); reward.setFloat("SS", SS);
        tag.setTag("Reward", reward); GongFas.setTag(id, tag);
    }

    @Override public NBTTagCompound getGongFas() { return GongFas; }
    @Override public NBTTagCompound getGongFaTag(NBTTagCompound root, String id) { NBTBase base = root.getTag(id); return (base instanceof NBTTagCompound) ? (NBTTagCompound) base : null; }

    @Override
    public void addProgress(String id, double value, EntityPlayerMP player)
    {
        NBTTagCompound tag = getGongFaTag(GongFas, id);
        if (tag == null || isMaxAllJingJie(id)) return;
        float now = tag.getFloat("Progress");
        tag.setDouble("Progress", now + value);
        checkProgressFull(id, player);
        syncGongFa(player);
    }

    @Override public int getJingJie(String id) { NBTTagCompound tag = getGongFaTag(GongFas, id); return (tag == null) ? -1 : tag.getInteger("JingJie"); }
    @Override public int getMaxJingJie(String id) { NBTTagCompound tag = getGongFaTag(GongFas, id); return (tag == null) ? 0 : tag.getInteger("MaxJingJie"); }
    @Override public double getProgress(String id) { NBTTagCompound tag = getGongFaTag(GongFas, id); return (tag == null) ? 0.0D : tag.getDouble("Progress"); }

    @Override public boolean isMaxAllJingJie(String id) { int now = getJingJie(id); int max = getMaxJingJie(id); return now >= max; }
    @Override public double getBaseNum(int JJ)
    {
        if (JJ <= 13) return 100.0D; else if (JJ <= 17) return 150.0D; else if (JJ <= 21) return 200.0D;
        else if (JJ <= 22) return 250.0D; else if (JJ <= 26) return 400.0D; else if (JJ <= 30) return 475.0D;
        else if (JJ <= 33) return 600.0D; else if (JJ <= 35) return 700.0D; else return 100.0D;
    }

    @Override
    public void syncGongFa(EntityPlayerMP player)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        saveNBTData(nbt);
        PacketHandler.INSTANCE.sendTo(new MessageSyncGongFa(nbt), player);
    }

    @Override public void saveNBTData(NBTTagCompound compound) { compound.setTag("GongFas", GongFas); }
    @Override public void loadNBTData(NBTTagCompound compound) { if (compound.hasKey("GongFas", 10)) { GongFas.merge(compound.getCompoundTag("GongFas")); } }

    private void checkProgressFull(String id, EntityPlayerMP player)
    {
        NBTTagCompound tag = getGongFaTag(GongFas, id);
        if (tag == null) return;
        int JingJie = tag.getInteger("JingJie");
        int MaxJingJie = tag.getInteger("MaxJingJie");
        double Progress = tag.getDouble("Progress");

        if (JingJie < MaxJingJie && Progress >= this.getBaseNum(JingJie))
        {
            tag.setInteger("JingJie", JingJie + 1);
            tag.setFloat("Progress", 0F);
            NBTTagCompound reward = getGongFaTag(tag, "Reward"); if (reward == null) return;
            IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null); if (XiuXian == null) return;
            XiuXian.addMaxLingLi(reward.getFloat("LL")); XiuXian.addMaxHuDun(reward.getFloat("HD"));
            XiuXian.addShenShi(reward.getFloat("SS")); XiuXian.syncXiuXian(player);
            player.world.playSound(null, player.posX, player.posY, player.posZ, AllSound.GONG_FA_IMPROVE, SoundCategory.PLAYERS, 2.5F, .50F);
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, .50F, .50F);
            if (isMaxAllJingJie(id))
            {
                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DRIP_LAVA, false, player.posX, player.posY, player.posZ, 100, 1.5D, 1.5D, 1.5D, 0.5D, new int[EnumParticleTypes.DRIP_LAVA.getArgumentCount()]);
                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DRIP_WATER, false, player.posX, player.posY, player.posZ, 100, 1.5D, 1.5D, 1.5D, 0.5D, new int[EnumParticleTypes.DRIP_WATER.getArgumentCount()]);
                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.TOTEM, false, player.posX, player.posY, player.posZ, 100, 0.5D, 0.5D, 0.5D, 1D, new int[EnumParticleTypes.TOTEM.getArgumentCount()]);
            }
            else
            {
                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DRIP_LAVA, false, player.posX, player.posY, player.posZ, 50, 0.75D, 0.75D, 0.75D, 0.5D, new int[EnumParticleTypes.DRIP_LAVA.getArgumentCount()]);
                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DRIP_WATER, false, player.posX, player.posY, player.posZ, 50, 0.75D, 0.75D, 0.75D, 0.5D, new int[EnumParticleTypes.DRIP_WATER.getArgumentCount()]);
            }
        }
    }
}