package content

import backend.alex.Cache
import backend.alex.loaders.BasDefinition
import backend.alex.loaders.LocDefinition
import backend.alex.loaders.items.ItemDefinition
import backend.alex.tools.*
import content.items.AntiqueLamps
import content.items.ArdougneCloaks

object ContentLoader {
    @JvmStatic
    fun main(args: Array<String>) {
        runCatching {
            Cache.init()
            //print()
            load()
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    private fun load() {
        models()
        sprites()
        objects()
        interfaces()
        items()
    }

    private fun interfaces() {
        content.interfaces.AchievementDiaryInterface.add()
    }

    private fun items() {
        ArdougneCloaks.add()
        AntiqueLamps.add()
    }

    private fun objects() {

    }

    private fun models() {
        ModelPacker.add()
    }

    private fun sprites() {
        //SpritePacker.add()
    }

    private fun print() {
        LocDefinition.print(Cache.getStore(), "dumps/object_dumps.txt")
        ItemDefinition.print(Cache.getStore(), "dumps/item_dumps.txt")
        BasDefinition.print(Cache.getStore(), "dumps/bas_dumps.txt")
    }

    private fun dump() {
        MapDumper.dump()
        SpriteDumper.dump()
        ModelDumper.dump()
    }
}