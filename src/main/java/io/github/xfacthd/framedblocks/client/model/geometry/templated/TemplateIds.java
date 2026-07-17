package io.github.xfacthd.framedblocks.client.model.geometry.templated;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public final class TemplateIds {
    static final Identifier SLAB_MODEL = Utils.id("minecraft", "block/slab");
    public static final Identifier SLAB_EDGE = Utils.id("slab_edge");
    public static final Identifier SLAB_CORNER = Utils.id("slab_corner");
    public static final Identifier STAIRS_STRAIGHT = Utils.id("stairs_straight");
    public static final Identifier STAIRS_INNER = Utils.id("stairs_inner");
    public static final Identifier STAIRS_OUTER = Utils.id("stairs_outer");
    public static final Identifier HALF_STAIRS_LEFT = Utils.id("half_stairs_left");
    public static final Identifier THREEWAY_CORNER_PILLAR = Utils.id("threeway_corner_pillar");
    public static final Identifier FENCE_ARM = Utils.id("fence_arm");
    public static final Identifier FENCE_GATE = Utils.id("fence_gate");
    public static final Identifier FENCE_GATE_IN_WALL = Utils.id("fence_gate_in_wall");
    public static final Identifier FENCE_GATE_OPEN = Utils.id("fence_gate_open");
    public static final Identifier FENCE_GATE_IN_WALL_OPEN = Utils.id("fence_gate_in_wall_open");
    public static final Identifier DOOR = Utils.id("door");
    static final Identifier TRAPDOOR_BOTTOM = Utils.id("minecraft", "block/template_trapdoor_bottom");
    static final Identifier TRAPDOOR_TOP = Utils.id("minecraft", "block/template_trapdoor_top");
    static final Identifier TRAPDOOR_OPEN = Utils.id("minecraft", "block/template_trapdoor_open");
    static final Identifier PRESSURE_PLATE_UP = Utils.id("minecraft", "block/pressure_plate_up");
    static final Identifier PRESSURE_PLATE_DOWN = Utils.id("minecraft", "block/pressure_plate_down");
    public static final Identifier LADDER = Utils.id("ladder");
    static final Identifier BUTTON = Utils.id("minecraft", "block/button");
    public static final Identifier BUTTON_PRESSED = Utils.id("button_pressed");
    public static final Identifier LARGE_BUTTON = Utils.id("large_button");
    public static final Identifier LARGE_BUTTON_PRESSED = Utils.id("large_button_pressed");
    public static final Identifier WALL_SIGN = Utils.id("wall_sign");
    public static final Identifier HALF_BOARD = Utils.id("half_board");
    public static final Identifier CORNER_BOARD = Utils.id("corner_board");
    public static final Identifier INNER_CORNER_BOARD = Utils.id("inner_corner_board");
    public static final Identifier CORNER_STRIP = Utils.id("corner_strip");
    public static final Identifier LATTICE_CORE = Utils.id("lattice_core");
    public static final Identifier LATTICE_ARM = Utils.id("lattice_arm");
    public static final Identifier LATTICE_CORE_THICK = Utils.id("lattice_core_thick");
    public static final Identifier LATTICE_ARM_THICK = Utils.id("lattice_arm_thick");
    public static final Identifier HORIZONTAL_PANE = Utils.id("horizontal_pane");
    public static final Identifier PILLAR = Utils.id("pillar");
    public static final Identifier HALF_PILLAR = Utils.id("half_pillar");
    public static final Identifier PILLAR_SOCKET = Utils.id("pillar_socket");
    public static final Identifier POST = Utils.id("post");
    public static final Identifier CENTERED_SLAB = Utils.id("centered_slab");
    public static final Identifier CENTERED_PANEL = Utils.id("centered_panel");
    public static final Identifier BOOKSHELF = Utils.id("bookshelf");
    public static final Identifier CHISELED_BOOKSHELF = Utils.id("chiseled_bookshelf");
    public static final Identifier MASONRY_CORNER_SEGMENT = Utils.id("masonry_corner_segment");
    public static final Identifier CHECKERED_CUBE_SEGMENT = Utils.id("checkered_cube_segment");
    public static final Identifier CHECKERED_SLAB_SEGMENT = Utils.id("checkered_slab_segment");
    public static final Identifier CHECKERED_PANEL_SEGMENT = Utils.id("checkered_panel_segment");
    public static final Identifier TUBE = Utils.id("tube");
    public static final Identifier TUBE_THICK = Utils.id("tube_thick");
    public static final Identifier CORNER_TUBE = Utils.id("corner_tube");
    public static final Identifier CORNER_TUBE_THICK = Utils.id("corner_tube_thick");
    public static final Identifier HOPPER = Utils.id("hopper");
    public static final Identifier HOPPER_SIDE = Utils.id("hopper_side");
    static final Identifier CUBE = Utils.id("minecraft", "block/cube");
    static final Identifier[] SNOW_LAYERS = Util.make(() -> {
        List<Integer> values = BlockStateProperties.LAYERS.getPossibleValues();
        Identifier[] layers = new Identifier[values.size() - 1];
        for (Integer value : values) {
            if (value < 8) {
                layers[value - 1] = Utils.id("minecraft", "block/snow_height" + (value * 2));
            }
        }
        return layers;
    });
    static final Identifier DIRT_PATH = Utils.id("minecraft", "block/dirt_path");
    static final Identifier SHELF_BODY = Utils.id("minecraft", "block/template_shelf_body");
    static final Identifier SHELF_UNPOWERED = Utils.id("minecraft", "block/template_shelf_unpowered");

    private TemplateIds() { }
}
