package soi.client.gui.xiu_xian.lun_hui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import soi.common.entity.corpse.PlayerSkin;
import soi.network.MessageRequestGameRule;
import soi.network.PacketHandler;
import soi.util.GuiUtils;
import soi.util.lun_hui.LunHuiTeam;
import soi.network.xiu_xian.lun_hui.MessageIVZhuLunHui;
import soi.network.xiu_xian.lun_hui.MessageLunHuiTeamKick;
import soi.network.xiu_xian.lun_hui.MessageLunHuiTeamLeave;
import soi.network.xiu_xian.lun_hui.MessageLunHuiTeamReady;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiLunHuiMenu extends GuiScreen
{
    private static LunHuiTeam teamData = null;
    public static void setClientLunHuiTeam(LunHuiTeam team) { teamData = team; }

    private static int LocX;
    private static int LocY;

    private GuiButton IVZhu;
    private boolean openSelectPage = false;

    private int currentListIndex = -1;
    private int listSize = 0;
    private int scrollOffset = 0;
    private int selectedItem = -1;
    private static final int ITEMS_PER_PAGE = 12;

    private int nian = -1, yue = -1, ri = -1, shi = -1;

    public GuiLunHuiMenu() { }

    @Override
    public void initGui()
    {
        super.initGui();
        LocX = width / 2;
        LocY = height / 2;

        buttonList.clear();
        GuiButton LunHui = new GuiButton(0, LocX - 100, LocY + 10, 200, 20, new TextComponentTranslation("button.lun_hui.run").getFormattedText());
        LunHui.enabled = (nian != -1 && yue != -1 && ri != -1 && shi != -1) || (nian == -1 && yue == -1 && ri == -1 && shi == -1);
        buttonList.add(LunHui);

        if (!openSelectPage)
        {
            IVZhu = new GuiButton(1, LocX - 100, LocY - 20, 200, 20, new TextComponentTranslation("button.lun_hui.selectIVZhu").getFormattedText());
            IVZhu.enabled = false;
            buttonList.add(IVZhu);
            PacketHandler.INSTANCE.sendToServer(new MessageRequestGameRule("enableOptionalIVZhu"));
        }
        else
        {
            GuiButton NianZhu = new GuiButton(2, LocX - 100, LocY - 20, 50, 20, nian == -1 ? "年柱"
                    : TextFormatting.GOLD.toString() + TextFormatting.BOLD + GetGanZhi(nian, 1));
            NianZhu.enabled = (currentListIndex != 1);
            buttonList.add(NianZhu);
            GuiButton YueZhu = new GuiButton(3, LocX - 50, LocY - 20, 50, 20, yue == -1 ? "月柱"
                    : TextFormatting.GOLD.toString() + TextFormatting.BOLD + GetGanZhi(yue, 2));
            YueZhu.enabled = (nian != -1 && currentListIndex != 2);
            buttonList.add(YueZhu);

            GuiButton RiZhu = new GuiButton(4, LocX, LocY - 20, 50, 20, ri == -1 ? "日柱"
                    : TextFormatting.GOLD.toString() + TextFormatting.BOLD + GetGanZhi(ri, 3));
            RiZhu.enabled = (currentListIndex != 3);
            buttonList.add(RiZhu);
            GuiButton ShiZhu = new GuiButton(5, LocX + 50, LocY - 20, 50, 20, shi == -1 ? "时柱"
                    : TextFormatting.GOLD.toString() + TextFormatting.BOLD + GetGanZhi(shi, 4));
            ShiZhu.enabled = (ri != -1 && currentListIndex != 4);
            buttonList.add(ShiZhu);
        }

        if (teamData != null && !teamData.members.isEmpty()) buttonList.add(new GuiButton(6, width - 80, height - 25, 75, 20, new TextComponentTranslation("button.lun_hui.leaveTeam").getFormattedText()));

    }

    public void updateRuleValue(String ruleName, boolean value)
    {
        if ("enableOptionalIVZhu".equals(ruleName) && IVZhu != null) IVZhu.enabled = value;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!openSelectPage && buttonList.size() > 1 && buttonList.get(1).isMouseOver())
            this.drawHoveringText(new TextComponentTranslation(buttonList.get(1).enabled
                    ? "button.lun_hui.selectIVZhu.text.enabled"
                    : "button.lun_hui.selectIVZhu.text.disabled").getFormattedText(), mouseX, mouseY);

        if (openSelectPage && currentListIndex != -1) drawScrollableList(mouseX, mouseY);

        if (teamData != null && !teamData.members.isEmpty())
        {
            int startX = width - 37;
            int startY = 5;
            int size = 32;
            int col = Math.max(1, (height - 25) / 37);

            for (int i = 0; i < teamData.members.size(); i++)
            {
                LunHuiTeam.TeamMember m = teamData.members.get(i);
                int bx = startX - (i / col) * 37;
                int by = startY + (i % col) * 37;

                boolean hover = mouseX >= bx && mouseX <= bx + size && mouseY >= by && mouseY <= by + size;
                if (hover) drawRect(bx - 1, by - 1, bx + size + 1, by + size + 1, 0xFFFFAA00);
                drawRect(bx, by, bx + size, by + size, m.isReady ? 0xFF00FF00 : 0xFFFF0000);

                GlStateManager.color(1F, 1F, 1F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                ResourceLocation skin = PlayerSkin.getSkin(m.uuid, m.name);
                mc.getTextureManager().bindTexture(skin);

                drawScaledCustomSizeModalRect(bx + 4, by + 4, 8, 8, 8, 8, 24, 24, 64,64);
                GuiUtils.drawScaledCustomSizeModalRect(bx + 2.5, by + 2.5, 40, 8, 8, 8, 27, 27, 64,64);
                GlStateManager.disableBlend();

                if (hover)
                {
                    String tip = m.name + ":" + (new TextComponentTranslation(m.isReady ? "button.generic.ready" : "button.generic.notReady").getFormattedText());
                    drawHoveringText(tip, mouseX, mouseY);
                    if (Mouse.isButtonDown(1) && teamData.owner.equals(mc.player.getUniqueID())) PacketHandler.INSTANCE.sendToServer(new MessageLunHuiTeamKick(m.uuid));
                }
            }
        }
    }

    private void drawScrollableList(int mouseX, int mouseY)
    {
        int ListX = LocX - 100 + (currentListIndex - 1) * 50;

        drawRect(ListX, LocY, ListX + 50, LocY + 240, 0xFF000000);
        drawRect(ListX + 1, LocY + 1, ListX + 49, LocY + 239, 0xFF555555);

        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            int actualIndex = i + scrollOffset;
            if (actualIndex >= listSize) break;

            int itemY = LocY + i * 20;

            boolean hovered = mouseX >= ListX + 1 && mouseX <= ListX + 49 && mouseY >= itemY && mouseY <= itemY + 20;

            if (hovered) drawRect(ListX + 1, itemY, ListX + 49, itemY + 20, 0xFFAAAAAA);

            int color = (actualIndex == selectedItem) ? 0xFF00FF00 : 0xFFFFFFFF;

            String itemName = GetGanZhi(actualIndex, currentListIndex) + " #" + (actualIndex + 1);
            this.fontRenderer.drawString(itemName, ListX + 5, itemY + 6, color);
        }

        if (listSize > ITEMS_PER_PAGE)
        {
            int scrollBarHeight = (int)(12.0F / listSize * 240);
            int scrollBarY = LocY + (int)((float)scrollOffset / listSize * 240);
            drawRect(ListX + 50 - 4, scrollBarY, ListX + 50, scrollBarY + scrollBarHeight, 0xFF000000);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        if (!openSelectPage || currentListIndex == -1) return;

        int scroll = Mouse.getEventDWheel();
        if (scroll != 0)
        {
            if (scroll > 0) scrollOffset = Math.max(0, scrollOffset - 1);
            else scrollOffset = Math.min(listSize - ITEMS_PER_PAGE, scrollOffset + 1);
            if (listSize <= ITEMS_PER_PAGE) scrollOffset = 0;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (openSelectPage && currentListIndex != -1)
        {
            int ListX = LocX - 100 + (currentListIndex - 1) * 50;

            for (int i = 0; i < ITEMS_PER_PAGE; i++)
            {
                int actualIndex = i + scrollOffset;
                if (actualIndex >= listSize) break;

                int itemY = LocY + i * 20;
                if (mouseX >= ListX + 1 && mouseX <= ListX + 49 && mouseY >= itemY && mouseY <= itemY + 20)
                {
                    selectedItem = actualIndex;
                    switch (currentListIndex)
                    {
                        case 1 : nian = selectedItem; break;
                        case 2 : yue = selectedItem; break;
                        case 3 : ri = selectedItem; break;
                        case 4 : shi = selectedItem; break;
                    }
                    this.initGui();
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            if (teamData != null && !teamData.members.isEmpty()) PacketHandler.INSTANCE.sendToServer(new MessageLunHuiTeamReady(nian, yue, ri, shi));
            else { PacketHandler.INSTANCE.sendToServer(new MessageIVZhuLunHui(nian, yue, ri, shi)); mc.displayGuiScreen(null); }
        }
        if (button.id == 1) { openSelectPage = true; initGui(); }
        if (button.id >= 2 && button.id <= 5)
        {
            button.enabled = false;
            if (currentListIndex >= 1 && currentListIndex <= 4) buttonList.get(currentListIndex).enabled = true;
            buttonList.get(0).enabled = (nian != -1 && yue != -1 && ri != -1 && shi != -1) || (nian == -1 && yue == -1 && ri == -1 && shi == -1);

            currentListIndex = button.id - 1;
            scrollOffset = 0;
            selectedItem = -1;

            listSize = (button.id == 2 || button.id == 4) ? 60 : 12;
        }
        if (button.id == 6) if (teamData != null && !teamData.members.isEmpty()) PacketHandler.INSTANCE.sendToServer(new MessageLunHuiTeamLeave());
    }

    private static final String[] TIAN_GAN = {
            "tianGan.jia", "tianGan.yi", "tianGan.bing", "tianGan.ding", "tianGan.wu",
            "tianGan.ji", "tianGan.geng", "tianGan.xin", "tianGan.ren", "tianGan.gui"
    };
    private static final String[] DI_ZHI = {
            "diZhi.zi", "diZhi.chou", "diZhi.yin", "diZhi.mao",
            "diZhi.chen", "diZhi.si", "diZhi.wu", "diZhi.wei",
            "diZhi.shen", "diZhi.you", "diZhi.xu", "diZhi.hai"
    };
    private static final int[] WU_HU = {
            2, 4, 6, 8, 0,
            2, 4, 6, 8, 0
    };
    private static final int[] WU_SHU = {
            0, 2, 4, 6, 8,
            0, 2, 4, 6, 8
    };

    private String GetGanZhi(int ganZhiIndex, int ivZhuIndex)
    {
        if (ivZhuIndex == 1 || ivZhuIndex == 3) return translate(TIAN_GAN[ganZhiIndex % 10]) + translate(DI_ZHI[ganZhiIndex % 12]);
        else if (ivZhuIndex == 2) return translate(TIAN_GAN[(WU_HU[nian % 10] + ganZhiIndex) % 10]) + translate(DI_ZHI[(ganZhiIndex + 2) % 12]);
        else if (ivZhuIndex == 4) return translate(TIAN_GAN[(WU_SHU[ri % 10] + ganZhiIndex) % 10]) + translate(DI_ZHI[ganZhiIndex]);
        else return "";
    }

    private String translate(String key)
    {
        return new TextComponentTranslation(key).getUnformattedText();
    }
}