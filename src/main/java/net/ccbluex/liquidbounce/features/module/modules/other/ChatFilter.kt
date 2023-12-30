
package net.ccbluex.liquidbounce.features.module.modules.other

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.ccbluex.liquidbounce.features.value.ListValue
import net.ccbluex.liquidbounce.features.value.TextValue
import net.minecraft.network.play.client.C01PacketChatMessage

@ModuleInfo(name = "ChatFilter", spacedName = "Chat Filter", category = ModuleCategory.OTHER)
class ChatFilter : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Null", "RandomChar", "Unicode", "RandomUnicode", "ToPinyin"), "Null")
    private val chanceValue = FloatValue("Chance", 0.2F, 0F, 0.5F).displayable { !modeValue.equals("Unicode") }
    private val pinyinFillValue = TextValue("Pinyin-Fill", " ").displayable { modeValue.equals("ToPinyin") }
    private val minUnicodeValue: IntegerValue = object : IntegerValue("MinUnicode", 1000, 0, 100000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if (newValue >= maxUnicodeValue.get()) {
                set(oldValue)
            }
        }
    }.displayable { modeValue.contains("RandomChar") } as IntegerValue
    private val maxUnicodeValue: IntegerValue = object : IntegerValue("MaxUnicode", 20000, 0, 100000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if (newValue <= minUnicodeValue.get()) {
                set(oldValue)
            }
        }
    }.displayable { modeValue.contains("RandomChar") } as IntegerValue

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val packet = event.packet
            val message = packet.message
            if (message.startsWith("/")) return

            when (modeValue.get().lowercase()) {
                "topinyin" -> {
                    packet.message = StringUtils.toPinyin(packet.message, pinyinFillValue.get())
                }
                else -> {
                    val sb = StringBuilder()

                    for (char in message.toCharArray()) {
                        when (modeValue.get().lowercase()) {
                            "null" -> {
                                sb.append(char)
                                if (Math.random() < chanceValue.get()) {
                                    sb.append("\uF8FF")
                                }
                            }

                            "randomchar" -> {
                                sb.append(char)
                                if (Math.random() < chanceValue.get()) {
                                    sb.append((RandomUtils.nextInt(minUnicodeValue.get(), maxUnicodeValue.get())).toChar())
                                }
                            }

                            "unicode" -> {
                                if (char.code in 33..128) {
                                    sb.append(Character.toChars(char.code + 65248))
                                } else {
                                    sb.append(char)
                                }
                            }

                            "randomunicode" -> {
                                if ((Math.random() < chanceValue.get()) && (char.code in 33..128)) {
                                    sb.append(Character.toChars(char.code + 65248))
                                } else {
                                    sb.append(char)
                                }
                            }
                        }
                    }

                    packet.message = sb.toString()
                }
            }

            if (packet.message.length > 100) {
                packet.message = packet.message.substring(0, 100)
            }
        }
    }
    override val tag: String?
        get() = modeValue.get()
}