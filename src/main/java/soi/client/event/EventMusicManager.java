package soi.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import soi.SOI;
import soi.common.sound.AllSound;

import java.util.Random;

@EventBusSubscriber(modid = SOI.MODID, value = Side.CLIENT)
public class EventMusicManager
{
    private static final double MOVE_DIST_THRESHOLD = 75D;
    private static final int TIME_WINDOW_TICK = 100;
    private static final int MUSIC_COOL_TICK = 12000;
    private static final int MUSIC_PLAY_TICK = 4200;

    private static int coolDownTick = 6000;
    private static int timeWindowTick = 0;
    private static int musicPlayTick = 0;

    private static double totalMoveMeter = 0;
    private static double lastX, lastY, lastZ;
    private static int lastDimension;

    public static boolean isPlayingCustomMusic = false;

    private static boolean initPos = false;

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event)
    {
        if (isPlayingCustomMusic && event.getSound().getCategory() == SoundCategory.MUSIC)
        {
            event.setResultSound(null);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.side.isServer()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        if (isPlayingCustomMusic)
        {
            musicPlayTick--;
            if (musicPlayTick <= 0)
            {
                isPlayingCustomMusic = false;
                musicPlayTick = 0;
                resetWindow();
            }
            return;
        }

        if (coolDownTick > 0) { coolDownTick--; resetWindow(); return; }

        if (!initPos)
        {
            initPos = true;
            timeWindowTick = TIME_WINDOW_TICK;
            lastDimension = player.dimension;
            totalMoveMeter = 0;
            return;
        }

        double dx = player.posX - lastX;
        double dy = player.posY - lastY;
        double dz = player.posZ - lastZ;
        if (timeWindowTick != TIME_WINDOW_TICK && player.dimension == lastDimension) totalMoveMeter += Math.sqrt(dx * dx + dy * dy + dz * dz);

        timeWindowTick--;

        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;
        lastDimension = player.dimension;

        if (totalMoveMeter >= MOVE_DIST_THRESHOLD)
        {
            playMusic(mc);
            return;
        }

        if (timeWindowTick <= 0) resetWindow();
    }

    private static void playMusic(Minecraft mc)
    {
        PositionedSoundRecord sound = PositionedSoundRecord.getMusicRecord(new Random().nextBoolean() ? AllSound.BU_FAN : AllSound.BU_FAN_PX);
        mc.getSoundHandler().playSound(sound);

        isPlayingCustomMusic = true;
        musicPlayTick = MUSIC_PLAY_TICK;
        coolDownTick = MUSIC_COOL_TICK;
        resetWindow();
    }

    private static void resetWindow()
    {
        totalMoveMeter = 0;
        timeWindowTick = 0;
        initPos = false;
    }
}