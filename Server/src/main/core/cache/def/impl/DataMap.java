package core.cache.def.impl;

import core.cache.Cache;
import core.cache.misc.buffer.ByteBufferUtils;
import core.tools.Log;
import core.tools.StringUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.log;

public class DataMap {
    private static final Map<Integer, DataMap> definitions = new HashMap<>();
    private final int id;
    public char keyType = '?';
    public char valueType = '?';
    public String defaultString;
    public int defaultInt;

    public HashMap<Integer, Object> dataStore = new HashMap<>();

    public DataMap(int id) {
        this.id = id;
    }

    public int getInt(int key) {
        Object value = dataStore.get(key);

        if (value != null) {
            return (int) value;
        }

        if (defaultInt != 0) {
            return defaultInt;
        }

        log(this.getClass(), Log.ERR, "Missing int key: " + key + " in DataMap: " + id);

        return 0;
    }

    public String getString(int key) {
        Object value = dataStore.get(key);

        if (value != null) {
            return (String) value;
        }

        return defaultString != null ? defaultString : "";
    }

    public static DataMap get(int id) {
        DataMap def = definitions.get(id);
        if (def != null) {
            return def;
        }
        byte[] data = Cache.getIndexes()[17].getFileData(id >>> 8, id & 0xFF, null);
        def = parse(id, data);
        definitions.put(id, def);
        return def;
    }

    public static DataMap parse(int id, byte[] data) {
        DataMap def = new DataMap(id);
        if (data == null) {
            return def;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int opcode;

        while ((opcode = buffer.get() & 0xFF) != 0) {
            if (opcode == 1) {
                def.keyType = StringUtils.getFromByte(buffer.get());
            } else if (opcode == 2) {
                def.valueType = StringUtils.getFromByte(buffer.get());
            } else if (opcode == 3) {
                def.defaultString = ByteBufferUtils.getString(buffer);
            } else if (opcode == 4) {
                def.defaultInt = buffer.getInt();
            } else if (opcode == 5 || opcode == 6) {
                int size = buffer.getShort() & 0xFFFF;
                for (int i = 0; i < size; i++) {
                    int key = buffer.getInt();
                    Object value;
                    if (opcode == 5) {
                        value = ByteBufferUtils.getString(buffer);
                    } else {
                        value = buffer.getInt();
                    }
                    def.dataStore.put(key, value);
                }
            }
        }
        return def;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DataMapDefinition{" + "id=" + id + ", keyType=" + keyType + ", valueType=" + valueType + ", defaultString='" + defaultString + '\'' + ", defaultInt=" + defaultInt + ", dataStore=" + dataStore + '}';
    }
}