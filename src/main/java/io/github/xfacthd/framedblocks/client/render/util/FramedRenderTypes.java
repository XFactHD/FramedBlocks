package io.github.xfacthd.framedblocks.client.render.util;

import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.fml.ModList;

public final class FramedRenderTypes {
    private static final boolean IRIS_LOADED = ModList.get().isLoaded("iris");
    public static final RenderType DEBUG_QUADS_DEPTH = RenderType.create(
            "debug_quads_depth",
            RenderSetup.builder(FramedRenderPipelines.DEBUG_QUADS_DEPTH)
                    .sortOnUpload()
                    .createRenderSetup()
    );
    private static final RenderType BLOCK_ENTITY_SOLID_NO_SHADE = RenderType.create(
            "entity_solid_no_shade",
            RenderSetup.builder(FramedRenderPipelines.ENTITY_SOLID_NO_SHADE)
                    .withTexture("Sampler0", ClientUtils.BLOCK_ATLAS)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
    );
    private static final RenderType BLOCK_ENTITY_CUTOUT_CULL_NO_SHADE = RenderType.create(
            "entity_cutout_cull_no_shade",
            RenderSetup.builder(FramedRenderPipelines.ENTITY_CUTOUT_CULL_NO_SHADE)
                    .withTexture("Sampler0", ClientUtils.BLOCK_ATLAS)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
    );
    public static final RenderType BLOCK_ENTITY_TRANSLUCENT_CULL_ITEM_TARGET_NO_SHADE = RenderType.create(
            "entity_translucent_cull_item_target_no_shade",
            RenderSetup.builder(FramedRenderPipelines.ENTITY_TRANSLUCENT_CULL_NO_SHADE)
                    .withTexture("Sampler0", ClientUtils.BLOCK_ATLAS)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
    );

    public static RenderType getEntityRenderType(ChunkSectionLayer chunkLayer, boolean noShade) {
        if (!noShade || IRIS_LOADED) {
            return ClientUtils.getEntityRenderType(chunkLayer);
        }
        return switch (chunkLayer) {
            case SOLID -> BLOCK_ENTITY_SOLID_NO_SHADE;
            case CUTOUT -> BLOCK_ENTITY_CUTOUT_CULL_NO_SHADE;
            case TRANSLUCENT -> BLOCK_ENTITY_TRANSLUCENT_CULL_ITEM_TARGET_NO_SHADE;
        };
    }

    private FramedRenderTypes() { }
}
