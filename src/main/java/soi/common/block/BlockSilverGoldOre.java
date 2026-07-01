package soi.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import soi.SOI;

import java.util.Random;

import static soi.common.item.ordinary.ItemElectrum.ELECTRUM;

public class BlockSilverGoldOre extends Block
{
    public static final Block BLOCK_SILVER_GOLD_ORE = new BlockSilverGoldOre();
    public static final ItemBlock SILVER_GOLD_ORE = new ItemBlock(BLOCK_SILVER_GOLD_ORE);

    public BlockSilverGoldOre()
    {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 2);
        this.setHardness(4.0f);
        this.setResistance(10.0f);
        this.setLightLevel(0.64f);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ELECTRUM;
    }

    @Override
    public int quantityDropped(Random random)
    {
        int min = 1;
        int max = 2;
        return random.nextInt(max) + min;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        if (fortune > 0)
        {
            int bonusFactor = Math.max(random.nextInt(fortune + 2) - 1, 0);
            return this.quantityDropped(random) * (bonusFactor + 1);
        }
        else
        {
            return this.quantityDropped(random);
        }
    }
}