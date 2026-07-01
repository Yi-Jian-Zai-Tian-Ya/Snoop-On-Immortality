package soi.client.gui.xiu_xian;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import org.lwjgl.input.Keyboard;
import soi.api.dao_shu.DaoShuCapabilities;
import soi.client.event.EventDaoShuHandler;
import soi.api.dao_shu.IDaoShu;
import soi.network.PacketHandler;
import soi.network.xiu_xian.dao_shu.MessageUpdateKeyBinding;

import java.io.IOException;
import java.util.*;

public class GuiDaoShuConfig extends GuiScreen
{
    private final List<String> DaoShus = new ArrayList<>();
    private String selected = "";
    private int maxListLabelWidth;

    @Override
    public void initGui()
    {
        buttonList.clear();
        IDaoShu DaoShu = Minecraft.getMinecraft().player.getCapability(DaoShuCapabilities.DAO_SHU, null);

        if (DaoShu == null) return;

        this.DaoShus.clear();
        this.DaoShus.addAll(DaoShu.getDaoShus());

        for (int i = 0; i < this.DaoShus.size(); i++)
        {
            String name = this.DaoShus.get(i); String key = DaoShu.getKeyBinding(name);
            String keyName = selected.equals(name) ? "> " + key + " <" : key;

            int j = fontRenderer.getStringWidth(I18n.format(this.DaoShus.get(i) + ".name"));
            this.maxListLabelWidth = Math.max(j, this.maxListLabelWidth);

            addButton(new GuiButton(i, width / 2 - 75, 30 + i * 25, 150, 20, keyName));
        }

        addButton(new GuiButton(20200420, width / 2 - 50, height - 30, 100, 20, I18n.format("gui.done")));
    }

    @Override public void updateScreen() { super.updateScreen(); }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 20200420) mc.displayGuiScreen(null);
        else if (button.id < DaoShus.size()) { selected = DaoShus.get(button.id); this.initGui(); }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!selected.isEmpty())
        {
            String keyName = (keyCode == 1) ? "NONE" : Keyboard.getKeyName(keyCode);
            IDaoShu DaoShu = Minecraft.getMinecraft().player.getCapability(DaoShuCapabilities.DAO_SHU, null);
            if (DaoShu != null)
            {
                DaoShu.setKeyBinding(selected, keyName);
                EventDaoShuHandler.updateBinding(selected, keyName);
                PacketHandler.INSTANCE.sendToServer(new MessageUpdateKeyBinding(selected, keyName));
            }

            selected = "";
            this.initGui();
        }
        else super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, I18n.format("gui.dao_shu.title"), width / 2, 10, 0xFFFFFF);

        IDaoShu DaoShu = Minecraft.getMinecraft().player.getCapability(DaoShuCapabilities.DAO_SHU, null);
        if (DaoShu != null) for (int i = 0; i < this.DaoShus.size(); i++)
        {
            String name = I18n.format(this.DaoShus.get(i) + ".name");
            drawString(fontRenderer, name, width / 2 - 90 - this.maxListLabelWidth, 36 + i * 25, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}