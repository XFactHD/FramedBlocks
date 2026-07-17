package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.datagen.templates.AbstractFramedTemplateProvider;
import io.github.xfacthd.framedblocks.api.datagen.templates.GeometryTemplateBuilder;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.client.model.geometry.templated.TemplateIds;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.EnumSet;

public final class FramedTemplateProvider extends AbstractFramedTemplateProvider {
    public FramedTemplateProvider(PackOutput output) {
        super(output, FramedConstants.MOD_ID);
    }

    @Override
    protected void registerTemplates() {
        template(TemplateIds.SLAB_EDGE)
                .cube(0, 0, 0, 16, 8, 8, DIR_ALL);
        template(TemplateIds.SLAB_CORNER)
                .cube(0, 0, 0, 8, 8, 8, DIR_ALL);
        template(TemplateIds.STAIRS_STRAIGHT)
                .cube(0, 0, 0, 16, 8, 16, DIR_EXCEPT_UP)
                .cube(0, 8, 8, 16, 8, 16, Direction.UP)
                .cube(0, 8, 0, 16, 16, 8, DIR_EXCEPT_DOWN);
        template(TemplateIds.STAIRS_INNER)
                .cube(0, 0, 0, 16, 8, 16, DIR_EXCEPT_UP)
                .cube(8, 8, 8, 16, 8, 16, Direction.UP)
                .cube(0, 8, 0, 16, 16, 8, EnumSet.of(Direction.UP, Direction.NORTH, Direction.EAST))
                .cube(8, 8, 8, 16, 16, 8, Direction.SOUTH)
                .cube(0, 8, 0, 0, 16, 16, Direction.WEST)
                .cube(0, 8, 8, 8, 16, 16, EnumSet.of(Direction.UP, Direction.SOUTH, Direction.WEST, Direction.EAST));
        template(TemplateIds.STAIRS_OUTER)
                .cube(0, 0, 0, 16, 8, 16, DIR_EXCEPT_UP)
                .cube(0, 8, 8, 16, 8, 16, Direction.UP)
                .cube(8, 8, 0, 16, 8, 8, Direction.UP)
                .cube(0, 8, 0, 8, 16, 8, DIR_EXCEPT_DOWN);
        template(TemplateIds.HALF_STAIRS_LEFT)
                .cube(0, 0, 0, 8, 8, 16, DIR_EXCEPT_UP)
                .cube(0, 8, 8, 8, 8, 16, Direction.UP)
                .cube(0, 8, 0, 8, 16, 8, DIR_EXCEPT_DOWN);
        template(TemplateIds.THREEWAY_CORNER_PILLAR)
                .cube(0, 0, 0, 16, 8, 8, EnumSet.of(Direction.DOWN, Direction.NORTH, Direction.EAST))
                .cube(0, 0, 0, 8, 8, 16, EnumSet.of(Direction.WEST, Direction.SOUTH))
                .cube(0, 8, 0, 8, 16, 8, DIR_EXCEPT_DOWN)
                .cube(0, 0, 8, 8, 0, 16, Direction.DOWN)
                .cube(8, 0, 8, 16, 8, 8, Direction.SOUTH)
                .cube(8, 0, 8, 8, 8, 16, Direction.EAST)
                .cube(8, 8, 0, 16, 8, 8, Direction.UP)
                .cube(0, 8, 8, 8, 8, 16, Direction.UP);
        template(TemplateIds.FENCE_ARM)
                .cube(7, 6, 0, 9, 9, 6, DIR_EXCEPT_SOUTH)
                .cube(7, 12, 0, 9, 15, 6, DIR_EXCEPT_SOUTH);
        fenceGate(TemplateIds.FENCE_GATE, false);
        fenceGate(TemplateIds.FENCE_GATE_IN_WALL, true);
        fenceGateOpen(TemplateIds.FENCE_GATE_OPEN, false);
        fenceGateOpen(TemplateIds.FENCE_GATE_IN_WALL_OPEN, true);
        template(TemplateIds.DOOR)
                .cube(0, 0, 0, 16, 16, 3, DIR_ALL);
        ladder();
        template(TemplateIds.BUTTON_PRESSED)
                .cube(5, 0, 6, 11, 1, 10, DIR_ALL);
        template(TemplateIds.LARGE_BUTTON)
                .cube(1, 0, 1, 15, 2, 15, DIR_ALL);
        template(TemplateIds.LARGE_BUTTON_PRESSED)
                .cube(1, 0, 1, 15, 1, 15, DIR_ALL);
        template(TemplateIds.WALL_SIGN)
                .cube(0, 4.5F, 14, 16, 12.5F, 16, DIR_ALL);
        template(TemplateIds.HALF_BOARD)
                .cube(0, 0, 0, 16, 1, 8, DIR_ALL);
        template(TemplateIds.CORNER_BOARD)
                .cube(0, 0, 0, 8, 1, 8, DIR_ALL);
        template(TemplateIds.INNER_CORNER_BOARD)
                .cube(0, 0, 0, 16, 1, 8, DIR_EXCEPT_SOUTH)
                .cube(8, 0, 8, 16, 1, 8, Direction.SOUTH)
                .cube(0, 0, 8, 8, 1, 16, DIR_EXCEPT_NORTH);
        template(TemplateIds.CORNER_STRIP)
                .cube(0, 0, 0, 16, 1, 1, DIR_ALL);
        template(TemplateIds.LATTICE_CORE)
                .cube(6, 6, 6, 10, 10, 10, DIR_VERT);
        template(TemplateIds.LATTICE_ARM)
                .cube(6, 0, 6, 10, 6, 10, DIR_EXCEPT_UP)
                .cube(6, 10, 6, 10, 16, 10, DIR_EXCEPT_DOWN);
        template(TemplateIds.LATTICE_CORE_THICK)
                .cube(4, 4, 4, 12, 12, 12, DIR_VERT);
        template(TemplateIds.LATTICE_ARM_THICK)
                .cube(4, 0, 4, 12, 4, 12, DIR_EXCEPT_UP)
                .cube(4, 12, 4, 12, 16, 12, DIR_EXCEPT_DOWN);
        template(TemplateIds.HORIZONTAL_PANE)
                .cube(0, 7, 0, 16, 9, 16, DIR_ALL);
        template(TemplateIds.PILLAR)
                .cube(4, 0, 4, 12, 16, 12, DIR_ALL);
        template(TemplateIds.HALF_PILLAR)
                .cube(4, 4, 0, 12, 12, 8, DIR_ALL);
        template(TemplateIds.PILLAR_SOCKET)
                .cube(4, 4, 8, 12, 12, 16, DIR_EXCEPT_NORTH)
                .cube(0, 0, 0, 16, 16, 8, DIR_EXCEPT_SOUTH)
                .cube(0, 0, 8, 16, 4, 8, Direction.SOUTH)
                .cube(0, 12, 8, 16, 16, 8, Direction.SOUTH)
                .cube(0, 4, 8, 4, 12, 8, Direction.SOUTH)
                .cube(12, 4, 8, 16, 12, 8, Direction.SOUTH);
        template(TemplateIds.POST)
                .cube(6, 0, 6, 10, 16, 10, DIR_ALL);
        bookshelf();
        template(TemplateIds.CHISELED_BOOKSHELF)
                .cube(0, 0, 0, 16, 16, 16, DIR_EXCEPT_NORTH)
                .cube(0, 0, 0, 16, 1, 0, Direction.NORTH)
                .cube(0, 15, 0, 16, 16, 0, Direction.NORTH)
                .cube(0, 1, 0, 1, 15, 0, Direction.NORTH)
                .cube(15, 1, 0, 16, 15, 0, Direction.NORTH)
                .singleFaceHorizontalBand(Direction.NORTH, 14, 16, 7, 9);
        template(TemplateIds.CENTERED_SLAB)
                .cube(0, 4, 0, 16, 12, 16, DIR_ALL);
        template(TemplateIds.CENTERED_PANEL)
                .cube(0, 0, 4, 16, 16, 12, DIR_ALL);
        template(TemplateIds.MASONRY_CORNER_SEGMENT)
                .cube(0, 0, 8, 16, 8, 16, DIR_EXCEPT_UP)
                .cube(8, 8, 0, 16, 16, 16, DIR_EXCEPT_DOWN)
                .cube(0, 8, 8, 8, 8, 16, Direction.UP)
                .cube(8, 8, 0, 16, 8, 8, Direction.DOWN);
        template(TemplateIds.CHECKERED_CUBE_SEGMENT)
                .cube(0, 0, 0,  8,  8,  8, DIR_ALL)
                .cube(8, 0, 8, 16,  8, 16, DIR_ALL)
                .cube(8, 8, 0, 16, 16,  8, DIR_ALL)
                .cube(0, 8, 8,  8, 16, 16, DIR_ALL);
        template(TemplateIds.CHECKERED_SLAB_SEGMENT)
                .cube(0, 0, 0,  8,  8,  8, DIR_ALL)
                .cube(8, 0, 8, 16,  8, 16, DIR_ALL);
        template(TemplateIds.CHECKERED_PANEL_SEGMENT)
                .cube(0, 0, 0,  8,  8,  8, DIR_ALL)
                .cube(8, 8, 0, 16, 16,  8, DIR_ALL);
        tube(TemplateIds.TUBE, 2F);
        tube(TemplateIds.TUBE_THICK, 3F);
        cornerTube(TemplateIds.CORNER_TUBE, 2F);
        cornerTube(TemplateIds.CORNER_TUBE_THICK, 3F);
        hopper(TemplateIds.HOPPER, false);
        hopper(TemplateIds.HOPPER_SIDE, true);
    }

