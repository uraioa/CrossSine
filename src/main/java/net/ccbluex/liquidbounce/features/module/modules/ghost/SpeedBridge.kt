package net.ccbluex.liquidbounce.features.module.modules.ghost

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.minecraft.init.Blocks
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "SpeedBridge", category = ModuleCategory.GHOST)
class SpeedBridge : Module() {

    private val airValue = BoolValue("Air", false)
    private val PitchLitmit = BoolValue("Pitch", false)
    private val PitchMax: IntegerValue = object : IntegerValue("Pitch-Max", 0, 0, 90) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val PitchMin = PitchMin.get()
            if (PitchMin > newValue) {
                set(PitchMin)
            }
        }
    }.displayable { PitchLitmit.get() } as IntegerValue
    private val PitchMin : IntegerValue = object :  IntegerValue("Pitch-Min", 0, 0, 90) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val PitchMax = PitchMax.get()
            if (PitchMax < newValue) {
                set(PitchMax)
            }
        }
    }.displayable { PitchLitmit.get() } as IntegerValue
    private val noSpeedPotion = BoolValue("NoPotionSpeed", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        if (mc.gameSettings.keyBindBack.isKeyDown){


            if (airValue.get() || mc.thePlayer.onGround) {
                if (!noSpeedPotion.get() || !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    if (!PitchLitmit.get() || mc.thePlayer.rotationPitch < PitchMax.get() && mc.thePlayer.rotationPitch > PitchMin.get()) {
                        mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(
                            BlockPos(
                                mc.thePlayer.posX + mc.thePlayer.motionX * 0.2,
                                mc.thePlayer.posY - 1.0,
                                mc.thePlayer.posZ + mc.thePlayer.motionZ * 0.2
                            )
                        ).block == Blocks.air
                        return
                    }
                }
            }


        }
        if (mc.thePlayer.moveForward > 0 && mc.thePlayer.isSneaking && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
            mc.gameSettings.keyBindSneak.pressed = false
        }
    }


    override fun onDisable() {
        if (mc.thePlayer == null) {
            return
        }
    }

}