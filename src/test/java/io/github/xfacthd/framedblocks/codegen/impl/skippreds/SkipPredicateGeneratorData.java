package io.github.xfacthd.framedblocks.codegen.impl.skippreds;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("SameParameterValue")
final class SkipPredicateGeneratorData {
    private static final String IGNORED_PKG = "ignored";
    static final Map<String, String> TEST_DIR_COMPUTE_CLASS_NAME_OVERRIDES = Map.of(
            "slopeedge", "SlopeEdge",
            "slopepanel", "SlopePanel",
            "slopepanelcorner", "SlopePanelCorner",
            "slopeslab", "SlopeSlab"
    );
    static final Map<String, Type> KNOWN_TYPES = ofEntries(
            entry("FRAMED_CUBE", "cube"),
            entry("FRAMED_SLOPE", "slope")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
                    )
                    .dirs(new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ, TestDirIds.SLOPE_TRI_Y)),
            entry("FRAMED_HALF_SLOPE", "slope")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_VERTICAL_HALF_SLOPE", "slope")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_CORNER_SLOPE", "slope")
                    .shortName("Corner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
                    )
                    .dirs(new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ, TestDirIds.SLOPE_TRI_Y)),
            entry("FRAMED_INNER_CORNER_SLOPE", "slope")
                    .shortName("InnerCorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
                    )
                    .dirs(new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ, TestDirIds.SLOPE_TRI_Y)),
            entry("FRAMED_THREEWAY_CORNER", "slope")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ, TestDirIds.SLOPE_TRI_Y))
                    .altTypes("FRAMED_PRISM_CORNER"),
            entry("FRAMED_INNER_THREEWAY_CORNER", "slope")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ, TestDirIds.SLOPE_TRI_Y))
                    .altTypes("FRAMED_INNER_PRISM_CORNER"),
            entry("FRAMED_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM),
                            Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_TRI_XZ, TestDirIds.SLOPE_EDGE_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_ELEVATED_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_EDGE_TRI_XZ, TestDirIds.ELEV_SLOPE_EDGE_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_CORNER_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM),
                            Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_TRI_XZ, TestDirIds.SLOPE_EDGE_TRI_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_INNER_CORNER_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM),
                            Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_TRI_XZ, TestDirIds.SLOPE_EDGE_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_ELEVATED_CORNER_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_EDGE_TRI_XZ, TestDirIds.ELEV_SLOPE_EDGE_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("CornerType", "type", "CORNER_TYPE", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_EDGE_TRI_XZ, TestDirIds.ELEV_SLOPE_EDGE_TRI_Y),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_THREEWAY_CORNER_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE),
                            Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_TRI_XZ, TestDirIds.SLOPE_EDGE_TRI_Y)),
            entry("FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE),
                            Property.internal("boolean", "alt", "ALT_TYPE", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_TRI_XZ, TestDirIds.SLOPE_EDGE_TRI_Y),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_SLOPE_EDGE_SLAB", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_Y),
                            new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_SLAB_TRI)
                    ),
            entry("FRAMED_SLOPE_EDGE_PANEL", "slopeedge")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                            Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("QuarterTriangleDir", "Tri", null, TestDirIds.SLOPE_EDGE_PANEL_TRI_HOR, TestDirIds.SLOPE_EDGE_PANEL_TRI_VERT)
                    ),
            entry("FRAMED_SLAB", "slab")
                    .props(Property.api("boolean", "top", "TOP", PropType.PRIMITIVE))
                    .dirs(new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR)),
            entry("FRAMED_SLAB_EDGE", "slab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ)
                    ),
            entry("FRAMED_SLAB_CORNER", "slab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)),
            entry("FRAMED_PANEL", "slab")
                    .props(Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE))
                    .dirs(new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)),
            entry("FRAMED_CORNER_PILLAR", "pillar")
                    .props(Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE))
                    .dirs(
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_STAIRS", "stairs")
                    .props(
                            Property.vanilla("Direction", "dir", "HORIZONTAL_FACING", PropType.PRIMITIVE),
                            Property.vanilla("StairsShape", "shape", "STAIRS_SHAPE", PropType.VANILLA),
                            Property.vanilla("Half", "half", "HALF", PropType.VANILLA)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_HALF_STAIRS", "stairs")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "right", "RIGHT", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_SLOPED_STAIRS", "stairs")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_VERTICAL_STAIRS", "stairs")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("StairsType", "type", "STAIRS_TYPE", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_VERTICAL_HALF_STAIRS", "stairs")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ)
                    ),
            entry("FRAMED_VERTICAL_SLOPED_STAIRS", "stairs")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_THREEWAY_CORNER_PILLAR", "pillar")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_WALL", "pillar")
                    .props(Property.vanilla("boolean", "up", "UP", PropType.PRIMITIVE))
                    .dirs(
                            new TestDir(null, "WallArm", List.of(), TestDirIds.WALL_ARM),
                            new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_VERT)
                    ),
            entry("FRAMED_FENCE", "pillar")
                    .dirs(
                            new TestDir(null, "FenceArm", List.of(), TestDirIds.FENCE_ARM),
                            new TestDir("boolean", "Post", List.of(), TestDirIds.POST_VERT)
                    )
                    .oneWayTest("FRAMED_FENCE_GATE", new TestDir(null, "FenceArmToGate", List.of(), "fence_arm_gate")),
            entry("FRAMED_FENCE_GATE", "door"),
            entry("FRAMED_DOOR", "door")
                    .props(
                            Property.vanilla("Direction", "dir", "HORIZONTAL_FACING", PropType.PRIMITIVE),
                            Property.vanilla("DoorHingeSide", "hinge", "DOOR_HINGE", PropType.VANILLA),
                            Property.vanilla("boolean", "open", "OPEN", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("HalfDir", "DoorEdge", null, TestDirIds.DOOR_EDGE_HOR_Y, TestDirIds.DOOR_EDGE_VERT))
                    .altTypes("FRAMED_IRON_DOOR"),
            entry("FRAMED_TRAPDOOR", "door")
                    .props(
                            Property.vanilla("Direction", "dir", "HORIZONTAL_FACING", PropType.PRIMITIVE),
                            Property.vanilla("Half", "half", "HALF", PropType.VANILLA),
                            Property.vanilla("boolean", "open", "OPEN", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("HalfDir", "DoorEdge", null, TestDirIds.DOOR_EDGE_HOR_XZ, TestDirIds.DOOR_EDGE_HOR_Y, TestDirIds.DOOR_EDGE_VERT))
                    .altTypes("FRAMED_IRON_TRAPDOOR"),
            entry("FRAMED_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_WATERLOGGABLE_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_STONE_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_OBSIDIAN_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_GOLD_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_IRON_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE", IGNORED_PKG),
            entry("FRAMED_LADDER", "misc"),
            entry("FRAMED_BUTTON", IGNORED_PKG),
            entry("FRAMED_STONE_BUTTON", IGNORED_PKG),
            entry("FRAMED_LARGE_BUTTON", IGNORED_PKG),
            entry("FRAMED_LARGE_STONE_BUTTON", IGNORED_PKG),
            entry("FRAMED_LEVER", IGNORED_PKG),
            entry("FRAMED_SIGN", IGNORED_PKG),
            entry("FRAMED_WALL_SIGN", IGNORED_PKG),
            entry("FRAMED_HANGING_SIGN", IGNORED_PKG),
            entry("FRAMED_WALL_HANGING_SIGN", IGNORED_PKG),
            entry("FRAMED_TORCH", IGNORED_PKG),
            entry("FRAMED_WALL_TORCH", IGNORED_PKG),
            entry("FRAMED_SOUL_TORCH", IGNORED_PKG),
            entry("FRAMED_SOUL_WALL_TORCH", IGNORED_PKG),
            entry("FRAMED_COPPER_TORCH", IGNORED_PKG),
            entry("FRAMED_COPPER_WALL_TORCH", IGNORED_PKG),
            entry("FRAMED_REDSTONE_TORCH", IGNORED_PKG),
            entry("FRAMED_REDSTONE_WALL_TORCH", IGNORED_PKG),
            entry("FRAMED_BOARD", "pane")
                    .props(
                            Property.internal("int", "faces", "FACES", PropType.PRIMITIVE)
                                    .withEarlyExit()
                    )
                    .dirs(
                            new TestDir("int", "EdgeMask", null, TestDirIds.BOARD_EDGE_MASK),
                            new TestDir("HalfDir", "SingleEdge", null, TestDirIds.BOARD_SINGLE_EDGE)
                                    .withExcludedTypes("FRAMED_BOARD")
                    ),
            entry("FRAMED_HALF_BOARD", "pane")
                    .props(Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM))
                    .dirs(
                            new TestDir("HalfDir", "Edge", null, TestDirIds.BOARD_SINGLE_EDGE),
                            new TestDir("CornerDir", "HalfEdge", null, TestDirIds.HALF_BOARD_HALF_EDGE),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_CORNER_BOARD", "pane")
                    .props(Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM))
                    .dirs(
                            new TestDir("CornerDir", "HalfEdge", null, TestDirIds.HALF_BOARD_HALF_EDGE),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_INNER_CORNER_BOARD", "pane")
                    .props(Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM))
                    .dirs(
                            new TestDir("HalfDir", "Edge", null, TestDirIds.BOARD_SINGLE_EDGE),
                            new TestDir("CornerDir", "HalfEdge", null, TestDirIds.HALF_BOARD_HALF_EDGE),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_CORNER_STRIP", "pane")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("SlopeType", "type", "SLOPE_TYPE", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfDir", "Edge", null, TestDirIds.BOARD_SINGLE_EDGE),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_STRIP_CORNER)
                    ),
            entry("FRAMED_LATTICE_BLOCK", "pillar")
                    .shortName("Lattice")
                    .props(
                            Property.api("boolean", "xAxis", "X_AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.api("boolean", "yAxis", "Y_AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.api("boolean", "zAxis", "Z_AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit()
                    )
                    .dirs(new TestDir("boolean", "Post", null, TestDirIds.POST_HOR, TestDirIds.POST_VERT)),
            entry("FRAMED_THICK_LATTICE", "pillar")
                    .props(
                            Property.api("boolean", "xAxis", "X_AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.api("boolean", "yAxis", "Y_AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.api("boolean", "zAxis", "Z_AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit()
                    )
                    .dirs(new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)),
            entry("FRAMED_CHEST", "misc"),
            entry("FRAMED_SECRET_STORAGE", IGNORED_PKG),
            entry("FRAMED_TANK", IGNORED_PKG),
            entry("FRAMED_BARS", "pane"),
            entry("FRAMED_PANE", "pane"),
            entry("FRAMED_HORIZONTAL_PANE", "pane"),
            entry("FRAMED_RAIL_SLOPE", "slope")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE)
                                    .withPropLookup("io/github/xfacthd/framedblocks/common/block/SlopeBlock", "slopeBlock", "getFacing")
                    )
                    .dirs(new TestDir("TriangleDir", "Tri", null, TestDirIds.SLOPE_TRI_XZ))
                    .altTypes(
                            "FRAMED_POWERED_RAIL_SLOPE",
                            "FRAMED_DETECTOR_RAIL_SLOPE",
                            "FRAMED_ACTIVATOR_RAIL_SLOPE"
                    ),
            entry("FRAMED_FANCY_RAIL", IGNORED_PKG),
            entry("FRAMED_FANCY_POWERED_RAIL", IGNORED_PKG),
            entry("FRAMED_FANCY_DETECTOR_RAIL", IGNORED_PKG),
            entry("FRAMED_FANCY_ACTIVATOR_RAIL", IGNORED_PKG),
            entry("FRAMED_FLOWER_POT", IGNORED_PKG),
            entry("FRAMED_PILLAR", "pillar")
                    .props(Property.vanilla("Direction.Axis", "axis", "AXIS", PropType.PRIMITIVE).withEarlyExit())
                    .dirs(new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)),
            entry("FRAMED_HALF_PILLAR", "pillar")
                    .props(Property.vanilla("Direction", "dir", "FACING", PropType.PRIMITIVE).withEarlyExit())
                    .dirs(new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)),
            entry("FRAMED_PILLAR_SOCKET", "pillar")
                    .props(Property.vanilla("Direction", "dir", "FACING", PropType.PRIMITIVE))
                    .dirs(
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)
                    ),
            entry("FRAMED_POST", "pillar")
                    .props(Property.vanilla("Direction.Axis", "axis", "AXIS", PropType.PRIMITIVE).withEarlyExit())
                    .dirs(new TestDir("boolean", "Post", null, TestDirIds.POST_HOR, TestDirIds.POST_VERT)),
            entry("FRAMED_COLLAPSIBLE_BLOCK", "misc"),
            entry("FRAMED_COLLAPSIBLE_COPYCAT_BLOCK", "misc"),
            entry("FRAMED_BOUNCY_CUBE", IGNORED_PKG),
            entry("FRAMED_REDSTONE_BLOCK", IGNORED_PKG),
            entry("FRAMED_PRISM", "prism")
                    .props(Property.internal("DirectionAxis", "dirAxis", "FACING_AXIS", PropType.CUSTOM).withEarlyExit())
                    .dirs(new TestDir("HalfDir", "Tri", null, TestDirIds.PRISM_TRI_XZ_HOR, TestDirIds.PRISM_TRI_XZ_VERT, TestDirIds.PRISM_TRI_Y)),
            entry("FRAMED_ELEVATED_INNER_PRISM", "prism")
                    .props(Property.internal("DirectionAxis", "dirAxis", "FACING_AXIS", PropType.CUSTOM).withEarlyExit())
                    .dirs(new TestDir("HalfDir", "Tri", null, TestDirIds.ELEV_INNER_PRISM_TRI_XZ_HOR, TestDirIds.ELEV_INNER_PRISM_TRI_XZ_VERT, TestDirIds.ELEV_INNER_PRISM_TRI_Y)),
            entry("FRAMED_SLOPED_PRISM", "prism")
                    .props(Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM).withEarlyExit())
                    .dirs(new TestDir("HalfDir", "Tri", null, TestDirIds.PRISM_TRI_XZ_HOR, TestDirIds.PRISM_TRI_XZ_VERT, TestDirIds.PRISM_TRI_Y)),
            entry("FRAMED_ELEVATED_INNER_SLOPED_PRISM", "prism")
                    .props(Property.internal("CompoundDirection", "cmpDir", "FACING_DIR", PropType.CUSTOM).withEarlyExit())
                    .dirs(new TestDir("HalfDir", "Tri", null, TestDirIds.ELEV_INNER_PRISM_TRI_XZ_HOR, TestDirIds.ELEV_INNER_PRISM_TRI_XZ_VERT, TestDirIds.ELEV_INNER_PRISM_TRI_Y)),
            entry("FRAMED_SLOPE_SLAB", "slopeslab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_SLAB_TRI_TOP),
                            new TestDir("HalfDir", "Half", List.of("dir", "topHalf"), TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_ELEVATED_SLOPE_SLAB", "slopeslab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_COMPOUND_SLOPE_SLAB", "slopeslab")
                    .props(Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE))
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.CMP_SLOPE_SLAB_TRI),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_FLAT_SLOPE_SLAB_CORNER", "slopeslab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_SLAB_TRI_TOP)),
            entry("FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER", "slopeslab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "topHalf", "TOP_HALF", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_SLAB_TRI_TOP),
                            new TestDir("HalfDir", "Half", List.of("dir", "topHalf"), TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER", "slopeslab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR)
                    ),
            entry("FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER", "slopeslab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI)),
            entry("FRAMED_SLOPE_PANEL", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                            Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK, TestDirIds.SLOPE_PANEL_TRI_XZ_FRONT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK, TestDirIds.SLOPE_PANEL_TRI_Y_FRONT),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_EXTENDED_SLOPE_PANEL", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ, TestDirIds.EXT_SLOPE_PANEL_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_COMPOUND_SLOPE_PANEL", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.CMP_SLOPE_PANEL_TRI_XZ, TestDirIds.CMP_SLOPE_PANEL_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_FLAT_SLOPE_PANEL_CORNER", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                            Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK, TestDirIds.SLOPE_PANEL_TRI_XZ_FRONT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK, TestDirIds.SLOPE_PANEL_TRI_Y_FRONT)),
            entry("FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM),
                            Property.internal("boolean", "front", "FRONT", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK, TestDirIds.SLOPE_PANEL_TRI_XZ_FRONT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK, TestDirIds.SLOPE_PANEL_TRI_Y_FRONT),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ, TestDirIds.EXT_SLOPE_PANEL_TRI_Y),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                    ),
            entry("FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER", "slopepanel")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ, TestDirIds.EXT_SLOPE_PANEL_TRI_Y)),
            entry("FRAMED_SMALL_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_SMALL_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ)
                    ),
            entry("FRAMED_LARGE_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_FRONT),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_LARGE_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_TOP, TestDirIds.SLOPE_PANEL_TRI_Y_FRONT),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ)
                    ),
            entry("FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_FRONT),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_TOP, TestDirIds.SLOPE_PANEL_TRI_Y_FRONT),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ)
                    ),
            entry("FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ)
                    ),
            entry("FRAMED_EXT_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_EXT_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI, TestDirIds.EXT_SLOPE_PANEL_TRI_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ)
                    ),
            entry("FRAMED_EXT_INNER_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_Y)
                    ),
            entry("FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "Tri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI, TestDirIds.EXT_SLOPE_PANEL_TRI_Y),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ)
                    ),
            entry("FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK),
                            new TestDir("QuarterTriangleDir", "BottomTri", null, TestDirIds.SLOPE_EDGE_TRI_Y)
                    ),
            entry("FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK),
                            new TestDir("QuarterTriangleDir", "BackTri", null, TestDirIds.SLOPE_EDGE_TRI_XZ)
                    ),
            entry("FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ),
                            new TestDir("TriangleDir", "BottomTri", null, TestDirIds.SLOPE_TRI_Y),
                            new TestDir("QuarterTriangleDir", "TopTri", null, TestDirIds.SLOPE_EDGE_TRI_Y)
                    ),
            entry("FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI, TestDirIds.EXT_SLOPE_PANEL_TRI_Y),
                            new TestDir("TriangleDir", "BackTri", null, TestDirIds.SLOPE_TRI_XZ),
                            new TestDir("QuarterTriangleDir", "FrontTri", null, TestDirIds.SLOPE_EDGE_TRI_XZ)
                    ),
            entry("FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.EXT_SLOPE_PANEL_TRI_XZ),
                            new TestDir("TriangleDir", "TopTri", null, TestDirIds.PRISM_SLOPE_PANEL_CORNER_TRI_Y)
                    ),
            entry("FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.ELEV_SLOPE_SLAB_TRI, TestDirIds.EXT_SLOPE_PANEL_TRI_Y),
                            new TestDir("TriangleDir", "FrontTri", null, TestDirIds.PRISM_SLOPE_PANEL_CORNER_TRI_XZ)
                    ),
            entry("FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.SLOPE_PANEL_TRI_XZ_BACK),
                            new TestDir("TriangleDir", "BottomTri", null, TestDirIds.PRISM_SLOPE_PANEL_CORNER_TRI_Y),
                            new TestDir("TriangleDir", "TopTri", null, TestDirIds.SLOPE_TRI_Y)
                    ),
            entry("FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W", "slopepanelcorner")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("HorizontalRotation", "rot", "ROTATION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfTriangleDir", "SideTri", null, TestDirIds.SLOPE_SLAB_TRI_BOT, TestDirIds.SLOPE_PANEL_TRI_Y_BACK),
                            new TestDir("TriangleDir", "BackTri", null, TestDirIds.PRISM_SLOPE_PANEL_CORNER_TRI_XZ),
                            new TestDir("TriangleDir", "FrontTri", null, TestDirIds.SLOPE_TRI_XZ)
                    ),
            entry("FRAMED_PYRAMID", "slope")
                    .props(
                            Property.vanilla("Direction", "dir", "FACING", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.internal("PillarConnection", "connection", "PILLAR_CONNECTION", PropType.CUSTOM)
                                    .withEarlyExit()
                    )
                    .dirs(
                            new TestDir("boolean", "Post", null, TestDirIds.POST_HOR, TestDirIds.POST_VERT)
                                    .withExcludedTypes("FRAMED_PYRAMID", "FRAMED_ELEVATED_PYRAMID_SLAB", "FRAMED_UPPER_PYRAMID_SLAB"),
                            new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)
                                    .withExcludedTypes("FRAMED_PYRAMID", "FRAMED_ELEVATED_PYRAMID_SLAB", "FRAMED_UPPER_PYRAMID_SLAB")
                    ),
            entry("FRAMED_PYRAMID_SLAB", "slope"),
            entry("FRAMED_ELEVATED_PYRAMID_SLAB", "slope")
                    .props(
                            Property.vanilla("Direction", "dir", "FACING", PropType.PRIMITIVE),
                            Property.internal("PillarConnection", "connection", "PILLAR_CONNECTION", PropType.CUSTOM)
                    )
                    .dirs(
                            new TestDir("HalfDir", "Half", List.of("dir"), TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y),
                            new TestDir("boolean", "Post", null, TestDirIds.POST_HOR, TestDirIds.POST_VERT)
                                    .withExcludedTypes("FRAMED_ELEVATED_PYRAMID_SLAB", "FRAMED_PYRAMID", "FRAMED_UPPER_PYRAMID_SLAB"),
                            new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)
                                    .withExcludedTypes("FRAMED_ELEVATED_PYRAMID_SLAB", "FRAMED_PYRAMID", "FRAMED_UPPER_PYRAMID_SLAB")
                    ),
            entry("FRAMED_UPPER_PYRAMID_SLAB", "slope").props(
                            Property.vanilla("Direction", "dir", "FACING", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.internal("PillarConnection", "connection", "PILLAR_CONNECTION", PropType.CUSTOM)
                                    .withEarlyExit()
                    )
                    .dirs(
                            new TestDir("boolean", "Post", null, TestDirIds.POST_HOR, TestDirIds.POST_VERT)
                                    .withExcludedTypes("FRAMED_UPPER_PYRAMID_SLAB", "FRAMED_PYRAMID", "FRAMED_ELEVATED_PYRAMID_SLAB"),
                            new TestDir("boolean", "Pillar", null, TestDirIds.PILLAR_HOR, TestDirIds.PILLAR_VERT)
                                    .withExcludedTypes("FRAMED_UPPER_PYRAMID_SLAB", "FRAMED_PYRAMID", "FRAMED_ELEVATED_PYRAMID_SLAB")
                    ),
            entry("FRAMED_TARGET", IGNORED_PKG),
            entry("FRAMED_GATE", "door")
                    .props(
                            Property.vanilla("Direction", "dir", "HORIZONTAL_FACING", PropType.PRIMITIVE),
                            Property.vanilla("DoorHingeSide", "hinge", "DOOR_HINGE", PropType.VANILLA),
                            Property.vanilla("boolean", "open", "OPEN", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("HalfDir", "DoorEdge", null, TestDirIds.DOOR_EDGE_HOR_Y, TestDirIds.DOOR_EDGE_VERT))
                    .altTypes("FRAMED_IRON_GATE"),
            entry("FRAMED_ITEM_FRAME", IGNORED_PKG),
            entry("FRAMED_GLOWING_ITEM_FRAME", IGNORED_PKG),
            entry("FRAMED_MINI_CUBE", IGNORED_PKG),
            entry("FRAMED_ONE_WAY_WINDOW", "misc"),
            entry("FRAMED_BOOKSHELF", IGNORED_PKG),
            entry("FRAMED_CHISELED_BOOKSHELF", IGNORED_PKG),
            entry("FRAMED_CENTERED_SLAB", "slab"),
            entry("FRAMED_CENTERED_PANEL", "slab"),
            entry("FRAMED_MASONRY_CORNER_SEGMENT", "slab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ),
                            new TestDir("TriangleDir", "Stair", null, TestDirIds.STAIR_XZ)
                    ),
            entry("FRAMED_CHECKERED_CUBE_SEGMENT", "slab")
                    .props(Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE))
                    .dirs(new TestDir("DiagCornerDir", "DiagCorner", null, TestDirIds.CHECKER_XZ, TestDirIds.CHECKER_Y)),
            entry("FRAMED_CHECKERED_SLAB_SEGMENT", "slab")
                    .props(
                            Property.api("boolean", "top", "TOP", PropType.PRIMITIVE),
                            Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("DiagCornerDir", "DiagCorner", null, TestDirIds.CHECKER_Y),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ)
                    ),
            entry("FRAMED_CHECKERED_PANEL_SEGMENT", "slab")
                    .props(
                            Property.api("Direction", "dir", "FACING_HOR", PropType.PRIMITIVE),
                            Property.internal("boolean", "second", "SECOND", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("DiagCornerDir", "DiagCorner", null, TestDirIds.CHECKER_XZ),
                            new TestDir("CornerDir", "Corner", null, TestDirIds.CORNER_XZ, TestDirIds.CORNER_Y)
                    ),
            entry("FRAMED_TUBE", "misc")
                    .props(
                            Property.vanilla("Direction.Axis", "axis", "AXIS", PropType.PRIMITIVE)
                                    .withEarlyExit(),
                            Property.internal("boolean", "thick", "THICK", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("TubeOpening", "Opening", null, TestDirIds.TUBE_OPENING)),
            entry("FRAMED_CORNER_TUBE", "misc")
                    .props(
                            Property.internal("CornerTubeOrientation", "orientation", "CORNER_TYPE_ORIENTATION", PropType.CUSTOM)
                                    .withEarlyExit(),
                            Property.internal("boolean", "thick", "THICK", PropType.PRIMITIVE)
                    )
                    .dirs(new TestDir("TubeOpening", "Opening", null, TestDirIds.TUBE_OPENING)),
            entry("FRAMED_CHAIN", "pillar"),
            entry("FRAMED_LANTERN", IGNORED_PKG),
            entry("FRAMED_SOUL_LANTERN", IGNORED_PKG),
            entry("FRAMED_COPPER_LANTERN", IGNORED_PKG),
            entry("FRAMED_HOPPER", "misc")
                    .dirs(
                            new TestDir("boolean", "HopperSide", null, TestDirIds.HOPPER_SIDE),
                            new TestDir("TubeOpening", "Opening", null, TestDirIds.TUBE_OPENING)
                                    .withExcludedTypes("FRAMED_HOPPER")
                    ),
            entry("FRAMED_LAYERED_CUBE", "misc")
                    .props(
                            Property.vanilla("Direction", "facing", "FACING", PropType.PRIMITIVE),
                            Property.vanilla("int", "layers", "LAYERS", PropType.PRIMITIVE)
                    )
                    .dirs(
                            new TestDir("int", "Layers", null, TestDirIds.LAYERS_DIR),
                            new TestDir("HalfDir", "Half", null, TestDirIds.HALF_XZ_HOR, TestDirIds.HALF_XZ_VERT, TestDirIds.HALF_Y)
                                    .withExcludedTypes("FRAMED_LAYERED_CUBE")
                    ),
            entry("FRAMED_LIGHTNING_ROD", IGNORED_PKG),
            entry("FRAMED_PATH", IGNORED_PKG),
            entry("FRAMED_SHELF", IGNORED_PKG)
    );

    private static EntryBuilder entry(String type, String subPackage) {
        return new EntryBuilder(type, subPackage);
    }

    private static Map<String, Type> ofEntries(EntryBuilder... entries) {
        Map<String, Type> types = new LinkedHashMap<>();
        for (EntryBuilder entry : entries) {
            types.put(entry.type, entry.build());
        }
        return Collections.unmodifiableMap(validate(types));
    }

    private static Map<String, Type> validate(Map<String, Type> map) {
        Map<String, TestType> dirTypeById = new HashMap<>();
        Map<String, Type> specialDirIdToOwingType = new HashMap<>();
        for (Type type : map.values()) {
            for (TestDir dir : type.testDirs) {
                for (String id : dir.identifiers) {
                    TestType prevType = dirTypeById.put(id, dir.type);
                    if (prevType != null && !prevType.equals(dir.type)) {
                        throw new IllegalStateException(
                                "BlockType '%s' specifies type '%s' for dir '%s', previously found with type '%s'".formatted(
                                        type.type, dir.type, id, prevType
                                )
                        );
                    }
                    if (dir.isSpecial()) {
                        Type owningType = specialDirIdToOwingType.put(id, type);
                        if (owningType != null && owningType != type) {
                            throw new IllegalStateException(
                                    "BlockType '%s' uses special dir with id '%s', previously found on BlockType '%s'. Special tests are only supported for same-type tests".formatted(
                                            type.type, id, owningType.type
                                    )
                            );
                        }
                    }
                }
            }
        }

        Map<String, String> altTypesReverse = new HashMap<>();
        map.forEach((name, type) -> {
            for (String altType : type.altTypes) {
                if (altTypesReverse.putIfAbsent(altType, name) != null) {
                    throw new IllegalStateException("BlockType." + altType + " specified as alt type on multiple types");
                }
            }
        });
        validateTypePresence(map, altTypesReverse);

        Set<String> oneWayTargets = new HashSet<>();
        for (Type type : map.values()) {
            oneWayTargets.addAll(type.oneWayTests.keySet());
        }
        Iterator<Map.Entry<String, Type>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Type type = iterator.next().getValue();
            if (type.testDirs.isEmpty() && !oneWayTargets.contains(type.type)) {
                iterator.remove();
            }
        }

        return map;
    }

    private static void validateTypePresence(Map<String, Type> map, Map<String, String> altTypesReverse) {
        Path enumPath = Path.of("./src/main/java/io/github/xfacthd/framedblocks/common/data/BlockType.java");
        List<String> lines;
        try {
            lines = Files.readAllLines(enumPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read enum for validation", e);
        }

        record Entry(String name, boolean doubleBlock) { }

        List<Entry> entries = new ArrayList<>();
        boolean startFound = false;
        for (String line : lines) {
            if (!startFound) {
                startFound = line.equals("public enum BlockType implements IBlockType {");
                continue;
            }
            if (line.contains(";")) {
                break;
            }
            // Ignore comments
            if (line.trim().startsWith("//")) {
                continue;
            }

            int paramStart = line.indexOf("(");
            int paramEnd = line.lastIndexOf(")");
            int commentStart = line.indexOf("/");
            int nameEnd = commentStart > -1 ? Math.min(commentStart, paramStart) : paramStart;
            String name = line.substring(0, nameEnd).trim();
            if (name.startsWith("//")) {
                continue;
            }
            String params = line.substring(paramStart + 1, paramEnd);
            boolean doubleBlock = params.split(",")[5].trim().equals("true");

            entries.add(new Entry(name, doubleBlock));
        }

        List<String> relevantLoadedEntries = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            boolean altType = altTypesReverse.containsKey(entry.name);
            boolean present = map.containsKey(entry.name) || altType;
            if (!present && !entry.doubleBlock) {
                Entry prevEntry = entry;
                do {
                    prevEntry = entries.get(entries.indexOf(prevEntry) - 1);
                }
                while (prevEntry.doubleBlock);
                throw new IllegalStateException("BlockType." + entry.name + " missing from KNOWN_TYPES, expected after BlockType." + prevEntry.name);
            } else if (present && entry.doubleBlock) {
                throw new IllegalStateException("BlockType." + entry.name + " is a double block and must not be in KNOWN_TYPES");
            }
            if (!entry.doubleBlock && !altType) {
                relevantLoadedEntries.add(entry.name);
            }
        }
        Set<String> loadedEntriesSet = new HashSet<>(relevantLoadedEntries);
        for (String entry : map.keySet()) {
            if (!loadedEntriesSet.contains(entry)) {
                throw new IllegalStateException("Found unrecognized type BlockType." + entry);
            }
        }
        List<String> knownEntries = new ArrayList<>(map.keySet());
        for (int i = 0; i < relevantLoadedEntries.size(); i++) {
            String entry = relevantLoadedEntries.get(i);
            int idx = knownEntries.indexOf(entry);
            if (idx != i) {
                throw new IllegalStateException("Expected BlockType." + entry + " at index " + i + ", found it at index " + idx);
            }
        }
    }

    private static final class EntryBuilder {
        private final String type;
        private final String subPackage;
        private String shortName;
        private List<Property> properties = List.of();
        private List<TestDir> testDirs = List.of();
        private List<String> altTypes = List.of();
        private final Map<String, TestDir> oneWayTests = new HashMap<>();

        private EntryBuilder(String type, String subPackage) {
            this.type = type;
            this.subPackage = subPackage;
            StringBuilder builder = new StringBuilder();
            for (String part : type.replace("FRAMED_", "").split("_")) {
                builder.append(switch (part) {
                    case "EXT" -> "Extended";
                    case "ELEV" -> "Elevated";
                    case "W" -> "Wall";
                    default -> SkipPredicateGeneratorImpl.capitalize(part, true);
                });
            }
            shortName = builder.toString();
        }

        public EntryBuilder shortName(String shortName) {
            if (shortName.equals(this.shortName)) {
                throw new IllegalStateException("Unnecessary explicit short name on BlockType." + type);
            }
            this.shortName = shortName;
            return this;
        }

        public EntryBuilder props(Property... props) {
            this.properties = List.of(props);
            return this;
        }

        public EntryBuilder dirs(TestDir... dirs) {
            this.testDirs = List.of(dirs);
            return this;
        }

        public EntryBuilder altTypes(String... altTypes) {
            this.altTypes = List.of(altTypes);
            return this;
        }

        public EntryBuilder oneWayTest(String targetType, TestDir dir) {
            if (!dir.isSpecial()) {
                throw new IllegalArgumentException("One-way test on BlockType." + type + " against BlockType." + targetType + " must be special");
            }
            oneWayTests.put(targetType, dir);
            return this;
        }

        Type build() {
            Map<String, Property> propertyMap = properties.stream().collect(Collectors.toMap(Property::name, p -> p));
            List<TestDir> newTestDirs = new ArrayList<>(testDirs.size());
            List<String> propNames = List.copyOf(properties.stream().map(Property::name).toList());
            for (TestDir dir : testDirs) {
                if (dir.props == null) {
                    dir = new TestDir(dir.type, dir.name, propNames, dir.identifiers, dir.excludedTypes, dir.comparison);
                }
                newTestDirs.add(dir);
            }
            return new Type(type, altTypes, shortName, subPackage, properties, propertyMap, List.copyOf(newTestDirs), Map.copyOf(oneWayTests));
        }
    }

    record Type(
            String type,
            List<String> altTypes,
            String shortName,
            String subPackage,
            List<Property> properties,
            Map<String, Property> propertyMap,
            List<TestDir> testDirs,
            Map<String, TestDir> oneWayTests
    ) {
        public List<String> allTypes(@Nullable List<TestDir> targetTestDirs) {
            List<String> types = new ArrayList<>();
            types.add(type);
            types.addAll(altTypes);
            if (targetTestDirs != null) {
                types.removeIf(type ->
                        targetTestDirs.stream().allMatch(dir -> dir.isExcluded(type))
                );
            }
            return types;
        }

        public boolean hasSelfTest() {
            return !testDirs.stream().allMatch(dir -> dir.isExcluded(this));
        }

        public boolean hasSpecialTests() {
            return testDirs.stream().anyMatch(TestDir::isSpecial);
        }

        public boolean hasOneWayTestAgainst(Type type) {
            return oneWayTests.containsKey(type.type);
        }
    }

    record Property(String typeName, String name, String propHolder, String propName, PropType type, @Nullable SpecialPropLookup specialPropLookup, boolean earlyExit) {
        static Property vanilla(String typeName, String name, String propName, PropType type) {
            return new Property(typeName, name, "BlockStateProperties", propName, type, null, false);
        }

        static Property api(String typeName, String name, String propName, PropType type) {
            return new Property(typeName, name, "FramedProperties", propName, type, null, false);
        }

        static Property internal(String typeName, String name, String propName, PropType type) {
            return new Property(typeName, name, "PropertyHolder", propName, type, null, false);
        }

        Property withPropLookup(String classFqn, String varName, String method) {
            String importLine = classFqn.replace("/", ".");
            int dollarIdx = importLine.indexOf('$');
            if (dollarIdx != -1) {
                importLine = importLine.substring(0, dollarIdx);
            }
            int lastSlash = classFqn.lastIndexOf("/");
            String varType = classFqn.substring(lastSlash + 1).replace("$", ".");
            return new Property(typeName, name, propHolder, propName, type, new SpecialPropLookup(importLine, varType, varName, method), false);
        }

        Property withEarlyExit() {
            return new Property(typeName, name, propHolder, propName, type, specialPropLookup, true);
        }

        boolean hasSpecialLookup() {
            return specialPropLookup != null;
        }
    }

    record SpecialPropLookup(String classImport, String varType, String varName, String method) { }

    enum PropType {
        PRIMITIVE,
        VANILLA,
        CUSTOM
    }

    record TestDir(TestType type, String name, @Nullable List<String> props, Set<String> identifiers, Set<String> excludedTypes, ComparisonType comparison) {
        TestDir(@Nullable String type, String name, @Nullable List<String> props, String... identifiers) {
            this(new TestType(type), name, props, Set.of(identifiers), Set.of(), ComparisonType.of(type));
        }

        List<String> getProps() {
            return Objects.requireNonNull(props);
        }

        TestDir withExcludedTypes(String... types) {
            return new TestDir(type, name, props, identifiers, Set.of(types), comparison);
        }

        boolean isExcluded(Type type) {
            return isExcluded(type.type);
        }

        boolean isExcluded(String type) {
            return excludedTypes.contains(type);
        }

        boolean isSpecial() {
            return comparison == ComparisonType.SPECIAL;
        }
    }

    enum ComparisonType {
        TEST_DIR,
        BOOLEAN,
        IDENTITY,
        EQUALS,
        SPECIAL;

        private static ComparisonType of(@Nullable String type) {
            return switch (type) {
                case null -> SPECIAL;
                case "boolean" -> BOOLEAN;
                case "int" -> IDENTITY;
                default -> TEST_DIR;
            };
        }
    }

    record TestType(@Nullable String type) { }

    private SkipPredicateGeneratorData() { }
}
