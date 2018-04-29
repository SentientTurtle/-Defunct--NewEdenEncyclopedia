package net.sentientturtle.nee;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.data.SQLiteDataSupplier;
import net.sentientturtle.nee.orm.SolarSystem;
import net.sentientturtle.nee.pages.MapPage;
import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.nee.pages.TypePage;
import net.sentientturtle.nee.util.SDEUtils;
import net.sentientturtle.nee.util.Tuple2;
import net.sentientturtle.nee.util.Tuple3;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class Main {
    /**
     * Boolean flag to switch between normal output mode, and zip output mode, which saves pages in a zip archive, increasing performance.
     */
    private static final boolean USE_ZIP = false;

    /*
     * Various config magic-variables, there should be no reason to change these.
     */
    private static final File sdeFile = new File("rsc/sqlite-latest.sqlite");
    public static final String converterExecutable = "rsc/grannytemp/evegr2toobj/evegr2toobj.exe";
    public static final String modelSourceFolder = "rsc/grannytemp/dx9";
    public static final String modelOutputFolder = "rsc/grannytemp/out";
    public static final String grannySqlite = "rsc/GRANNY-OUT.sqlite";
    private static final String searchIndex = "rsc/searchindex.js";

    public static void main(String[] args) throws SQLiteException, IOException {
        long startTime = System.nanoTime();
        System.setProperty("sqlite4java.library.path", "native");
        sanityCheck();
        DataSupplier dataSupplier = new SQLiteDataSupplier(new SQLiteConnection(sdeFile), grannySqlite);
        ZipOutputStream zipOutputStream;
        if (USE_ZIP) {
            /*
             * Most operating systems do not appreciate having to create thousands of files, saving to an archive is significantly faster.
             */
            zipOutputStream = new ZipOutputStream(new FileOutputStream(new File("pages/html/pageArchive.zip")));
            //zipOutputStream.setLevel(ZipOutputStream.STORED); TODO: Benchmark
        } else {
            zipOutputStream = null;
            for (PageType pageType : PageType.values()) {
                new File("pages/html/" + pageType + "/").mkdir();
            }
        }

        HashSet<String> folderSet = new HashSet<>();

        JSONObject index = new JSONObject();

        final int[] i = {0};
        PageType.pageStream(dataSupplier)
                .peek(page -> index.put(page.getPageName(), page.getPageType() + "/" + page.getPageName() + ".html"))
                .filter(page -> page.getPageName().equals("Badger"))
                .forEach(page -> {
                    String pageName = page.getPageName();
                    page.getHTML();
                    if (USE_ZIP && zipOutputStream != null) {
                        try {
                            zipOutputStream.putNextEntry(new ZipEntry(page.getPageType() + "/" + pageName + ".html"));
                            zipOutputStream.write(page.getHTML().getBytes(StandardCharsets.UTF_8));
                            zipOutputStream.closeEntry();
                            i[0]++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if (folderSet.add("pages/html/" + page.getPageType() + "/")) {
                                new File("pages/html/" + page.getPageType() + "/").mkdirs();
                            }
                            page.getHTML();
                            FileOutputStream fileOutputStream = new FileOutputStream("pages/html/" + page.getPageType() + "/" + pageName + ".html");
                            IOUtils.write(page.getHTML(), fileOutputStream, StandardCharsets.UTF_8);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            i[0]++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        FileWriter fileWriter = new FileWriter(searchIndex);
        fileWriter.write("searchindex = " + index.toString(4));
        fileWriter.flush();
        fileWriter.close();

        if (USE_ZIP) {
            zipOutputStream.close();
        }
        long endTime = System.nanoTime();
        System.out.println("Took: " + TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS));
        System.out.println("Generated: " + i[0] + " pages.");
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Using: " + ((runtime.totalMemory() - runtime.freeMemory()) / (1000 * 1000)) + " out of " + runtime.totalMemory() / (1000 * 1000) + " MB of memory");
    }

    private static void sanityCheck() throws IOException {
        if (!new File("rsc/itemicons").exists()) {
            throw new IllegalStateException("Please download the EVE Online Image Export Collection (Icons) from https://developers.eveonline.com/resource/resources and place the 'itemicons' folder in the project's /rsc folder");
        }
        SDEUtils.updateSDE(sdeFile);
    }
}
