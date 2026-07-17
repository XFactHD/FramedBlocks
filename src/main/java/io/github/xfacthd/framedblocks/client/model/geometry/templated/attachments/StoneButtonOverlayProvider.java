package io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public record StoneButtonOverlayProvider(
        @Nullable Direction[] cullFaces,
        Material.Baked primaryFrameSprite,
        OverlayPartGenerator.MaterialGetter materialGetter
) implements TemplateOverlayProvider {
    private static final Material FRAME_LOCATION_SMALL_FRONT = new Material(Utils.id("block/stone_button_frame_front"));
    private static final Material FRAME_LOCATION_SMALL_NARROW = new Material(Utils.id("block/stone_button_frame_narrow"));
    private static final Material FRAME_LOCATION_SMALL_WIDE = new Material(Utils.id("block/stone_button_frame_wide"));
    private static final Material FRAME_LOCATION_LARGE_FRONT = new Material(Utils.id("block/large_stone_button_frame_front"));
    private static final Material FRAME_LOCATION_LARGE_SIDE = new Material(Utils.id("block/large_stone_button_frame_side"));
    private static final Factory[] SMALL_FACTORIES = makeSmallFactories();
    private static final Factory[] LARGE_FACTORIES = makeLargeFactories();

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData) {
        return !blockData.getCamoContent().isEmpty();
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand) {
        generator.generate(cullFaces, materialGetter, primaryFrameSprite, Blocks.STONE.defaultBlockState());
    }

    public static Factory factory(BlockState state, boolean large) {
        AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
        Direction facing = switch (face) {
            case FLOOR -> Direction.UP;
            case WALL -> Direction.DOWN;
            case CEILING -> state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        };
        if (large) {
            return LARGE_FACTORIES[facing.ordinal()];
        } else {
            return SMALL_FACTORIES[smallIndex(face, facing)];
        }
    }

    private static int smallIndex(AttachFace face, Direction facing) {
        return switch (face) {
            case FLOOR -> facing.getAxis().ordinal();
            case WALL -> facing.get2DDataValue() + 4;
            case CEILING -> facing.getAxis().ordinal() + 1;
        };
    }

    private static Factory[] makeSmallFactories() {
        Factory[] factories = new Factory[8];
        for (Direction.Axis axis : new Direction.Axis[] { Direction.Axis.X, Direction.Axis.Z }) {
            factories[smallIndex(AttachFace.FLOOR, axis.getPositive())] = makeSmallFactory(Direction.UP, axis);
            factories[smallIndex(AttachFace.CEILING, axis.getPositive())] = makeSmallFactory(Direction.DOWN, axis);
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            factories[smallIndex(AttachFace.WALL, dir)] = makeSmallFactory(dir, Direction.Axis.Y);
        }
        return factories;
    }

    private static Factory makeSmallFactory(Direction facing, Direction.Axis wideAxis) {
        return ctx -> {
            if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
                return null;
            }

            @Nullable Direction[] cullFaces = { facing.getOpposite(), null };
            Material.Baked matFront = ctx.materialLookup().getMaterial(FRAME_LOCATION_SMALL_FRONT);
            Material.Baked matNarrow = ctx.materialLookup().getMaterial(FRAME_LOCATION_SMALL_NARROW);
            Material.Baked matWide = ctx.materialLookup().getMaterial(FRAME_LOCATION_SMALL_WIDE);
            OverlayPartGenerator.MaterialGetter matGetter = dir -> {
                if (dir.getAxis() == facing.getAxis()) {
                    return matFront;
                }
                if (dir.getAxis() == wideAxis) {
                    return matWide;
                }
                return matNarrow;
            };
            return new StoneButtonOverlayProvider(cullFaces, matFront, matGetter);
        };
    }

    private static Factory[] makeLargeFactories() {
        Factory[] factories = new Factory[6];
        for (Direction facing : Direction.values()) {
            @Nullable Direction[] cullFaces = new @Nullable Direction[] { facing.getOpposite(), null };
            factories[facing.ordinal()] = ctx -> {
                if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
                    return null;
                }

                Material.Baked matFront = ctx.materialLookup().getMaterial(FRAME_LOCATION_LARGE_FRONT);
                Material.Baked matSide = ctx.materialLookup().getMaterial(FRAME_LOCATION_LARGE_SIDE);
                return new StoneButtonOverlayProvider(cullFaces, matFront, dir -> dir.getAxis() == facing.getAxis() ? matFront : matSide);
            };
        }
        return factories;
    }
}
