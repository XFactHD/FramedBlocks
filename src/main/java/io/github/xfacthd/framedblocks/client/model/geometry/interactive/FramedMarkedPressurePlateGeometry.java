package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedMarkedPressurePlateGeometry extends FramedPressurePlateGeometry {
    private static final Material STONE_FRAME_LOCATION = new Material(Utils.id("block/stone_plate_frame"));
    private static final Material OBSIDIAN_FRAME_LOCATION = new Material(Utils.id("block/obsidian_plate_frame"));
    private static final Material GOLD_FRAME_LOCATION = new Material(Utils.id("block/gold_plate_frame"));
    private static final Material IRON_FRAME_LOCATION = new Material(Utils.id("block/iron_plate_frame"));
    private static final @Nullable Direction[] OVERLAY_CULL_FACES = { Direction.DOWN, null };

    private final Material.Baked frameMaterial;
    private final BlockState frameShaderState;

    private FramedMarkedPressurePlateGeometry(Material.Baked frameMaterial, BlockState frameShaderState, boolean powered) {
        super(powered, true);
        this.frameMaterial = frameMaterial;
        this.frameShaderState = frameShaderState;
    }

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        return !blockData.getCamoContent().isEmpty();
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData) {
        generator.generate(OVERLAY_CULL_FACES, frameMaterial, DirUtils::isY, frameShaderState);
    }

    public static FramedPressurePlateGeometry stone(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
            return new FramedPressurePlateGeometry(powered, true);
        }

        Material.Baked frame = ctx.materialLookup().getMaterial(STONE_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, Blocks.STONE.defaultBlockState(), powered);
    }

    public static FramedPressurePlateGeometry obsidian(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
            return new FramedPressurePlateGeometry(powered, true);
        }

        Material.Baked frame = ctx.materialLookup().getMaterial(OBSIDIAN_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, Blocks.OBSIDIAN.defaultBlockState(), powered);
    }

    public static FramedPressurePlateGeometry gold(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
            return new FramedPressurePlateGeometry(powered, true);
        }

        Material.Baked frame = ctx.materialLookup().getMaterial(GOLD_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, Blocks.GOLD_BLOCK.defaultBlockState(), powered);
    }

    public static FramedPressurePlateGeometry iron(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
            return new FramedPressurePlateGeometry(powered, true);
        }

        Material.Baked frame = ctx.materialLookup().getMaterial(IRON_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, Blocks.IRON_BLOCK.defaultBlockState(), powered);
    }
}
