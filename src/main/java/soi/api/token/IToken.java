package soi.api.token;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import java.util.List;
import java.util.Map;

public interface IToken
{
    Map<Integer, String> getTokens();
    List<String> getTokenList();
    void markDirty(EntityPlayer player);
    boolean addToken(String playerName);
    boolean removeToken(int index);
    boolean removeToken(String playerName);
    int getTokenCount();
    void clearTokens();

    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
}