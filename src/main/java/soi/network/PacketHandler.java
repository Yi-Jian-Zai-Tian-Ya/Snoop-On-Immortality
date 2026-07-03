package soi.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import soi.SOI;
import soi.network.corpse.*;
import soi.network.rune.*;
import soi.network.xiu_xian.*;
import soi.network.xiu_xian.dao_shu.*;
import soi.network.xiu_xian.dao_zai.gong_fa.MessageGongFaWorn;
import soi.network.xiu_xian.dao_zai.inventory.*;
import soi.network.xiu_xian.dao_zai.storage_bag.*;
import soi.network.xiu_xian.lun_hui.*;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SOI.MODID.toLowerCase());
    private static int ID = 0;

    public static void init()
    {
        INSTANCE.registerMessage(MessageOpenInventory.class, MessageOpenInventory.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageInwardObserve.class, MessageInwardObserve.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageOpenDaoZaiInventory.class, MessageOpenDaoZaiInventory.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessagePacketSync.Handler.class, MessagePacketSync.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageSyncDaoZai.class, MessageSyncDaoZai.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageRefreshDaoZai.Handler.class, MessageRefreshDaoZai.class, ID++, Side.CLIENT);

        INSTANCE.registerMessage(MessageGetItem.class, MessageGetItem.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageOnPickStorageBag.class, MessageOnPickStorageBag.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageGetStorageBag.class, MessageGetStorageBag.class, ID++, Side.SERVER);

        INSTANCE.registerMessage(MessageItemShowing.class, MessageItemShowing.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageSyncXiuXian.class, MessageSyncXiuXian.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageSyncXiuXianPlayer.class, MessageSyncXiuXianPlayer.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageSyncMingGe.class, MessageSyncMingGe.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageSyncGongFa.class, MessageSyncGongFa.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageTriggerSit.class, MessageTriggerSit.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageFlying.class, MessageFlying.class, ID++, Side.SERVER);

        INSTANCE.registerMessage(MessageGongFaWorn.Handler.class, MessageGongFaWorn.class, ID++, Side.CLIENT);

        INSTANCE.registerMessage(MessageUpdateKeyBinding.class, MessageUpdateKeyBinding.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageCastDaoShu.class, MessageCastDaoShu.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageSyncDaoShu.class, MessageSyncDaoShu.class, ID++, Side.CLIENT);

        INSTANCE.registerMessage(MessageUpdateTeleportVoiceRune.Handler.class, MessageUpdateTeleportVoiceRune.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageSyncToken.Handler.class, MessageSyncToken.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageRequestTokenUpdate.Handler.class, MessageRequestTokenUpdate.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageDeleteToken.Handler.class, MessageDeleteToken.class, ID++, Side.SERVER);

        INSTANCE.registerMessage(MessageRequestGameRule.Handler.class, MessageRequestGameRule.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageResponseGameRule.Handler.class, MessageResponseGameRule.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageIVZhuLunHui.Handler.class, MessageIVZhuLunHui.class, ID++, Side.SERVER);

        INSTANCE.registerMessage(MessageTransferToStorageBag.Handler.class, MessageTransferToStorageBag.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageRequestShenShiProbe.Handler.class, MessageRequestShenShiProbe.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageShenShiProbe.Handler.class, MessageShenShiProbe.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageFocusEnemy.Handler.class, MessageFocusEnemy.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageMaxFocusEnemy.Handler.class, MessageMaxFocusEnemy.class, ID++, Side.SERVER);

        INSTANCE.registerMessage(MessageLunHuiTeamInvite.Handler.class, MessageLunHuiTeamInvite.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageLunHuiTeamReady.Handler.class, MessageLunHuiTeamReady.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageSyncLunHuiTeam.Handler.class, MessageSyncLunHuiTeam.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageLunHuiTeamKick.Handler.class, MessageLunHuiTeamKick.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessageLunHuiTeamLeave.Handler.class, MessageLunHuiTeamLeave.class, ID++, Side.SERVER);
    }
}