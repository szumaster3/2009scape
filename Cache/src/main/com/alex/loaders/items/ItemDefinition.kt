package com.alex.loaders.items

import com.alex.io.InputStream
import com.alex.io.OutputStream
import com.alex.store.Store

class ItemDefinition @JvmOverloads constructor(cache: Store, var id: Int, load: Boolean = true) :
    Cloneable {
    var isLoaded: Boolean = false
    var invModelId: Int = 0
    var name: String? = null
    var invModelZoom: Int = 0
    var xan2d: Int = 0
    var yan2d: Int = 0
    var xOffset2d: Int = 0
    var yOffset2d: Int = 0
    var equipSlot: Int = 0
    var equipType: Int = 0
    var stackable: Int = 0
    var cost: Int = 0
    var isMembersOnly: Boolean = false
    var maleEquipModelId1: Int = 0
    var femaleEquipModelId1: Int = 0
    var maleEquipModelId2: Int = 0
    var femaleEquipModelId2: Int = 0
    var maleEquipModelId3: Int = 0
    var femaleEquipModelId3: Int = 0
    var inventoryOptions: Array<String?>? = null
    var originalModelColors: IntArray? = null
    var modifiedModelColors: IntArray? = null
    var originalTextureColors: ShortArray? = null
    var modifiedTextureColors: ShortArray? = null
    var recolorPalette: ByteArray? = null
    var unknownArray2: IntArray? = null
    var groundOptions: Array<String?> = arrayOf()
    var isUnnoted: Boolean = false
    var primaryMaleDialogueHead: Int = 0
    var primaryFemaleDialogueHead: Int = 0
    var secondaryMaleDialogueHead: Int = 0
    var secondaryFemaleDialogueHead: Int = 0
    var Zan2d: Int = 0
    var dummyItem: Int = 0
    var switchNoteItemId: Int = 0
    var notedItemId: Int = 0
    var stackIds: IntArray? = null
    var stackAmounts: IntArray? = null
    var floorScaleX: Int = 0
    var floorScaleY: Int = 0
    var floorScaleZ: Int = 0
    var ambience: Int = 0
    var diffusion: Int = 0
    var teamId: Int = 0
    var switchLendItemId: Int = 0
    var lendedItemId: Int = 0
    var maleWieldX: Int = 0
    var maleWieldY: Int = 0
    var maleWieldZ: Int = 0
    var femaleWieldX: Int = 0
    var femaleWieldY: Int = 0
    var femaleWieldZ: Int = 0
    var unknownInt18: Int = 0
    var unknownInt19: Int = 0
    var unknownInt20: Int = 0
    var unknownInt21: Int = 0
    var unknownInt22: Int = 0
    var unknownInt23: Int = 0
    var unknownValue1: Int = 0
    var unknownValue2: Int = 0
    var clientScriptData: MutableMap<Int, Any>? = null

    init {
        this.setDefaultsVariableValules()
        this.setDefaultOptions()
        if (load) {
            this.loadItemDefinition(cache)
        }
    }

    fun getxOffset2d(): Int {
        return this.xOffset2d
    }

    fun setxOffset2d(xOffset2d: Int) {
        this.xOffset2d = xOffset2d
    }

    fun getyOffset2d(): Int {
        return this.yOffset2d
    }

    fun setyOffset2d(yOffset2d: Int) {
        this.yOffset2d = yOffset2d
    }

    fun write(store: Store) {
        store.indexes[19].putFile(this.archiveId, this.fileId, this.encode())
    }

    private fun loadItemDefinition(cache: Store) {
        val data = cache.indexes[19].getFile(this.archiveId, fileId)
        if (data != null) {
            try {
                this.readOpcodeValues(InputStream(data))
            } catch (var4: RuntimeException) {
                var4.printStackTrace()
            }

            if (this.notedItemId != -1) {
                this.toNote(cache)
            }

            if (this.lendedItemId != -1) {
                this.toLend(cache)
            }

            this.isLoaded = true
        }
    }

    private fun toNote(store: Store) {
        val realItem = getItemDefinition(store, this.switchNoteItemId)
        this.isMembersOnly = realItem.isMembersOnly
        this.cost = realItem.cost
        this.name = realItem.name
        this.stackable = 1
    }

    private fun toLend(store: Store) {
        val realItem = getItemDefinition(store, this.switchLendItemId)
        this.originalModelColors = realItem.originalModelColors
        this.modifiedModelColors = realItem.modifiedModelColors
        this.teamId = realItem.teamId
        this.cost = 0
        this.isMembersOnly = realItem.isMembersOnly
        this.name = realItem.name
        this.inventoryOptions = arrayOfNulls(5)
        this.groundOptions = realItem.groundOptions
        if (realItem.inventoryOptions != null) {
            System.arraycopy(realItem.inventoryOptions, 0, this.inventoryOptions, 0, 4)
        }

        inventoryOptions!![4] = "Discard"
        this.maleEquipModelId1 = realItem.maleEquipModelId1
        this.maleEquipModelId2 = realItem.maleEquipModelId2
        this.femaleEquipModelId1 = realItem.femaleEquipModelId1
        this.femaleEquipModelId2 = realItem.femaleEquipModelId2
        this.maleEquipModelId3 = realItem.maleEquipModelId3
        this.femaleEquipModelId3 = realItem.femaleEquipModelId3
        this.equipType = realItem.equipType
        this.equipSlot = realItem.equipSlot
    }

    val archiveId: Int
        get() = this.id ushr 8

    val fileId: Int
        get() = 0xff and this.id

    fun hasSpecialBar(): Boolean {
        if (this.clientScriptData == null) {
            return false
        } else {
            val specialBar = clientScriptData!![686]
            return (specialBar != null && specialBar is Int) && specialBar == 1
        }
    }

    val renderAnimId: Int
        get() {
            if (this.clientScriptData == null) {
                return 1426
            } else {
                val animId = clientScriptData!![644]
                return if (animId != null && animId is Int) animId else 1426
            }
        }

    val questId: Int
        get() {
            if (this.clientScriptData == null) {
                return -1
            } else {
                println(this.clientScriptData)
                val questId = clientScriptData!![861]
                return if (questId != null && questId is Int) questId else -1
            }
        }

    val wearingSkillRequirements: Map<Int, Int>?
        get() {
            val data = clientScriptData ?: return null
            val skills = mutableMapOf<Int, Int>()
            var nextLevel = -1
            var nextSkill = -1

            for ((keyAny, value) in data) {
                val key = keyAny as? Int ?: continue
                if (value !is String) {
                    if (key == 23) {
                        skills[4] = value as? Int ?: 0
                        skills[11] = 61
                    } else if (key in 749 until 797) {
                        if (key % 2 == 0) {
                            nextLevel = value as? Int ?: -1
                        } else {
                            nextSkill = value as? Int ?: -1
                        }
                        if (nextLevel != -1 && nextSkill != -1) {
                            skills[nextSkill] = nextLevel
                            nextLevel = -1
                            nextSkill = -1
                        }
                    }
                }
            }

            return skills
        }

    fun printClientScriptData() {
        val key2: Iterator<*> = clientScriptData!!.keys.iterator()

        while (key2.hasNext()) {
            val requiriments = (key2.next() as Int)
            val value = clientScriptData!![requiriments]
            println("KEY: $requiriments, VALUE: $value")
        }

        val requiriments1 = this.wearingSkillRequirements
        if (requiriments1 == null) {
            println("null.")
        } else {
            println(requiriments1.keys.size)
            val value1: Iterator<*> = requiriments1.keys.iterator()

            while (value1.hasNext()) {
                val key21 = (value1.next() as Int)
                val value2 = requiriments1[key21]
                println("SKILL: $key21, LEVEL: $value2")
            }
        }
    }

    private fun setDefaultOptions() {
        this.groundOptions = arrayOf(null, null, "Take", null, null)
        this.inventoryOptions = arrayOf(null, null, null, null, "Drop")
    }

    private fun setDefaultsVariableValules() {
        this.name = "null"
        this.maleEquipModelId1 = -1
        this.maleEquipModelId2 = -1
        this.femaleEquipModelId1 = -1
        this.femaleEquipModelId2 = -1
        this.invModelZoom = 2000
        this.switchLendItemId = -1
        this.lendedItemId = -1
        this.switchNoteItemId = -1
        this.notedItemId = -1
        this.floorScaleZ = 128
        this.floorScaleX = 128
        this.floorScaleY = 128
        this.cost = 1
        this.maleEquipModelId3 = -1
        this.femaleEquipModelId3 = -1
        this.teamId = -1
        this.equipType = -1
        this.equipSlot = -1
        this.primaryMaleDialogueHead = -1
        this.secondaryMaleDialogueHead = -1
        this.primaryFemaleDialogueHead = -1
        this.secondaryFemaleDialogueHead = -1
        this.Zan2d = 0
    }

    fun encode(): ByteArray {
        val stream = OutputStream()
        stream.writeByte(1)
        stream.writeBigSmart(this.invModelId)
        if (name != "null" && this.notedItemId == -1) {
            stream.writeByte(2)
            stream.writeString(this.name)
        }

        if (this.invModelZoom != 2000) {
            stream.writeByte(4)
            stream.writeShort(this.invModelZoom)
        }

        if (this.xan2d != 0) {
            stream.writeByte(5)
            stream.writeShort(this.xan2d)
        }

        if (this.yan2d != 0) {
            stream.writeByte(6)
            stream.writeShort(this.yan2d)
        }

        var data: Int
        if (this.xOffset2d != 0) {
            stream.writeByte(7)
            var translateX = this.xOffset2d
            if (translateX < -32767) {
                translateX += 65536
            }
            stream.writeShort(translateX)
        }

        if (this.yOffset2d != 0) {
            stream.writeByte(8)
            var translateY = this.yOffset2d
            if (translateY < -32767) {
                translateY += 65536
            }
            stream.writeShort(translateY)
        }

        if (this.stackable >= 1 && this.notedItemId == -1) {
            stream.writeByte(11)
        }

        if (this.cost != 1 && this.lendedItemId == -1) {
            stream.writeByte(12)
            stream.writeInt(this.cost)
        }

        if (this.equipSlot != -1) {
            stream.writeByte(13)
            stream.writeByte(this.equipSlot)
        }

        if (this.equipType != -1) {
            stream.writeByte(14)
            stream.writeByte(this.equipType)
        }

        if (this.isMembersOnly && this.notedItemId == -1) {
            stream.writeByte(16)
        }

        if (this.maleEquipModelId1 != -1) {
            stream.writeByte(23)
            stream.writeBigSmart(this.maleEquipModelId1)
        }

        if (this.maleEquipModelId2 != -1) {
            stream.writeByte(24)
            stream.writeBigSmart(this.maleEquipModelId2)
        }

        if (this.femaleEquipModelId1 != -1) {
            stream.writeByte(25)
            stream.writeBigSmart(this.femaleEquipModelId1)
        }

        if (this.femaleEquipModelId2 != -1) {
            stream.writeByte(26)
            stream.writeBigSmart(this.femaleEquipModelId2)
        }

        for (index in 0..4) {
            val option = groundOptions[index]
            if (
                (index == 5 && option == "Examine") ||
                (index == 2 && option == "Take") ||
                (option == null)
            ) {
                continue
            }
            stream.writeByte(30 + index)
            stream.writeString(groundOptions[index])
        }

        for (index in 0..4) {
            val option = inventoryOptions!![index]
            if (index == 4 && option == "Drop" || option == null) {
                continue
            }
            stream.writeByte(35 + index)
            stream.writeString(inventoryOptions!![index])
        }

        if (this.originalModelColors != null && this.modifiedModelColors != null) {
            stream.writeByte(40)
            stream.writeByte(originalModelColors!!.size)

            data = 0
            while (data < originalModelColors!!.size) {
                stream.writeShort(originalModelColors!![data])
                stream.writeShort(modifiedModelColors!![data])
                ++data
            }
        }

        if (this.originalTextureColors != null && this.modifiedTextureColors != null) {
            stream.writeByte(41)
            stream.writeByte(originalTextureColors!!.size)

            data = 0
            while (data < originalTextureColors!!.size) {
                stream.writeShort(originalTextureColors!![data].toInt())
                stream.writeShort(modifiedTextureColors!![data].toInt())
                ++data
            }
        }

        if (this.recolorPalette != null) {
            stream.writeByte(42)
            stream.writeByte(recolorPalette!!.size)

            data = 0
            while (data < recolorPalette!!.size) {
                stream.writeByte(recolorPalette!![data].toInt())
                ++data
            }
        }

        if (this.isUnnoted) {
            stream.writeByte(65)
        }

        if (this.maleEquipModelId3 != -1) {
            stream.writeByte(78)
            stream.writeBigSmart(this.maleEquipModelId3)
        }

        if (this.femaleEquipModelId3 != -1) {
            stream.writeByte(79)
            stream.writeBigSmart(this.femaleEquipModelId3)
        }

        if (this.primaryMaleDialogueHead != -1) {
            stream.writeByte(90)
            stream.writeBigSmart(this.primaryMaleDialogueHead)
        }

        if (this.primaryFemaleDialogueHead != -1) {
            stream.writeByte(91)
            stream.writeBigSmart(this.primaryFemaleDialogueHead)
        }

        if (this.secondaryMaleDialogueHead != -1) {
            stream.writeByte(92)
            stream.writeBigSmart(this.secondaryMaleDialogueHead)
        }

        if (this.secondaryFemaleDialogueHead != -1) {
            stream.writeByte(93)
            stream.writeBigSmart(this.secondaryFemaleDialogueHead)
        }

        if (this.Zan2d != 0) {
            stream.writeByte(95)
            stream.writeShort(this.Zan2d)
        }

        if (this.switchNoteItemId != -1) {
            stream.writeByte(97)
            stream.writeShort(this.switchNoteItemId)
        }

        if (this.notedItemId != -1) {
            stream.writeByte(98)
            stream.writeShort(this.notedItemId)
        }

        if (this.stackIds != null && this.stackAmounts != null) {
            data = 0
            while (data < stackIds!!.size) {
                if (stackIds!![data] != 0 || stackAmounts!![data] != 0) {
                    stream.writeByte(100 + data)
                    stream.writeShort(stackIds!![data])
                    stream.writeShort(stackAmounts!![data])
                }
                ++data
            }
        }

        if (this.floorScaleX != 128) {
            stream.writeByte(110)
            stream.writeShort(this.floorScaleX)
        }

        if (this.floorScaleY != 128) {
            stream.writeByte(111)
            stream.writeShort(this.floorScaleY)
        }

        if (this.floorScaleZ != 128) {
            stream.writeByte(112)
            stream.writeShort(this.floorScaleZ)
        }

        if (this.ambience != 0) {
            stream.writeByte(113)
            stream.writeByte(this.ambience)
        }

        if (this.diffusion != 0) {
            stream.writeByte(114)
            stream.writeByte(this.diffusion)
        }

        if (this.teamId != 0) {
            stream.writeByte(115)
            stream.writeByte(this.teamId)
        }

        if (this.switchLendItemId != -1) {
            stream.writeByte(121)
            stream.writeShort(this.switchLendItemId)
        }

        if (this.lendedItemId != -1) {
            stream.writeByte(122)
            stream.writeShort(this.lendedItemId)
        }

        if (this.maleWieldX != 0 || (this.maleWieldY != 0) || (this.maleWieldZ != 0)) {
            stream.writeByte(125)
            stream.writeByte(this.maleWieldX)
            stream.writeByte(this.maleWieldY)
            stream.writeByte(this.maleWieldZ)
        }

        if (this.femaleWieldX != 0 || (this.femaleWieldY != 0) || (this.femaleWieldZ != 0)) {
            stream.writeByte(126)
            stream.writeByte(this.femaleWieldX)
            stream.writeByte(this.femaleWieldY)
            stream.writeByte(this.femaleWieldZ)
        }

        if (this.unknownArray2 != null) {
            stream.writeByte(132)
            stream.writeByte(unknownArray2!!.size)

            data = 0
            while (data < unknownArray2!!.size) {
                stream.writeShort(unknownArray2!![data])
                ++data
            }
        }

        if (this.clientScriptData != null) {
            stream.writeByte(249)
            stream.writeByte(clientScriptData!!.size)
            val var5: Iterator<*> = clientScriptData!!.keys.iterator()

            while (var5.hasNext()) {
                data = (var5.next() as Int)
                val value2 = clientScriptData!![data]
                stream.writeByte(if (value2 is String) 1 else 0)
                stream.write24BitInt(data)
                if (value2 is String) {
                    stream.writeString(value2 as String?)
                } else {
                    stream.writeInt(((value2 as Int?)!!))
                }
            }
        }

        stream.writeByte(0)
        val var6 = ByteArray(stream.getOffset())
        stream.setOffset(0)
        stream.getBytes(var6, 0, var6.size)
        return var6
    }

    private fun decode(stream: InputStream, opcode: Int) {
        if (opcode == 1) {
            this.invModelId = stream.readUnsignedShort()
        } else if (opcode == 2) {
            this.name = stream.readString()
        } else if (opcode == 4) {
            this.invModelZoom = stream.readUnsignedShort()
        } else if (opcode == 5) {
            this.xan2d = stream.readUnsignedShort()
        } else if (opcode == 6) {
            this.yan2d = stream.readUnsignedShort()
        } else if (opcode == 7) {
            this.xOffset2d = stream.readUnsignedShort()
            if (this.xOffset2d > Short.MAX_VALUE) {
                this.xOffset2d -= 65536
            }
        } else if (opcode == 8) {
            this.yOffset2d = stream.readUnsignedShort()
            if (this.yOffset2d > Short.MAX_VALUE) {
                this.yOffset2d -= 65536
            }
        } else if (opcode == 11) {
            this.stackable = 1
        } else if (opcode == 12) {
            this.cost = stream.readInt()
        } else if (opcode == 13) {
            this.equipSlot = stream.readUnsignedByte()
        } else if (opcode == 14) {
            this.equipType = stream.readUnsignedByte()
        } else if (opcode == 16) {
            this.isMembersOnly = true
        } else if (opcode == 18) {
            stream.readUnsignedShortLE()
        } else if (opcode == 23) {
            this.maleEquipModelId1 = stream.readUnsignedShort()
        } else if (opcode == 24) {
            this.maleEquipModelId2 = stream.readUnsignedShort()
        } else if (opcode == 25) {
            this.femaleEquipModelId1 = stream.readUnsignedShort()
        } else if (opcode == 26) {
            this.femaleEquipModelId2 = stream.readUnsignedShort()
        } else if (opcode == 27) {
            stream.readUnsignedByte()
        } else if (opcode >= 30 && opcode < 35) {
            groundOptions[opcode - 30] = stream.readString()
        } else if (opcode >= 35 && opcode < 40) {
            inventoryOptions!![opcode - 35] = stream.readString()
        } else {
            val length: Int
            var index: Int
            if (opcode == 40) {
                length = stream.readUnsignedByte()
                this.originalModelColors = IntArray(length)
                this.modifiedModelColors = IntArray(length)

                index = 0
                while (index < length) {
                    originalModelColors!![index] = stream.readUnsignedShort()
                    modifiedModelColors!![index] = stream.readUnsignedShort()
                    ++index
                }
            } else if (opcode == 41) {
                length = stream.readUnsignedByte()
                this.originalTextureColors = ShortArray(length)
                this.modifiedTextureColors = ShortArray(length)

                index = 0
                while (index < length) {
                    originalTextureColors!![index] = stream.readUnsignedShort().toShort()
                    modifiedTextureColors!![index] = stream.readUnsignedShort().toShort()
                    ++index
                }
            } else if (opcode == 42) {
                length = stream.readUnsignedByte()
                this.recolorPalette = ByteArray(length)

                index = 0
                while (index < length) {
                    recolorPalette!![index] = stream.readByte().toByte()
                    ++index
                }
            } else if (opcode == 65) {
                this.isUnnoted = true
            } else if (opcode == 78) {
                this.maleEquipModelId3 = stream.readUnsignedShort()
            } else if (opcode == 79) {
                this.femaleEquipModelId3 = stream.readUnsignedShort()
            } else if (opcode == 90) {
                this.primaryMaleDialogueHead = stream.readUnsignedShort()
            } else if (opcode == 91) {
                this.primaryFemaleDialogueHead = stream.readUnsignedShort()
            } else if (opcode == 92) {
                this.secondaryMaleDialogueHead = stream.readUnsignedShort()
            } else if (opcode == 93) {
                this.secondaryFemaleDialogueHead = stream.readUnsignedShort()
            } else if (opcode == 95) {
                this.Zan2d = stream.readUnsignedShort()
            } else if (opcode == 96) {
                this.dummyItem = stream.readUnsignedByte()
            } else if (opcode == 97) {
                this.switchNoteItemId = stream.readUnsignedShort()
            } else if (opcode == 98) {
                this.notedItemId = stream.readUnsignedShort()
            } else if (opcode >= 100 && opcode < 110) {
                if (this.stackIds == null) {
                    this.stackIds = IntArray(10)
                    this.stackAmounts = IntArray(10)
                }
                stackIds!![opcode - 100] = stream.readUnsignedShort()
                stackAmounts!![opcode - 100] = stream.readUnsignedShort()
            } else if (opcode == 110) {
                this.floorScaleX = stream.readUnsignedShort()
            } else if (opcode == 111) {
                this.floorScaleY = stream.readUnsignedShort()
            } else if (opcode == 112) {
                this.floorScaleZ = stream.readUnsignedShort()
            } else if (opcode == 113) {
                this.ambience = stream.readByte()
            } else if (opcode == 114) {
                this.diffusion = stream.readByte()
            } else if (opcode == 115) {
                this.teamId = stream.readUnsignedByte()
            } else if (opcode == 121) {
                this.switchLendItemId = stream.readUnsignedShort()
            } else if (opcode == 122) {
                this.lendedItemId = stream.readUnsignedShort()
            } else if (opcode == 125) {
                this.maleWieldX = stream.readByte()
                this.maleWieldY = stream.readByte()
                this.maleWieldZ = stream.readByte()
            } else if (opcode == 126) {
                this.femaleWieldX = stream.readByte()
                this.femaleWieldY = stream.readByte()
                this.femaleWieldZ = stream.readByte()
            } else if (opcode == 127) {
                this.unknownInt18 = stream.readUnsignedByte()
                this.unknownInt19 = stream.readUnsignedShort()
            } else if (opcode == 128) {
                this.unknownInt20 = stream.readUnsignedByte()
                this.unknownInt21 = stream.readUnsignedShort()
            } else if (opcode == 129) {
                this.unknownInt20 = stream.readUnsignedByte()
                this.unknownInt21 = stream.readUnsignedShort()
            } else if (opcode == 130) {
                this.unknownInt22 = stream.readUnsignedByte()
                this.unknownInt23 = stream.readUnsignedShort()
            } else if (opcode == 132) {
                length = stream.readUnsignedByte()
                this.unknownArray2 = IntArray(length)

                index = 0
                while (index < length) {
                    unknownArray2!![index] = stream.readUnsignedShort()
                    ++index
                }
            } else if (opcode == 134) {
                stream.readUnsignedByte()
            } else if (opcode == 139) {
                this.unknownValue2 = stream.readUnsignedShort()
            } else if (opcode == 140) {
                this.unknownValue1 = stream.readUnsignedShort()
            } else if (opcode == 191) {
                // int opcode191 = 0;
            } else if (opcode == 218) {
                // int opcode218 = 0;
            } else if (opcode == 219) {
                // int opcode219 = 0;
            } else if (opcode == 249) {
                val length = stream.readUnsignedByte()
                if (clientScriptData == null) {
                    clientScriptData = mutableMapOf()
                }
                repeat(length) {
                    val stringInstance = stream.readUnsignedByte() == 1
                    val key = stream.read24BitInt()
                    val value: Any = if (stringInstance) stream.readString() else stream.readInt()

                    clientScriptData!![key] = value
                }
            } else {
                throw RuntimeException("MISSING OPCODE $opcode FOR ITEM $id")
            }
        }
    }

    private fun readOpcodeValues(stream: InputStream) {
        while (true) {
            val opcode = stream.readUnsignedByte()
            if (opcode == 0) {
                return
            }

            this.decode(stream, opcode)
        }
    }

    fun resetTextureColors() {
        this.originalTextureColors = null
        this.modifiedTextureColors = null
    }

    val isWearItem: Boolean
        get() = this.equipSlot != -1

    fun changeTextureColor(originalModelColor: Short, modifiedModelColor: Short) {
        if (this.originalTextureColors != null) {
            for (newOriginalModelColors in originalTextureColors!!.indices) {
                if (originalTextureColors!![newOriginalModelColors] == originalModelColor) {
                    modifiedTextureColors!![newOriginalModelColors] = modifiedModelColor
                    return
                }
            }

            val var5 = originalTextureColors!!.copyOf(originalTextureColors!!.size + 1)
            val newModifiedModelColors =
                modifiedTextureColors!!.copyOf(modifiedTextureColors!!.size + 1)
            var5[var5.size - 1] = originalModelColor
            newModifiedModelColors[newModifiedModelColors.size - 1] = modifiedModelColor
            this.originalTextureColors = var5
            this.modifiedTextureColors = newModifiedModelColors
        } else {
            this.originalTextureColors = shortArrayOf(originalModelColor)
            this.modifiedTextureColors = shortArrayOf(modifiedModelColor)
        }
    }

    fun resetModelColors() {
        this.originalModelColors = null
        this.modifiedModelColors = null
    }

    fun changeModelColor(originalModelColor: Int, modifiedModelColor: Int) {
        if (this.originalModelColors != null) {
            for (newOriginalModelColors in originalModelColors!!.indices) {
                if (originalModelColors!![newOriginalModelColors] == originalModelColor) {
                    modifiedModelColors!![newOriginalModelColors] = modifiedModelColor
                    return
                }
            }

            val var5 = originalModelColors!!.copyOf(originalModelColors!!.size + 1)
            val newModifiedModelColors =
                modifiedModelColors!!.copyOf(modifiedModelColors!!.size + 1)
            var5[var5.size - 1] = originalModelColor
            newModifiedModelColors[newModifiedModelColors.size - 1] = modifiedModelColor
            this.originalModelColors = var5
            this.modifiedModelColors = newModifiedModelColors
        } else {
            this.originalModelColors = intArrayOf(originalModelColor)
            this.modifiedModelColors = intArrayOf(modifiedModelColor)
        }
    }

    public override fun clone(): Any {
        return try {
            super.clone()
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            this
        }
    }

    companion object {
        fun getItemDefinition(cache: Store, itemId: Int): ItemDefinition {
            return getItemDefinition(cache, itemId, true)
        }

        fun getItemDefinition(cache: Store, itemId: Int, load: Boolean): ItemDefinition {
            return ItemDefinition(cache, itemId, load)
        }
    }
}
