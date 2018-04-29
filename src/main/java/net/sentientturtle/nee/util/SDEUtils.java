package net.sentientturtle.nee.util;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

/**
 * Utility class to automatically download and update the EVE Online Static Data Export SQLite conversion from Fuzzwork.co.uk
 */
public class SDEUtils {
    private static final long downloadRate = 1024*1024*100;  // 100 Megs
    private static final String SDE_URL = "https://www.fuzzwork.co.uk/dump/sqlite-latest.sqlite.bz2";
    private static final String MD5_URL = "https://www.fuzzwork.co.uk/dump/sqlite-latest.sqlite.bz2.md5";

    public static void updateSDE(File file) throws IOException {
        String currentHash = SDEUtils.downloadMD5().toUpperCase();
        boolean download;
        if (file.exists()) {
            try {
                SQLiteConnection sqLiteConnection = new SQLiteConnection(file);
                sqLiteConnection.open();
                SQLiteStatement prepare = sqLiteConnection.prepare("SELECT * FROM metaData");
                download = !prepare.step() || !prepare.columnString(0).equals(currentHash);
                prepare.dispose();
                sqLiteConnection.dispose();
            } catch (SQLiteException e) {
                e.printStackTrace();
                download = true;
            }
            if (download) file.delete();
        } else {
            download = true;
        }
        if (download) {
            System.out.println("SDE Outdated!");
            downloadSDE(file);
        } else {
            System.out.println("SDE Up to date!");
        }
    }

    private static void downloadSDE(File file) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            CompressorStreamFactory compressorStreamFactory = new CompressorStreamFactory();
            try (
                    InputStream inputstream = new URL(SDE_URL).openStream();
                    DigestInputStream digestInputStream = new DigestInputStream(inputstream, md5);
                    CompressorInputStream compressorInputStream = compressorStreamFactory.createCompressorInputStream(CompressorStreamFactory.BZIP2, digestInputStream);
                    ReadableByteChannel rbc = Channels.newChannel(compressorInputStream);
                    FileOutputStream fos = new FileOutputStream(file);
                    FileChannel channel = fos.getChannel()
            ) {
                long total = 0;
                long count;
                while ((count = channel.transferFrom(rbc, total, downloadRate)) > 0) {
                    total += count;
                    System.out.println("Downloading... (" + total + ")");
                }
            } catch (CompressorException | IOException e) {
                e.printStackTrace();
            }
            SQLiteConnection sqliteConnection = new SQLiteConnection(file);
            sqliteConnection.open()
                    .exec("CREATE TABLE metaData (MD5 TEXT)")
                    .prepare("INSERT INTO metaData VALUES (?)")
                    .bind(1, Hex.toHex(md5.digest()))
                    .stepThrough()
                    .dispose();
            sqliteConnection.dispose();
            System.out.println("SDE Updated!");
        } catch (NoSuchAlgorithmException | SQLiteException e) {
            // There's nothing to be done, so throwing a checked exception is moot.
            throw new RuntimeException(e);
        }
    }

    private static String downloadMD5() throws IOException {
        URLConnection conn = new URL(MD5_URL).openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.US_ASCII))) {
            return reader.lines().collect(Collectors.joining("\n")).split(" {2}")[0];
        }
    }
}
