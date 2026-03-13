package com.alex.tools

import com.alex.Cache
import com.alex.loaders.items.ItemDefinition
import com.alex.store.Store
import java.util.function.Consumer

class ItemCopy private constructor() {

    private val tasks = mutableListOf<ItemTask>()
    private var currentId = 0
    private var modifier: Consumer<ItemDefinition>? = null
    private val copiedItems = mutableListOf<ItemDefinition>()

    fun startAt(id: Int): ItemCopy {
        this.currentId = id
        return this
    }

    fun modify(modifier: Consumer<ItemDefinition>?): ItemCopy {
        this.modifier = modifier
        return this
    }

    fun copy(sourceId: Int): ItemTask {
        val task = ItemTask(sourceId, currentId++)
        tasks.add(task)
        return task
    }

    fun copy(vararg sourceIds: Int): ItemCopy {
        sourceIds.forEach { copy(it) }
        return this
    }

    fun copyRange(from: Int, to: Int): ItemCopy {
        for (i in from..to) copy(i)
        return this
    }

    fun addItems(vararg modifiers: Consumer<ItemDefinition>?): ItemCopy {
        for (mod in modifiers) {
            val task = ItemTask(-1, currentId++)
            task.modifier = mod
            tasks.add(task)
        }
        return this
    }

    fun addItems(vararg modifiers: ItemDefinition.() -> Unit): ItemCopy {
        for (mod in modifiers) {
            val task = ItemTask(-1, currentId++)
            task.kModifier = mod
            tasks.add(task)
        }
        return this
    }

    private fun createNewItem(store: Store, id: Int): ItemDefinition {
        return ItemDefinition(store, id, false)
    }

    fun save(): List<ItemDefinition> {
        copiedItems.clear()
        val store = Cache.getStore() ?: return copiedItems

        for (task in tasks) {

            val item = if (task.sourceId >= 0) {
                val src = ItemDefinition.getItemDefinition(store, task.sourceId)
                val clone = src.clone() as ItemDefinition
                clone.id = task.targetId
                clone
            } else {
                createNewItem(store, task.targetId)
            }

            modifier?.accept(item)
            task.modifier?.accept(item)
            task.kModifier?.invoke(item)

            item.write(store)

            copiedItems.add(item)
        }

        return copiedItems
    }

    fun addNoteItem(templateId: Int = 799): ItemDefinition {
        val store = Cache.getStore() ?: error("Store not initialized")
        val baseItem = copiedItems.lastOrNull() ?: error("No base item to create a note from")

        val newId = currentId++

        val noteItem = ItemDefinition(store, newId, false).apply {
            id = newId
            notedItemId = baseItem.id
            switchNoteItemId = templateId
        }
        noteItem.write(store)
        copiedItems.add(noteItem)
        return noteItem
    }

    fun getCopiedItems(): List<ItemDefinition> = copiedItems

    class ItemTask(val sourceId: Int, val targetId: Int) {
        var modifier: Consumer<ItemDefinition>? = null
        var kModifier: (ItemDefinition.() -> Unit)? = null
    }

    fun load(): Int {
        val store = Cache.getStore() ?: error("Store not initialized")
        val lastArchiveId: Int = store.indexes[19].lastArchiveId
        val validFiles = store.indexes[19].getValidFilesCount(lastArchiveId)
        return lastArchiveId * 256 + validFiles
    }

    companion object {
        @JvmStatic
        fun create() = ItemCopy()
    }
}