package soi.common.entity.rune;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;

public abstract class EntityRuneBase extends Entity
{
    public EntityRuneBase(World worldIn)
    {
        super(worldIn);
        setSize(0.3525F, 0.96F);
        this.setNoGravity(true);
    }

    @Override public boolean canBeCollidedWith() { return true; }

    public void moveTo(double targetX, double targetY, double targetZ) { setPositionAndUpdate(targetX, targetY, targetZ); }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (this.isEntityInvulnerable(source)) return false;
        else if (!this.world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null) return false;
            else
            {
                ItemStack dropStack = this.getRuneItemStack();
                if (!dropStack.isEmpty())
                {
                    EntityItem entityItem = this.entityDropItem(dropStack, 0.0F);
                    if (entityItem != null) entityItem.setNoDespawn();
                }

                this.setDead();
                return true;
            }
        }
        return true;
    }

    protected abstract ItemStack getRuneItemStack();
    @Override public void onUpdate() { super.onUpdate(); }
    @Override protected void entityInit() { }
    @Override protected void readEntityFromNBT(NBTTagCompound nbt) { }
    @Override protected void writeEntityToNBT(NBTTagCompound nbt) { }
}