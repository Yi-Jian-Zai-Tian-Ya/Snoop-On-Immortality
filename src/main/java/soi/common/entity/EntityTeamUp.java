package soi.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import soi.client.gui.xiu_xian.lun_hui.GuiLunHuiTeamUp;

public class EntityTeamUp extends Entity
{
    public EntityTeamUp(World worldIn)
    {
        super(worldIn);
        setSize(1.0F, 2.0F);
        this.noClip = true;
        this.setNoGravity(true);
    }

    @Override public boolean canBeCollidedWith() { return true; }
    @Override public AxisAlignedBB getCollisionBoundingBox() { return this.getEntityBoundingBox(); }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand)
    {
        if (this.world.isRemote) Minecraft.getMinecraft().displayGuiScreen(new GuiLunHuiTeamUp());
        return EnumActionResult.SUCCESS;
    }

    @Override public void onUpdate() { super.onUpdate(); }
    @Override protected void entityInit() { }
    @Override protected void readEntityFromNBT(NBTTagCompound compound) { }
    @Override protected void writeEntityToNBT(NBTTagCompound compound) { }
}