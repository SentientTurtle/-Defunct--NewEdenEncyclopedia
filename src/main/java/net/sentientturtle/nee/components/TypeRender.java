package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;

/**
 * Displays a render of a {@link Type}, usually a Ship, Drone, or Structure
 * Also used to display maps
 */
public class TypeRender extends Component {
    private final ResourceLocation resourceLocation;

    public TypeRender(Type type, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        resourceLocation = new ResourceLocation(String.valueOf(type.typeID), ResourceLocation.Type.TYPE_RENDER_512, dataSupplier, page);
    }

    public TypeRender(ResourceLocation resourceLocation, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.resourceLocation = resourceLocation;
    }

    @Override
    public String buildHTML() {
        return "<div class='component type_render'>" +
                "<img src='" + resourceLocation + "'>" +
                "</div>";
    }

    @Override
    public String buildCSS() {
        return ".type_render img {\n" +
                "  display: block;\n" +
                "  margin: 0 auto;\n" +
                "  max-width: 100%;\n" +
                "}";
    }
}
