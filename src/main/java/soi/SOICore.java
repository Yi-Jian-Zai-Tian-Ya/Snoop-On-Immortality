package soi;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import soi.asm.*;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
public class SOICore implements IFMLLoadingPlugin
{
    @Override public String[] getASMTransformerClass()
    {
        return new String[] {
                TransformerLocaleFont.class.getName(),
                TransformerShulkerClass.class.getName(),
                TransformerShulkerTileClass.class.getName()
        };
    }

    @Override public String getModContainerClass() { return null; }

    @Override public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override public String getAccessTransformerClass() { return null; }
}