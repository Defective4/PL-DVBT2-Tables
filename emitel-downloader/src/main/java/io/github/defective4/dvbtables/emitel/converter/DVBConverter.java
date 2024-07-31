package io.github.defective4.dvbtables.emitel.converter;

import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DVBConverter {
    public static String convertToDVBTable(Map<String, Map<String, Integer>> table) {
        StringBuilder dvbBuilder = new StringBuilder();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        dvbBuilder.append("[date]\n");
        dvbBuilder.append(fmt.format(new Date()));
        dvbBuilder.append("\n");

        table.entrySet().forEach(transmitterEntry -> {
            dvbBuilder.append(String.format("[dvb-t/pl-%s-dvb-t2]\n", transmitterEntry.getKey()));
            transmitterEntry.getValue().entrySet().forEach(muxEntry -> {
                dvbBuilder.append("T2 " + muxEntry.getValue() * 1000000 + " 8MHz AUTO AUTO AUTO AUTO AUTO AUTO 0\n");
            });
        });

        return dvbBuilder.toString();
    }
}
