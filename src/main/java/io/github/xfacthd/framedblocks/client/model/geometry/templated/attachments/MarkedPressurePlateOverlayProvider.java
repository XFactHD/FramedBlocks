package io.github.xfacthd.framedblocks.client.model.geometry.templated.attachments;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record MarkedPressurePlateOverlayProvider(Material.Baked frameMaterial, BlockState frameShaderState) implements TemplateOverlayProvider {
    private static final Material STONE_FRAME_LOCATION = new Material(Utils.id("block/stone_plate_frame"));
    private static final Material OBSIDIAN_FRAME_LOCATION = new Material(Utils.id("block/obsidian_plate_frame"));
    private static final Material GOLD_FRAME_LOCATION = new Material(Utils.id("block/gold_plate_frame"));
    private static final Material IRON_FRAME_LOCATION = new Material(Utils.id("block/iron_plate_frame"));
    private static final @Nullable Direction[] OVERLAY_CULL_FACES = { Direction.DOWN, null };

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData) {
        return !blockData.getCamoContent().isEmpty();
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand) {
        generator.generate(OVERLAY_CULL_FACES, frameMaterial, DirUtils::isY, frameShaderState);
    }

    public static @Nullable Factory factory(Holder<Block> block) {
        BlockType type = (BlockType) ((IFramedBlock) block.value()).getBlockType();
        if (type == BlockType.FRAMED_PRESSURE_PLATE) {
            return null;
        }
        Material frameMaterial = switch (type) {
            case FRAMED_STONE_PRESSURE_PLATE -> STONE_FRAME_LOCATION;
            case FRAMED_OBSIDIAN_PRESSURE_PLATE -> OBSIDIAN_FRAME_LOCATION;
            case FRAMED_GOLD_PRESSURE_PLATE -> GOLD_FRAME_LOCATION;
            case FRAMED_IRON_PRESSURE_PLATE -> IRON_FRAME_LOCATION;
            default -> throw new IllegalArgumentException("Invalid block: " + block);
        };
        BlockState frameShaderState = switch (type) {
            case FRAMED_STONE_PRESSURE_PLATE -> Blocks.STONE.defaultBlockState();
            case FRAMED_OBSIDIAN_PRESSURE_PLATE -> Blocks.OBSIDIAN.defaultBlockState();
            case FRAMED_GOLD_PRESSURE_PLATE -> Blocks.GOLD_BLOCK.defaultBlockState();
            case FRAMED_IRON_PRESSURE_PLATE -> Blocks.IRON_BLOCK.defaultBlockState();
            default -> throw new IllegalArgumentException("Invalid block: " + block);
        };
        return ctx -> {
            if (!ClientConfig.VIEW.showButtonPlateOverlay()) {
                return null;
            }
            return new MarkedPressurePlateOverlayProvider(ctx.materialLookup().getMaterial(frameMaterial), frameShaderState);
        };
    }
}
