package net.sentientturtle.nee.util;

import com.almworks.sqlite4java.*;
import net.sentientturtle.nee.Main;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Parses EVE Online Granny3D models in order to determine ship sizes
 * This class expects the "evegr2toobj" executable to be located in rsc/grannytemp/evegr2toobj
 *
 * WARNING: Generates multiple gigabytes of intermediate files
 */
public class GrannyParser {
    public static void main(String[] args) throws IOException {
        System.setProperty("sqlite4java.library.path", "native");
        SQLiteQueue sqLiteQueue = new SQLiteQueue(new File("rsc/sqlite-latest.sqlite")).start();
        getModels(sqLiteQueue, Main.CONVERTER_EXECUTABLE, new File(Main.MODEL_SOURCE_FOLDER), new File(Main.MODEL_OUTPUT_FOLDER));
        sqLiteQueue.stop(true);
    }

    private static void getModels(SQLiteQueue SDE, String converterExe, File cacheDir, File outDir) throws IOException {
        FileUtils.cleanDirectory(outDir);

        HashMap<Integer, String> eveGraphics = SDE.execute(new SQLiteJob<HashMap<Integer, String>>() {
            @Override
            protected HashMap<Integer, String> job(SQLiteConnection connection) throws Throwable {
                HashMap<Integer, String> out = new HashMap<>();
                SQLiteStatement st = connection.prepare("SELECT\n" +
                        "  typeID,\n" +
                        "  sofHullName\n" +
                        "FROM eveGraphics\n" +
                        "  JOIN invTypes ON invTypes.graphicID = eveGraphics.graphicID\n" +
                        "WHERE sofHullName NOT NULL AND sofHullName != ''");
                while (st.step()) {
                    out.put(st.columnInt(0), st.columnString(1));
                }

                HashMap<Integer, Integer> metaTypeMap = new HashMap<>();
                HashSet<Integer> parentTypes = new HashSet<>();
                st = connection.prepare("SELECT invTypes.typeID, invMetaTypes.parentTypeID FROM invTypes\n" +
                        "  JOIN invMetaTypes ON invMetaTypes.typeID = invTypes.typeID\n" +
                        "  JOIN invGroups ON invTypes.groupID = invGroups.groupID\n" +
                        "WHERE invGroups.categoryID = 7;");
                while (st.step()) {
                    metaTypeMap.put(st.columnInt(0), st.columnInt(1));
                    parentTypes.add(st.columnInt(1));
                }

                parentTypes.forEach(integer -> {
                    try {
                        SQLiteStatement st1 = connection.prepare("SELECT eveGraphics.graphicFile FROM eveGraphics JOIN invTypes ON eveGraphics.graphicID = invTypes.graphicID WHERE invTypes.typeID = ?");
                        st1.bind(1, integer);
                        if (st1.step()) {
                            out.put(integer, FilenameUtils.getBaseName(st1.columnString(0)).toLowerCase());
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                });

                metaTypeMap.forEach((type, parent) -> {
                    if (out.get(parent) != null) {
                        out.put(type, out.get(parent));
                    }
                });

                return out;
            }
        }).complete();

        HashSet<String> graphicFiles = new HashSet<>(eveGraphics.values());
        ArrayList<String> replacementGraphics = new ArrayList<>();

        graphicFiles.forEach(s -> replacementGraphics.add(s.replaceAll("t2[a|b]?", "t1")));
        graphicFiles.addAll(replacementGraphics);

        parseDir(converterExe, cacheDir, outDir, graphicFiles);

        List<String> missedFiles = new ArrayList<>();
        HashMap<String, Vector3> boundingBoxes = new HashMap<>();

        graphicFiles.forEach(file -> {
            try {
                boundingBoxes.put(file, getObjBoundingBox(new File(outDir, file + ".obj").getAbsolutePath()));
            } catch (FileNotFoundException e) {
                System.out.println("Missed: " + file);
                missedFiles.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        HashMap<Integer, String> replace = new HashMap<>();

        missedFiles.forEach(missed -> eveGraphics.forEach((integer, s) -> {
            if (s.equals(missed)) {
                replace.put(integer, missed.replaceAll("t2[a|b]?", "t1"));
            }
        }));

        eveGraphics.putAll(replace);

        SQLiteQueue outDB = new SQLiteQueue().start();
        outDB.execute(new SQLiteJob<Void>() {
            @Override
            protected Void job(SQLiteConnection connection) {
                try {
                    connection.exec("CREATE TABLE IF NOT EXISTS `invSizes` (\n" +
                            "  `typeID`\tINTEGER NOT NULL,\n" +
                            "  `sizeX`\tINTEGER NOT NULL,\n" +
                            "  `sizeY`\tINTEGER NOT NULL,\n" +
                            "  `sizeZ`\tINTEGER NOT NULL,\n" +
                            "  PRIMARY KEY(typeID)\n" +
                            ");");
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
                int size = eveGraphics.size();
                final int[] count = {0};
                eveGraphics.forEach((integer, s) -> {
                    try {
                        count[0]++;
                        Vector3 vector3 = boundingBoxes.get(s);
                        if (vector3 != null) {
                            SQLiteStatement st = connection.prepare("INSERT OR REPLACE INTO invSizes(typeID, sizeX, sizeY, sizeZ) VALUES (?, ?, ?, ?)");
                            st.bind(1, integer);
                            st.bind(2, vector3.x);
                            st.bind(3, vector3.y);
                            st.bind(4, vector3.z);
                            st.step();
                        } else {
                            System.out.println("No model!? " + integer + " " + s);
                        }
                        System.out.println(count[0] + "/" + size);
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                });
                return null;
            }
        }).complete();

        FileUtils.deleteQuietly(new File(Main.GRANNY_SQLITE));
        outDB.execute(new SQLiteJob<Void>() {
            @Override
            protected Void job(SQLiteConnection connection) throws Throwable {
                SQLiteBackup backup = connection.initializeBackup(new File(Main.GRANNY_SQLITE));
                try {
                    while (!backup.isFinished()) {
                        backup.backupStep(32);
                    }
                } finally {
                    backup.dispose();
                }
                return null;
            }
        }).complete();
        outDB.stop(true);
    }

    private static void parseDir(String converterExe, File curDir, File outDir, HashSet<String> graphicFiles) throws IOException {
        if (curDir.isDirectory()) {
            try {
                //IO errors in listFiles() will return null, this should not happen and is dealt with as an exception
                //noinspection ConstantConditions
                for (File file : curDir.listFiles()) {
                    if (file.isDirectory()) {
                        parseDir(converterExe, file, outDir, graphicFiles);
                    } else {
                        if (file.getName().endsWith(".gr2") && graphicFiles.contains(FilenameUtils.removeExtension(file.getName()))) {
                            Process exec = Runtime.getRuntime().exec(new String[]{converterExe, file.getAbsolutePath(), new File(outDir, FilenameUtils.removeExtension(file.getName()) + ".obj").getAbsolutePath()});
                            exec.waitFor();
                        }
                    }
                }
            } catch (NullPointerException e) {
                throw new IOException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static Vector3 getObjBoundingBox(String objFile) throws IOException {
        LineIterator lineIterator = FileUtils.lineIterator(new File(objFile), "UTF-8");

        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        double zMin = Double.MAX_VALUE;
        double zMax = Double.MIN_VALUE;

        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if (line.startsWith("v ")) {
                String[] split = line.split(" ");
                double vx = Double.parseDouble(split[1]);
                double vy = Double.parseDouble(split[2]);
                double vz = Double.parseDouble(split[3]);
                    if (vx < xMin) xMin = vx;
                    if (vy < yMin) yMin = vy;
                    if (vz < zMin) zMin = vz;

                    if (vx > xMax) xMax = vx;
                    if (vy > yMax) yMax = vy;
                    if (vz > zMax) zMax = vz;
            } else {
                break;
            }
        }
        return new Vector3(xMax-xMin, yMax-yMin, zMax-zMin);
    }


    @SuppressWarnings("WeakerAccess")
    private final static class Vector3 {
        public final double x;
        public final double y;
        public final double z;

        private Vector3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public String toString() {
            return "[" + x + ", " + y + ", " + z + "]";
        }
    }
}
