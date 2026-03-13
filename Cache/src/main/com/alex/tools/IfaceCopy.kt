package com.alex.tools

import com.alex.Cache
import com.alex.loaders.interfaces.ComponentDefinition
import com.alex.util.crc32.CRC32HGenerator
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

class IfaceCopy private constructor(val targetInterface: Int) {
    private val tasks: MutableList<CopyTask> = ArrayList()

    private var src = 0
    private var startId = 0
    private var currentId = 0
    private var modifier: Consumer<ComponentDefinition?>? = null
    private val copiedComponents = mutableListOf<ComponentDefinition>()

    fun from(sourceInterface: Int): IfaceCopy {
        this.src = sourceInterface
        return this
    }

    fun startAt(id: Int): IfaceCopy {
        this.startId = id
        this.currentId = id
        return this
    }

    fun modify(modifier: Consumer<ComponentDefinition?>?): IfaceCopy {
        this.modifier = modifier
        return this
    }

    fun copy(sourceId: Int): CopyTask {
        val task = CopyTask(this, sourceId, currentId++)
        tasks.add(task)
        return task
    }

    fun copy(vararg sourceIds: Int): IfaceCopy {
        for (id in sourceIds) {
            tasks.add(CopyTask(this, id, currentId++))
        }
        return this
    }

    fun copyRange(from: Int, to: Int): IfaceCopy {
        for (i in from..to) {
            copy(i)
        }
        return this
    }

    fun addComponents(vararg modifiers: Consumer<ComponentDefinition>?): IfaceCopy {
        for (mod in modifiers) {
            val task = CopyTask(this, -1, currentId++)
            task.modifier = mod
            tasks.add(task)
        }
        return this
    }

    private fun createNewComponent(targetId: Int): ComponentDefinition? {
        var comps = ComponentDefinition.getInterface(targetInterface, true)
        if (comps!!.size <= targetId) {
            val expanded = arrayOfNulls<ComponentDefinition>(targetId + 1)
            System.arraycopy(comps, 0, expanded, 0, comps.size)
            ComponentDefinition.componentDefinition?.set(targetInterface, expanded)
            comps = expanded
        }
        if (comps[targetId] == null) {
            comps[targetId] = ComponentDefinition()
        }
        comps[targetId]!!.componentHash = (targetInterface shl 16) or targetId
        return comps[targetId]
    }

    fun save(): List<ComponentDefinition> {
        copiedComponents.clear()
        val store = Cache.getStore() ?: return copiedComponents

        for (task in tasks) {
            val comp: ComponentDefinition? = if (task.sourceId >= 0) {
                ComponentDefinition.getInterfaceComponent(src, task.sourceId)
            } else {
                createNewComponent(task.targetId)
            }

            comp!!.componentHash = (targetInterface shl 16) or task.targetId

            modifier?.accept(comp)
            task.modifier?.accept(comp)

            val fileNameHash: Int? = comp.name?.takeIf { it.isNotBlank() }?.let {
                CRC32HGenerator.getHash(it.uppercase(Locale.getDefault()).toByteArray(Charsets.UTF_8))
            }

            if (fileNameHash != null) {
                store.indexes[3].putFile(targetInterface, task.targetId, 2, comp.encode(comp.hasScripts), null, true, true, -1, fileNameHash)
            } else {
                store.indexes[3].putFile(targetInterface, task.targetId, comp.encode(comp.hasScripts))
            }

            copiedComponents.add(comp)
        }

        init(targetInterface)

        return copiedComponents
    }

    private fun init(targetId: Int) {
        val compDefs = ComponentDefinition.componentDefinition
        if (compDefs == null || compDefs.size <= targetId) {
            val newSize = targetId + 1
            val expanded = arrayOfNulls<Array<ComponentDefinition?>?>(newSize)
            compDefs?.copyInto(expanded)
            ComponentDefinition.componentDefinition = expanded
        }

        var comps = ComponentDefinition.componentDefinition!![targetId]
        val maxTarget = tasks.maxOfOrNull { it.targetId } ?: -1
        if (comps == null || comps.size <= maxTarget) {
            val newComps = arrayOfNulls<ComponentDefinition>(maxTarget + 1)
            comps?.copyInto(newComps)
            ComponentDefinition.componentDefinition!![targetId] = newComps
        }
    }

    fun addComponents(vararg modifiers: ComponentDefinition.() -> Unit): IfaceCopy {
        for (mod in modifiers) {
            val task = CopyTask(this, -1, currentId++)
            task.modifier = Consumer { c -> c.mod() }
            tasks.add(task)
        }
        return this
    }

    fun getCopiedComponents(): List<ComponentDefinition> = copiedComponents

    companion object {

        @JvmStatic
        fun to(targetInterface: Int): IfaceCopy = IfaceCopy(targetInterface)

        @JvmStatic
        fun newInterface(): IfaceCopy {
            val id = createInterface(6, 36)
            return IfaceCopy(id)
        }

        @JvmStatic
        fun newInterface(templateInterface: Int, templateComponent: Int): IfaceCopy {
            val id = createInterface(templateInterface, templateComponent)
            return IfaceCopy(id)
        }

        @JvmStatic
        fun createInterface(templateInterface: Int, templateComponent: Int): Int {
            val newId = ComponentDefinition.getInterfaceDefinitionsSize()
            val base = ComponentDefinition.getInterfaceComponent(templateInterface, templateComponent)
            if (base != null) {
                base.baseX = 0
                base.baseY = 0
                base.parentId = -1
                Cache.getStore()!!.indexes[3].putFile(newId, 0, base.encode(base.hasScripts))
            }
            ComponentDefinition.componentDefinition = arrayOfNulls(newId + 1)
            return newId
        }
    }
}