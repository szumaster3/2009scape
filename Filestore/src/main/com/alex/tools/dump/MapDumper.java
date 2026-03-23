package com.alex.tools.dump;

import com.alex.store.ArchiveReference;
import com.alex.store.Index;
import com.alex.store.ReferenceTable;
import com.alex.store.Store;
import com.alex.util.XTEAManager;
import com.alex.util.crypto.Djb2;
import com.alex.utils.CompressionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MapDumper {

    private static int dump(Index index, int fileId, File outDir, String name, int[] keys) {
        try {
            byte[] data = (keys == null)
                    ? index.getFile(fileId, 0)
                    : index.getFile(fileId, 0, keys);

            if (data == null) return 0;

            Files.write(new File(outDir, name).toPath(), CompressionUtils.gzip(data));
            return 1;

        } catch (Exception e) {
            System.out.println("Failed to dump: " + name + " -> " + e.getMessage());
            return 0;
        }
    }

    public static void dump(Store store, String path, Path xteaPath) {

        File outDir = new File(path);
        if (!outDir.exists()) outDir.mkdirs();

        if (!XTEAManager.load(xteaPath)) {
            System.out.println("Failed to load keys!");
            return;
        }

        Index mapIndex = store.getIndexes()[5];
        if (mapIndex == null || mapIndex.getTable() == null) {
            System.out.println("Maps not found!");
            return;
        }

        ReferenceTable table = mapIndex.getTable();

        Map<Integer, Integer> hashToRegion = new HashMap<>(256 * 256);

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {

                int regionId = (x << 8) | y;

                hashToRegion.put(Djb2.hash("m" + x + "_" + y), regionId);
                hashToRegion.put(Djb2.hash("l" + x + "_" + y), regionId);
            }
        }

        Map<Integer, Integer> mapFiles = new HashMap<>();
        Map<Integer, Integer> landFiles = new HashMap<>();

        for (int fileId : table.getValidArchiveIds()) {

            ArchiveReference entry = table.getArchives()[fileId];
            if (entry == null) continue;

            int ident = entry.getNameHash();

            Integer regionId = hashToRegion.get(ident);
            if (regionId == null) continue;

            int x = (regionId >> 8) & 0xFF;
            int y = regionId & 0xFF;

            if (ident == Djb2.hash("m" + x + "_" + y)) {
                mapFiles.put(regionId, fileId);
            } else {
                landFiles.put(regionId, fileId);
            }
        }

        Set<Integer> regions = new HashSet<>(mapFiles.keySet());
        regions.retainAll(landFiles.keySet());

        System.out.println("Regions found: " + regions.size());

        File mapsDir = new File(outDir, "map_files");
        File landsDir = new File(outDir, "land_files");

        if (!mapsDir.exists()) mapsDir.mkdirs();
        if (!landsDir.exists()) landsDir.mkdirs();

        int dumpedMaps = 0;
        int dumpedLands = 0;

        for (int regionId : regions) {

            int[] keys = XTEAManager.lookup(regionId);

            int x = (regionId >> 8) & 0xFF;
            int y = regionId & 0xFF;

            String mapName = "m" + x + "_" + y + ".gz";
            String landName = "l" + x + "_" + y + ".gz";

            dumpedMaps  += dump(mapIndex, mapFiles.get(regionId), mapsDir, mapName, null);
            dumpedLands += dump(mapIndex, landFiles.get(regionId), landsDir, landName, keys);
        }

        System.out.println("Dumped maps: " + dumpedMaps);
        System.out.println("Dumped lands: " + dumpedLands);

        print(outDir, regions, mapFiles, landFiles);
    }

    private static void print(File dir,
                              Set<Integer> regions,
                              Map<Integer, Integer> mapFiles,
                              Map<Integer, Integer> landFiles) {

        File file = new File(dir, "map_index.txt");

        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (var writer = Files.newBufferedWriter(file.toPath())) {
                for (int regionId : regions) {

                    Integer mapFile = mapFiles.get(regionId);
                    Integer landFile = landFiles.get(regionId);

                    if (mapFile == null || landFile == null) continue;

                    int regionX = (regionId >> 8) & 0xFF;
                    int regionY = regionId & 0xFF;

                    int x = regionX * 64;
                    int y = regionY * 64;

                    writer.write(
                            "regionId=" + regionId + " " +
                                    "x=" + x + " " +
                                    "y=" + y + " " +
                                    "mapFile=" + mapFile + " " +
                                    "landFile=" + landFile
                    );

                    writer.newLine();
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to save index: " + e.getMessage());
        }
    }

    public static void verify(Store store, String path) {

        Index mapIndex = store.getIndexes()[5];
        if (mapIndex == null || mapIndex.getTable() == null) {
            System.out.println("Maps not found!");
            return;
        }

        ReferenceTable table = mapIndex.getTable();

        File file = new File(path, "map_verify.txt");

        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            int errors = 0;

            try (var writer = java.nio.file.Files.newBufferedWriter(file.toPath())) {

                for (int fileId : table.getValidArchiveIds()) {

                    ArchiveReference entry = table.getArchives()[fileId];
                    if (entry == null) continue;

                    int regionX = (entry.getNameHash() >>> 8) & 0xFF;
                    int regionY = entry.getNameHash() & 0xFF;

                    int regionId = (regionX << 8) | regionY;

                    int[] keys = XTEAManager.lookup(regionId);

                    try {
                        mapIndex.getFile(fileId, 0, keys);

                    } catch (Exception e) {

                        try {
                            mapIndex.getFile(fileId, 0);

                            String msg = String.format(
                                    "Region %d (%d, %d) is NOT encrypted",
                                    regionId,
                                    regionX * 64,
                                    regionY * 64
                            );

                            System.out.println(msg);
                            writer.write(msg);
                            writer.newLine();

                        } catch (Exception ex) {

                            String msg = String.format(
                                    "FAILED Region %d (%d, %d), fileId=%d, keys=%s",
                                    regionId,
                                    regionX * 64,
                                    regionY * 64,
                                    fileId,
                                    keys != null ? java.util.Arrays.toString(keys) : "null"
                            );

                            System.out.println(msg);
                            writer.write(msg);
                            writer.newLine();

                            errors++;
                        }
                    }
                }

                String summary = "Verification errors: " + errors;

                System.out.println(summary);
                writer.write(summary);
                writer.newLine();
            }

        } catch (Exception e) {
            System.out.println("Failed to save verify log: " + e.getMessage());
        }
    }
}