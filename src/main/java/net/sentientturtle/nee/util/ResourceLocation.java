package net.sentientturtle.nee.util;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.util.tuple.Tuple2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;

/**
 * Object for resource locations, used to handle pre-emptive caching of resources like images, and allow switching to different reference formats
 */
@SuppressWarnings("WeakerAccess")
public class ResourceLocation {
    public static final Format FORMAT = Format.EXTERNAL;
    public static final String PAGE_RES_FOLDER = "rsc/";              // Destination res folder relative to page
    public static final String HTML_RES_FOLDER = "rsc/";              // Destination res folder relative to working dir
    public static final String RES_FOLDER = "rsc/";                  // Origin res folder relative to working dir
    public final Object value;
    public final Type type;
    private final DataSupplier dataSupplier;
    private final Page page;

    public ResourceLocation(Object value, Type type, DataSupplier dataSupplier, Page page) {
        this.value = value;
        this.type = type;
        this.dataSupplier = dataSupplier;
        this.page = page;
        if (!type.valueClass.isInstance(value)) {
            throw new IllegalArgumentException("Invalid value type (" + value + ") must be of type: " + type.valueClass);
        }
    }

    public static ResourceLocation iconOfIconID(int iconID, DataSupplier dataSupplier, Page page) {
        return new ResourceLocation(FilenameUtils.getName(dataSupplier.getEveIcons().get(iconID)), Type.ITEM_ICON, dataSupplier, page);
    }

    public static ResourceLocation iconOfTypeID(int typeID, DataSupplier dataSupplier, Page page) {
        return new ResourceLocation(String.valueOf(typeID), Type.TYPE_ICON_64, dataSupplier, page);
    }

    public static ResourceLocation iconOfCorpID(int corporationID, DataSupplier dataSupplier, Page page) {
        return new ResourceLocation(String.valueOf(corporationID), Type.CORP_ICON, dataSupplier, page);
    }

    public Tuple2<Object, OriginType> getOrigin() {
        switch (type) {
            case FILE:
                return new Tuple2<>(RES_FOLDER + value, OriginType.FILE);
            case TYPE_ICON_64:
                return new Tuple2<>("https://imageserver.eveonline.com/Type/" + value + "_64.png", OriginType.REMOTE);
            case TYPE_RENDER_512:
                return new Tuple2<>("https://imageserver.eveonline.com/Render/" + value + "_512.png", OriginType.REMOTE);
            case ITEM_ICON:
                return new Tuple2<>(RES_FOLDER + "itemicons/" + value, OriginType.FILE);
            case CORP_ICON:
                return new Tuple2<>("https://imageserver.eveonline.com/Corporation/" + value + "_64.png", OriginType.REMOTE);
            case MAP:
                return new Tuple2<>(value, OriginType.MAP_RENDER);
            case INDEX:
                return new Tuple2<>(value, OriginType.INDEX);
        }
        throw new RuntimeException("UNKNOWN RESOURCE TYPE: " + type);
    }

    public String toString() {
        Tuple2<Object, OriginType> origin;
        switch (FORMAT) {
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
                    case INDEX:
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
                    case INDEX:
                        dest = (String) value;
                        break;
                    default:
                        throw new RuntimeException("UNKNOWN RESOURCE TYPE: " + type);
                }
                origin = getOrigin();
                page.putFileDependency(HTML_RES_FOLDER + dest, () -> origin.v2.getData(origin.v1, dataSupplier));
                return PageReference.cancelDepth(page.getPageType().getFolderDepth()) + PAGE_RES_FOLDER + dest;
            case DATA:
                origin = getOrigin();
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
        throw new RuntimeException("UNKNOWN RESOURCE FORMAT: " + FORMAT);
    }

    public enum Type {
        FILE(String.class),
        TYPE_ICON_64(String.class),
        TYPE_RENDER_512(String.class),
        ITEM_ICON(String.class),    // Not to be confused with type icons
        CORP_ICON(String.class),
        MAP(Mappable.class),
        INDEX(String.class);

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
        }, INDEX {
            @Override
            public byte[] getData(Object value, DataSupplier dataSupplier) {
                assert value.equals("search_index.js");
                String json = "searchindex = " + PageType.pageStream(dataSupplier)
                        .map(page -> new JSONObject()
                                .put("name", page.getPageName())
                                .put("path", new PageReference(page, 0).toString())
                                .put("icon", page.getIcon() != null ? page.getIcon().toString() : null)
                        )
                        .collect(JSONArray::new, JSONArray::put, (array1, array2) -> {
                            for (Object value1 : array2) {
                                array1.put(value1);
                            }
                        }).toString();
                return json.getBytes(StandardCharsets.UTF_8);
            }
        };
        private static HashMap<String, byte[]> remoteCache = new HashMap<>();

        public abstract byte[] getData(Object value, DataSupplier dataSupplier) throws IOException;
    }
}
