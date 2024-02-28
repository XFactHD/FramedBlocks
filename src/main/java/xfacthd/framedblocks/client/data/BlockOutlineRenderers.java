package xfacthd.framedblocks.client.data;

import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.client.data.outline.*;
import xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import xfacthd.framedblocks.common.data.BlockType;

public final class BlockOutlineRenderers
{
    public static void register()
    {
        register(BlockType.FRAMED_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_HALF_SLOPE, new HalfSlopeOutlineRenderer());
        register(BlockType.FRAMED_VERTICAL_HALF_SLOPE, new VerticalHalfSlopeOutlineRenderer());
        register(BlockType.FRAMED_DIVIDED_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_CORNER_SLOPE, new CornerSlopeOutlineRenderer());
        register(BlockType.FRAMED_INNER_CORNER_SLOPE, new InnerCornerSlopeOutlineRenderer());
        register(BlockType.FRAMED_PRISM_CORNER, new PrismCornerOutlineRenderer());
        register(BlockType.FRAMED_INNER_PRISM_CORNER, new InnerPrismCornerOutlineRenderer());
        register(BlockType.FRAMED_THREEWAY_CORNER, new ThreewayCornerOutlineRenderer());
        register(BlockType.FRAMED_INNER_THREEWAY_CORNER, new InnerThreewayCornerOutlineRenderer());
        register(BlockType.FRAMED_SLOPE_EDGE, new SlopeEdgeOutlineRenderer());
        register(BlockType.FRAMED_ELEVATED_SLOPE_EDGE, ElevatedSlopeEdgeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_STACKED_SLOPE_EDGE, ElevatedSlopeEdgeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_SLOPED_STAIRS, new SlopedStairsOutlineRenderer());
        register(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS, new VerticalSlopedStairsOutlineRenderer());
        register(BlockType.FRAMED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_POWERED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_DETECTOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_FANCY_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        register(BlockType.FRAMED_PRISM, new PrismOutlineRenderer());
        register(BlockType.FRAMED_ELEVATED_INNER_PRISM, new ElevatedInnerPrismOutlineRenderer());
        register(BlockType.FRAMED_SLOPED_PRISM, new SlopedPrismOutlineRenderer());
        register(BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM, new ElevatedInnerSlopedPrismOutlineRenderer());
        register(BlockType.FRAMED_SLOPE_SLAB, new SlopeSlabOutlineRenderer());
        register(BlockType.FRAMED_ELEVATED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        register(BlockType.FRAMED_COMPOUND_SLOPE_SLAB, InverseDoubleSlopeSlabOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, InverseDoubleSlopeSlabOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_STACKED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        register(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER, new FlatSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, new FlatInnerSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER, new FlatInverseSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        register(BlockType.FRAMED_SLOPE_PANEL, new SlopePanelOutlineRenderer());
        register(BlockType.FRAMED_EXTENDED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_COMPOUND_SLOPE_PANEL, InverseDoubleSlopePanelOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, InverseDoubleSlopePanelOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_STACKED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER, new FlatSlopePanelCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, new FlatInnerSlopePanelCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER, new FlatInverseDoubleSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        register(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        register(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL, new SmallCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W, new SmallCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL, new LargeCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, new LargeCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, new SmallInnerCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, new SmallInnerCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, new LargeInnerCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, new LargeInnerCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL, new InverseDoubleCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W, new InverseDoubleCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelOutlineRenderer());
        register(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallOutlineRenderer());
        register(BlockType.FRAMED_PYRAMID, new PyramidOutlineRenderer(false));
        register(BlockType.FRAMED_PYRAMID_SLAB, new PyramidOutlineRenderer(true));
        register(BlockType.FRAMED_ITEM_FRAME, NoopOutlineRenderer.INSTANCE);
        register(BlockType.FRAMED_GLOWING_ITEM_FRAME, NoopOutlineRenderer.INSTANCE);
    }

    private static void register(BlockType type, OutlineRenderer renderer)
    {
        BlockOutlineRenderer.registerOutlineRender(type, renderer);
    }



    private BlockOutlineRenderers() { }
}
