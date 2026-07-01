package soi.common.item.rune;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import soi.SOI;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.common.CommonProxy;
import soi.common.entity.rune.EntityTeleportVoiceRune;
import soi.util.LocUtils;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTeleportVoiceRune extends ItemRune
{
    public static final ItemTeleportVoiceRune TELEPORT_VOICE_RUNE = new ItemTeleportVoiceRune();

    @Override
    public boolean hasEffect(ItemStack stack) { return true; }

    private void initNBT(ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return;
        if (!nbt.hasKey("message")) nbt.setString("message", "");
        if (!nbt.hasKey("select")) nbt.setString("select", "");
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        initNBT(stack);

        if (player.isSneaking())
        {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) return new ActionResult<>(EnumActionResult.FAIL, stack);
            if (nbt.getString("select").equals(player.getName()) || nbt.getString("select").isEmpty()) return new ActionResult<>(EnumActionResult.FAIL, stack);
            RayTraceResult ray = player.rayTrace(5.0, 1.0f);

            if (ray != null && ray.typeOfHit == RayTraceResult.Type.ENTITY) return new ActionResult<>(EnumActionResult.FAIL, stack);

            if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK)
                if (world.getBlockState(ray.getBlockPos()).getBlock().hasTileEntity(world.getBlockState(ray.getBlockPos()))) return new ActionResult<>(EnumActionResult.FAIL, stack);

            if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

            Vec3d pos = player.getPositionVector().add(player.getLookVec().scale(2.0));

            EntityTeleportVoiceRune entity = new EntityTeleportVoiceRune(world);
            entity.setPosition(pos.x, pos.y + player.getEyeHeight() - 0.5D, pos.z);

            Vec3d runeSpawnPos = new Vec3d(pos.x, pos.y + player.getEyeHeight(), pos.z);
            Vec3d playerPos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            Vec3d lookToPlayer = playerPos.subtract(runeSpawnPos);
            double horizontalLen = Math.sqrt(lookToPlayer.x * lookToPlayer.x + lookToPlayer.z * lookToPlayer.z);

            float spawnYaw = (float)(Math.atan2(lookToPlayer.z, lookToPlayer.x) * 180F / Math.PI) - 90F;
            float spawnPitch = (float)(Math.atan2(horizontalLen, lookToPlayer.y) * 180F / Math.PI) - 90F;

            entity.rotationYaw = spawnYaw;
            entity.rotationPitch = spawnPitch;

            entity.setDataFromItem(stack);

            IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
            float speed = 0.0F;
            if (XiuXian != null) speed = (float) (Math.pow(1.5, XiuXian.getJingJie()) + Math.log(XiuXian.getShenShi()) / Math.log(2) * 0.2);
            entity.setSpeed(speed);

            if (world.spawnEntity(entity))
            {
                stack.shrink(1);
                player.sendMessage(new TextComponentTranslation("message.rune.teleport_voice.activated", entity.getSelect()));
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        else
        {
            if (world.isRemote) player.openGui(SOI.instance, CommonProxy.GUI_TELEPORT_VOICE_RUNE, world, hand == EnumHand.OFF_HAND ? 1 : 0, 42000, 0);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        initNBT(stack);
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return;
        tooltip.add(TextFormatting.GREEN + LocUtils.translateRecursive(getUnlocalizedName(stack) + ".tooltip.message", nbt.getString("message").isEmpty() ? "false" : "true"));
        tooltip.add(TextFormatting.GREEN + LocUtils.translateRecursive(getUnlocalizedName(stack) + ".tooltip.select", nbt.getString("select")));
    }

    /*

     */
}