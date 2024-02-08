package xfacthd.framedblocks.codegen.impl.skippreds;

import java.util.List;
import java.util.Map;

public final class SkipPredicateGeneratorData
{
    static final Map<String, Type> KNOWN_TYPES = Map.ofEntries(
            entry("FRAMED_SLOPE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
            )),
            entry("FRAMED_HALF_SLOPE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE)
            )),
            entry("FRAMED_VERTICAL_HALF_SLOPE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_CORNER_SLOPE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
            )),
            entry("FRAMED_INNER_CORNER_SLOPE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
            )),
            entry("FRAMED_PRISM_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_INNER_PRISM_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_THREEWAY_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_INNER_THREEWAY_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SLOPE_EDGE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM),
                    Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
            )),
            entry("FRAMED_ELEVATED_SLOPE_EDGE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
            )),
            entry("FRAMED_SLAB", List.of(
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SLAB_EDGE", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SLAB_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            )),
            entry("FRAMED_CORNER_PILLAR", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            )),
            entry("FRAMED_STAIRS", List.of(
                    Property.vanilla("Direction", "dir", "HORIZONTAL_FACING", PropType.PRIMITIVE),
                    Property.vanilla("StairsShape", "shape", "STAIRS_SHAPE", PropType.VANILLA),
                    Property.vanilla("Half", "half", "HALF", PropType.VANILLA)
            )),
            entry("FRAMED_HALF_STAIRS", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SLOPED_STAIRS", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_VERTICAL_STAIRS", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("StairsType", "type", "STAIRS_TYPE", PropType.CUSTOM)
            )),
            entry("FRAMED_VERTICAL_HALF_STAIRS", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_VERTICAL_SLOPED_STAIRS", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_THREEWAY_CORNER_PILLAR", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_PRISM", List.of(
                    Property.internal("DirectionAxis", "dirAxis", "FACING_AXIS", PropType.CUSTOM)
            )),
            entry("FRAMED_INNER_PRISM", List.of(
                    Property.internal("DirectionAxis", "dirAxis", "FACING_AXIS", PropType.CUSTOM)
            )),
            entry("FRAMED_SLOPED_PRISM", List.of(
                    Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM)
            )),
            entry("FRAMED_INNER_SLOPED_PRISM", List.of(
                    Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM)
            )),
            entry("FRAMED_SLOPE_SLAB", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
            )),
            entry("FRAMED_ELEVATED_SLOPE_SLAB", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_COMPOUND_SLOPE_SLAB", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            )),
            entry("FRAMED_FLAT_SLOPE_SLAB_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
            )),
            entry("FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
            )),
            entry("FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                    Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
            )),
            entry("FRAMED_EXTENDED_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_COMPOUND_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_FLAT_SLOPE_PANEL_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                    Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
            )),
            entry("FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                    Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
            )),
            entry("FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_SMALL_CORNER_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SMALL_CORNER_SLOPE_PANEL_W", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_LARGE_CORNER_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_LARGE_CORNER_SLOPE_PANEL_W", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_EXT_CORNER_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_EXT_CORNER_SLOPE_PANEL_W", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_EXT_INNER_CORNER_SLOPE_PANEL", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            )),
            entry("FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            )),
            entry("FRAMED_MASONRY_CORNER_SEGMENT", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            )),
            entry("FRAMED_CHECKERED_CUBE_SEGMENT", List.of(
                    Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
            )),
            entry("FRAMED_CHECKERED_SLAB_SEGMENT", List.of(
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
            )),
            entry("FRAMED_CHECKERED_PANEL_SEGMENT", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
            ))
    );

    private static Map.Entry<String, Type> entry(String type, List<Property> properties)
    {
        return Map.entry(type, new Type(type, properties));
    }



    record Type(String type, List<Property> properties) { }

    record Property(String typeName, String name, String propHolder, String propName, PropType type)
    {
        static Property vanilla(String typeName, String name, String propName, PropType type)
        {
            return new Property(typeName, name, "BlockStateProperties", propName, type);
        }

        static Property api(String typeName, String name, String propName, PropType type)
        {
            return new Property(typeName, name, "FramedProperties", propName, type);
        }

        static Property internal(String typeName, String name, String propName, PropType type)
        {
            return new Property(typeName, name, "PropertyHolder", propName, type);
        }
    }

    enum PropType
    {
        PRIMITIVE,
        VANILLA,
        CUSTOM
    }



    private SkipPredicateGeneratorData() { }
}
