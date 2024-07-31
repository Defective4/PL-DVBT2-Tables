package io.github.defective4.dvbtables.emitel.converter;

import java.util.Map;

public class DVBConverter {
    public static String convertToDVBTable(Map<String, Map<String, Integer>> table) {
        StringBuilder dvbBuilder = new StringBuilder();

        table.entrySet().forEach(transmitterEntry -> {
            dvbBuilder.append(String.format("[dvb-t/pl-%s-dvb-t2]\n", transmitterEntry.getKey()));
            transmitterEntry.getValue().entrySet().forEach(muxEntry -> {
                dvbBuilder.append("T2 " + muxEntry.getValue() * 1000000 + " 8MHz AUTO AUTO AUTO AUTO AUTO AUTO 0\n");
            });
        });

        return dvbBuilder.toString();
    }
}
