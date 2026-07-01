package soi.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySeat extends Entity
{
    private EntityPlayer rider;

    public EntitySeat(World world)
    {
        super(world);
        this.setSize(0.001F, 0.001F);
        this.noClip = true;
        this.setInvisible(true);
        this.setNoGravity(false);
    }
    public EntitySeat(World world, EntityPlayer player) { this(world); this.rider = player; }

    @Override public void onUpdate() { if (!this.isBeingRidden()) { if (rider != null) rider.dismountRidingEntity(); this.setDead(); } }

    @Override protected void entityInit() { }
    @Override protected void readEntityFromNBT(NBTTagCompound compound) { }
    @Override protected void writeEntityToNBT(NBTTagCompound compound) { }
}
