package net.ccbluex.liquidbounce.features.module.modules.other.phases.other

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.other.phases.PhaseMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class OldMatrixPhase : PhaseMode("OldMatrix") {

    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3, mc.thePlayer.posZ)
        mc.gameSettings.keyBindForward.pressed = true
        MovementUtils.strafe(0.1f)
        mc.gameSettings.keyBindForward.pressed = false
    }
}