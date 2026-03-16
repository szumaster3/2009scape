package backend.alex.tools

import backend.alex.Cache
import backend.alex.loaders.MapIndex
import java.io.File

object MapDumper {

    @JvmStatic
    fun dump(hash: Boolean = true) {
        val store = Cache.getStore()
        MapIndex.init(store)
        val mapIndex = store.indexes[5]
        val outDir = File("dumps/maps/")
        if (!outDir.exists()) outDir.mkdirs()
        val dumpedRegionHashes = mutableListOf<Int>()
        for (regionId in 0 until 32768) {
            val x = regionId shr 8
            val y = regionId and 0xFF

            val mapArchive = mapIndex.getArchiveId("m${x}_$y")
            val landArchive = mapIndex.getArchiveId("l${x}_$y")

            if (mapArchive != null || landArchive != null) {
                if (mapArchive != null) {
                    val mapData = mapIndex.getFile(mapArchive, 0)
                    if (mapData != null) {
                        val mapFileName = if (hash) "map_$regionId.dat" else "m${x}_$y.dat"
                        File(outDir, mapFileName).writeBytes(mapData)
                        println("Dumped $mapFileName")
                    }
                }

                if (landArchive != null) {
                    val landData = mapIndex.getFile(landArchive, 0)
                    if (landData != null) {
                        val landFileName = if (hash) "land_$regionId.dat" else "l${x}_$y.dat"
                        File(outDir, landFileName).writeBytes(landData)
                        println("Dumped $landFileName")
                    }
                }

                MapIndex.setRegion(regionId, landArchive ?: -1, mapArchive ?: -1)
                dumpedRegionHashes.add(regionId)
            }
        }

        val indexFile = File(outDir, "dumps/map_index.txt")
        indexFile.printWriter().use { writer ->
            MapIndex.getAll().forEach { (regionHash, landId, mapId) ->
                writer.println("$regionHash,$landId,$mapId")
            }
        }
    }
}