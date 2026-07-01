package soi.util.lun_hui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import soi.network.PacketHandler;
import soi.network.xiu_xian.lun_hui.MessageSyncLunHuiTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LunHuiTeamData extends WorldSavedData
{
    private static final String DATA_ID = "LunHuiTeamStorage";
    public final Map<UUID, LunHuiTeam> teamMap = new ConcurrentHashMap<>();

    public LunHuiTeamData() { super(DATA_ID); }
    public LunHuiTeamData(String dataId)  { super(dataId); }

    @Override
    public void readFromNBT(NBTTagCompound root)
    {
        teamMap.clear();
        if (!root.hasKey("team_list", 10)) return;
        NBTTagList teamTagList = root.getTagList("team_list", 10);
        for (int i = 0; i < teamTagList.tagCount(); i++)
        {
            NBTTagCompound teamTag = teamTagList.getCompoundTagAt(i);
            if (!teamTag.hasKey("own_msb") || !teamTag.hasKey("own_lsb")) continue;
            UUID owner = new UUID(teamTag.getLong("own_msb"), teamTag.getLong("own_lsb"));

            ArrayList<LunHuiTeam.TeamMember> memberList = new ArrayList<>();
            if (teamTag.hasKey("member_list", 10))
            {
                NBTTagList memberTagList = teamTag.getTagList("member_list", 10);
                for (int j = 0; j < memberTagList.tagCount(); j++)
                {
                    NBTTagCompound mTag = memberTagList.getCompoundTagAt(j);
                    UUID memUuid = new UUID(mTag.getLong("uuid_msb"), mTag.getLong("uuid_lsb"));
                    String name = mTag.getString("player_name");
                    boolean ready = mTag.getBoolean("is_ready");
                    int n = mTag.getInteger("nian");
                    int y = mTag.getInteger("yue");
                    int r = mTag.getInteger("ri");
                    int s = mTag.getInteger("shi");
                    memberList.add(new LunHuiTeam.TeamMember(memUuid, name, ready, n, y, r, s));
                }
            }

            long createTicks = System.currentTimeMillis();
            if (teamTag.hasKey("create_ticks")) createTicks = teamTag.getLong("create_ticks");

            LunHuiTeam team = new LunHuiTeam(owner, memberList, createTicks);
            teamMap.put(owner, team);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound root)
    {
        NBTTagList teamTagList = new NBTTagList();
        for (Map.Entry<UUID, LunHuiTeam> entry : teamMap.entrySet())
        {
            UUID owner = entry.getKey();
            LunHuiTeam team = entry.getValue();
            NBTTagCompound teamTag = new NBTTagCompound();
            teamTag.setLong("own_msb", owner.getMostSignificantBits());
            teamTag.setLong("own_lsb", owner.getLeastSignificantBits());
            NBTTagList memberTagList = getNbtBases(team);
            teamTag.setTag("member_list", memberTagList);
            teamTag.setLong("create_ticks", team.createTime);
            teamTagList.appendTag(teamTag);
        }
        root.setTag("team_list", teamTagList);
        return root;
    }

    private static NBTTagList getNbtBases(LunHuiTeam team)
    {
        NBTTagList memberTagList = new NBTTagList();
        for (LunHuiTeam.TeamMember m : team.members)
        {
            NBTTagCompound mTag = new NBTTagCompound();
            mTag.setLong("uuid_msb", m.uuid.getMostSignificantBits());
            mTag.setLong("uuid_lsb", m.uuid.getLeastSignificantBits());
            mTag.setString("player_name", m.name);
            mTag.setBoolean("is_ready", m.isReady);
            mTag.setInteger("nian", m.nian);
            mTag.setInteger("yue", m.yue);
            mTag.setInteger("ri", m.ri);
            mTag.setInteger("shi", m.shi);
            memberTagList.appendTag(mTag);
        }
        return memberTagList;
    }

    public void clearExpiredTeams(MinecraftServer server)
    {
        List<UUID> removeKeyList = new ArrayList<>();
        IMessage emptySync = new MessageSyncLunHuiTeam(null);
        PlayerList list = server.getPlayerList();

        for (Map.Entry<UUID, LunHuiTeam> entry : teamMap.entrySet())
        {
            LunHuiTeam team = entry.getValue();
            if (team.isExpired())
            {
                removeKeyList.add(entry.getKey());
                for (LunHuiTeam.TeamMember member : team.members) { EntityPlayerMP mp = list.getPlayerByUUID(member.uuid); if (list.getPlayerByUsername(member.name) != null)
                { PacketHandler.INSTANCE.sendTo(emptySync, mp); mp.sendMessage(new TextComponentTranslation("message.lun_hui.team.removeAuto",  TextFormatting.DARK_RED + list.getPlayerByUUID(team.owner).getName())); } }
            }
        }

        for (UUID removeId : removeKeyList) teamMap.remove(removeId);

        if (!removeKeyList.isEmpty()) this.markDirty();
    }

    public static LunHuiTeamData getWorldData(World world)
    {
        if (world.isRemote) return null;
        MapStorage storage = world.getPerWorldStorage();
        LunHuiTeamData data = null;
        try { data = (LunHuiTeamData) storage.getOrLoadData(LunHuiTeamData.class, DATA_ID); }
        catch (Exception e) { e.printStackTrace(); data = new LunHuiTeamData(); }
        if (data == null) { data = new LunHuiTeamData(); storage.setData(DATA_ID, data); data.markDirty(); }
        return data;
    }
}