package soi.client.gui.rune;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import soi.SOI;
import soi.api.token.IToken;
import soi.api.token.TokenProvider;
import soi.common.inventory.rune.ContainerTeleportVoiceRune;
import soi.network.PacketHandler;
import soi.network.rune.MessageDeleteToken;
import soi.network.rune.MessageRequestTokenUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiTeleportVoice extends GuiScreen
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(SOI.MODID, "textures/gui/rune/teleport_voice.png");
    private static final int TEXT_WIDTH_LIMIT = 80;
    private static final int TEXT_OFFSET_X = 8;
    private static final int TEXT_OFFSET_Y = 13;

    public final ContainerTeleportVoiceRune container;
    private int guiX;
    private int guiY;

    private String rawText;
    private String selectToken;
    private boolean textModified;
    private int blinkTimer;
    private final int BLINK_INTERVAL = 8;

    private int caretPos;
    private int selectionStart;
    private int selectionEnd;

    public boolean showTokenList = false;
    private List<String> tokenList = new ArrayList<>();
    private int selectedTokenIndex = -1;

    public GuiTeleportVoice(ContainerTeleportVoiceRune container)
    {
        this.container = container;
        this.rawText = container.getMessage();
        this.selectToken = container.getSelect();
        this.caretPos = this.rawText.length();
        this.selectionStart = this.caretPos;
        this.selectionEnd = this.caretPos;
        this.textModified = false;
        this.blinkTimer = 0;
    }

    private static class UiLine
    {
        public String content;
        public int globalStart;
        public int globalEnd;

        public UiLine(String content, int start, int end)
        {
            this.content = content;
            this.globalStart = start;
            this.globalEnd = end;
        }
    }

    private List<UiLine> buildUiLines()
    {
        List<UiLine> uiLines = new ArrayList<>();
        int globalIndex = 0;
        String[] hardLines = rawText.split("\n", -1);

        for (int hlIdx = 0; hlIdx < hardLines.length; hlIdx++)
        {
            String hardLine = hardLines[hlIdx];
            List<String> wrapped = fontRenderer.listFormattedStringToWidth(hardLine, TEXT_WIDTH_LIMIT);

            for (String wrap : wrapped)
            {
                int len = wrap.length();
                uiLines.add(new UiLine(wrap, globalIndex, globalIndex + len));
                globalIndex += len;
            }

            if (hlIdx != hardLines.length - 1) globalIndex += 1;
        }
        return uiLines;
    }

    private int[] getCaretUiInfo()
    {
        List<UiLine> lines = buildUiLines();
        int target = caretPos;

        for (int i = 0; i < lines.size(); i++)
        {
            UiLine line = lines.get(i);
            if (target >= line.globalStart && target <= line.globalEnd)
            {
                int inLine = target - line.globalStart;
                return new int[]{i, inLine};
            }
        }

        if (!lines.isEmpty())
        {
            UiLine last = lines.get(lines.size() - 1);
            return new int[]{lines.size() - 1, last.content.length()};
        }
        return new int[]{0, 0};
    }

    @Override
    public void updateScreen() { super.updateScreen(); blinkTimer++; }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        this.guiX = (this.width - 94) / 2;
        this.guiY = (this.height - 256) / 2;

        if (mc != null && mc.player != null) PacketHandler.INSTANCE.sendToServer(new MessageRequestTokenUpdate());

        checkTokenValid();

        this.addButton(new GuiButton(0, guiX + 10, guiY + 280, 74, 20, I18n.format("gui.done")));
        this.addButton(new GuiButtonImage(1, guiX + 94, guiY, 20, 20, 96, 0, 20, TEXTURE));
        this.addButton(new GuiButtonImage(2, guiX + 94, guiY + 20, 20, 20, 128, 0, 20, TEXTURE));

        loadTokenList();

        if (!selectToken.isEmpty() && selectedTokenIndex == -1)
            for (int i = 0; i < tokenList.size(); i++) if (tokenList.get(i).equals(selectToken)) { selectedTokenIndex = i; break; }

        if (showTokenList) addTokenButtons();
    }

    private void loadTokenList()
    {
        tokenList.clear();
        if (mc != null && mc.player != null)
        {
            IToken Token = mc.player.getCapability(TokenProvider.TOKEN, null);
            if (Token != null) tokenList = Token.getTokenList();
        }
    }

    private void checkTokenValid()
    {
        if (mc != null && mc.player != null && !selectToken.isEmpty())
        {
            IToken Token = mc.player.getCapability(TokenProvider.TOKEN, null);
            if (Token != null && !Token.getTokenList().contains(selectToken))
            {
                selectToken = "";
                textModified = true;
                saveData();
            }
        }
    }

    private void addTokenButtons()
    {
        int startY = guiY + 25;
        int btnW = 100;
        int btnH = 20;
        int gap = 5;

        for (int i = 0; i < tokenList.size(); i++)
        {
            String name = tokenList.get(i);
            GuiButton btn = new GuiButton(100 + i, guiX + 120, startY + i * (btnH + gap), btnW, btnH, name);
            btn.enabled = !name.equals(selectToken) && (i != selectedTokenIndex);
            this.addButton(btn);
        }

        int bottomY = startY + tokenList.size() * (btnH + gap) + gap;
        GuiButton btnSel = new GuiButton(3, guiX + 120, bottomY, 48, 20, "Select");
        GuiButton btnDel = new GuiButton(4, guiX + 172, bottomY, 48, 20, "Delete");
        btnSel.enabled = (selectedTokenIndex != -1) && !tokenList.get(selectedTokenIndex).equals(selectToken);
        btnDel.enabled = (selectedTokenIndex != -1);
        this.addButton(btnSel);
        this.addButton(btnDel);
    }

    @Override
    public void onGuiClosed() { Keyboard.enableRepeatEvents(false); saveData(); }
    private void saveData() { if (textModified) { container.syncDataToServer(rawText, selectToken); textModified = false; } }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0) { saveData(); mc.player.closeScreen(); }
        else if (button.id == 1) { showTokenList = !showTokenList; initGui(); }
        else if (button.id == 2) insertString("\u00A7");
        else if (button.id == 3)
        {
            if (selectedTokenIndex >= 0 && selectedTokenIndex < tokenList.size())
            {
                selectToken = tokenList.get(selectedTokenIndex);
                textModified = true;
                initGui();
            }
        }
        else if (button.id == 4)
        {
            if (selectedTokenIndex >= 0 && selectedTokenIndex < tokenList.size() && mc.player != null)
            {
                String delName = tokenList.get(selectedTokenIndex);
                IToken Token = mc.player.getCapability(TokenProvider.TOKEN, null);
                if (Token != null && Token.removeToken(delName))
                {
                    PacketHandler.INSTANCE.sendToServer(new MessageDeleteToken(delName));
                    selectToken = "";
                    textModified = true;
                    selectedTokenIndex = -1;
                    loadTokenList();
                    initGui();
                }
            }
        }
        else if (button.id >= 100 && button.id < 100 + tokenList.size()) { selectedTokenIndex = button.id - 100; initGui(); }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException { super.keyTyped(typedChar, keyCode); handleKey(typedChar, keyCode); }

    private void handleKey(char ch, int key)
    {
        if (key == Keyboard.KEY_ESCAPE) { mc.player.closeScreen(); return; }

        if (GuiScreen.isKeyComboCtrlA(key))
        {
            selectionStart = 0;
            selectionEnd = rawText.length();
            caretPos = rawText.length();
            return;
        }
        if (GuiScreen.isKeyComboCtrlC(key))
        {
            int s = Math.min(selectionStart, selectionEnd);
            int e = Math.max(selectionStart, selectionEnd);
            GuiScreen.setClipboardString(rawText.substring(s, e));
            return;
        }
        if (GuiScreen.isKeyComboCtrlV(key)) { insertString(GuiScreen.getClipboardString()); return; }

        boolean hasSel = (selectionStart != selectionEnd);

        switch (key)
        {
            case Keyboard.KEY_BACK: doBackspace(hasSel); break;
            case Keyboard.KEY_DELETE: doDelete(hasSel); break;
            case Keyboard.KEY_LEFT: moveLeft(hasSel); break;
            case Keyboard.KEY_RIGHT: moveRight(hasSel); break;
            case Keyboard.KEY_UP: moveUp(); break;
            case Keyboard.KEY_DOWN: moveDown(); break;
            case Keyboard.KEY_RETURN: case Keyboard.KEY_NUMPADENTER: doEnter(); break;
            case Keyboard.KEY_HOME: moveHome(); break;
            case Keyboard.KEY_END: moveEnd(); break;
            default: if (ChatAllowedCharacters.isAllowedCharacter(ch)) insertString(Character.toString(ch)); break;
        }
    }

    private void insertString(String str)
    {
        int s = Math.min(selectionStart, selectionEnd);
        int e = Math.max(selectionStart, selectionEnd);
        rawText = rawText.substring(0, s) + str + rawText.substring(e);
        caretPos = s + str.length();
        selectionStart = caretPos;
        selectionEnd = caretPos;
        textModified = true;
    }

    private void doEnter() { insertString("\n"); }

    private void doBackspace(boolean hasSel)
    {
        if (hasSel) { insertString(""); return; }
        if (caretPos > 0)
        {
            rawText = rawText.substring(0, caretPos - 1) + rawText.substring(caretPos);
            caretPos--;
            selectionStart = caretPos;
            selectionEnd = caretPos;
            textModified = true;
        }
    }

    private void doDelete(boolean hasSel)
    {
        if (hasSel) { insertString(""); return; }
        if (caretPos < rawText.length()) { rawText = rawText.substring(0, caretPos) + rawText.substring(caretPos + 1); textModified = true; }
    }

    private void moveLeft(boolean hasSel)
    {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) selectionStart = selectionEnd = caretPos;
        if (caretPos > 0) { caretPos--; selectionEnd = caretPos; }
        selectionStart = selectionEnd = caretPos;
    }

    private void moveRight(boolean hasSel)
    {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) selectionStart = selectionEnd = caretPos;
        if (caretPos < rawText.length()) { caretPos++; selectionEnd = caretPos; }
        selectionStart = selectionEnd = caretPos;
    }

    private void moveHome() { caretPos = 0; selectionStart = selectionEnd = caretPos; }
    private void moveEnd() { caretPos = rawText.length(); selectionStart = selectionEnd = caretPos; }

    private void moveUp()
    {
        List<UiLine> lines = buildUiLines();
        int[] uiInfo = getCaretUiInfo();
        int lineIdx = uiInfo[0];
        int inLine = uiInfo[1];

        if (lineIdx <= 0)
        {
            caretPos = 0;
            selectionStart = selectionEnd = caretPos;
            return;
        }

        UiLine targetLine = lines.get(lineIdx - 1);
        int newIn = Math.min(inLine, targetLine.content.length());
        caretPos = targetLine.globalStart + newIn;
        selectionStart = selectionEnd = caretPos;
    }

    private void moveDown()
    {
        List<UiLine> lines = buildUiLines();
        int[] uiInfo = getCaretUiInfo();
        int lineIdx = uiInfo[0];
        int inLine = uiInfo[1];

        if (lineIdx >= lines.size() - 1)
        {
            caretPos = rawText.length();
            selectionStart = selectionEnd = caretPos;
            return;
        }

        UiLine targetLine = lines.get(lineIdx + 1);
        int newIn = Math.min(inLine, targetLine.content.length());
        caretPos = targetLine.globalStart + newIn;
        selectionStart = selectionEnd = caretPos;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
        {
            int tx = guiX + TEXT_OFFSET_X;
            int ty = guiY + TEXT_OFFSET_Y;
            int tw = 80;
            int th = 225;
            if (mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th) { setCaretByMouse(mouseX, mouseY, false); blinkTimer = 0; }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long time)
    {
        if (mouseButton == 0)
        {
            int tx = guiX + TEXT_OFFSET_X;
            int ty = guiY + TEXT_OFFSET_Y;
            int tw = 80;
            int th = 225;
            if (mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th) setCaretByMouse(mouseX, mouseY, true);
        }
    }

    private void setCaretByMouse(int mx, int my, boolean isSelect)
    {
        int relX = mx - (guiX + TEXT_OFFSET_X);
        int relY = my - (guiY + TEXT_OFFSET_Y);
        List<UiLine> lines = buildUiLines();

        int lineIdx = relY / fontRenderer.FONT_HEIGHT;
        lineIdx = Math.max(0, Math.min(lineIdx, lines.size() - 1));
        UiLine line = lines.get(lineIdx);

        int charIndex = fontRenderer.trimStringToWidth(line.content, relX).length();
        int newPos = line.globalStart + charIndex;
        newPos = Math.min(newPos, rawText.length());

        caretPos = newPos;
        if (!isSelect) selectionStart = selectionEnd = newPos;
        else selectionEnd = newPos;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiX, guiY, 0, 0, 94, 256);

        int drawX = guiX + TEXT_OFFSET_X;
        int drawY = guiY + TEXT_OFFSET_Y;

        fontRenderer.drawSplitString(rawText, drawX, drawY, TEXT_WIDTH_LIMIT, 0xFFFFFF);

        if (selectionStart != selectionEnd) drawSelection(drawX, drawY);

        boolean showCaret = (blinkTimer / BLINK_INTERVAL) % 2 == 0;
        if (showCaret) drawCaret(drawX, drawY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawSelection(int baseX, int baseY)
    {
        int s = Math.min(selectionStart, selectionEnd);
        int e = Math.max(selectionStart, selectionEnd);
        if (s == e) return;

        List<UiLine> lines = buildUiLines();
        int globalIdx = 0;
        int y = baseY;

        for (UiLine line : lines)
        {
            int len = line.content.length();
            if (e > globalIdx && s < globalIdx + len)
            {
                int segS = Math.max(0, s - globalIdx);
                int segE = Math.min(len, e - globalIdx);
                if (segS < segE)
                {
                    String pre = line.content.substring(0, segS);
                    String sel = line.content.substring(segS, segE);
                    int x = baseX + fontRenderer.getStringWidth(pre);
                    int w = fontRenderer.getStringWidth(sel);
                    int h = fontRenderer.FONT_HEIGHT;
                    drawRect(x, y, x + w, y + h, 0x803399FF);
                }
            }
            globalIdx += len;
            y += fontRenderer.FONT_HEIGHT;
        }
    }

    private void drawCaret(int baseX, int baseY)
    {
        List<UiLine> lines = buildUiLines();
        int[] uiInfo = getCaretUiInfo();
        int lineIdx = uiInfo[0];
        int inLine = uiInfo[1];

        if (lines.isEmpty()) { drawRect(baseX, baseY, baseX + 1, baseY + fontRenderer.FONT_HEIGHT, 0xFFFFFFFF); return; }
        if (lineIdx < 0 || lineIdx >= lines.size()) return;

        UiLine line = lines.get(lineIdx);
        int actualInLine = Math.min(inLine, line.content.length());
        String part = line.content.substring(0, actualInLine);

        int x = baseX + fontRenderer.getStringWidth(part);
        int y = baseY + lineIdx * fontRenderer.FONT_HEIGHT;
        int h = fontRenderer.FONT_HEIGHT;

        if (actualInLine >= line.content.length()) fontRenderer.drawString(TextFormatting.BLACK + "_", x, y, 0);
        else drawRect(x, y, x + 1, y + h, 0xFFFFFFFF);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}