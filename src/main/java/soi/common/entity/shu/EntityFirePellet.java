package soi.common.entity.shu;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;

import java.util.Comparator;

public class EntityFirePellet extends Entity
{
    private EntityLivingBase owner;
    private EntityLivingBase target;
    private int noTargetTicks = 0;
    private static final int MAX_NO_TARGET_TICKS = 200;

    public EntityFirePellet(World world)
    {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.noClip = true;
    }

    public EntityFirePellet(World world, EntityLivingBase owner, EntityLivingBase target)
    {
        this(world);
        this.owner = owner;
        this.target = (target == null || !target.isEntityAlive()) ? null : target;
        this.setPosition(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 1.0F, 1.0F);

    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (world.isRemote) { spawnFireParticles(); return; }

        updateTargetStatus();

        if (target == null)
        {
            noTargetTicks++;
            if (noTargetTicks >= MAX_NO_TARGET_TICKS) { this.setDead(); return; }
        }
        else noTargetTicks = 0;

        if (owner == null) return;
        Vec3d targetPos = (target == null) ? owner.getPositionEyes(1).add(owner.getLookVec().scale(4))
                : new Vec3d(target.posX, target.posY + target.height / 2, target.posZ);
        Vec3d direction = targetPos.subtract(this.getPositionVector()).normalize();
        this.motionX += (direction.x * 0.5f - this.motionX) * 0.1f;
        this.motionY += (direction.y * 0.5f - this.motionY) * 0.1f;
        this.motionZ += (direction.z * 0.5f - this.motionZ) * 0.1f;

        if (target != null)
        {
            double distance = targetPos.distanceTo(this.getPositionVector());
            if (distance < 0.5D) { attackTarget(target); this.setDead(); return; }
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionZ, this.motionX) * (180D / Math.PI)) - 90.0F;
        float horizontalDist = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        this.rotationPitch = (float)(MathHelper.atan2(motionY, horizontalDist) * (180D / Math.PI));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!this.isEntityInvulnerable(source)) { if (!world.isRemote) this.setDead(); return true; }
        return false;
    }

    private void updateTargetStatus()
    {
        if (target != null) if (!target.isEntityAlive() || target.getDistance(this) > 15.0D) target = null;
        if (target == null && owner != null)
        {
            IXiuXianPlayer XiuXianPlayer = owner.getCapability(XiuXianPlayerCapabilities.XIU_XIAN_PLAYER, null); if (XiuXianPlayer == null) return;
            target = findNearestTarget(owner, 5.0D + (XiuXianPlayer.getFocusEnemyActive() ? XiuXianPlayer.getFocusRange() : 0));
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnFireParticles()
    {
        for (int i = 0; i < 8; i++)
        {
            world.spawnParticle(EnumParticleTypes.FLAME,
                    posX + (rand.nextDouble() - 0.5) * 0.5,
                    posY + (rand.nextDouble() - 0.5) * 0.5,
                    posZ + (rand.nextDouble() - 0.5) * 0.5,
                    motionX * 0.2,
                    motionY * 0.2,
                    motionZ * 0.2
            );

            if (i % 3 == 0)
            {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                        posX + (rand.nextDouble() - 0.5) * 0.5,
                        posY + (rand.nextDouble() - 0.5) * 0.5,
                        posZ + (rand.nextDouble() - 0.5) * 0.5,
                        0, 0, 0
                );
            }
        }
    }

    private void attackTarget(EntityLivingBase target)
    {
        //owner.setLastAttackedEntity(target);
        if (owner instanceof EntityPlayer) world.playSound((EntityPlayer) owner, owner.posX, owner.posY, owner.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.75F, 0.75F);
        target.setFire(3);
        target.attackEntityFrom(DamageSource.causeThrownDamage(this, owner), 8.0f);
        target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20, 1));
    }

    private EntityLivingBase findNearestTarget(EntityLivingBase owner, double range)
    {
        Vec3d pos = owner.getPositionEyes(1).add(owner.getLookVec().scale(4));
        RayTraceResult block = world.rayTraceBlocks(owner.getPositionEyes(1), pos, true, false, false);
        if (block != null && block.typeOfHit == RayTraceResult.Type.BLOCK) pos = block.hitVec;

        AxisAlignedBB area = new AxisAlignedBB(
                pos.x - range, pos.y - range, pos.z - range,
                pos.x + range, pos.y + range, pos.z + range
        );

        Vec3d Pos = pos;
        return owner.world.getEntitiesWithinAABB(EntityLivingBase.class, area, e ->
                        e != owner && e.isEntityAlive() && !e.isOnSameTeam(owner)
                                && !(e instanceof EntityPlayer) && owner.canEntityBeSeen(e)).stream()
                        .min(Comparator.comparingDouble(e -> Pos.squareDistanceTo(e.getPositionVector()))).orElse(null);
    }

    @Override protected void entityInit() { }
    @Override protected void writeEntityToNBT(NBTTagCompound compound) { compound.setInteger("NoTargetTicks", noTargetTicks); }
    @Override protected void readEntityFromNBT(NBTTagCompound compound) { noTargetTicks = compound.getInteger("NoTargetTicks"); }
}