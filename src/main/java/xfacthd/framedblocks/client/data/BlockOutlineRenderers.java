package xfacthd.framedblocks.client.data;

import xfacthd.framedblocks.client.data.outline.*;
import xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import xfacthd.framedblocks.common.data.BlockType;

public final class BlockOutlineRenderers
{
    public static void register()
    {
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_HALF_SLOPE, new HalfSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_VERTICAL_HALF_SLOPE, new VerticalHalfSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_DIVIDED_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_CORNER_SLOPE, new CornerSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_CORNER_SLOPE, new InnerCornerSlopeOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM_CORNER, new PrismCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_PRISM_CORNER, new InnerPrismCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_THREEWAY_CORNER, new ThreewayCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_THREEWAY_CORNER, new InnerThreewayCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPED_STAIRS, new SlopedStairsOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS, new VerticalSlopedStairsOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_POWERED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_DETECTOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FANCY_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, SlopeOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PRISM, new PrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_PRISM, new InnerPrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPED_PRISM, new SlopedPrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INNER_SLOPED_PRISM, new InnerSlopedPrismOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE_SLAB, new SlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_ELEVATED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, new InverseDoubleSlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_STACKED_SLOPE_SLAB, new ElevatedSlopeSlabOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER, new FlatSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, new FlatInnerSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER, new FlatInverseSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SLOPE_PANEL, new SlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXTENDED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, new InverseDoubleSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_STACKED_SLOPE_PANEL, new ExtendedSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER, new FlatSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, new FlatInnerSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER, new FlatInverseDoubleSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL, new SmallCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W, new SmallCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL, new LargeCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, new LargeCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, new SmallInnerCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, new SmallInnerCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, new LargeInnerCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, new LargeInnerCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL, new InverseDoubleCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W, new InverseDoubleCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallOutlineRenderer());
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PYRAMID, new PyramidOutlineRenderer(false));
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_PYRAMID_SLAB, new PyramidOutlineRenderer(true));
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_ITEM_FRAME, NoopOutlineRenderer.INSTANCE);
        BlockOutlineRenderer.registerOutlineRender(BlockType.FRAMED_GLOWING_ITEM_FRAME, NoopOutlineRenderer.INSTANCE);
    }



    private BlockOutlineRenderers() { }
}