    private void fenceGate(Identifier id, boolean inWall) {
        float baseY = inWall ? 2 : 5;
        template(id)
                .cube(0, baseY, 7, 2, baseY + 11, 9, DIR_EXCEPT_EAST)
                .cube(14, baseY, 7, 16, baseY + 11, 9, DIR_EXCEPT_WEST)
                .cube(2, baseY, 7, 2, baseY + 1, 9, Direction.EAST)
                .cube(14, baseY, 7, 14, baseY + 1, 9, Direction.WEST)
                .cube(2, baseY + 4, 7, 2, baseY + 7, 9, Direction.EAST)
                .cube(14, baseY + 4, 7, 14, baseY + 7, 9, Direction.WEST)
                .cube(2, baseY + 10, 7, 2, baseY + 11, 9, Direction.EAST)
                .cube(14, baseY + 10, 7, 14, baseY + 11, 9, Direction.WEST)
                .cube(2, baseY + 1, 7, 14, baseY + 4, 9, EnumSet.of(Direction.DOWN, Direction.NORTH, Direction.SOUTH))
                .cube(2, baseY + 7, 7, 14, baseY + 10, 9, EnumSet.of(Direction.UP, Direction.NORTH, Direction.SOUTH))
                .cube(6, baseY + 4, 7, 10, baseY + 7, 9, DIR_HOR)
                .cube(2, baseY + 4, 7, 6, baseY + 4, 9, Direction.UP)
                .cube(10, baseY + 4, 7, 14, baseY + 4, 9, Direction.UP)
                .cube(2, baseY + 7, 7, 6, baseY + 7, 9, Direction.DOWN)
                .cube(10, baseY + 7, 7, 14, baseY + 7, 9, Direction.DOWN);
    }

