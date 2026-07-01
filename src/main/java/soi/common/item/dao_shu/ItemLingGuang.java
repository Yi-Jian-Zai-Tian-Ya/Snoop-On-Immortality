package soi.common.item.dao_shu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.common.potion.PotionLingGuang;

public class ItemLingGuang extends ItemDaoShuBase
{
    public static final Item LING_GUANG = new ItemLingGuang();

    @Override public void castOnServer(World world, EntityPlayer player)
    {
        if (world.isRemote) return;

        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null) return;

        if (player.isPotionActive(PotionLingGuang.LING_GUANG))
        {
            player.removePotionEffect(PotionLingGuang.LING_GUANG);
            XiuXian.addHDCostLingLi(-0.075D);
            XiuXian.addMaxHuDun(-20.0);
            XiuXian.syncXiuXian((EntityPlayerMP) player);
            return;
        }

        if (XiuXian.getLingLi() >= 20.0D || player.isCreative())
        {
            if (!player.isCreative()) XiuXian.addLingLi(-20.0D);
            XiuXian.addMaxHuDun(20.0D);
            XiuXian.addHuDun(20.0D);
            XiuXian.addHDCostLingLi(0.075D);
            player.addPotionEffect(new PotionEffect(PotionLingGuang.LING_GUANG, Integer.MAX_VALUE, 0, true, true));
            XiuXian.syncXiuXian((EntityPlayerMP) player);
        }
        else player.sendStatusMessage(new TextComponentTranslation("message.dao_shu.lack", 20.0F), true);
    }
}