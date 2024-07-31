package io.github.defective4.dvbtables.emitel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EmitelAPI {
    private static final URL SITE_URL;

    static {
        try {
            SITE_URL = URI.create("https://emitel.pl/strefa-widza/").toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, Map<String, Integer>> downloadTransmittersTable() throws IOException {
        Map<String, Map<String, Integer>> transmitters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(SITE_URL, 60000);
        Elements accordions = doc.getElementsByClass("accordion");
        for (Element accordion : accordions.get(1).getElementsByClass("accordion__panel")) {
            String emissionType = accordion.getElementsByTag("h3").get(0).text();
            if (emissionType.contains(" (DVB-T2")) {
                String mux = emissionType.substring(0, emissionType.indexOf(' '));
                Element table = accordion.getElementsByTag("tbody").get(0);
                Elements rows = table.getElementsByTag("tr");
                for (Element row : rows) {
                    Elements cells = row.getElementsByTag("td");
                    String name = cells
                            .get(1)
                            .text()
                            .replace(" / ", "_")
                            .replace('/', '_')
                            .replace(' ', '_')
                            .replace(".", "");
                    int freq = Integer.parseInt(cells.get(2).text());
                    if (!transmitters.containsKey(name)) transmitters.put(name, new LinkedHashMap<>());
                    transmitters.get(name).put(mux, freq);
                }
            }
        }
        return Collections.unmodifiableMap(transmitters);
    }
}
