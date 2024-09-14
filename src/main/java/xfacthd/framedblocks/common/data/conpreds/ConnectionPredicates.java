package xfacthd.framedblocks.common.data.conpreds;

import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.conpreds.door.*;
import xfacthd.framedblocks.common.data.conpreds.misc.*;
import xfacthd.framedblocks.common.data.conpreds.pane.*;
import xfacthd.framedblocks.common.data.conpreds.pillar.*;
import xfacthd.framedblocks.common.data.conpreds.prism.*;
import xfacthd.framedblocks.common.data.conpreds.slab.*;
import xfacthd.framedblocks.common.data.conpreds.slope.*;
import xfacthd.framedblocks.common.data.conpreds.slopeedge.*;
import xfacthd.framedblocks.common.data.conpreds.slopepanel.*;
import xfacthd.framedblocks.common.data.conpreds.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.conpreds.slopeslab.*;
import xfacthd.framedblocks.common.data.conpreds.stairs.*;
import xfacthd.framedblocks.common.util.BlockTypeMap;

public final class ConnectionPredicates extends BlockTypeMap<ConnectionPredicate>
{
    public static final ConnectionPredicates PREDICATES = new ConnectionPredicates();

    private ConnectionPredicates()
    {
        super(ConnectionPredicate.FALSE);
    }

