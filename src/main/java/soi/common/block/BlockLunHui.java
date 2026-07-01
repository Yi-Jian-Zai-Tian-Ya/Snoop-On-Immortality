package soi.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.client.gui.xiu_xian.lun_hui.GuiLunHuiMenu;

public class BlockLunHui extends Block
{
    public static final Block BLOCK_LUN_HUI = new BlockLunHui();
    public static final ItemBlock LUN_HUI = new ItemBlock(BLOCK_LUN_HUI);

    public BlockLunHui()
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.dimension != 2) return false;
        if (world.isRemote) Minecraft.getMinecraft().displayGuiScreen(new GuiLunHuiMenu());
        return true;
    }
}