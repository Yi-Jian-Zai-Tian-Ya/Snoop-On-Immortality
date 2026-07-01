package soi.common.pattern;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.common.util.EnumHelper;

public class PatternHandler
{
    public static void init()
    {
        addCraftingPattern("jin", new ItemStack(Blocks.DOUBLE_PLANT, 1, 5));
        addCraftingPattern("nie", new ItemStack(Items.ENDER_EYE));
        addCraftingPattern("jiang", new ItemStack(Items.CHORUS_FRUIT_POPPED));

    }

    public static BannerPattern addCraftingPattern(String id, ItemStack item)
    {
        final Class<?>[] paramTypes = { String.class, String.class, ItemStack.class };
        final Object[] paramValues = { id, id, item };
        return EnumHelper.addEnum(BannerPattern.class, id.toUpperCase(), paramTypes, paramValues);
    }
}