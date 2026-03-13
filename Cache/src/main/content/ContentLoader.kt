package content

import com.alex.Cache
import com.alex.loaders.sprites.SpriteUtils
import com.alex.utils.Utils

object ContentLoader {

    @JvmStatic
    fun main(args: Array<String>)
    {
        runCatching {
            println("Initializing cache...")

            Cache.init()
            println("Populating cache...")

            load()
            println("Cache populated successfully.")
        }.onFailure { e ->
            e.printStackTrace()
        }

    }

    private fun load()
    {
        sprites()
        interfaces()
    }

    private fun sprites()
    {
        SpriteUtils.importSprites()
    }

    private fun interfaces()
    {
        content.interfaces.CustomSpellBookInterface.add()
    }
}