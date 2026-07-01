package soi.common.item.dao_zai.gong_fa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import soi.SOI;
import soi.api.dao_zai.DaoZaiAPI;
import soi.api.dao_zai.DaoZaiType;
import soi.api.dao_zai.IDaoZai;
import soi.api.dao_zai.cap.IDaoZaiItemHandler;
import soi.api.dao_zai.gong_fa.GongFaCapabilities;
import soi.api.dao_zai.gong_fa.IGongFa;
import soi.api.ming_ge.IMingGe;
import soi.api.ming_ge.MingGeCapabilities;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_zai.gong_fa.MessageGongFaWorn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemGongFaBase extends Item implements IDaoZai
{
    protected String LingGen = "无";
    protected boolean AnyLingGen = false;
    protected float LingLi = 10.0F;
    protected float HuDun = 0.0F;
    protected float ShenShi = 2.5F;
    protected int MaxJingJie = 13;

    public ItemGongFaBase()
    {
        super();
        this.maxStackSize = 1;
        this.setCreativeTab(SOI.ZAI);
    }

    @Override public DaoZaiType getDaoType(ItemStack stack) { return DaoZaiType.GONG_FA; }

    public int getMaxJingJie(ItemStack stack) { return this.MaxJingJie; }

    @Override public void onEquipped(ItemStack stack, EntityLivingBase entity)
    {
        entity.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, .75F, 2F);
        if (!(entity instanceof EntityPlayerMP)) return;

        IGongFa GongFa = entity.getCapability(GongFaCapabilities.GONG_FA, null); if (GongFa == null) return;
        GongFa.loadGongFa(String.valueOf(this.getRegistryName()), this.getMaxJingJie(stack), this.LingLi, this.HuDun, this.ShenShi);
        GongFa.syncGongFa((EntityPlayerMP) entity);
    }
    @Override public void onUnequipped(ItemStack stack, EntityLivingBase entity) { entity.playSound(SoundEvents.ITEM_SHIELD_BREAK, .75F, 2F); }
    @Override public boolean canEquip(ItemStack stack, EntityLivingBase entity) { if (!(entity instanceof EntityPlayer)) return false; EntityPlayer player = (EntityPlayer) entity; if (hasSameItem(player)) return false; return isSuitableLingGen(stack, (EntityPlayer) entity); }
    @Override public boolean hasSameItem(EntityPlayer player) { IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(player); for (int i = 0; i < DaoZais.getSlots(); i++) if (DaoZais.getStackInSlot(i).getItem().equals(this)) return true; return false; }
    @Override public boolean isSuitableLingGen(ItemStack stack, EntityPlayer player)
    {
        IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null);
        if (MingGe == null) return false;
        if (this.AnyLingGen) return this.LingGen.chars().anyMatch(c -> MingGe.getLingGen().indexOf(c) > -1);
        return this.LingGen.equals("无") || MingGe.getLingGen().contains(this.LingGen); }

    @Override public void onWornTick(ItemStack stack, EntityLivingBase entity, int slot)
    {
        if (!(entity instanceof EntityPlayerMP)) return; EntityPlayerMP player = (EntityPlayerMP) entity;
        if (!player.isRiding() && player.getRidingEntity() == null) return;

        IGongFa GongFaCap = player.getCapability(GongFaCapabilities.GONG_FA, null); if (GongFaCap == null) return;
        IXiuXian XiuXian = player.getCapability(XiuXianCapabilities.XIU_XIAN, null); if (XiuXian == null) return;
        IMingGe MingGe = player.getCapability(MingGeCapabilities.MING_GE, null); if (MingGe == null) return;

        String id = String.valueOf(this.getRegistryName());
        int JingJie = GongFaCap.getJingJie(id);

        if (JingJie > XiuXian.getJingJie()) { if ((player.ticksExisted - 42 * (slot - 3)) % 100 == 0) player.sendStatusMessage(new TextComponentTranslation("message.dao_zai.gongFa.jingJie.tooLow", stack.getDisplayName()), true); return; }
        if (GongFaCap.isMaxAllJingJie(id)) return;
        float v = (XiuXian.getJingJie() - JingJie) * 0.0015F + 0.00025F * MingGe.getVXing(this.LingGen, this.LingGen.length() == 1);
        GongFaCap.addProgress(id, v, player);

        if (player.ticksExisted % 10 == 0) ((WorldServer) player.world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, false, player.posX, player.posY + 1.0D, player.posZ, 1, 0.25D, 0.25D, 0.25D, .75D, new int[EnumParticleTypes.ENCHANTMENT_TABLE.getArgumentCount()]);
        if ((player.ticksExisted - 42 * (slot - 3)) % 200 == 0) PacketHandler.INSTANCE.sendTo(new MessageGongFaWorn(true, new ItemStack(this)), player);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack held = player.getHeldItem(hand);
        if (world.isRemote) { if (this.canEquip(held, player)) player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, .75F, 2F); return new ActionResult<ItemStack>(EnumActionResult.FAIL, held); }

        IDaoZaiItemHandler DaoZais = DaoZaiAPI.getDaoZaiHandler(player);

        for (int i = 0; i < DaoZais.getSlots(); i++)
            if (DaoZais.getStackInSlot(i).isEmpty() && DaoZais.isItemValidForSlot(i, held, player))
            {
                DaoZais.setStackInSlot(i, held.copy());
                if (!player.isCreative()) player.setHeldItem(hand, ItemStack.EMPTY);
                this.onEquipped(held, player); break;
            }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (worldIn == null || !worldIn.isRemote) return;
        EntityPlayer player = Minecraft.getMinecraft().player; if (player == null) return;

        IGongFa GongFaCap = player.getCapability(GongFaCapabilities.GONG_FA, null);
        if (GongFaCap == null) return;
        NBTTagCompound GongFas = GongFaCap.getGongFas();
        String id = String.valueOf(this.getRegistryName());;

        if (!this.isSuitableLingGen(stack, player)) { tooltip.add(TextFormatting.DARK_RED + I18n.format("tooltip.lingGen.unsuitable")); return; }
        if (GongFaCap.getGongFaTag(GongFas, id) == null) { tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.gongFa.noCultivate")); return; }

        int JingJie = GongFaCap.getJingJie(id);
        double Progress = GongFaCap.getProgress(id);

        tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.gongFa.jingJie", (GongFaCap.isMaxAllJingJie(id)) ? TextFormatting.GOLD + I18n.format("jingJie.max") : TextFormatting.YELLOW + I18n.format("jingJie." + JingJie)));

        char[] barChars = new char[20];
        double base = GongFaCap.getBaseNum(JingJie);
        double percent = Progress / base;
        int fillCount = (int) Math.min(20, Math.round(20 * percent));

        Arrays.fill(barChars, '|');
        String barStr = new String(barChars);

        StringBuilder barBuilder = new StringBuilder();
        if (GongFaCap.isMaxAllJingJie(id)) barBuilder.append(TextFormatting.GOLD).append(barStr);
        else { barBuilder.append(TextFormatting.YELLOW); barBuilder.append(barStr, 0, fillCount);
            barBuilder.append(TextFormatting.GRAY); barBuilder.append(barStr.substring(fillCount)); }

        tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.gongFa.progress") + " " + barBuilder);
    }

}