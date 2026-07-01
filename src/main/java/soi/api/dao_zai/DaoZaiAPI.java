/*
  Source from Baubles
 */

package soi.api.dao_zai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import net.minecraft.item.Item;
import soi.api.dao_zai.cap.*;

public class DaoZaiAPI
{
    public static IDaoZaiItemHandler getDaoZaiHandler(EntityLivingBase entity)
    {
        IDaoZaiItemHandler handler = entity.getCapability(DaoZaiCapabilities.DAO_ZAI, null);
        if (handler != null) handler.setEntity(entity);
        return handler;
    }

    public static IDaoZaiItemHandler getDaoZaiHandler(EntityPlayer player) { return getDaoZaiHandler((EntityLivingBase) player); }

    @Deprecated
    public static IInventory getDaoZai(EntityPlayer player)
    {
        IDaoZaiItemHandler handler = player.getCapability(DaoZaiCapabilities.DAO_ZAI, null);
        if (handler != null) handler.setEntity(player);
        return new DaoZaiInventoryWrapper(handler, player);
    }
}