package io.github.xfacthd.framedblocks.client.data;

import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.api.render.outline.RegisterOutlineRenderersEvent;
import io.github.xfacthd.framedblocks.client.data.outline.*;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;

public final class BlockOutlineRenderers {
    public static void onRegisterOutlineRenderers(RegisterOutlineRenderersEvent event) {
        for (BlockType type : BlockType.values()) {
            if (type.useModelBasedOutline()) {
                event.registerModelBased(FBContent.byType(type));
            }
        }

        event.register(BlockType.FRAMED_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_POWERED_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_DETECTOR_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, RailSlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        event.register(BlockType.FRAMED_ITEM_FRAME, OutlineRenderer.NO_OP);
        event.register(BlockType.FRAMED_GLOWING_ITEM_FRAME, OutlineRenderer.NO_OP);
    }

    private BlockOutlineRenderers() { }
}
