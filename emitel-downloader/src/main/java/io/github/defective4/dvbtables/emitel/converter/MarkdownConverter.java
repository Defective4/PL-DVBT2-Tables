package io.github.defective4.dvbtables.emitel.converter;

import java.util.Map;

public class MarkdownConverter {

    private static final String FREQ_TITLE = "Frequency (MHz)";

    private static final String LOCATION_TITLE = "Location";
    private static final String MUX_TITLE = "Multiplex";
    private static final String TABLE_FORMAT = "| %s | %s | %s |\n";

    public static String convertToMarkdownTables(Map<String, Map<String, Integer>> map) {
        StringBuilder builder = new StringBuilder();
        int locationMaxLength = LOCATION_TITLE.length();
        int muxMaxLength = MUX_TITLE.length();
        int freqMaxLength = FREQ_TITLE.length();

        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            locationMaxLength = Math.max(locationMaxLength, entry.getKey().length());
            for (Map.Entry<String, Integer> txEntry : entry.getValue().entrySet()) {
                muxMaxLength = Math.max(muxMaxLength, entry.getKey().length());
                freqMaxLength = Math.max(freqMaxLength, Integer.toString(txEntry.getValue()).length());
            }
        }

        builder
                .append(String
                        .format(TABLE_FORMAT, LOCATION_TITLE + " ".repeat(locationMaxLength - LOCATION_TITLE.length()),
                                MUX_TITLE + " ".repeat(muxMaxLength - MUX_TITLE.length()),
                                FREQ_TITLE + " ".repeat(freqMaxLength - FREQ_TITLE.length())));
        builder
                .append(String
                        .format(TABLE_FORMAT, "-".repeat(locationMaxLength), "-".repeat(muxMaxLength),
                                "-".repeat(freqMaxLength)));

        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            for (Map.Entry<String, Integer> txEntry : entry.getValue().entrySet()) {
                String location = entry.getKey();
                String mux = txEntry.getKey();
                String freq = Integer.toString(txEntry.getValue());

                builder
                        .append(String
                                .format(TABLE_FORMAT, location + " ".repeat(locationMaxLength - location.length()),
                                        mux + " ".repeat(muxMaxLength - mux.length()),
                                        freq + " ".repeat(freqMaxLength - freq.length())));
            }
        }

        return builder.toString();
    }
}
