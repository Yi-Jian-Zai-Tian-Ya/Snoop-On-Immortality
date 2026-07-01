package soi.common.inventory.rune;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import soi.common.entity.rune.EntityTeleportVoiceRune;
import soi.network.PacketHandler;
import soi.network.rune.MessageUpdateTeleportVoiceRune;

public class ContainerTeleportVoiceRune extends Container
{
    public final EntityPlayer player;
    private final ItemStack stack;
    private final EntityTeleportVoiceRune rune;
    private final boolean isEntity;

    private final EnumHand heldHand;
    private final BlockPos runePos;

    public ContainerTeleportVoiceRune(EntityPlayer player, ItemStack stack, EnumHand hand)
    {
        this.player = player;
        this.stack = stack;
        this.heldHand = hand;
        this.rune = null;
        this.runePos = BlockPos.ORIGIN;
        this.isEntity = false;
    }
    public ContainerTeleportVoiceRune(EntityPlayer player, EntityTeleportVoiceRune rune, double x, double y, double z)
    {
        this.player = player;
        this.rune = rune;
        this.runePos = new BlockPos(x, y, z);
        this.stack = null;
        this.heldHand = null;
        this.isEntity = true;
    }


    public boolean getIsEntity() { return isEntity; }
    public ItemStack getStack() { return stack; }
    public EntityTeleportVoiceRune getRune() { return rune; }

    public String getMessage()
    {
        if (isEntity && rune != null) return rune.getMessage();
        else
        {
            if (stack == null || stack.isEmpty() || !stack.hasTagCompound() || stack.getTagCompound() == null) return "";
            return stack.getTagCompound().getString("message");
        }
    }

    public String getSelect()
    {
        if (isEntity && rune != null) return rune.getSelect();
        else
        {
            if (stack == null || stack.isEmpty() || !stack.hasTagCompound() || stack.getTagCompound() == null) return "";
            return stack.getTagCompound().getString("select");
        }
    }

    public void setMessage(String message)
    {
        if (isEntity && rune != null) rune.setMessage(message);
        else
        {
            if (stack == null || stack.isEmpty()) return;
            if (!stack.hasTagCompound() || stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
            if (stack.getTagCompound() != null) stack.getTagCompound().setString("message", message);
        }
    }

    public void setSelect(String select)
    {
        if (isEntity && rune != null) rune.setSelect(select);
        else
        {
            if (stack == null || stack.isEmpty()) return;
            if (!stack.hasTagCompound() || stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
            if (stack.getTagCompound() != null) stack.getTagCompound().setString("select", select);
        }
    }

    public void syncDataToServer(String msg, String sel)
    {
        if (!player.world.isRemote) return;

        if (isEntity) PacketHandler.INSTANCE.sendToServer(new MessageUpdateTeleportVoiceRune(msg, sel, this.runePos));
        else
            PacketHandler.INSTANCE.sendToServer(new MessageUpdateTeleportVoiceRune(msg, sel, this.heldHand));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        if (isEntity) return rune != null && !rune.isDead;
        else return stack != null && !stack.isEmpty();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        syncDataToServer(getMessage(), getSelect());
    }
}