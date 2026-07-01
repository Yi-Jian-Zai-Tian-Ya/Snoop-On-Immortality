package soi.common.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import soi.SOI;

import java.util.Random;

public class GenStructure extends WorldGenerator
{
    private String structureName;
    public GenStructure(String name)
    {
        this.structureName = name;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        ResourceLocation location = new ResourceLocation(SOI.MODID, structureName);
        Template template = manager.get(world.getMinecraftServer(), location);
        if (template != null)
        {
            template.addBlocksToWorld(world, pos, new PlacementSettings());
            return true;
        }
        return false;
    }
}