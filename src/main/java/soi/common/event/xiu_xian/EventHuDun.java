package soi.common.event.xiu_xian;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.relauncher.Side;
import soi.api.xiu_xian.IXiuXian;
import soi.api.xiu_xian.XiuXianCapabilities;

@EventBusSubscriber(Side.SERVER)
public class EventHuDun
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingHurt(LivingHurtEvent event) //受护甲计算前的真实伤害
    {
        EntityLivingBase entity = event.getEntityLiving();
        IXiuXian XiuXian = entity.getCapability(XiuXianCapabilities.XIU_XIAN, null);

        if (XiuXian != null)
        {
            double huDun = XiuXian.getHuDun();
            double damage = event.getAmount();
            if (huDun > 0.0)
            {
                double actualDamage = huDun - damage;

                XiuXian.addHuDun(actualDamage >= 0.0 ? -damage : -huDun);
                event.setAmount(actualDamage >= 0.0 ? 0.0F : (float) -actualDamage);

                if (entity instanceof EntityPlayerMP) XiuXian.syncXiuXian((EntityPlayerMP) entity);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingHurt(LivingDamageEvent event) //受护甲计算后的伤害
    {

    }
}
