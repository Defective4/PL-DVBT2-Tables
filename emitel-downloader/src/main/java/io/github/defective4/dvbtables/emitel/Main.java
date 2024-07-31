package io.github.defective4.dvbtables.emitel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import io.github.defective4.dvbtables.emitel.converter.DVBConverter;
import io.github.defective4.dvbtables.emitel.converter.MarkdownConverter;
import io.github.defective4.dvbtables.emitel.converter.VLCConverter;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("Usage: emitel-downloader <target directory>");
                System.exit(1);
                return;
            }
            File target = new File(args[0]);
            if (target.exists()) {
                System.err.println("Directory \"" + args[0] + "\" already exists!");
                System.exit(1);
                return;
            }
            target.mkdirs();
            System.err.println("Downloading DVB-T2 transmitters' data from https://emitel.pl");
            Map<String, Map<String, Integer>> table = EmitelAPI.downloadTransmittersTable();

            System.err.println("Converting to Kaffeine DVB format...");
            String dvbTable = DVBConverter.convertToDVBTable(table);

            System.err.println("Converting to VLC playlists format...");
            Map<String, Map<String, Document>> vlcPlaylists = VLCConverter.convertToVLCPlaylists(table);

            System.err.println("Converting to Markdown...");
            String markdownTables = MarkdownConverter.convertToMarkdownTables(table);

            File dvbFile = new File(target, "Kaffeine/scanfile.dvb");
            File vlcDir = new File(target, "VLC");
            File markdownFile = new File(target, "table.md");

            System.err.println("Saving Kaffeine's file to " + dvbFile);
            dvbFile.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(dvbFile)) {
                writer.write(dvbTable);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            System.err.println("Saving VLC playlists...");
            vlcPlaylists.entrySet().forEach(entry -> {
                File locationDir = new File(target, "VLC/" + entry.getKey());
                locationDir.mkdirs();
                entry.getValue().entrySet().forEach(muxEntry -> {
                    File muxFile = new File(locationDir, muxEntry.getKey() + ".xspf");
                    System.err.println("Saving to " + muxFile);
                    try (OutputStream os = new FileOutputStream(muxFile)) {
                        transformer.transform(new DOMSource(muxEntry.getValue()), new StreamResult(os));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });

            System.err.println("Saving Markdown table to " + markdownFile);
            try (PrintWriter writer = new PrintWriter(markdownFile)) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd MMM YYYY", Locale.ENGLISH);
                fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                writer.println("# Emitel transmitters list  ");
                writer.println("*Last updated: " + fmt.format(new Date()) + "*  \n");
                writer.write(markdownTables);
            }

        } catch (IOException | TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
