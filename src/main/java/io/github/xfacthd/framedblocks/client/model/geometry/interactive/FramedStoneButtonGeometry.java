package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jspecify.annotations.Nullable;

public class FramedStoneButtonGeometry extends FramedButtonGeometry {
    private static final Material FRAME_LOCATION_FRONT = new Material(Utils.id("block/stone_button_frame_front"));
    private static final Material FRAME_LOCATION_NARROW = new Material(Utils.id("block/stone_button_frame_narrow"));
    private static final Material FRAME_LOCATION_WIDE = new Material(Utils.id("block/stone_button_frame_wide"));

    private final Material.Baked frameMaterialFront;
    private final Material.Baked frameMaterialNarrow;
    private final Material.Baked frameMaterialWide;
    private final @Nullable Direction[] overlayCullFaces;
    private final OverlayPartGenerator.MaterialGetter overlayMaterialGetter;

    private FramedStoneButtonGeometry(GeometryFactory.Context ctx) {
        super(ctx);
        this.frameMaterialFront = ctx.materialLookup().getMaterial(FRAME_LOCATION_FRONT);
        this.frameMaterialNarrow = ctx.materialLookup().getMaterial(FRAME_LOCATION_NARROW);
        this.frameMaterialWide = ctx.materialLookup().getMaterial(FRAME_LOCATION_WIDE);
        this.overlayCullFaces = new @Nullable Direction[] { facing.getOpposite(), null };
        Direction.Axis wideAxis = face == AttachFace.WALL ? Direction.Axis.Y : dir.getAxis();
        this.overlayMaterialGetter = dir -> {
            if (dir.getAxis() == facing.getAxis()) return frameMaterialFront;
            if (dir.getAxis() == wideAxis) return frameMaterialWide;
            return frameMaterialNarrow;
        };
    }

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        return !blockData.getCamoContent().isEmpty();
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData) {
        generator.generate(overlayCullFaces, overlayMaterialGetter, frameMaterialFront, Blocks.STONE.defaultBlockState());
    }

    @Override
    public boolean useBaseModel() {
        return true;
    }

    public static FramedButtonGeometry create(GeometryFactory.Context ctx) {
        if (ClientConfig.VIEW.showButtonPlateOverlay()) {
            return new FramedStoneButtonGeometry(ctx);
        }
        return new FramedButtonGeometry(ctx);
    }
}
