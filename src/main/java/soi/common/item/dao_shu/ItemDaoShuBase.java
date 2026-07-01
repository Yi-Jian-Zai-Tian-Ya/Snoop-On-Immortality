package soi.common.item.dao_shu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import soi.SOI;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.api.dao_shu.IDaoShu;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class ItemDaoShuBase extends Item
{
    public ItemDaoShuBase()
    {
        super();
        this.maxStackSize = 1;
        this.setCreativeTab(SOI.SHU);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack held = player.getHeldItem(hand);
        if (!world.isRemote)
        {
            IDaoShu DaoShu = player.getCapability(DaoShuCapabilities.DAO_SHU, null);
            if (DaoShu != null && !DaoShu.getDaoShus().contains(this.getUnlocalizedName()))
            {
                DaoShu.learnDaoShu(this.getUnlocalizedName());
                DaoShu.syncDaoShu((EntityPlayerMP) player);
                player.sendMessage(new TextComponentTranslation("message.dao_shu.learned", new TextComponentTranslation(this.getUnlocalizedName() + ".name")));

                return ActionResult.newResult(EnumActionResult.SUCCESS, held);
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, held);
    }

    public void castOnServer(World world, EntityPlayer player) { return; }
}