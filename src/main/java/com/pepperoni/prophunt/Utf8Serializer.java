package com.pepperoni.prophunt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utf8Serializer {
    public static Utf8SerializedData serialize(byte[] message, int size, int offset) {
        List<Byte> sizeBuffer = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            sizeBuffer.add(message[offset]);
            offset += 1;
        }

        List<String> data = new ArrayList<>();
        for (Byte length : sizeBuffer) {
            String utf8String = new String(message, offset, length, StandardCharsets.UTF_8);
            data.add(utf8String);
            offset += length;
        }

        return new Utf8SerializedData(data.toArray(new String[0]), offset);
    }

    public static class Utf8SerializedData {
        public String[] data;
        public int offset;

        public Utf8SerializedData(String[] data, int offset) {
            this.data = data;
            this.offset = offset;
        }
    }
}