package net.sentientturtle.nee.util;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;

/**
 * Object for resource locations, used to handle pre-emptive caching of resources like images, and allow switching to different reference formats
 */
public class ResourceLocation {
    public static Format format = Format.EXTERNAL;
    public static String pageResFolder = "../../rsc/";  // Destination res folder relative to page
    public static String htmlResFolder = "pages/rsc/";  // Destination res folder relative to working dir
    public static String resFolder = "rsc/";            // Origin res folder relative to working dir
    public final Object value;
    public final Type type;
    private final DataSupplier dataSupplier;

    public ResourceLocation(Object value, Type type, DataSupplier dataSupplier) {
        this.value = value;
        this.type = type;
        this.dataSupplier = dataSupplier;
        if (!type.valueClass.isInstance(value)) {
            throw new IllegalArgumentException("Invalid value type (" + value + ") must be of type: " + type.valueClass);
        }
    }

    public static ResourceLocation iconOfIconID(int iconID, DataSupplier dataSupplier) {
        return new ResourceLocation(FilenameUtils.getName(dataSupplier.getEveIcons().get(iconID)), Type.ITEM_ICON, dataSupplier);
    }

    public static ResourceLocation iconOfTypeID(int typeID, DataSupplier dataSupplier) {
        return new ResourceLocation(String.valueOf(typeID), Type.TYPE_ICON_64, dataSupplier);
    }

    public static ResourceLocation iconOfCorpID(int corporationID, DataSupplier dataSupplier) {
        return new ResourceLocation(String.valueOf(corporationID), Type.CORP_ICON, dataSupplier);
    }

    public Tuple2<Object, OriginType> getOrigin() {
        switch (type) {
            case FILE:
                return new Tuple2<>(resFolder + value, OriginType.FILE);
            case TYPE_ICON_64:
                return new Tuple2<>("https://imageserver.eveonline.com/Type/" + value + "_64.png", OriginType.REMOTE);
            case TYPE_RENDER_512:
                return new Tuple2<>("https://imageserver.eveonline.com/Render/" + value + "_512.png", OriginType.REMOTE);
            case ITEM_ICON:
                return new Tuple2<>(resFolder + "itemicons/" + value, OriginType.FILE);
            case CORP_ICON:
                return new Tuple2<>("https://imageserver.eveonline.com/Corporation/" + value + "_64.png", OriginType.REMOTE);
            case MAP:
                return new Tuple2<>(value, OriginType.MAP_RENDER);
        }
        throw new RuntimeException("UNKNOWN RESOURCE TYPE: " + type);
    }

    public String toString() {
        switch (format) {
            case EXTERNAL:
                switch (type) {
                    case TYPE_ICON_64:
                        return "https://imageserver.eveonline.com/Type/" + value + "_64.png";
                    case TYPE_RENDER_512:
                        return "https://imageserver.eveonline.com/Render/" + value + "_512.png";
                    case CORP_ICON:
                        return "https://imageserver.eveonline.com/Corporation/" + value + "_64.png";
                    case FILE:
                    case ITEM_ICON:
                    case MAP:
                        // Fallthrough to INTERNAL
                }
            case INTERNAL:
                String dest;
                switch (type) {
                    case FILE:
                        dest = (String) value;
                        break;
                    case TYPE_ICON_64:
                        dest = "typeicons/" + value + "_64.png";
                        break;
                    case TYPE_RENDER_512:
                        dest = "typerenders/" + value + "_512.png";
                        break;
                    case CORP_ICON:
                        dest = "corpicons/" + value + "_64.png";
                        break;
                    case ITEM_ICON:
                        dest = "itemicons/" + value;
                        break;
                    case MAP:
                        dest = "maps/" + ((Mappable) value).getName() + ".png";
                        break;
                    default:
                        throw new RuntimeException("UNKNOWN RESOURCE TYPE: " + type);
                }
                ensureCopy(new File(htmlResFolder + dest));
                return pageResFolder + dest;
            case DATA:
                Tuple2<Object, OriginType> origin = getOrigin();
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("data:")
                            .append(MIME.getType(((String) origin.v1).substring(((String) origin.v1).lastIndexOf('.'))))
                            .append(";base64,");
                    byte[] encode = Base64.getEncoder().encode(origin.v2.getData(origin.v1, dataSupplier));
                    builder.append(new String(encode));
                    return builder.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        throw new RuntimeException("UNKNOWN RESOURCE FORMAT: " + format);
    }

    private void ensureCopy(File dest) {
        if (!dest.exists()) {
            dest.getAbsoluteFile().getParentFile().mkdirs();
            Tuple2<Object, OriginType> origin = getOrigin();
            try {
                System.out.println("Writing: " + dest.getAbsoluteFile());
                IOUtils.write(origin.v2.getData(origin.v1, dataSupplier), new FileOutputStream(dest));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public enum Type {
        FILE(String.class),
        TYPE_ICON_64(String.class),
        TYPE_RENDER_512(String.class),
        ITEM_ICON(String.class),    // Not to be confused with type icons
        CORP_ICON(String.class),
        MAP(Mappable.class);

        public final Class valueClass;

        Type(Class valueClass) {
            this.valueClass = valueClass;
        }
    }

    public enum Format {
        EXTERNAL, INTERNAL, DATA
    }

    public enum OriginType {
        FILE {
            @Override
            public byte[] getData(Object path, DataSupplier dataSupplier) throws IOException {
                assert path instanceof String;
                return Files.readAllBytes(new File((String) path).toPath());
            }
        }, REMOTE {
            @Override
            public byte[] getData(Object value, DataSupplier dataSupplier) throws IOException {
                assert value instanceof String;
                try {
                    return remoteCache.computeIfAbsent((String) value, s -> {
                        try {
                            return IOUtils.toByteArray(new URL(s));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (RuntimeException e) {
                    throw (IOException) e.getCause();
                }
            }
        }, MAP_RENDER {
            @Override
            public byte[] getData(Object value, DataSupplier dataSupplier) {
                assert value instanceof Mappable;
                return MapRenderer.render((Mappable) value, dataSupplier);
            }
        };
        private static HashMap<String, byte[]> remoteCache = new HashMap<>();

        public abstract byte[] getData(Object value, DataSupplier dataSupplier) throws IOException;
    }
}
