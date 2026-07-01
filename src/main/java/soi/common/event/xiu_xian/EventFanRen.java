package soi.common.event.xiu_xian;

import net.minecraft.advancements.Advancement;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import soi.SOI;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.api.xiu_xian.player.IXiuXianPlayer;
import soi.api.xiu_xian.player.XiuXianPlayerCapabilities;
import soi.common.potion.PotionFanRen;

import java.util.Objects;
import java.util.Random;

public class EventFanRen
{
    private static final Potion FanRen = PotionFanRen.FAN_REN;
    private static final String[] GONG_FAS = { "chang_chun", "qing_qi", "tai_yang" };
    private static final Random Rand = new Random();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null); if (XiuXian == null) return;
        IXiuXianPlayer XiuXianPlayer = player.getCapability(XiuXianPlayerCapabilities.XIU_XIAN_PLAYER, null); if (XiuXianPlayer == null) return;

        if (!player.isPotionActive(PotionFanRen.FAN_REN) && XiuXian.getJingJie() < 4 && player.dimension != 2) applyMortalDeBuffs(player, XiuXian);
        if (player.isPotionActive(PotionFanRen.FAN_REN) && player.getActivePotionEffect(PotionFanRen.FAN_REN).getAmplifier() != 3 - XiuXian.getJingJie()) updateMortalDeBuffs(player, XiuXian);
    }

    @SubscribeEvent
    public void onTamedWolf(AdvancementEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        Advancement adv = event.getAdvancement();
        if (!adv.getId().equals(new ResourceLocation(SOI.MODID, "xiu_xian/tamed_wolf"))) return;

        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null);
        if (XiuXian == null || XiuXian.getJingJie() != 0) return;

        WorldServer world = player.getServerWorld();
        BlockPos grass = findRandomTopGrassBlock(world, player.getPosition());
        if (grass == null) { return; }

        world.spawnParticle(player, EnumParticleTypes.BARRIER, false, grass.getX() + 0.5D, grass.getY() + 1.0D, grass.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0);
        BlockPos potPos = grass.down(); IBlockState pot = Blocks.FLOWER_POT.getDefaultState(); world.setBlockState(potPos, pot, 3);

        TileEntityFlowerPot potTE = (TileEntityFlowerPot) world.getTileEntity(potPos);if (potTE == null) return; NBTTagCompound potNBT = new NBTTagCompound();
        potNBT.setString("Item", "soi:" + GONG_FAS[Rand.nextInt(GONG_FAS.length)]); potTE.readFromNBT(potNBT); potTE.markDirty();

        ItemStack shovel = new ItemStack(Items.IRON_SHOVEL);
        shovel.addEnchantment(Objects.requireNonNull(Enchantment.getEnchantmentByID(32)), 8); shovel.setItemDamage(shovel.getMaxDamage());
        EntityItem entityI = player.dropItem(shovel, false); if (entityI != null) { entityI.setNoPickupDelay(); entityI.setOwner(player.getName()); }
        player.sendMessage(new TextComponentTranslation("message.fan_ren.tamedWolf", TextFormatting.DARK_RED + String.format("%.2f", grass.getX() + 0.5D - player.posX) + "," + String.format("%.2f", grass.getZ() + 0.5D - player.posZ)));
    }

    private BlockPos findRandomTopGrassBlock(WorldServer world, BlockPos center)
    {
        for (int i = 0; i < 64; i++)
        {
            int x = new Random().nextInt(17) - 8;
            int z = new Random().nextInt(17) - 8;
            BlockPos topPos = world.getTopSolidOrLiquidBlock(center.add(x, 0, z)).down();

            if (world.getBlockState(topPos).getBlock() instanceof BlockGrass) return topPos;
        }
        return null;
    }

    public static void applyMortalDeBuffs(EntityPlayer player, IXiuXian XiuXian) { player.addPotionEffect(new PotionEffect(FanRen, Integer.MAX_VALUE, Math.max(0, 3 - XiuXian.getJingJie()), true, true)); }
    public static void updateMortalDeBuffs(EntityPlayer player, IXiuXian XiuXian) { player.removePotionEffect(FanRen); if (XiuXian.getJingJie() < 4) applyMortalDeBuffs(player, XiuXian); }
}