package soi.common.world.dimension;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import soi.common.entity.EntityTeamUp;
import soi.common.world.GenStructure;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class LunHuiChunkGenerator implements IChunkGenerator
{
    private final World world;

    public LunHuiChunkGenerator(World world)
    {
        this.world = world;
    }

    @Override
    public Chunk generateChunk(int x, int z)
    {
        ChunkPrimer primer = new ChunkPrimer();

        for (int blockX = 0; blockX < 16; blockX++)
        {
            for (int blockZ = 0; blockZ < 16; blockZ++)
                primer.setBlockState(blockX, 0, blockZ, Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.WHITE));
        }

        Chunk chunk = new Chunk(world, primer, x, z);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z)
    {
        int centerX = (x << 4) + 8;
        int centerZ = (z << 4) + 8;

        if (x == 0 && z == 0)
        {
            if (Math.abs(centerX) <= 16 && Math.abs(centerZ) <= 16) new GenStructure("lun_hui").generate(world, world.rand, new BlockPos(-1, 1, -1));

            if (world.getEntitiesWithinAABB(EntityTeamUp.class, new AxisAlignedBB(new BlockPos(0.5, 1.5, 5.5))).isEmpty())
            {
                EntityTeamUp entity = new EntityTeamUp(world);
                entity.setPosition(0.5, 1.5, 5.5);
                entity.rotationYaw = 180.0F;
                world.spawnEntity(entity);
            }
        }
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) { }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        return false;
    }
}