package net.ccbluex.liquidbounce.features.module.modules.movement.flights.other

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flights.FlightMode
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.minecraft.network.play.client.C03PacketPlayer

class FlagFlight : FlightMode("Flag") {
    override fun onEnable() {
        if(mc.isSingleplayer) {
            CrossSine.hud.addNotification(Notification("FlagFly", "Using FlagFly in SinglePlayer will crash your client while saving world!", NotifyType.ERROR))
            flight.state = false
        }
    }
    override fun onUpdate(event: UpdateEvent) {
        if(mc.isSingleplayer) return

        mc.netHandler.addToSendQueue(C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + mc.thePlayer.motionX * 999, mc.thePlayer.posY + (if (mc.gameSettings.keyBindJump.isKeyDown) 1.5624 else 0.00000001) - if (mc.gameSettings.keyBindSneak.isKeyDown) 0.0624 else 0.00000002, mc.thePlayer.posZ + mc.thePlayer.motionZ * 999, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true))
        mc.netHandler.addToSendQueue(C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + mc.thePlayer.motionX * 999, mc.thePlayer.posY - 6969, mc.thePlayer.posZ + mc.thePlayer.motionZ * 999, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true))
        mc.thePlayer.setPosition(mc.thePlayer.posX + mc.thePlayer.motionX * 11, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.motionZ * 11)
        mc.thePlayer.motionY = 0.0
    }
}