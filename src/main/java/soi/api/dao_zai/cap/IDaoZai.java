package soi.api.dao_zai.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.Set;

public interface IDaoZai
{
    DaoZaiInv getInv(DaoZaiType type);

    void addSlot(DaoZaiType type, int amount);
    void removeSlot(DaoZaiType type, int amount);

    ItemStack getStack(DaoZaiType type, int slot);
    void setStack(DaoZaiType type, int slot, ItemStack stack);

    Set<DaoZaiType> getTypes();
    Collection<DaoZaiInv> getInvs();

    void setOwner(EntityLivingBase living);
    EntityLivingBase getOwner();

    void syncDaoZai(EntityPlayerMP player);
    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound nbt);
}