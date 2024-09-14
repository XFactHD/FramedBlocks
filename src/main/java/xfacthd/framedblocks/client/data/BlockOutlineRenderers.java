package xfacthd.framedblocks.client.data;

import xfacthd.framedblocks.api.render.RegisterOutlineRenderersEvent;
import xfacthd.framedblocks.client.data.outline.*;
import xfacthd.framedblocks.common.data.BlockType;

public final class BlockOutlineRenderers
{
    public static void onRegisterOutlineRenderers(RegisterOutlineRenderersEvent event)
    {
        event.register(BlockType.FRAMED_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_HALF_SLOPE, new HalfSlopeOutlineRenderer());
        event.register(BlockType.FRAMED_VERTICAL_HALF_SLOPE, new VerticalHalfSlopeOutlineRenderer());
        event.register(BlockType.FRAMED_DIVIDED_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_CORNER_SLOPE, new CornerSlopeOutlineRenderer());
        event.register(BlockType.FRAMED_INNER_CORNER_SLOPE, new InnerCornerSlopeOutlineRenderer());
        event.register(BlockType.FRAMED_PRISM_CORNER, new PrismCornerOutlineRenderer());
        event.register(BlockType.FRAMED_INNER_PRISM_CORNER, new InnerPrismCornerOutlineRenderer());
        event.register(BlockType.FRAMED_THREEWAY_CORNER, new ThreewayCornerOutlineRenderer());
        event.register(BlockType.FRAMED_INNER_THREEWAY_CORNER, new InnerThreewayCornerOutlineRenderer());
        event.register(BlockType.FRAMED_SLOPE_EDGE, new SlopeEdgeOutlineRenderer());
        event.register(BlockType.FRAMED_ELEVATED_SLOPE_EDGE, ElevatedSlopeEdgeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_STACKED_SLOPE_EDGE, ElevatedSlopeEdgeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_CORNER_SLOPE_EDGE, new CornerSlopeEdgeOutlineRenderer());
        event.register(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE, new InnerCornerSlopeEdgeOutlineRenderer());
        event.register(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE, ElevatedCornerSlopeEdgeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE, ElevatedInnerCornerSlopeEdgeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_STACKED_CORNER_SLOPE_EDGE, ElevatedCornerSlopeEdgeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE, ElevatedInnerCornerSlopeEdgeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_SLOPED_STAIRS, new SlopedStairsOutlineRenderer());
        event.register(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS, new VerticalSlopedStairsOutlineRenderer());
        event.register(BlockType.FRAMED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_POWERED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_DETECTOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        event.register(BlockType.FRAMED_PRISM, new PrismOutlineRenderer());
        event.register(BlockType.FRAMED_ELEVATED_INNER_PRISM, new ElevatedInnerPrismOutlineRenderer());
        event.register(BlockType.FRAMED_SLOPED_PRISM, new SlopedPrismOutlineRenderer());
        event.register(BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM, new ElevatedInnerSlopedPrismOutlineRenderer());
        event.register(BlockType.FRAMED_SLOPE_SLAB, new SlopeSlabOutlineRenderer());
        event.register(BlockType.FRAMED_ELEVATED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        event.register(BlockType.FRAMED_COMPOUND_SLOPE_SLAB, InverseDoubleSlopeSlabOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, InverseDoubleSlopeSlabOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_STACKED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER, new FlatSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, new FlatInnerSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER, new FlatInverseSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        event.register(BlockType.FRAMED_SLOPE_PANEL, new SlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_EXTENDED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_COMPOUND_SLOPE_PANEL, InverseDoubleSlopePanelOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, InverseDoubleSlopePanelOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_STACKED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER, new FlatSlopePanelCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, new FlatInnerSlopePanelCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER, new FlatInverseDoubleSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        event.register(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        event.register(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL, new SmallCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W, new SmallCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL, new LargeCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, new LargeCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, new SmallInnerCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, new SmallInnerCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, new LargeInnerCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, new LargeInnerCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL, new InverseDoubleCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W, new InverseDoubleCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelOutlineRenderer());
        event.register(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallOutlineRenderer());
        event.register(BlockType.FRAMED_PYRAMID, new PyramidOutlineRenderer(false));
        event.register(BlockType.FRAMED_PYRAMID_SLAB, new PyramidOutlineRenderer(true));
        event.register(BlockType.FRAMED_ITEM_FRAME, NoopOutlineRenderer.INSTANCE);
        event.register(BlockType.FRAMED_GLOWING_ITEM_FRAME, NoopOutlineRenderer.INSTANCE);
    }



    private BlockOutlineRenderers() { }
}
