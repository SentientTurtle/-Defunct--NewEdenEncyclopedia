package net.sentientturtle.nee.util;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import net.sentientturtle.util.tuple.Tuple2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.function.Function;

/**
 * Utility class to render {@link Mappable} maps/charts, which are returned as byte arrays, containing the image in PNG format
 */
public class MapRenderer {
    private static final double REGION_SCALE = 5;
    private static final double CONSTELLATION_SCALE = 15;
    private static final int TARGET_WIDTH = 512;
    private static final int TARGET_HEIGHT = 512;
    private static final double MARGIN = 0.05;
    private static final int STAR_SIZE = 32;


    @SuppressWarnings("WeakerAccess")
    public static byte[] render(Mappable mappable, DataSupplier dataSupplier) {
        HashMap<Integer, Mappable> points = mappable.getMapPoints(dataSupplier)
                .map((Function<Mappable, Tuple2<Integer, Mappable>>) point -> new Tuple2<>(point.getID(), point))
                .collect(Tuple2.collectToMap());

        double miniX = Double.MAX_VALUE;
        double maxiX = Double.MAX_VALUE * -1;
        double miniZ = Double.MAX_VALUE;
        double maxiZ = Double.MAX_VALUE * -1;

        for (Mappable point : points.values()) {
            miniX = point.getX() < miniX ? point.getX() : miniX;
            miniZ = point.getZ() < miniZ ? point.getZ() : miniZ;
            maxiX = point.getX() > maxiX ? point.getX() : maxiX;
            maxiZ = point.getZ() > maxiZ ? point.getZ() : maxiZ;
        }

        final double minX = miniX;
        final double maxX = maxiX;
        final double minZ = miniZ;
        final double maxZ = maxiZ;

        final int renderHeight;
        final int renderWidth;
        if (points.size() <= 20) {
            renderWidth = (int) (10_000 / CONSTELLATION_SCALE);
            renderHeight = (int) (10_000 / CONSTELLATION_SCALE);
        } else if (points.size() <= 200) {
            renderWidth = (int) (10_000 / REGION_SCALE);
            renderHeight = (int) (10_000 / REGION_SCALE);
        } else {
            renderHeight = 10_000;
            renderWidth = 10_000;
        }

        double scaleX = renderWidth / (maxX - minX);
        double scaleZ = renderHeight / (maxZ - minZ);

        BufferedImage image = new BufferedImage((int) (renderWidth * (2 * MARGIN + 1)), (int) (renderHeight * (2 * MARGIN + 1)), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setStroke(new BasicStroke(5));
        graphics.setColor(new Color(155, 155, 155));

        mappable.getMapLines(dataSupplier).forEach(jump -> {
            Mappable from = points.get(jump.v1);
            Mappable to = points.get(jump.v2);
            graphics.drawLine(
                    (int) ((MARGIN * renderWidth) + (from.getX() - minX) * scaleX),
                    (int) ((MARGIN * renderHeight) + (from.getZ() - minZ) * scaleZ),
                    (int) ((MARGIN * renderWidth) + (to.getX() - minX) * scaleX),
                    (int) ((MARGIN * renderHeight) + (to.getZ() - minZ) * scaleZ)
            );
        });

        for (Mappable point : points.values()) {
            OptionalDouble security = point.getSecurity(dataSupplier);
            if (security.isPresent()) {
                graphics.setColor(calcColor(security.getAsDouble()));
            } else {
                graphics.setColor(new Color(255, 255, 255));
            }
            graphics.fillOval(
                    (int) ((MARGIN * renderWidth) + ((point.getX() - minX) * scaleX) - (STAR_SIZE / 2)),
                    (int) ((MARGIN * renderHeight) + ((point.getZ() - minZ) * scaleZ) - (STAR_SIZE / 2)),
                    STAR_SIZE,
                    STAR_SIZE
            );
        }

        graphics.dispose();

        BufferedImage scaledImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, image.getType());
        Graphics2D scaledImageGraphics = scaledImage.createGraphics();
        scaledImageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        scaledImageGraphics.drawImage(image, 0, TARGET_HEIGHT, TARGET_WIDTH, 0, 0, 0, image.getWidth(), image.getHeight(), null);   // Scale and flip image
        scaledImageGraphics.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(scaledImage, "PNG", byteArrayOutputStream);
        } catch (IOException e) {
            System.err.println("ByteArrayOutputStream shouldn't throw IO Errors!");
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }


    public static final String[] SECURITY_COLORS = {
            "#F00000",
            "#D73000",
            "#F04800",
            "#F06000",
            "#D77700",
            "#EFEF00",
            "#8FEF2F",
            "#00F000",
            "#00EF47",
            "#48F0C0",
            "#2FEFEF"
    };

    private static Color calcColor(Double temperature) {
        double security;
        if (temperature == null) {
            security = 0;
        } else {
            security = Math.max(temperature, 0);
        }
        security = Math.round(security*10);
        return Color.decode(SECURITY_COLORS[(int) security]);
    }
}
