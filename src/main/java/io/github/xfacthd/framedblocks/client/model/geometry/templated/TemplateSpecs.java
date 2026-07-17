package io.github.xfacthd.framedblocks.client.model.geometry.templated;

import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.template.SourceType;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import io.github.xfacthd.framedblocks.api.model.template.TemplateTransformBuilder;
import io.github.xfacthd.framedblocks.api.model.template.TemplateUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments.MarkedPressurePlateOverlayProvider;
import io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments.ShelfOverlayProvider;
import io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments.StoneButtonOverlayProvider;
import io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments.TrapdoorPostModifierProvider;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.CornerTubeOrientation;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

import java.util.function.UnaryOperator;

public final class TemplateSpecs {
    public static final GeometryTemplateSpec SLAB = TemplateUtils.createTopBottomSpec(FBContent.BLOCK_FRAMED_SLAB, SourceType.MODEL, TemplateIds.SLAB_MODEL);
    public static final GeometryTemplateSpec SLAB_EDGE = TemplateUtils.createTopBottomHorFacingSpec(FBContent.BLOCK_FRAMED_SLAB_EDGE, SourceType.TEMPLATE, TemplateIds.SLAB_EDGE);
    public static final GeometryTemplateSpec SLAB_CORNER = TemplateUtils.createTopBottomHorFacingSpec(FBContent.BLOCK_FRAMED_SLAB_CORNER, SourceType.TEMPLATE, TemplateIds.SLAB_CORNER);
    public static final GeometryTemplateSpec PANEL = panel();
    public static final GeometryTemplateSpec CORNER_PILLAR = cornerPillar();
    public static final GeometryTemplateSpec STAIRS = stairs();
    public static final GeometryTemplateSpec HALF_STAIRS = halfStairs();
    public static final GeometryTemplateSpec VERTICAL_STAIRS = verticalStairs();
    public static final GeometryTemplateSpec VERTICAL_HALF_STAIRS = verticalHalfStairs();
    public static final GeometryTemplateSpec THREEWAY_CORNER_PILLAR = TemplateUtils.createTopBottomHorFacingSpec(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR, SourceType.TEMPLATE, TemplateIds.THREEWAY_CORNER_PILLAR);
    public static final GeometryTemplateSpec FENCE = fence();
    public static final GeometryTemplateSpec FENCE_GATE = fenceGate();
    public static final GeometryTemplateSpec DOOR = door(FBContent.BLOCK_FRAMED_DOOR, false);
    public static final GeometryTemplateSpec IRON_DOOR = door(FBContent.BLOCK_FRAMED_IRON_DOOR, true);
    public static final GeometryTemplateSpec TRAPDOOR = trapdoor(FBContent.BLOCK_FRAMED_TRAP_DOOR, false);
    public static final GeometryTemplateSpec IRON_TRAPDOOR = trapdoor(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR, true);
    public static final GeometryTemplateSpec PRESSURE_PLATE = pressurePlate(FBContent.BLOCK_FRAMED_PRESSURE_PLATE, false);
    public static final GeometryTemplateSpec STONE_PRESSURE_PLATE = pressurePlate(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE, false);
    public static final GeometryTemplateSpec OBSIDIAN_PRESSURE_PLATE = pressurePlate(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE, false);
    public static final GeometryTemplateSpec GOLD_PRESSURE_PLATE = pressurePlate(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE, true);
    public static final GeometryTemplateSpec IRON_PRESSURE_PLATE = pressurePlate(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE, true);
    public static final GeometryTemplateSpec LADDER = ladder();
    public static final GeometryTemplateSpec BUTTON = button(FBContent.BLOCK_FRAMED_BUTTON, false, false);
    public static final GeometryTemplateSpec STONE_BUTTON = button(FBContent.BLOCK_FRAMED_STONE_BUTTON, false, true);
    public static final GeometryTemplateSpec LARGE_BUTTON = button(FBContent.BLOCK_FRAMED_LARGE_BUTTON, true, false);
    public static final GeometryTemplateSpec LARGE_STONE_BUTTON = button(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON, true, true);
    public static final GeometryTemplateSpec WALL_SIGN = wallSign();
    public static final GeometryTemplateSpec HALF_BOARD = partialBoard(FBContent.BLOCK_FRAMED_HALF_BOARD, TemplateIds.HALF_BOARD);
    public static final GeometryTemplateSpec CORNER_BOARD = partialBoard(FBContent.BLOCK_FRAMED_CORNER_BOARD, TemplateIds.CORNER_BOARD);
    public static final GeometryTemplateSpec INNER_CORNER_BOARD = partialBoard(FBContent.BLOCK_FRAMED_INNER_CORNER_BOARD, TemplateIds.INNER_CORNER_BOARD);
    public static final GeometryTemplateSpec CORNER_STRIP = cornerStrip();
    public static final GeometryTemplateSpec LATTICE = lattice(false);
    public static final GeometryTemplateSpec THICK_LATTICE = lattice(true);
    public static final GeometryTemplateSpec HORIZONTAL_PANE = TemplateUtils.createUnitSpec(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE, SourceType.TEMPLATE, TemplateIds.HORIZONTAL_PANE);
    public static final GeometryTemplateSpec PILLAR = TemplateUtils.createAxisSpec(FBContent.BLOCK_FRAMED_PILLAR, SourceType.TEMPLATE, TemplateIds.PILLAR);
    public static final GeometryTemplateSpec HALF_PILLAR = TemplateUtils.createFacingSpec(FBContent.BLOCK_FRAMED_HALF_PILLAR, SourceType.TEMPLATE, TemplateIds.HALF_PILLAR);
    public static final GeometryTemplateSpec PILLAR_SOCKET = TemplateUtils.createFacingSpec(FBContent.BLOCK_FRAMED_PILLAR_SOCKET, SourceType.TEMPLATE, TemplateIds.PILLAR_SOCKET);
    public static final GeometryTemplateSpec POST = post();
    public static final GeometryTemplateSpec GATE = door(FBContent.BLOCK_FRAMED_GATE, false);
    public static final GeometryTemplateSpec IRON_GATE = door(FBContent.BLOCK_FRAMED_IRON_GATE, true);
    public static final GeometryTemplateSpec BOOKSHELF = bookshelf();
    public static final GeometryTemplateSpec CHISELED_BOOKSHELF = chiseledBookshelf();
    public static final GeometryTemplateSpec CENTERED_SLAB = TemplateUtils.createUnitSpec(FBContent.BLOCK_FRAMED_CENTERED_SLAB, SourceType.TEMPLATE, TemplateIds.CENTERED_SLAB);
    public static final GeometryTemplateSpec CENTERED_PANEL = TemplateUtils.createFacingSpec(FBContent.BLOCK_FRAMED_CENTERED_PANEL, FramedProperties.FACING_NE, SourceType.TEMPLATE, TemplateIds.CENTERED_PANEL);
    public static final GeometryTemplateSpec MASONRY_CORNER_SEGMENT = masonryCornerSegment();
    public static final GeometryTemplateSpec CHECKERED_CUBE_SEGMENT = checkeredCubeSegment();
    public static final GeometryTemplateSpec CHECKERED_SLAB_SEGMENT = checkeredSlabSegment();
    public static final GeometryTemplateSpec CHECKERED_PANEL_SEGMENT = checkeredPanelSegment();
    public static final GeometryTemplateSpec TUBE = TemplateUtils.createAxisSpec(FBContent.BLOCK_FRAMED_TUBE, SourceType.TEMPLATE, state -> state.getValue(PropertyHolder.THICK) ? TemplateIds.TUBE_THICK : TemplateIds.TUBE);
    public static final GeometryTemplateSpec CORNER_TUBE = cornerTube();
    public static final GeometryTemplateSpec HOPPER = hopper();
    public static final GeometryTemplateSpec LAYERED_CUBE = layeredCube();
    public static final GeometryTemplateSpec PATH = TemplateUtils.createUnitSpec(FBContent.BLOCK_FRAMED_PATH, SourceType.MODEL, TemplateIds.DIRT_PATH);
    public static final GeometryTemplateSpec SHELF = shelf();

