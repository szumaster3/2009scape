package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.SpotAnimDefinition

class SpotAnimPacker private constructor(private val startId: Int = 0) {
    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copied = mutableListOf<SpotAnimDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (SpotAnimDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): SpotAnimPacker = SpotAnimPacker()
    }

    fun startAt(id: Int): SpotAnimPacker {
        currentId = id
        return this
    }

    fun addSpotAnim(modifier: SpotAnimDefinition.() -> Unit): SpotAnimPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addSpotAnims(vararg modifiers: SpotAnimDefinition.() -> Unit): SpotAnimPacker {
        modifiers.forEach { addSpotAnim(it) }
        return this
    }

    fun copySpotAnim(sourceId: Int, modifier: SpotAnimDefinition.() -> Unit = {}): SpotAnimPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editSpotAnim(id: Int, modifier: SpotAnimDefinition.() -> Unit = {}): SpotAnimPacker {
        tasks.add(Task(id, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: SpotAnimDefinition.() -> Unit = {}): SpotAnimPacker {
        for (id in fromId..toId) {
            copySpotAnim(id, modifier)
        }
        return this
    }

    fun save(): List<SpotAnimDefinition> {
        val store = Cache.getStore()
            ?: throw IllegalStateException("Cache store not loaded!")

        copied.clear()

        val index = store.indexes[2]
        val archive = 4

        for (task in tasks) {

            val spot = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val src = SpotAnimDefinition(store, task.sourceId)
                    val copy = SpotAnimDefinition(currentId)
                    copy.modelId = src.modelId
                    copy.seqId = src.seqId
                    copy.resizeXZ = src.resizeXZ
                    copy.resizeY = src.resizeY
                    copy.angle = src.angle
                    copy.ambient = src.ambient
                    copy.contrast = src.contrast
                    copy.aBoolean100 = src.aBoolean100
                    copy.recol_s = src.recol_s
                    copy.recol_d = src.recol_d
                    copy.retex_s = src.retex_s
                    copy.retex_d = src.retex_d
                    copy.apply { task.modifier?.invoke(this) }
                }

                task.sourceId >= 0 && task.isEdit -> {
                    val existing = SpotAnimDefinition(store, task.sourceId)
                    existing.apply { task.modifier?.invoke(this) }
                }

                else -> {
                    val newDef = SpotAnimDefinition(currentId)
                    newDef.apply { task.modifier?.invoke(this) }
                }
            }

            val data = spot.encode()
            index.putFile(archive, spot.id, data)

            copied.add(spot)

            println("Packed spotanim ${spot.id}")

            if (!task.isEdit) {
                currentId++
            }
        }

        return copied
    }

    fun getCopied(): List<SpotAnimDefinition> = copied
}