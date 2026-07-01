package soi.common.world.dimension;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;

public class LunHuiWorldProvider extends WorldProvider
{
    public LunHuiWorldProvider()
    {
        this.biomeProvider = new BiomeProviderSingle(Biomes.VOID);
    }

    @Nonnull
    @Override
    public DimensionType getDimensionType()
    {
        return InitDimension.LUN_HUI_DIM;
    }

    @Nonnull
    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return new LunHuiChunkGenerator(world);
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }

    @Nonnull
    @Override
    public BlockPos getSpawnPoint()
    {
        return new BlockPos(0, 2, 0);
    }

    @Nonnull
    @Override
    public BlockPos getRandomizedSpawnPoint()
    {
        return getSpawnPoint();
    }

    @Override
    public int getAverageGroundLevel()
    {
        return 1;
    }
}