package soi.common.world.structure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class StructureLaiFu extends MapGenStructure
{
    private static final int SPACING = 32;
    private static final int SEPARATION = 8;

    @Override
    public String getStructureName() { return "LaiFu"; }

    public void setWorld(World world) { this.world = world; }

    @Override
    public boolean generateStructure(World worldIn, Random random, ChunkPos chunkPos)
    {
        this.world = worldIn;
        return super.generateStructure(worldIn, random, chunkPos);
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
    {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, SPACING, SPACING, 10387313, true, 100, findUnexplored);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0) chunkX -= SPACING - 1;
        if (chunkZ < 0) chunkZ -= SPACING - 1;

        int k = chunkX / SPACING;
        int l = chunkZ / SPACING;
        Random random = this.world.setRandomSeed(k, l, 10387313);
        k = k * SPACING;
        l = l * SPACING;
        k = k + random.nextInt(SPACING - SEPARATION);
        l = l + random.nextInt(SPACING - SEPARATION);

        return i == k && j == l;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new Start(this.world, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart
    {

        public Start() { super(); }

        public Start(World worldIn, Random random, int chunkX, int chunkZ)
        {
            super(chunkX, chunkZ);
            this.create(worldIn, random, chunkX, chunkZ);
        }

        private void create(World worldIn, Random random, int chunkX, int chunkZ)
        {
            int x = (chunkX << 4) + 8;
            int z = (chunkZ << 4) + 8;
            int y = worldIn.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();

            EnumFacing facing = EnumFacing.HORIZONTALS[random.nextInt(4)];
            LaiFuComponent component = new LaiFuComponent(random, x, y, z, facing);

            this.components.add(component);
            component.buildComponent(this.components.get(0), this.components, random);
            this.updateBoundingBox();
        }

        @Override
        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structureBB)
        {
            super.generateStructure(worldIn, rand, structureBB);
        }
    }

    public static class LaiFuComponent extends StructureComponent
    {

        private static final ResourceLocation STRUCTURE_TEMPLATE = new ResourceLocation("soi", "lai_fu");

        private int posX;
        private int posY;
        private int posZ;
        private EnumFacing facing;

        public LaiFuComponent() { super(0); }

        public LaiFuComponent(Random random, int x, int y, int z, EnumFacing facing)
        {
            super(0);
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.facing = facing;
            this.setCoordBaseMode(facing);

            this.boundingBox = new StructureBoundingBox(
                    x - 16, y, z - 16,
                    x + 16, y + 31, z + 16
            );
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setInteger("PosX", this.posX);
            tagCompound.setInteger("PosY", this.posY);
            tagCompound.setInteger("PosZ", this.posZ);
            tagCompound.setInteger("Facing", this.facing.getHorizontalIndex());
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager templateManager)
        {
            this.posX = tagCompound.getInteger("PosX");
            this.posY = tagCompound.getInteger("PosY");
            this.posZ = tagCompound.getInteger("PosZ");
            this.facing = EnumFacing.HORIZONTALS[tagCompound.getInteger("Facing")];
            this.setCoordBaseMode(this.facing);
        }

        @Override
        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) { }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            MinecraftServer server = worldIn.getMinecraftServer();
            if (server == null) return false;

            TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();
            Template template = templateManager.getTemplate(server, STRUCTURE_TEMPLATE);

            BlockPos templateSize = template.getSize();
            int avgGround = this.getAverageGroundLevel(worldIn, templateSize);
            if (avgGround >= 0) this.posY = avgGround;

            this.boundingBox = new StructureBoundingBox(
                    this.posX, this.posY, this.posZ,
                    this.posX + templateSize.getX() - 1,
                    this.posY + templateSize.getY() - 1,
                    this.posZ + templateSize.getZ() - 1
            );

            PlacementSettings placementSettings = new PlacementSettings();
            placementSettings.setMirror(Mirror.NONE);
            placementSettings.setRotation(this.getRotationFromFacing());
            placementSettings.setIgnoreEntities(false);
            placementSettings.setIntegrity(1.0f);

            BlockPos basePos = new BlockPos(this.posX, this.posY, this.posZ);

            this.fillBelow(worldIn, basePos, templateSize);

            template.addBlocksToWorld(worldIn, basePos, placementSettings);

            return true;
        }

        private int getAverageGroundLevel(World world, BlockPos templateSize)
        {
            int totalHeight = 0;
            int count = 0;
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int x = 0; x < templateSize.getX(); x += 4)
            {
                for (int z = 0; z < templateSize.getZ(); z += 4)
                {
                    int worldX = this.posX + x;
                    int worldZ = this.posZ + z;

                    for (int y = 255; y > 0; y--)
                    {
                        mutablePos.setPos(worldX, y, worldZ);
                        IBlockState state = world.getBlockState(mutablePos);

                        if (state.isFullBlock() && state.isOpaqueCube() && !state.getMaterial().isLiquid())
                        {
                            totalHeight += y + 1;
                            count++;
                            break;
                        }
                    }
                }
            }

            return count > 0 ? totalHeight / count : this.posY;
        }

        private Rotation getRotationFromFacing()
        {
            switch (this.facing)
            {
                case WEST:  return Rotation.CLOCKWISE_90;
                case NORTH: return Rotation.CLOCKWISE_180;
                case EAST:  return Rotation.COUNTERCLOCKWISE_90;
                case SOUTH:
                default:    return Rotation.NONE;
            }
        }

        private void fillBelow(World world, BlockPos basePos, BlockPos templateSize)
        {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int x = 0; x < templateSize.getX(); x++)
            {
                for (int z = 0; z < templateSize.getZ(); z++)
                {
                    mutablePos.setPos(
                            basePos.getX() + x,
                            basePos.getY() - 1,
                            basePos.getZ() + z
                    );

                    for (int y = basePos.getY() - 1; y > 0; y--)
                    {
                        mutablePos.setY(y);
                        IBlockState state = world.getBlockState(mutablePos);

                        if (state.getBlock() == Blocks.AIR || state.getMaterial().isLiquid()) world.setBlockState(mutablePos, Blocks.DIRT.getDefaultState(), 2);
                        else if (state.isFullBlock() && state.isOpaqueCube()) break;
                    }
                }
            }
        }
    }
}