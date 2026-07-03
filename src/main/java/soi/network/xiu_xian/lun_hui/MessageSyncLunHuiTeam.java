package soi.network.xiu_xian.lun_hui;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soi.client.gui.xiu_xian.lun_hui.GuiLunHuiMenu;
import soi.util.lun_hui.LunHuiTeam;

import java.util.ArrayList;
import java.util.UUID;

public class MessageSyncLunHuiTeam implements IMessage
{
    private LunHuiTeam teamData;
    public MessageSyncLunHuiTeam() { }
    public MessageSyncLunHuiTeam(LunHuiTeam team) { this.teamData = team; }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if (teamData == null) { buf.writeBoolean(false); return; }
        buf.writeBoolean(true);

        buf.writeLong(teamData.owner.getMostSignificantBits());
        buf.writeLong(teamData.owner.getLeastSignificantBits());
        buf.writeInt(teamData.members.size());
        for(LunHuiTeam.TeamMember m : teamData.members)
        {
            buf.writeLong(m.uuid.getMostSignificantBits());
            buf.writeLong(m.uuid.getLeastSignificantBits());
            ByteBufUtils.writeUTF8String(buf, m.name);
            buf.writeBoolean(m.isReady);
            buf.writeInt(m.nian);
            buf.writeInt(m.yue);
            buf.writeInt(m.ri);
            buf.writeInt(m.shi);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        boolean hasTeam = buf.readBoolean();
        if (!hasTeam) { teamData = null; return; }

        UUID owner = new UUID(buf.readLong(), buf.readLong());
        int size = buf.readInt();
        ArrayList<LunHuiTeam.TeamMember> list = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            UUID uuid = new UUID(buf.readLong(), buf.readLong());
            String name = ByteBufUtils.readUTF8String(buf);
            boolean ready = buf.readBoolean();
            int n = buf.readInt(), y = buf.readInt(), r = buf.readInt(), s = buf.readInt();
            list.add(new LunHuiTeam.TeamMember(uuid, name, ready, n,y,r,s));
        }
        teamData = new LunHuiTeam(owner, list);
    }

    public static class Handler implements IMessageHandler<MessageSyncLunHuiTeam, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageSyncLunHuiTeam msg, MessageContext ctx)
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                GuiLunHuiMenu.setClientLunHuiTeam(msg.teamData);
            });
            return null;
        }
    }
}