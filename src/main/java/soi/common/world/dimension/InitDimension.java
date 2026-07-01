package soi.common.world.dimension;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class InitDimension
{
    public static final DimensionType LUN_HUI_DIM = DimensionType.register("LunHui", "_dim", 2, LunHuiWorldProvider.class, false);

    public static void registerDimensions() { DimensionManager.registerDimension(2, LUN_HUI_DIM); }
}