    @Override
    protected void fill()
    {
        put(BlockType.FRAMED_CUBE, ConnectionPredicate.FULL_EDGE);
        put(BlockType.FRAMED_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_DOUBLE_SLOPE, new DoubleSlopeConnectionPredicate());
        put(BlockType.FRAMED_HALF_SLOPE, new HalfSlopeConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_HALF_SLOPE, new VerticalHalfSlopeConnectionPredicate());
        put(BlockType.FRAMED_DIVIDED_SLOPE, new DividedSlopeConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_HALF_SLOPE, new DoubleHalfSlopeConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, new VerticalDoubleHalfSlopeConnectionPredicate());
        put(BlockType.FRAMED_CORNER_SLOPE, new CornerSlopeConnectionPredicate());
        put(BlockType.FRAMED_INNER_CORNER_SLOPE, new InnerCornerSlopeConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_CORNER, new DoubleCornerConnectionPredicate());
        put(BlockType.FRAMED_PRISM_CORNER, new PrismCornerConnectionPredicate());
        put(BlockType.FRAMED_INNER_PRISM_CORNER, new InnerPrismCornerConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_PRISM_CORNER, DoubleThreewayCornerConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_THREEWAY_CORNER, new ThreewayCornerConnectionPredicate());
        put(BlockType.FRAMED_INNER_THREEWAY_CORNER, new InnerThreewayCornerConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_THREEWAY_CORNER, DoubleThreewayCornerConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_SLOPE_EDGE, new SlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_SLOPE_EDGE, new ElevatedSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE, new ElevatedDoubleSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_STACKED_SLOPE_EDGE, new StackedSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_CORNER_SLOPE_EDGE, new CornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE, new InnerCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE, new ElevatedCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE, new ElevatedInnerCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_ELEV_DOUBLE_CORNER_SLOPE_EDGE, new ElevatedDoubleCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_ELEV_DOUBLE_INNER_CORNER_SLOPE_EDGE, new ElevatedDoubleInnerCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_STACKED_CORNER_SLOPE_EDGE, new StackedCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE, new StackedInnerCornerSlopeEdgeConnectionPredicate());
        put(BlockType.FRAMED_SLAB, new SlabConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_SLAB, DoubleSlabConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_ADJ_DOUBLE_SLAB, DoubleSlabConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_SLAB, DoubleSlabConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_DIVIDED_SLAB, new DividedSlabConnectionPredicate());
        put(BlockType.FRAMED_SLAB_EDGE, new SlabEdgeConnectionPredicate());
        put(BlockType.FRAMED_SLAB_CORNER, new SlabCornerConnectionPredicate());
        put(BlockType.FRAMED_PANEL, new PanelConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_PANEL, DoublePanelConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_ADJ_DOUBLE_PANEL, DoublePanelConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_PANEL, DoublePanelConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL, new DividedPanelHorizontalConnectionPredicate());
        put(BlockType.FRAMED_DIVIDED_PANEL_VERTICAL, new DividedPanelVerticalConnectionPredicate());
        put(BlockType.FRAMED_CORNER_PILLAR, new CornerPillarConnectionPredicate());
        put(BlockType.FRAMED_STAIRS, new StairsConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_STAIRS, new DoubleStairsConnectionPredicate());
        put(BlockType.FRAMED_HALF_STAIRS, new HalfStairsConnectionPredicate());
        put(BlockType.FRAMED_DIVIDED_STAIRS, new DividedStairsConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_HALF_STAIRS, new DoubleHalfStairsConnectionPredicate());
        put(BlockType.FRAMED_SLICED_STAIRS_SLAB, new SlicedStairsSlabConnectionPredicate());
        put(BlockType.FRAMED_SLICED_STAIRS_PANEL, new SlicedStairsPanelConnectionPredicate());
        put(BlockType.FRAMED_SLOPED_STAIRS, new SlopedStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_STAIRS, new VerticalStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS, new VerticalDoubleStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_HALF_STAIRS, new VerticalHalfStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS, new VerticalDividedStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_DOUBLE_HALF_STAIRS, new VerticalDoubleHalfStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_SLICED_STAIRS, new VerticalSlicedStairsConnectionPredicate());
        put(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS, new VerticalSlopeStairsConnectionPredicate());
        put(BlockType.FRAMED_THREEWAY_CORNER_PILLAR, new ThreewayCornerPillarConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, new DoubleThreewayCornerPillarConnectionPredicate());
        put(BlockType.FRAMED_WALL, new WallConnectionPredicate());
        put(BlockType.FRAMED_FENCE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_FENCE_GATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_DOOR, DoorConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_IRON_DOOR, DoorConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_TRAPDOOR, TrapdoorConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_IRON_TRAPDOOR, TrapdoorConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_STONE_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_GOLD_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_IRON_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_LADDER, new LadderConnectionPredicate());
        put(BlockType.FRAMED_BUTTON, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_STONE_BUTTON, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_LARGE_BUTTON, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_LARGE_STONE_BUTTON, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_LEVER, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_SIGN, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WALL_SIGN, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_HANGING_SIGN, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WALL_HANGING_SIGN, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_TORCH, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_WALL_TORCH, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_SOUL_TORCH, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_SOUL_WALL_TORCH, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_REDSTONE_TORCH, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_REDSTONE_WALL_TORCH, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_FLOOR_BOARD, new FloorBoardConnectionPredicate());
        put(BlockType.FRAMED_WALL_BOARD, new WallBoardConnectionPredicate());
        put(BlockType.FRAMED_CORNER_STRIP, new CornerStripConnectionPredicate());
        put(BlockType.FRAMED_LATTICE_BLOCK, LatticeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_THICK_LATTICE, LatticeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_CHEST, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_SECRET_STORAGE, ConnectionPredicate.FULL_EDGE);
        put(BlockType.FRAMED_TANK, ConnectionPredicate.FULL_FACE);
        put(BlockType.FRAMED_BARS, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_PANE, PaneConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_HORIZONTAL_PANE, new HorizontalPaneConnectionPredicate());
        put(BlockType.FRAMED_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_POWERED_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_DETECTOR_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_FANCY_RAIL, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_FANCY_POWERED_RAIL, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_FANCY_DETECTOR_RAIL, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_FANCY_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, SlopeConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_FLOWER_POT, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_PILLAR, PillarConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_HALF_PILLAR, new HalfPillarConnectionPredicate());
        put(BlockType.FRAMED_POST, PillarConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_COLLAPSIBLE_BLOCK, new CollapsibleBlockConnectionPredicate());
        put(BlockType.FRAMED_COLLAPSIBLE_COPYCAT_BLOCK, new CollapsibleCopycatConnectionPredicate());
        put(BlockType.FRAMED_BOUNCY_CUBE, ConnectionPredicate.FULL_EDGE);
        put(BlockType.FRAMED_REDSTONE_BLOCK, ConnectionPredicate.FULL_EDGE);
        put(BlockType.FRAMED_PRISM, new PrismConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_INNER_PRISM, new ElevatedInnerPrismConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_INNER_DOUBLE_PRISM, new ElevatedInnerDoublePrismConnectionPredicate());
        put(BlockType.FRAMED_SLOPED_PRISM, new SlopedPrismConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM, new ElevatedInnerSlopedPrismConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM, new ElevatedInnerDoubleSlopedPrismConnectionPredicate());
        put(BlockType.FRAMED_SLOPE_SLAB, new SlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_SLOPE_SLAB, new ElevatedSlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_COMPOUND_SLOPE_SLAB, new CompoundSlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_SLOPE_SLAB, new DoubleSlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, new InverseDoubleSlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, new ElevatedDoubleSlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_STACKED_SLOPE_SLAB, new StackedSlopeSlabConnectionPredicate());
        put(BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER, new FlatSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, new FlatInnerSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER, new FlatElevatedSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER, new FlatElevatedInnerSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, new FlatDoubleSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER, new FlatInverseDoubleSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER, new FlatElevatedDoubleSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER, new FlatElevatedInnerDoubleSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, new FlatStackedSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, new FlatStackedInnerSlopeSlabCornerConnectionPredicate());
        put(BlockType.FRAMED_SLOPE_PANEL, new SlopePanelConnectionPredicate());
        put(BlockType.FRAMED_EXTENDED_SLOPE_PANEL, new ExtendedSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_COMPOUND_SLOPE_PANEL, new CompoundSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_DOUBLE_SLOPE_PANEL, new DoubleSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, new InverseDoubleSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, new ExtendedDoubleSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_STACKED_SLOPE_PANEL, new StackedSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER, new FlatSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, new FlatInnerSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER, new FlatExtendedSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER, new FlatExtendedInnerSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, new FlatDoubleSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER, new FlatInverseDoubleSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER, new FlatExtendedDoubleSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER, new FlatExtendedInnerDoubleSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, new FlatStackedSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, new FlatStackedInnerSlopePanelCornerConnectionPredicate());
        put(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL, new SmallCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W, new SmallCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL, new LargeCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W, new LargeCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, new SmallInnerCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, new SmallInnerCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, new LargeInnerCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W, new LargeInnerCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL, new ExtendedCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W, new ExtendedCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL, new ExtendedInnerCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W, new ExtendedInnerCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, new SmallDoubleCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W, new SmallDoubleCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, new LargeDoubleCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W, new LargeDoubleCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL, new InverseDoubleCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W, new InverseDoubleCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL, new ExtendedDoubleCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W, new ExtendedDoubleCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL, new ExtendedInnerDoubleCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W, new ExtendedInnerDoubleCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL, new StackedCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W, new StackedCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, new StackedInnerCornerSlopePanelConnectionPredicate());
        put(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W, new StackedInnerCornerSlopePanelWallConnectionPredicate());
        put(BlockType.FRAMED_GLOWING_CUBE, ConnectionPredicate.FULL_EDGE);
        put(BlockType.FRAMED_PYRAMID, ConnectionPredicate.FULL_FACE);
        put(BlockType.FRAMED_PYRAMID_SLAB, ConnectionPredicate.FULL_FACE);
        put(BlockType.FRAMED_TARGET, ConnectionPredicate.FULL_EDGE);
        put(BlockType.FRAMED_GATE, DoorConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_IRON_GATE, DoorConnectionPredicate.INSTANCE);
        put(BlockType.FRAMED_ITEM_FRAME, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_GLOWING_ITEM_FRAME, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_MINI_CUBE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_ONE_WAY_WINDOW, ConnectionPredicate.FULL_FACE);
        put(BlockType.FRAMED_BOOKSHELF, new BookshelfConnectionPredicate());
        put(BlockType.FRAMED_CHISELED_BOOKSHELF, new ChiseledBookshelfConnectionPredicate());
        put(BlockType.FRAMED_CENTERED_SLAB, new CenteredSlabConnectionPredicate());
        put(BlockType.FRAMED_CENTERED_PANEL, new CenteredPanelConnectionPredicate());
        put(BlockType.FRAMED_MASONRY_CORNER_SEGMENT, new MasonryCornerSegmentConnectionPredicate());
        put(BlockType.FRAMED_MASONRY_CORNER, new MasonryCornerConnectionPredicate());
        put(BlockType.FRAMED_CHECKERED_CUBE_SEGMENT, new CheckeredCubeSegmentConnectionPredicate());
        put(BlockType.FRAMED_CHECKERED_CUBE, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT, new CheckeredSlabSegmentConnectionPredicate());
        put(BlockType.FRAMED_CHECKERED_SLAB, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_CHECKERED_PANEL_SEGMENT, new CheckeredPanelSegmentConnectionPredicate());
        put(BlockType.FRAMED_CHECKERED_PANEL, ConnectionPredicate.FALSE);
        put(BlockType.FRAMED_TUBE, new TubeConnectionPredicate());
        put(BlockType.FRAMED_CHAIN, new ChainConnectionPredicate());
    }
}