    private void fenceGateOpen(Identifier id, boolean inWall) {
        float baseY = inWall ? 2 : 5;
        template(id)
                .cube(0, baseY, 7, 2, baseY + 11, 9, DIR_EXCEPT_NORTH)
                .cube(14, baseY, 7, 16, baseY + 11, 9, DIR_EXCEPT_NORTH)
                .cube(0, baseY, 7, 2, baseY + 1, 7, Direction.NORTH)
                .cube(14, baseY, 7, 16, baseY + 1, 7, Direction.NORTH)
                .cube(0, baseY + 4, 7, 2, baseY + 7, 7, Direction.NORTH)
                .cube(14, baseY + 4, 7, 16, baseY + 7, 7, Direction.NORTH)
                .cube(0, baseY + 10, 7, 2, baseY + 11, 7, Direction.NORTH)
                .cube(14, baseY + 10, 7, 16, baseY + 11, 7, Direction.NORTH)
                .cube(0, baseY + 1, 1, 2, baseY + 4, 7, DIR_EXCEPT_UP)
                .cube(0, baseY + 7, 1, 2, baseY + 10, 7, DIR_EXCEPT_DOWN)
                .cube(14, baseY + 1, 1, 16, baseY + 4, 7, DIR_EXCEPT_UP)
                .cube(14, baseY + 7, 1, 16, baseY + 10, 7, DIR_EXCEPT_DOWN)
                .cube(0, baseY + 4, 1, 2, baseY + 7, 3, DIR_HOR)
                .cube(14, baseY + 4, 1, 16, baseY + 7, 3, DIR_HOR)
                .cube(0, baseY + 4, 3, 2, baseY + 4, 7, Direction.UP)
                .cube(0, baseY + 7, 3, 2, baseY + 7, 7, Direction.DOWN)
                .cube(14, baseY + 4, 3, 16, baseY + 4, 7, Direction.UP)
                .cube(14, baseY + 7, 3, 16, baseY + 7, 7, Direction.DOWN);
    }

    private void ladder() {
        GeometryTemplateBuilder builder = template(TemplateIds.LADDER)
                .cube(0, 0, 0, 2, 16, 2, DIR_ALL)
                .cube(14, 0, 0, 16, 16, 2, DIR_ALL);

        EnumSet<Direction> rungFaces = EnumSet.of(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH);
        for (int i = 0; i < 4; i++) {
            float minY = 1.5F + (i * 4F);
            builder.cube(2, minY, .5F, 14, minY + 1F, 1.5F, rungFaces);
        }
    }

