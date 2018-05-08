package net.sentientturtle.nee;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.data.SQLiteDataSupplier;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.nee.util.ResourceSupplier;
import net.sentientturtle.nee.util.SDEUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("WeakerAccess")
public class Main {
    /**
     * Boolean flag to switch between normal output mode, and zip output mode, which saves pages in a zip archive, increasing performance.
     */
    private static final boolean USE_ZIP = false;

    /*
     * Various config magic-variables, there should be no reason to change these.
     */
    private static final File sdeFile = new File("rsc/sqlite-latest.sqlite");
    public static final String CONVERTER_EXECUTABLE = "rsc/grannytemp/evegr2toobj/evegr2toobj.exe";
    public static final String MODEL_SOURCE_FOLDER = "rsc/grannytemp/dx9";
    public static final String MODEL_OUTPUT_FOLDER = "rsc/grannytemp/out";
    public static final String GRANNY_SQLITE = "rsc/GRANNY-OUT.sqlite";
    public static final String OUTPUT_DIR = "pages/";

    public static void main(String[] args) throws SQLiteException, IOException {
        long startTime = System.nanoTime();
        System.setProperty("sqlite4java.library.path", "native");
        sanityCheck();
        FileUtils.cleanDirectory(new File(OUTPUT_DIR));

        DataSupplier dataSupplier = new SQLiteDataSupplier(new SQLiteConnection(sdeFile), GRANNY_SQLITE);
        ZipOutputStream zipOutputStream;
        if (USE_ZIP) {
            /*
             * Most operating systems do not appreciate having to create thousands of files, saving to an archive is significantly faster.
             */
            zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(OUTPUT_DIR + "pageArchive.zip")));
            zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
        } else {
            zipOutputStream = null;
            for (PageType pageType : PageType.values()) {
                //noinspection ResultOfMethodCallIgnored
                new File(OUTPUT_DIR + "html/" + pageType + "/").mkdir();
            }
        }


        Set<String> folderSet = ConcurrentHashMap.newKeySet();
        Set<String> resourceFileSet = ConcurrentHashMap.newKeySet();
        final int[] i = {0};
        PageType.pageStream(dataSupplier)
//                .parallel()
//                .filter(page -> page.getPageType() == PageType.STATIC || page.getPageName().equals("Badger")) // For easy testing
                .forEach(page -> {
                    String pageName = page.getPageName();
//                    System.out.println("pageName = " + pageName); // Debug print to see if page generation halted
                    page.getHTML(); // Side effect: Generate HTML outside synchronized block.
                    if (USE_ZIP && zipOutputStream != null) {
                            try {
                                for (Map.Entry<String, ResourceSupplier> entry : page.getFileDependencies().entrySet()) {
                                    if (resourceFileSet.add(entry.getKey())) {
                                        synchronized (zipOutputStream) {
                                            zipOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
                                            zipOutputStream.write(entry.getValue().get());
                                            zipOutputStream.closeEntry();
                                        }
                                    }
                                }
                                synchronized (zipOutputStream) {
                                    zipOutputStream.putNextEntry(new ZipEntry(page.getPageType().getPageFilePath(pageName)));
                                    zipOutputStream.write(page.getHTML().getBytes(StandardCharsets.UTF_8));
                                    zipOutputStream.closeEntry();
                                    i[0]++;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    } else {
                        try {
                            for (Map.Entry<String, ResourceSupplier> entry : page.getFileDependencies().entrySet()) {
                                if (resourceFileSet.add(entry.getKey())) {
                                    //noinspection ResultOfMethodCallIgnored
                                    new File(OUTPUT_DIR + entry.getKey()).getParentFile().mkdirs();
                                    FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_DIR + entry.getKey());
                                    IOUtils.write(entry.getValue().get(), fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                }
                            }
                            if (folderSet.add(new File(OUTPUT_DIR + page.getPageType().getPageFilePath(pageName)).getParent())) {
                                //noinspection ResultOfMethodCallIgnored
                                new File(OUTPUT_DIR + page.getPageType().getPageFilePath(pageName)).getParentFile().mkdirs();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_DIR + page.getPageType().getPageFilePath(pageName));
                            IOUtils.write(page.getHTML(), fileOutputStream, StandardCharsets.UTF_8);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            i[0]++;
                        } catch (IOException e) {
                            System.err.println("Exception in page: " + page.toString());
                            e.printStackTrace();
                        }
                    }
                });

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
