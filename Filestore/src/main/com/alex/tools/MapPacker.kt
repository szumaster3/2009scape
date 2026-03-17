package com.alex.tools

import com.alex.Cache
import com.alex.loaders.MapIndex
import java.io.File
import java.nio.file.Files

object MapPacker {
    @JvmStatic
    fun pack() {
        val store = Cache.getStore()
        val mapIndex = store.indexes[5]

        val mapDir = File("Maps/map_files/")
        val landDir = File("Maps/land_files/")

        if (!mapDir.exists() || !landDir.exists()) {
            println("Folders not found!")
            return
        }

        mapDir.listFiles { f -> f.extension == "dat" }?.forEach { mapFile ->
            val regionId = mapFile.nameWithoutExtension.removePrefix("map_").toIntOrNull()
            if (regionId == null) {
                println("Invalid map file name: ${mapFile.name}")
                return@forEach
            }

            val landFile = File(landDir, "land_$regionId.dat")
            if (!landFile.exists()) {
                println("Missing land file for region $regionId")
                return@forEach
            }

            val x = regionId shr 8
            val y = regionId and 0xFF

            val mapArchiveId =
                mapIndex.getArchiveId("m${x}_$y").takeIf { it != -1 } ?: (mapIndex.lastArchiveId + 1)
            val landArchiveId =
                mapIndex.getArchiveId("l${x}_$y").takeIf { it != -1 } ?: (mapIndex.lastArchiveId + 1)

            val mapData = Files.readAllBytes(mapFile.toPath())
            val landData = Files.readAllBytes(landFile.toPath())

            mapIndex.putFile(mapArchiveId, 0, 0, mapData, null, false, false, 0, 0)
            mapIndex.putFile(landArchiveId, 0, 0, landData, null, false, false, 0, 0)

            MapIndex.setRegion(regionId, landArchiveId, mapArchiveId)

            println("Packed region $regionId  [m${x}_$y, l${x}_$y]")
        }
    }
}