package io.github.defective4.dvbtables.emitel.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VLCConverter {

    private static final DocumentBuilder BUILDER;

    static {
        try {
            BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException();
        }
    }

    public static Map<String, Map<String, Document>> convertToVLCPlaylists(Map<String, Map<String, Integer>> table) {
        Map<String, Map<String, Document>> map = new HashMap<>();
        table.entrySet().forEach(transmitterEntry -> {
            Map<String, Document> docs = new HashMap<>();
            transmitterEntry.getValue().entrySet().forEach(muxEntry -> {
                docs.put(muxEntry.getKey(), constructVLCPlaylist(muxEntry.getKey(), muxEntry.getValue()));
            });
            map.put(transmitterEntry.getKey(), docs);
        });
        return Collections.unmodifiableMap(map);
    }

    private static Document constructVLCPlaylist(String mux, int freq) {
        Document doc = BUILDER.newDocument();
        Element playlist = doc.createElement("playlist");
        playlist.setAttribute("xmlns", "http://xspf.org/ns/0/");
        playlist.setAttribute("xmlns:vlc", "http://www.videolan.org/vlc/playlist/ns/0/");
        playlist.setAttribute("version", "1");
        Element title = doc.createElement("title");
        title.setTextContent(mux);

        Element trackList = doc.createElement("trackList");
        Element track = doc.createElement("track");
        Element location = doc.createElement("location");
        location.setTextContent(String.format("dvb-t2://frequency=%s:bandwidth=0", freq * 1000));
        Element trackTitle = doc.createElement("title");
        trackTitle.setTextContent(mux);
        Element creator = doc.createElement("creator");
        creator.setTextContent("Emitel");
        Element trackExtension = doc.createElement("extension");
        trackExtension.setAttribute("application", "http://www.videolan.org/vlc/playlist/0");
        Element vlc_id = doc.createElement("vlc:id");
        vlc_id.setTextContent("0");
        Element vlc_option1 = doc.createElement("vlc:option");
        vlc_option1.setTextContent("dvb-adapter=0");
        Element vlc_option2 = doc.createElement("vlc:option");
        vlc_option2.setTextContent("live-caching=300");

        trackExtension.appendChild(vlc_id);
        trackExtension.appendChild(vlc_option1);
        trackExtension.appendChild(vlc_option2);

        track.appendChild(location);
        track.appendChild(trackTitle);
        track.appendChild(creator);
        track.appendChild(trackExtension);

        trackList.appendChild(track);

        Element extension = doc.createElement("extension");
        extension.setAttribute("application", "http://www.videolan.org/vlc/playlist/0");
        Element vlc_item = doc.createElement("vlc:item");
        vlc_item.setAttribute("tid", "0");
        extension.appendChild(vlc_item);

        playlist.appendChild(title);
        playlist.appendChild(trackList);
        playlist.appendChild(extension);

        doc.appendChild(playlist);
        return doc;
    }
}
