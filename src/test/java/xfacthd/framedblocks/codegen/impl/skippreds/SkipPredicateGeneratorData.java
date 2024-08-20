package xfacthd.framedblocks.codegen.impl.skippreds;

import java.util.*;
import java.util.stream.Collectors;

public final class SkipPredicateGeneratorData
{
    static final Map<String, Type> KNOWN_TYPES = validate(Map.ofEntries(
            entry("FRAMED_SLOPE", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_HALF_SLOPE", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_VERTICAL_HALF_SLOPE", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor")
            )),
            entry("FRAMED_CORNER_SLOPE", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_INNER_CORNER_SLOPE", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_PRISM_CORNER", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_INNER_PRISM_CORNER", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_THREEWAY_CORNER", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_INNER_THREEWAY_CORNER", "slope", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz", "slope_tri_y")
            )),
            entry("FRAMED_SLOPE_EDGE", "slopeedge", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM),
                    Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("QuarterTriangleDir", "Tri", null, "slope_edge_tri"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor", "half_xz_vert", "half_y")
            )),
            entry("FRAMED_ELEVATED_SLOPE_EDGE", "slopeedge", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "elev_slope_edge_tri"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor", "half_xz_vert", "half_y")
            )),
            entry("FRAMED_CORNER_SLOPE_EDGE", "slopeedge", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM),
                    Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("QuarterTriangleDir", "Tri", null, "slope_edge_tri"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz", "corner_y")
            )),
            entry("FRAMED_INNER_CORNER_SLOPE_EDGE", "slopeedge", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM),
                    Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("QuarterTriangleDir", "Tri", null, "slope_edge_tri"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor", "half_xz_vert", "half_y"),
                    new TestDir("TriangleDir", "Stair", null, "stair_xz", "stair_y")
            )),
            entry("FRAMED_SLAB", "slab", List.of(
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfDir", "Half", null, "half_xz_hor")
            )),
            entry("FRAMED_SLAB_EDGE", "slab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfDir", "Half", null, "half_xz_hor"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz")
            )),
            entry("FRAMED_SLAB_CORNER", "slab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("CornerDir", "Corner", null, "corner_xz", "corner_y")
            )),
            entry("FRAMED_PANEL", "slab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_CORNER_PILLAR", "pillar", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfDir", "Half", null, "half_xz_vert"),
                    new TestDir("CornerDir", "Corner", null, "corner_y")
            )),
            entry("FRAMED_STAIRS", "stairs", List.of(
                    Property.vanilla("Direction", "dir", "HORIZONTAL_FACING", PropType.PRIMITIVE),
                    Property.vanilla("StairsShape", "shape", "STAIRS_SHAPE", PropType.VANILLA),
                    Property.vanilla("Half", "half", "HALF", PropType.VANILLA)
            ), List.of(
                    new TestDir("TriangleDir", "Stair", null, "stair_xz", "stair_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor", "half_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_y")
            )),
            entry("FRAMED_HALF_STAIRS", "stairs", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Stair", null, "stair_xz"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz", "corner_y")
            )),
            entry("FRAMED_SLOPED_STAIRS", "stairs", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor")
            )),
            entry("FRAMED_VERTICAL_STAIRS", "stairs", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("StairsType", "type", "STAIRS_TYPE", PropType.CUSTOM)
            ), List.of(
                    new TestDir("TriangleDir", "Stair", null, "stair_xz", "stair_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz", "corner_y")
            )),
            entry("FRAMED_VERTICAL_HALF_STAIRS", "stairs", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Stair", null, "stair_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz")
            )),
            entry("FRAMED_VERTICAL_SLOPED_STAIRS", "stairs", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("TriangleDir", "Tri", null, "slope_tri_xz"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_THREEWAY_CORNER_PILLAR", "pillar", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("TriangleDir", "Stair", null, "stair_xz", "stair_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz", "corner_y")
            )),
            entry("FRAMED_PRISM", "prism", List.of(
                    Property.internal("DirectionAxis", "dirAxis", "FACING_AXIS", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfDir", "Tri", null, "prism_tri_xz_hor", "prism_tri_xz_vert", "prism_tri_y")
            )),
            entry("FRAMED_ELEVATED_INNER_PRISM", "prism", List.of(
                    Property.internal("DirectionAxis", "dirAxis", "FACING_AXIS", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfDir", "Tri", null, "elev_inner_prism_tri_xz_hor", "elev_inner_prism_tri_xz_vert", "elev_inner_prism_tri_y")
            )),
            entry("FRAMED_SLOPED_PRISM", "prism", List.of(
                    Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfDir", "Tri", null, "prism_tri_xz_hor", "prism_tri_xz_vert", "prism_tri_y")
            )),
            entry("FRAMED_ELEVATED_INNER_SLOPED_PRISM", "prism", List.of(
                    Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfDir", "Tri", null, "elev_inner_prism_tri_xz_hor", "elev_inner_prism_tri_xz_vert", "elev_inner_prism_tri_y")
            )),
            entry("FRAMED_SLOPE_SLAB", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri"),
                    new TestDir("HalfDir", "Half", List.of("dir", "topHalf"), "half_xz_hor")
            )),
            entry("FRAMED_ELEVATED_SLOPE_SLAB", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "elev_slope_slab_tri"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor")
            )),
            entry("FRAMED_COMPOUND_SLOPE_SLAB", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "cmp_slope_slab_tri"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor")
            )),
            entry("FRAMED_FLAT_SLOPE_SLAB_CORNER", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri")
            )),
            entry("FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri"),
                    new TestDir("HalfDir", "Half", List.of("dir", "topHalf"), "half_xz_hor")
            )),
            entry("FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "elev_slope_slab_tri"),
                    new TestDir("HalfDir", "Half", null, "half_xz_hor")
            )),
            entry("FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER", "slopeslab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "elev_slope_slab_tri")
            )),
            entry("FRAMED_SLOPE_PANEL", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                    Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz", "slope_panel_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_EXTENDED_SLOPE_PANEL", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "ext_slope_panel_tri_xz", "ext_slope_panel_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_COMPOUND_SLOPE_PANEL", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "cmp_slope_panel_tri_xz", "cmp_slope_panel_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_FLAT_SLOPE_PANEL_CORNER", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                    Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz", "slope_panel_tri_y")
            )),
            entry("FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                    Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz", "slope_panel_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "ext_slope_panel_tri_xz", "ext_slope_panel_tri_y"),
                    new TestDir("HalfDir", "Half", null, "half_xz_vert", "half_y")
            )),
            entry("FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER", "slopepanel", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "ext_slope_panel_tri_xz", "ext_slope_panel_tri_y")
            )),
            entry("FRAMED_SMALL_CORNER_SLOPE_PANEL", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz"),
                    new TestDir("CornerDir", "Corner", null, "corner_y")
            )),
            entry("FRAMED_SMALL_CORNER_SLOPE_PANEL_W", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri", "slope_panel_tri_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz")
            )),
            entry("FRAMED_LARGE_CORNER_SLOPE_PANEL", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz"),
                    new TestDir("TriangleDir", "Stair", null, "stair_y")
            )),
            entry("FRAMED_LARGE_CORNER_SLOPE_PANEL_W", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri", "slope_panel_tri_y"),
                    new TestDir("TriangleDir", "Stair", null, "stair_xz")
            )),
            entry("FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz"),
                    new TestDir("CornerDir", "Corner", null, "corner_y")
            )),
            entry("FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri", "slope_panel_tri_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz")
            )),
            entry("FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz"),
                    new TestDir("TriangleDir", "Stair", null, "stair_y")
            )),
            entry("FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_slab_tri", "slope_panel_tri_y"),
                    new TestDir("TriangleDir", "Stair", null, "stair_xz")
            )),
            entry("FRAMED_EXT_CORNER_SLOPE_PANEL", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "ext_slope_panel_tri_xz"),
                    new TestDir("CornerDir", "Corner", null, "corner_y")
            )),
            entry("FRAMED_EXT_CORNER_SLOPE_PANEL_W", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "elev_slope_slab_tri", "ext_slope_panel_tri_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz")
            )),
            entry("FRAMED_EXT_INNER_CORNER_SLOPE_PANEL", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "slope_panel_tri_xz"),
                    new TestDir("TriangleDir", "Stair", null, "stair_y")
            )),
            entry("FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W", "slopepanelcorner", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
            ), List.of(
                    new TestDir("HalfTriangleDir", "Tri", null, "elev_slope_slab_tri", "ext_slope_panel_tri_y"),
                    new TestDir("TriangleDir", "Stair", null, "stair_xz")
            )),
            entry("FRAMED_MASONRY_CORNER_SEGMENT", "slab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("HalfDir", "Half", null, "half_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz"),
                    new TestDir("TriangleDir", "Stair", null, "stair_xz")
            )),
            entry("FRAMED_CHECKERED_CUBE_SEGMENT", "slab", List.of(
                    Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("DiagCornerDir", "DiagCorner", null, "checker_xz", "checker_y")
            )),
            entry("FRAMED_CHECKERED_SLAB_SEGMENT", "slab", List.of(
                    Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                    Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("DiagCornerDir", "DiagCorner", null, "checker_y"),
                    new TestDir("CornerDir", "Corner", null, "corner_xz")
            )),
            entry("FRAMED_CHECKERED_PANEL_SEGMENT", "slab", List.of(
                    Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                    Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
            ), List.of(
                    new TestDir("DiagCornerDir", "DiagCorner", null, "checker_xz"),
                    new TestDir("CornerDir", "Corner", null, "corner_y")
            ))
    ));

    private static Map.Entry<String, Type> entry(String type, String subPackage, List<Property> properties, List<TestDir> testDirs)
    {
        Map<String, Property> propertyMap = properties.stream().collect(Collectors.toMap(Property::name, p -> p));
        List<TestDir> newTestDirs = new ArrayList<>(testDirs.size());
        List<String> propNames = List.copyOf(properties.stream().map(Property::name).toList());
        for (TestDir dir : testDirs)
        {
            if (dir.props == null)
            {
                dir = new TestDir(dir.type, dir.name, propNames, dir.identifiers);
            }
            newTestDirs.add(dir);
        }
        return Map.entry(type, new Type(type, subPackage, properties, propertyMap, List.copyOf(newTestDirs)));
    }

    private static Map<String, Type> validate(Map<String, Type> map)
    {
        Map<String, String> dirTypeById = new HashMap<>();
        for (Type type : map.values())
        {
            for (TestDir dir : type.testDirs)
            {
                for (String id : dir.identifiers)
                {
                    String prevType = dirTypeById.put(id, dir.type);
                    if (prevType != null && !prevType.equals(dir.type))
                    {
                        throw new IllegalStateException(
                                "BlockType '%s' specifies type '%s' for dir '%s', previously found with type '%s'".formatted(
                                        type.type(), dir.type, id, prevType
                                )
                        );
                    }
                }
            }
        }
        return map;
    }



    record Type(String type, String subPackage, List<Property> properties, Map<String, Property> propertyMap, List<TestDir> testDirs) { }

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

    record TestDir(String type, String name, List<String> props, Set<String> identifiers)
    {
        TestDir(String type, String name, List<String> props, String... identifiers)
        {
            this(type, name, props, Set.of(identifiers));
        }
    }



    private SkipPredicateGeneratorData() { }
}
