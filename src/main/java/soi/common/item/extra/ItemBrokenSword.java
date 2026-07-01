package soi.common.item.extra;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBrokenSword extends ItemSword
{
    public final static Item BROKEN_SWORD = new ItemBrokenSword();

    public ItemBrokenSword()
    {
        super(ToolMaterial.WOOD);
        this.maxStackSize = 1;
        this.setMaxDamage(0);
        this.setCreativeTab(null);
    }

    @Override
    public float getAttackDamage() { return 0.0F; }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() { return true; }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Broken Sword Modifier", -1.0D, 1));
        }
        return multimap;
    }
}