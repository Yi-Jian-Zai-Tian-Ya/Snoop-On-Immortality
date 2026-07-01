package soi.common.entity.rune;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import soi.SOI;
import soi.common.CommonProxy;
import soi.common.item.rune.ItemTeleportVoiceRune;

public class EntityTeleportVoiceRune extends EntityRuneBase
{
    public static final DataParameter<String> MESSAGE = EntityDataManager.createKey(EntityTeleportVoiceRune.class, DataSerializers.STRING);
    public static final DataParameter<String> SELECT = EntityDataManager.createKey(EntityTeleportVoiceRune.class, DataSerializers.STRING);
    public static final DataParameter<Float> SPEED = EntityDataManager.createKey(EntityTeleportVoiceRune.class, DataSerializers.FLOAT);

    private int noTargetTicks = 0;
    private static final int MAX_NO_TARGET_TICKS = 200;
    private boolean hasReachedTarget = false;

    public EntityTeleportVoiceRune(World worldIn)
    {
        super(worldIn);
        this.noClip = true;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataManager.register(MESSAGE, "");
        dataManager.register(SELECT, "");
        dataManager.register(SPEED, 0.0F);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (this.world.isRemote) return;

        EntityPlayer target = world.getPlayerEntityByName(getSelect());
        if (target == null || !target.isEntityAlive() || target.dimension != world.provider.getDimension())
        {
            noTargetTicks++;
            if (noTargetTicks > MAX_NO_TARGET_TICKS) { dropRuneItem(); setDead(); }
            return;
        }

        noTargetTicks = 0;

        Vec3d targetPos = new Vec3d(target.posX, target.posY + 0.5D, target.posZ);
        Vec3d selfPos = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d dirMove = targetPos.subtract(selfPos);
        Vec3d dirLook = selfPos.subtract(targetPos);

        double distance = dirMove.lengthVector();
        Vec3d dirNormal = dirMove.normalize();
        double moveSpeed = getSpeed() / 20.0D;

        if (hasReachedTarget) { if (distance > 1.0D) hasReachedTarget = false; else return; }

        float targetYaw = -90F + (float) Math.atan2(dirLook.z, dirLook.x) * 180F / (float) Math.PI;
        float targetPitch = -90F + (float) Math.atan2(dirMove.y, Math.sqrt(dirMove.x * dirMove.x + dirMove.z * dirMove.z)) * 180F / (float) Math.PI;

        setRotation(targetYaw, targetPitch);

        double newX = this.posX + dirNormal.x * moveSpeed;
        double newY = this.posY + dirNormal.y * moveSpeed;
        double newZ = this.posZ + dirNormal.z * moveSpeed;

        this.moveTo(newX, newY, newZ);

        if (distance < 2.0D && !hasReachedTarget) hasReachedTarget = true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (this.isEntityInvulnerable(source)) return false;
        else if (!this.world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null) return false;
            else
            {
                this.dropRuneItem();
                this.setDead();
                return true;
            }
        }
        return true;
    }

    private void dropRuneItem()
    {
        ItemStack runeItem = this.getRuneItemStack();
        if (!runeItem.isEmpty()) this.entityDropItem(runeItem, 0.5F);
    }

    public void setDataFromItem(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null) { setMessage(nbt.getString("message")); setSelect(nbt.getString("select")); }
        }
    }

    public void setSpeed(float speed) { dataManager.set(SPEED, speed); }
    public float getSpeed() { return dataManager.get(SPEED); }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand)
    {
        if (!this.world.isRemote && player instanceof EntityPlayerMP)
        {
            BlockPos pos = getPosition();
            player.openGui(SOI.instance, CommonProxy.GUI_TELEPORT_VOICE_RUNE, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return EnumActionResult.SUCCESS;
    }

    public String getMessage() { return dataManager.get(MESSAGE); }
    public void setMessage(String message) { dataManager.set(MESSAGE, message); }
    public String getSelect() { return dataManager.get(SELECT); }
    public void setSelect(String select) { dataManager.set(SELECT, select); }

    @Override
    protected ItemStack getRuneItemStack()
    {
        ItemStack stack = new ItemStack(ItemTeleportVoiceRune.TELEPORT_VOICE_RUNE);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("message", getMessage());
        nbt.setString("select", getSelect());
        stack.setTagCompound(nbt);
        return stack;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setMessage(nbt.getString("message"));
        setSelect(nbt.getString("select"));
        noTargetTicks = nbt.getInteger("noTargetTicks");
        setSpeed(nbt.getFloat("speed"));
        hasReachedTarget = nbt.getBoolean("hasReachedTarget");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setString("message", getMessage());
        nbt.setString("select", getSelect());
        nbt.setInteger("noTargetTicks", noTargetTicks);
        nbt.setFloat("speed", getSpeed());
        nbt.setBoolean("hasReachedTarget", hasReachedTarget);
    }

    public static EntityTeleportVoiceRune getRune(World world, int posX, int posY, int posZ)
    {
        return world.getEntitiesWithinAABB(
                EntityTeleportVoiceRune.class,
                new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D),
                input -> input.getPosition().equals(new BlockPos(posX, posY, posZ)) && !input.isDead
        ).stream().findFirst().orElse(null);
    }
}