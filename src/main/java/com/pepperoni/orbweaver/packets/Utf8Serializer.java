package com.pepperoni.orbweaver.packets;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utf8Serializer {
    public static Utf8SerializedData serialize(byte[] message, int size, int offset) {
        Utf8SerializedData result = new Utf8SerializedData();
        List<String> dataList = new ArrayList<>();

        List<Integer> sizeList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            sizeList.add(message[offset] & 0xFF);
            offset++;
        }

        for (int length : sizeList) {
            if (length <= 0) {
                continue;
            }

            String utf8String = new String(message, offset, length, StandardCharsets.UTF_8);
            dataList.add(utf8String);
            offset += length;
        }

        result.data = dataList.toArray(new String[0]);
        result.offset = offset;
        return result;
    }

    public static class Utf8SerializedData {
        public String[] data;
        public int offset;
    }
}