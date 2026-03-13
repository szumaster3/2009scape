package content.items

import com.alex.tools.ItemCopy

object ItemsLoader {

    fun add() {
        val copy = ItemCopy.create().startAt(20000)
        copy.addItems({ name = "Devastator" })
        copy.addNoteItem()
        copy.addItems({ name = "Pliers" })
        copy.addNoteItem()
        copy.save()
    }
}