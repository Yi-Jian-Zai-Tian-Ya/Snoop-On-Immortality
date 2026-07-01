package soi.common.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import soi.SOI;

import java.util.Random;

public class BlockNeonDressHerbs extends BlockBush
{
    public static final BlockBush BLOCK_NEON_DRESS_HERBS = new BlockNeonDressHerbs();
    public static final ItemBlock NEON_DRESS_HERBS = new ItemBlock(BLOCK_NEON_DRESS_HERBS);

    public BlockNeonDressHerbs()
    {
        super(Material.PLANTS);
        this.setHardness(2.0F);
        this.setCreativeTab(SOI.TAB);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return new ItemStack(NEON_DRESS_HERBS).getItem();
    }
}