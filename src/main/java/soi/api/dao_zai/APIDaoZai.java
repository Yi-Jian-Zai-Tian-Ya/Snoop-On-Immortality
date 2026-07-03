package soi.api.dao_zai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import soi.api.dao_zai.cap.DaoZaiCapability;
import soi.api.dao_zai.cap.IDaoZai;

public class APIDaoZai
{
    public static IDaoZai getDaoZaiInv(EntityLivingBase entity)
    {
        IDaoZai inv = entity.getCapability(DaoZaiCapability.INV_DAO_ZAI, null);
        if (inv != null) inv.setOwner(entity);
        return inv;
    }

    public static IDaoZai getDaoZaiInv(EntityPlayer player) { return getDaoZaiInv((EntityLivingBase) player); }
}