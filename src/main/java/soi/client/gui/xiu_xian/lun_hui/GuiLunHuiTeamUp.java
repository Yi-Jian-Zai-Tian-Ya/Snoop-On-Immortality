package soi.client.gui.xiu_xian.lun_hui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextComponentTranslation;
import soi.network.PacketHandler;
import soi.network.xiu_xian.lun_hui.MessageLunHuiTeamInvite;

import java.io.IOException;

public class GuiLunHuiTeamUp extends GuiScreen
{
    private static int LocX;
    private static int LocY;

    private GuiTextField nameInput;
    private GuiButton confirmBtn;

    @Override
    public void initGui()
    {
        super.initGui();
        LocX = width / 2;
        LocY = height / 2;

        nameInput = new GuiTextField(0, fontRenderer, LocX - 100, LocY - 10, 200, 20);
        nameInput.setMaxStringLength(32);
        nameInput.setText("");
        nameInput.setFocused(true);

        confirmBtn = new GuiButton(0, LocX - 60, LocY + 30, 120, 20, new TextComponentTranslation("button.lun_hui.teamUp").getFormattedText());
        buttonList.add(confirmBtn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        nameInput.drawTextBox();
        this.fontRenderer.drawString(new TextComponentTranslation("gui.lun_hui.teamUp").getFormattedText(), LocX - 100, LocY - 24, 0xFFFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == 0)
        {
            String target = nameInput.getText().trim();
            if(!target.isEmpty()) PacketHandler.INSTANCE.sendToServer(new MessageLunHuiTeamInvite(target));
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        nameInput.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28 || keyCode == 156) actionPerformed(confirmBtn);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameInput.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        nameInput.updateCursorCounter();
    }
}