    private static GeometryTemplateSpec panel() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_PANEL, (state, builder) ->
                builder.addSourceFile(SourceType.MODEL, TemplateIds.SLAB_MODEL, xform -> xform.rotationX(Quadrant.R270))
                        .transform(TemplateUtils.applyHorizontalRotation(state, false))
        );
    }

    private static GeometryTemplateSpec cornerPillar() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CORNER_PILLAR, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.SLAB_EDGE, xform -> xform.rotationZ(Quadrant.R90))
                        .transform(TemplateUtils.applyHorizontalRotation(state, false))
        );
    }

    private static GeometryTemplateSpec stairs() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_STAIRS, (state, builder) -> {
            StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            Identifier model = switch (shape) {
                case STRAIGHT -> TemplateIds.STAIRS_STRAIGHT;
                case INNER_LEFT, INNER_RIGHT -> TemplateIds.STAIRS_INNER;
                case OUTER_LEFT, OUTER_RIGHT -> TemplateIds.STAIRS_OUTER;
            };
            Quadrant modelRotY = switch (shape) {
                case INNER_RIGHT, OUTER_RIGHT -> Quadrant.R90;
                default -> Quadrant.R0;
            };
            boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
            builder.addSourceFile(SourceType.TEMPLATE, model, xform -> xform.rotationY(modelRotY))
                    .transform(xform -> xform.rotationY(TemplateUtils.getHorizontalQuadrant(state, false)).mirrorY(top));
        });
    }

    private static GeometryTemplateSpec halfStairs() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_HALF_STAIRS, (state, builder) -> {
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);
            builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.HALF_STAIRS_LEFT, xform -> xform.mirrorX(right))
                    .transform(xform -> xform.rotationY(TemplateUtils.getHorizontalQuadrant(state, false)).mirrorY(top));
        });
    }

    private static GeometryTemplateSpec verticalStairs() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS, (state, builder) -> {
            StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
            Identifier model = switch (type) {
                case VERTICAL -> TemplateIds.STAIRS_STRAIGHT;
                case BOTTOM_BOTH, TOP_BOTH -> TemplateIds.THREEWAY_CORNER_PILLAR;
                default -> TemplateIds.STAIRS_OUTER;
            };
            UnaryOperator<TemplateTransformBuilder> transform = xform -> switch (type) {
                case VERTICAL, BOTTOM_FWD -> xform.rotationZ(Quadrant.R90);
                case TOP_FWD -> xform.rotationY(Quadrant.R90).rotationZ(Quadrant.R90);
                case TOP_CCW -> xform.rotationX(Quadrant.R270).rotationZ(Quadrant.R270);
                case BOTTOM_CCW -> xform.rotationX(Quadrant.R270);
                case TOP_BOTH -> xform;
                case BOTTOM_BOTH -> xform.mirrorY(true);
            };
            builder.addSourceFile(SourceType.TEMPLATE, model, transform).transform(TemplateUtils.applyHorizontalRotation(state, false));
        });
    }

    private static GeometryTemplateSpec verticalHalfStairs() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.HALF_STAIRS_LEFT, xform -> xform.rotationZ(Quadrant.R90))
                        .transform(xform ->
                                xform.rotationY(TemplateUtils.getHorizontalQuadrant(state, false))
                                        .mirrorY(!state.getValue(FramedProperties.TOP))
                        )
        );
    }

    private static GeometryTemplateSpec fence() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_FENCE, (state, builder) -> {
            builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.POST).solidNoCamoModel(true);
            if (state.getValue(BlockStateProperties.NORTH)) {
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.FENCE_ARM);
            }
            if (state.getValue(BlockStateProperties.EAST)) {
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.FENCE_ARM, rot -> rot.rotationY(Quadrant.R90));
            }
            if (state.getValue(BlockStateProperties.SOUTH)) {
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.FENCE_ARM, rot -> rot.rotationY(Quadrant.R180));
            }
            if (state.getValue(BlockStateProperties.WEST)) {
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.FENCE_ARM, rot -> rot.rotationY(Quadrant.R270));
            }
        });
    }

    private static GeometryTemplateSpec fenceGate() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_FENCE_GATE, (state, builder) -> {
            boolean open = state.getValue(BlockStateProperties.OPEN);
            boolean inWall = state.getValue(BlockStateProperties.IN_WALL);
            Identifier model;
            if (inWall) {
                model = open ? TemplateIds.FENCE_GATE_IN_WALL_OPEN : TemplateIds.FENCE_GATE_IN_WALL;
            } else {
                model = open ? TemplateIds.FENCE_GATE_OPEN : TemplateIds.FENCE_GATE;
            }
            builder.addSourceFile(SourceType.TEMPLATE, model).transform(TemplateUtils.applyHorizontalRotation(state, false)).solidNoCamoModel(true);
        });
    }

    private static GeometryTemplateSpec door(Holder<Block> block, boolean iron) {
        return GeometryTemplateSpec.create(block, (state, builder) ->
                // Open state is "hidden" by the state merger
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.DOOR)
                        .transform(TemplateUtils.applyHorizontalRotation(state, true))
                        .useBaseModel(iron)
        );
    }

    private static GeometryTemplateSpec trapdoor(Holder<Block> block, boolean iron) {
        return GeometryTemplateSpec.create(block, (state, builder) -> {
            boolean open = state.getValue(BlockStateProperties.OPEN);
            boolean rotTex = state.getValue(PropertyHolder.ROTATE_TEXTURE);
            Identifier model;
            if (open && !rotTex) {
                model = TemplateIds.TRAPDOOR_OPEN;
                builder.transform(TemplateUtils.applyHorizontalRotation(state, false));
            } else {
                model = switch (state.getValue(BlockStateProperties.HALF)) {
                    case TOP -> TemplateIds.TRAPDOOR_TOP;
                    case BOTTOM -> TemplateIds.TRAPDOOR_BOTTOM;
                };
            }
            builder.addSourceFile(SourceType.MODEL, model).useBaseModel(iron);
            if (open && rotTex) {
                builder.postModifiers(TrapdoorPostModifierProvider.get(state));
            }
        });
    }

    private static GeometryTemplateSpec pressurePlate(Holder<Block> block, boolean weighted) {
        TemplateOverlayProvider.Factory overlay = MarkedPressurePlateOverlayProvider.factory(block);
        return GeometryTemplateSpec.create(block, (state, builder) -> {
            boolean down = weighted ? (state.getValue(BlockStateProperties.POWER) > 0) : state.getValue(BlockStateProperties.POWERED);
            builder.addSourceFile(SourceType.MODEL, down ? TemplateIds.PRESSURE_PLATE_DOWN : TemplateIds.PRESSURE_PLATE_UP);
            if (overlay != null) {
                builder.overlay(overlay);
            }
            builder.useBaseModel(!state.is(FBContent.BLOCK_FRAMED_PRESSURE_PLATE));
        });
    }

    private static GeometryTemplateSpec ladder() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_LADDER, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.LADDER)
                        .transform(TemplateUtils.applyHorizontalRotation(state, false))
                        .solidNoCamoModel(true)
        );
    }

    private static GeometryTemplateSpec button(Holder<Block> block, boolean large, boolean stone) {
        return GeometryTemplateSpec.create(block, (state, builder) -> {
            boolean down = state.getValue(BlockStateProperties.POWERED);
            if (large) {
                builder.addSourceFile(SourceType.TEMPLATE, down ? TemplateIds.LARGE_BUTTON_PRESSED : TemplateIds.LARGE_BUTTON);
            } else if (down) {
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.BUTTON_PRESSED);
            } else {
                builder.addSourceFile(SourceType.MODEL, TemplateIds.BUTTON);
            }
            AttachFace attachFace = state.getValue(BlockStateProperties.ATTACH_FACE);
            builder.transform(xform ->
                    xform.rotationX(switch (attachFace) {
                        case FLOOR -> Quadrant.R0;
                        case WALL -> Quadrant.R90;
                        case CEILING -> Quadrant.R180;
                    })
                    .rotationY(TemplateUtils.getHorizontalQuadrant(state, FramedProperties.FACING_HOR, attachFace == AttachFace.CEILING))
            );
            if (stone) {
                builder.useBaseModel(true).overlay(StoneButtonOverlayProvider.factory(state, large));
            }
        });
    }

    private static GeometryTemplateSpec wallSign() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_WALL_SIGN, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.WALL_SIGN)
                        .transform(TemplateUtils.applyHorizontalRotation(state, false))
                        .solidNoCamoModel(true)
        );
    }

    private static GeometryTemplateSpec partialBoard(Holder<Block> block, Identifier model) {
        return GeometryTemplateSpec.create(block, (state, builder) -> {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            Quadrant modelRotY = switch (cmpDir.orientation()) {
                case DOWN -> Quadrant.R0;
                case UP -> Quadrant.R180;
                case NORTH -> switch (cmpDir.direction()) {
                    case DOWN -> Quadrant.R0;
                    case UP -> Quadrant.R180;
                    case WEST -> Quadrant.R270;
                    case EAST -> Quadrant.R90;
                    default -> throw new AssertionError();
                };
                case SOUTH -> switch (cmpDir.direction()) {
                    case DOWN -> Quadrant.R180;
                    case UP -> Quadrant.R0;
                    case WEST -> Quadrant.R90;
                    case EAST -> Quadrant.R270;
                    default -> throw new AssertionError();
                };
                case WEST -> cmpDir.direction() == Direction.NORTH ? Quadrant.R90 : Quadrant.R270;
                case EAST -> cmpDir.direction() == Direction.NORTH ? Quadrant.R270 : Quadrant.R90;
            };
            Quadrant rootRotX = switch (cmpDir.direction()) {
                case DOWN -> Quadrant.R0;
                case UP -> Quadrant.R180;
                default -> Quadrant.R90;
            };
            Quadrant rootRotY = switch (cmpDir.direction()) {
                case NORTH -> Quadrant.R180;
                case WEST -> Quadrant.R90;
                case EAST -> Quadrant.R270;
                default -> Quadrant.R0;
            };
            builder.addSourceFile(SourceType.TEMPLATE, model, xform -> xform.rotationY(modelRotY))
                    .transform(xform -> xform.rotationX(rootRotX).rotationY(rootRotY));
        });
    }

    private static GeometryTemplateSpec cornerStrip() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CORNER_STRIP, (state, builder) -> {
            Quadrant rotZ = switch (state.getValue(PropertyHolder.SLOPE_TYPE)) {
                case BOTTOM -> Quadrant.R0;
                case HORIZONTAL -> Quadrant.R90;
                case TOP -> Quadrant.R180;
            };
            builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.CORNER_STRIP, xform -> xform.rotationZ(rotZ))
                    .transform(TemplateUtils.applyHorizontalRotation(state, false));
        });
    }

    private static GeometryTemplateSpec lattice(boolean thick) {
        Holder<Block> block = thick ? FBContent.BLOCK_FRAMED_THICK_LATTICE : FBContent.BLOCK_FRAMED_LATTICE;
        return GeometryTemplateSpec.create(block, (state, builder) -> {
            boolean xAxis = state.getValue(FramedProperties.X_AXIS);
            boolean yAxis = state.getValue(FramedProperties.Y_AXIS);
            boolean zAxis = state.getValue(FramedProperties.Z_AXIS);
            if ((xAxis && !yAxis && !zAxis) || (!xAxis && yAxis && !zAxis) || (!xAxis && !yAxis && zAxis)) {
                builder.addSourceFile(SourceType.TEMPLATE, thick ? TemplateIds.PILLAR : TemplateIds.POST);
                builder.transform(xform -> {
                    if (xAxis) {
                        xform.rotationZ(Quadrant.R90);
                    } else if (zAxis) {
                        xform.rotationX(Quadrant.R90);
                    }
                    return xform;
                });
            } else {
                Identifier coreModel = thick ? TemplateIds.LATTICE_CORE_THICK : TemplateIds.LATTICE_CORE;
                if (!xAxis && !yAxis /*&& !zAxis*/) {
                    builder.addSourceFile(SourceType.TEMPLATE, coreModel, rot -> rot.rotationZ(Quadrant.R90));
                    builder.addSourceFile(SourceType.TEMPLATE, coreModel);
                    builder.addSourceFile(SourceType.TEMPLATE, coreModel, rot -> rot.rotationX(Quadrant.R90));
                } else {
                    Identifier armModel = thick ? TemplateIds.LATTICE_ARM_THICK : TemplateIds.LATTICE_ARM;
                    if (xAxis) {
                        builder.addSourceFile(SourceType.TEMPLATE, armModel, rot -> rot.rotationZ(Quadrant.R90));
                    } else {
                        builder.addSourceFile(SourceType.TEMPLATE, coreModel, rot -> rot.rotationZ(Quadrant.R90));
                    }
                    if (yAxis) {
                        builder.addSourceFile(SourceType.TEMPLATE, armModel);
                    } else {
                        builder.addSourceFile(SourceType.TEMPLATE, coreModel);
                    }
                    if (zAxis) {
                        builder.addSourceFile(SourceType.TEMPLATE, armModel, rot -> rot.rotationX(Quadrant.R90));
                    } else {
                        builder.addSourceFile(SourceType.TEMPLATE, coreModel, rot -> rot.rotationX(Quadrant.R90));
                    }
                }
            }
            builder.solidNoCamoModel(true);
        });
    }

    private static GeometryTemplateSpec post() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_POST, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.POST)
                        .transform(TemplateUtils.applyAxisRotation(state))
                        .solidNoCamoModel(true)
        );
    }

    private static GeometryTemplateSpec bookshelf() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_BOOKSHELF, (_, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.BOOKSHELF)
                        .appendBaseModel(false, false, Blocks.BOOKSHELF.defaultBlockState())
        );
    }

    private static GeometryTemplateSpec chiseledBookshelf() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.CHISELED_BOOKSHELF)
                        .transform(TemplateUtils.applyHorizontalRotation(state, false))
                        .appendBaseModel(false, false, Blocks.CHISELED_BOOKSHELF.defaultBlockState())
        );
    }

    private static GeometryTemplateSpec masonryCornerSegment() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT, (state, builder) ->
                builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.MASONRY_CORNER_SEGMENT)
                        .transform(xform ->
                                xform.rotationY(TemplateUtils.getHorizontalQuadrant(state, false))
                                        .mirrorY(state.getValue(FramedProperties.TOP))
                )
        );
    }

    private static GeometryTemplateSpec checkeredCubeSegment() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT, (state, builder) -> {
            builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.CHECKERED_CUBE_SEGMENT);
            if (state.getValue(PropertyHolder.SECOND)) {
                builder.transform(xform -> xform.rotationY(Quadrant.R90));
            }
        });
    }

    private static GeometryTemplateSpec checkeredSlabSegment() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT, (state, builder) -> {
            boolean top = state.getValue(FramedProperties.TOP);
            boolean second = state.getValue(PropertyHolder.SECOND);
            builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.CHECKERED_SLAB_SEGMENT);
            builder.transform(xform ->
                    xform.rotationY(second ^ top ? Quadrant.R90 : Quadrant.R0)
                            .mirrorY(top)
            );
        });
    }

    private static GeometryTemplateSpec checkeredPanelSegment() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT, (state, builder) -> {
            boolean second = state.getValue(PropertyHolder.SECOND);
            boolean x = DirUtils.isX(state.getValue(FramedProperties.FACING_HOR));
            builder.addSourceFile(SourceType.TEMPLATE, TemplateIds.CHECKERED_PANEL_SEGMENT, rot -> rot.rotationZ(second ? Quadrant.R90 : Quadrant.R0).mirrorX(x))
                    .transform(TemplateUtils.applyHorizontalRotation(state, false));
        });
    }

    private static GeometryTemplateSpec cornerTube() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_CORNER_TUBE, (state, builder) -> {
            CornerTubeOrientation orientation = state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
            Identifier model = state.getValue(PropertyHolder.THICK) ? TemplateIds.CORNER_TUBE_THICK : TemplateIds.CORNER_TUBE;
            Direction yRotDir = DirUtils.isY(orientation.getPrimaryDir()) ? orientation.getSecondaryDir() : orientation.getPrimaryDir();
            builder.addSourceFile(SourceType.TEMPLATE, model, rot -> rot.rotationZ(switch (orientation.getPrimaryDir()) {
                        case UP -> Quadrant.R0;
                        case DOWN -> Quadrant.R180;
                        default -> Quadrant.R90;
                    }))
                    .transform(xform -> xform.rotationY(TemplateUtils.getHorizontalQuadrant(yRotDir)));
        });
    }

    private static GeometryTemplateSpec hopper() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_HOPPER, (state, builder) -> {
            Direction facing = state.getValue(BlockStateProperties.FACING_HOPPER);
            builder.addSourceFile(SourceType.TEMPLATE, facing == Direction.DOWN ? TemplateIds.HOPPER : TemplateIds.HOPPER_SIDE)
                    .transform(TemplateUtils.applyHorizontalRotation(facing))
                    .solidNoCamoModel(true);
        });
    }

    private static GeometryTemplateSpec layeredCube() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_LAYERED_CUBE, (state, builder) -> {
            int layers = state.getValue(BlockStateProperties.LAYERS);
            Identifier model;
            if (layers == 8) {
                model = TemplateIds.CUBE;
            } else {
                model = TemplateIds.SNOW_LAYERS[layers - 1];
            }
            Direction facing = state.getValue(BlockStateProperties.FACING);
            builder.addSourceFile(SourceType.MODEL, model).useBaseModel(true).transform(xform ->
                    xform.rotationX(switch (facing) {
                        case DOWN -> Quadrant.R180;
                        case UP -> Quadrant.R0;
                        default -> Quadrant.R90;
                    })
                    .rotationY(TemplateUtils.getHorizontalQuadrant(facing))
            );
        });
    }

    private static GeometryTemplateSpec shelf() {
        return GeometryTemplateSpec.create(FBContent.BLOCK_FRAMED_SHELF, (state, builder) ->
                builder.addSourceFile(SourceType.MODEL, TemplateIds.SHELF_BODY)
                        .addSourceFile(SourceType.MODEL, TemplateIds.SHELF_UNPOWERED)
                        .transform(TemplateUtils.applyHorizontalRotation(state, false))
                        .overlay(ShelfOverlayProvider.factory(state))
                        .solidNoCamoModel(true)
        );
    }

    private TemplateSpecs() { }
}
