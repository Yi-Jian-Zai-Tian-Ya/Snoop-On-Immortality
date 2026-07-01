package soi.util.lun_hui;

import java.util.ArrayList;
import java.util.UUID;

public class LunHuiTeam
{
    public final UUID owner;
    public ArrayList<TeamMember> members;
    public long createTime;

    public LunHuiTeam(UUID owner, ArrayList<TeamMember> list)
    {
        this.owner = owner;
        this.members = list;
        this.createTime = System.currentTimeMillis();
    }

    public LunHuiTeam(UUID owner, ArrayList<TeamMember> list, long createTime)
    {
        this.owner = owner;
        this.members = list;
        this.createTime = createTime;
    }

    public static class TeamMember
    {
        public UUID uuid;
        public String name;
        public boolean isReady;
        public int nian, yue, ri, shi;

        public TeamMember(UUID uuid, String name, boolean ready, int n, int y, int r, int s)
        {
            this.uuid = uuid;
            this.name = name;
            this.isReady = ready;
            this.nian = n; this.yue = y; this.ri = r; this.shi = s;
        }
    }

    public boolean isExpired() { return System.currentTimeMillis() - this.createTime > 180_000L; }
}