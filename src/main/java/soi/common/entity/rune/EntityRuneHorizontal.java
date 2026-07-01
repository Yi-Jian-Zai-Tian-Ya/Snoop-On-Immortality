package soi.common.entity.rune;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityRuneHorizontal extends Entity
{
    public EntityRuneHorizontal(World worldIn)
    {
        super(worldIn);
        setSize(0.96F, 0.2F);
        this.setNoGravity(true);
    }

    @Override public void onUpdate() { super.onUpdate(); }
    @Override protected void entityInit() { }
    @Override protected void readEntityFromNBT(NBTTagCompound nbt) { }
    @Override protected void writeEntityToNBT(NBTTagCompound nbt) { }
}