package soi.common.item.dao_shu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.common.entity.shu.EntityFirePellet;

public class ItemFirePellet extends ItemDaoShuBase
{
    public static final Item FIRE_PELLET = new ItemFirePellet();

    @Override public void castOnServer(World world, EntityPlayer player)
    {
        if (world.isRemote) return;

        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null) return;

        if (XiuXian.getLingLi() >= 10.0D || player.isCreative())
        {
            if (!player.isCreative()) { XiuXian.addLingLi(-10.0D); XiuXian.syncXiuXian((EntityPlayerMP) player); }
            world.spawnEntity(new EntityFirePellet(world, player, player.getLastAttackedEntity()));
        }
        else player.sendStatusMessage(new TextComponentTranslation("message.dao_shu.lack", 10.0f), true);
    }
}