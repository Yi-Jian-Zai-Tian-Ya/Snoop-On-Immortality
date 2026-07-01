package soi.api.token;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import soi.network.PacketHandler;
import soi.network.rune.MessageRequestTokenUpdate;
import soi.network.rune.MessageSyncToken;

import java.util.*;

public class Token implements IToken
{
    private final Map<Integer, String> tokens = new HashMap<>();
    private int friendCount = 0;

    @Override
    public Map<Integer, String> getTokens() { return Collections.unmodifiableMap(tokens); }

    @Override
    public List<String> getTokenList() { return new ArrayList<>(tokens.values()); }

    @Override
    public void markDirty(EntityPlayer player)
    {
        if (!player.world.isRemote)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            this.saveNBTData(nbt);
            if (player instanceof EntityPlayerMP) PacketHandler.INSTANCE.sendTo(new MessageSyncToken(nbt), (EntityPlayerMP) player);
        }
        else PacketHandler.INSTANCE.sendToServer(new MessageRequestTokenUpdate());
    }

    @Override
    public boolean addToken(String playerName)
    {
        if (playerName == null || playerName.isEmpty() || tokens.containsValue(playerName)) return false;

        tokens.put(friendCount, playerName);
        friendCount++;
        return true;
    }

    @Override
    public boolean removeToken(int index)
    {
        if (tokens.containsKey(index))
        {
            if (index != friendCount - 1)
            {
                String lastPlayer = tokens.get(friendCount - 1);
                tokens.put(index, lastPlayer);
            }

            tokens.remove(friendCount - 1);
            friendCount--;
            return true;
        }
        return false;
    }

    @Override
    public boolean removeToken(String playerName)
    {
        for (Map.Entry<Integer, String> entry : tokens.entrySet()) if (entry.getValue().equals(playerName)) return removeToken(entry.getKey());
        return false;
    }

    @Override
    public int getTokenCount() { return tokens.size(); }

    @Override
    public void clearTokens()
    {
        tokens.clear();
        friendCount = 0;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setInteger("friendCount", tokens.size());

        int i = 0;
        for (Map.Entry<Integer, String> entry : tokens.entrySet())
        {
            compound.setString(String.valueOf(i), entry.getValue());
            i++;
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        tokens.clear();
        int count = compound.getInteger("friendCount");

        for (int i = 0; i < count; i++)
        {
            String key = String.valueOf(i);
            if (compound.hasKey(key))
            {
                String playerName = compound.getString(key);
                tokens.put(i, playerName);
            }
        }

        friendCount = tokens.size();
    }
}