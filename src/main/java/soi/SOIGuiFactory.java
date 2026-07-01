package soi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Collections;
import java.util.Set;

public class SOIGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft mc) { }

    @Override
    public boolean hasConfigGui() { return true; }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        return new GuiConfig(parent, ConfigElement.from(SOIConfig.class).getChildElements(), "soi", false, false, "SOI");
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return Collections.emptySet(); }
}