package backend.alex.loaders

import backend.alex.store.Index
import backend.alex.store.Store

object MapIndex {
    private val regionHashList = mutableListOf<Int>()
    private val landscapeIds = mutableListOf<Int>()
    private val objectIds = mutableListOf<Int>()
    private lateinit var mapIndex: Index

    fun init(store: Store) {
        mapIndex = store.indexes[5]
    }

    fun setRegion(regionId: Int, landArchive: Int, mapArchive: Int) {
        val x = regionId shr 8
        val y = regionId and 0xFF
        val regionHash = (x shl 8) or y
        update(regionHash, landArchive, mapArchive)
        println("Updated map_index: region=$regionId, regionHash=$regionHash, land=$landArchive, map=$mapArchive")
    }

    fun update(regionHash: Int, land: Int, map: Int) {
        val index = regionHashList.indexOf(regionHash)
        if (index != -1) {
            landscapeIds[index] = land
            objectIds[index] = map
        } else {
            regionHashList.add(regionHash)
            landscapeIds.add(land)
            objectIds.add(map)
        }
    }

    fun getAll(): List<Triple<Int, Int, Int>> {
        return regionHashList.mapIndexed { i, hash -> Triple(hash, landscapeIds[i], objectIds[i]) }
    }
}