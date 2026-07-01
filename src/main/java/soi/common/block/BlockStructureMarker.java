package soi.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStructureMarker extends Block
{
    public static final Block BLOCK_STRUCTURE_MARKER = new BlockStructureMarker();
    public static final ItemBlock STRUCTURE_MARKER = new ItemBlock(BLOCK_STRUCTURE_MARKER);

    public BlockStructureMarker()
    {
        super(Material.GLASS);
        this.setHardness(-1.0F);
        this.setResistance(6000000.0F);
        this.setLightLevel(0.64f);
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityStructureMarker();
    }

    public static class TileEntityStructureMarker extends TileEntity
    {
        private String structureName = "None";

        public TileEntityStructureMarker() { }

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            super.readFromNBT(compound);
            if (compound.hasKey("StructureName"))
            {
                this.structureName = compound.getString("StructureName");
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound)
        {
            super.writeToNBT(compound);
            compound.setString("StructureName", structureName);
            return compound;
        }

        public String getStructureName()
        {
            return structureName;
        }
    }
}