    private void bookshelf() {
        GeometryTemplateBuilder builder = template(TemplateIds.BOOKSHELF)
                .cube(0, 0, 0, 16, 1, 16, DIR_EXCEPT_UP)
                .cube(0, 15, 0, 16, 16, 16, DIR_EXCEPT_DOWN)
                .cube(0, 1, 0, 1, 15, 1, EnumSet.of(Direction.NORTH, Direction.WEST))
                .cube(15, 1, 0, 16, 15, 1, EnumSet.of(Direction.NORTH, Direction.EAST))
                .cube(0, 1, 15, 1, 15, 16, EnumSet.of(Direction.SOUTH, Direction.WEST))
                .cube(15, 1, 15, 16, 15, 16, EnumSet.of(Direction.SOUTH, Direction.EAST));
        for (Direction dir : DIR_HOR) {
            builder.singleFaceHorizontalBand(dir, 14, 16, 7, 9);
        }
    }

    private void tube(Identifier id, float thickness) {
        GeometryTemplateBuilder builder = template(id)
                .cube(0, 0, 0, 16, 16, 16, DIR_HOR);
        tubeInnerWalls(builder, DIR_HOR, 0F, thickness);
        tubeVertBorder(builder, DIR_VERT, thickness);
    }

    private void cornerTube(Identifier id, float thickness) {
        float maxInner = 16F - thickness;
        GeometryTemplateBuilder builder = template(id)
                .cube(0, 0, 0, 16, 16, 16, EnumSet.complementOf(EnumSet.of(Direction.UP, Direction.NORTH)))
                .cube(thickness, thickness, 0, maxInner, thickness, maxInner, Direction.UP)
                .cube(thickness, thickness, 0, thickness, maxInner, thickness, Direction.EAST)
                .cube(maxInner, thickness, 0, maxInner, maxInner, thickness, Direction.WEST)
                .cube(0, 0, 0, 16, thickness, 0, Direction.NORTH)
                .cube(0, maxInner, 0, 16, 16, 0, Direction.NORTH)
                .cube(0, thickness, 0, thickness, maxInner, 0, Direction.NORTH)
                .cube(maxInner, thickness, 0, 16, maxInner, 0, Direction.NORTH)
                .cube(thickness, maxInner, thickness, maxInner, 16, thickness, Direction.SOUTH)
                .cube(thickness, maxInner, 0, maxInner, maxInner, thickness, Direction.DOWN);
        tubeInnerWalls(builder, EnumSet.of(Direction.NORTH, Direction.WEST, Direction.EAST), thickness, thickness);
        tubeVertBorder(builder, DIR_UP, thickness);
    }

    private static void tubeInnerWalls(GeometryTemplateBuilder builder, EnumSet<Direction> faces, float minY, float thickness) {
        for (Direction dir : faces) {
            builder.singleFaceHorizontalBand(dir, 16F - (thickness * 2F), thickness, minY, 16);
        }
    }

    private static void tubeVertBorder(GeometryTemplateBuilder builder, EnumSet<Direction> faces, float thickness) {
        float maxInner = 16F - thickness;
        builder.cube(0, 0, 0, 16, 16, thickness, faces)
                .cube(0, 0, maxInner, 16, 16, 16, faces)
                .cube(0, 0, thickness, thickness, 16, maxInner, faces)
                .cube(maxInner, 0, thickness, 16, 16, maxInner, faces);
    }

    private void hopper(Identifier id, boolean side) {
        GeometryTemplateBuilder builder = template(id)
                .cube(0, 10, 0, 16, 16, 16, DIR_HOR)
                .cube(0, 10, 0, 16, 10, 4, Direction.DOWN)
                .cube(0, 10, 12, 16, 10, 16, Direction.DOWN)
                .cube(0, 10, 4, 4, 10, 12, Direction.DOWN)
                .cube(12, 10, 4, 16, 10, 12, Direction.DOWN)
                .cube(2, 11, 2, 14, 11, 14, Direction.UP)
                .cube(4, 4, 4, 12, 10, 12, DIR_EXCEPT_UP);
        tubeVertBorder(builder, DIR_UP, 2F);
        for (Direction dir : DIR_HOR) {
            builder.singleFaceHorizontalBand(dir, 12F, 2F, 11F, 16F);
        }
        if (side) {
            builder.cube(6, 4, 0, 10, 8, 4, DIR_EXCEPT_SOUTH);
        } else {
            builder.cube(6, 0, 6, 10, 4, 10, DIR_EXCEPT_UP);
        }
    }
}
