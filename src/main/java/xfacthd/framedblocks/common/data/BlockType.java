package xfacthd.framedblocks.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.skippreds.*;

import java.util.Locale;

@SuppressWarnings("SameParameterValue")
public enum BlockType implements IBlockType
{
    FRAMED_CUBE                                     ( true, false, false, false,  true,  true, false, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_SLOPE                                    ( true,  true, false,  true,  true,  true, false, false, FramedSlopeBlock.CTM_PREDICATE, SlopeSkipPredicate.INSTANCE, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE                             ( true,  true, false,  true,  true,  true, false, false, FramedCornerSlopeBlock.CTM_PREDICATE, new CornerSkipPredicate(), FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE                       ( true,  true, false,  true,  true,  true, false, false, FramedCornerSlopeBlock.CTM_PREDICATE_INNER, new InnerCornerSkipPredicate(), FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER                             ( true,  true, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER                       ( true,  true, false,  true,  true,  true, false, false, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER                          ( true,  true, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER                    ( true,  true, false,  true,  true,  true, false, false, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                                     ( true, false, false,  true,  true,  true, false, false, CtmPredicate.TOP, new SlabSkipPredicate(), FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE                                (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new SlabEdgeSkipPredicate(), FramedSlabEdgeBlock::generateShapes),
    FRAMED_SLAB_CORNER                              (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new SlabCornerSkipPredicate(), FramedSlabCornerBlock::generateShapes),
    FRAMED_PANEL                                    ( true, false, false,  true,  true,  true, false, false, CtmPredicate.HOR_DIR, new PanelSkipPredicate(), FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR                            (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new CornerPillarSkipPredicate(), FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS                                   ( true, false, false,  true,  true,  true, false,  true, FramedStairsBlock.CTM_PREDICATE, new StairsSkipPredicate()),
    FRAMED_WALL                                     (false, false, false,  true,  true, false, false,  true, CtmPredicate.FALSE, new WallSkipPredicate()),
    FRAMED_FENCE                                    (false, false, false,  true,  true, false, false,  true, CtmPredicate.FALSE, FramedFenceBlock.SKIP_PREDICATE),
    FRAMED_GATE                                     (false, false, false,  true,  true, false, false, false, CtmPredicate.FALSE, FramedFenceGateBlock.SKIP_PREDICATE),
    FRAMED_DOOR                                     ( true, false, false, false,  true, false, false, false, FramedDoorBlock.CTM_PREDICATE, new DoorSkipPredicate()),
    FRAMED_IRON_DOOR                                ( true, false, false, false,  true, false, false, false, FramedDoorBlock.CTM_PREDICATE, new DoorSkipPredicate()),
    FRAMED_TRAPDOOR                                 ( true, false, false,  true,  true, false, false, false, FramedTrapDoorBlock.CTM_PREDICATE, new TrapdoorSkipPredicate()),
    FRAMED_IRON_TRAPDOOR                            ( true, false, false,  true,  true, false, false, false, FramedTrapDoorBlock.CTM_PREDICATE, new TrapdoorSkipPredicate()),
    FRAMED_PRESSURE_PLATE                           (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_PRESSURE_PLATE             (false, false, false,  true, false, false, false, false),
    FRAMED_STONE_PRESSURE_PLATE                     (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE       (false, false, false,  true, false, false, false, false),
    FRAMED_OBSIDIAN_PRESSURE_PLATE                  (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE    (false, false, false,  true, false, false, false, false),
    FRAMED_IRON_PRESSURE_PLATE                      (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE        (false, false, false,  true, false, false, false, false),
    FRAMED_GOLD_PRESSURE_PLATE                      (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE        (false, false, false,  true, false, false, false, false),
    FRAMED_LADDER                                   (false, false, false,  true,  true, false, false, false),
    FRAMED_BUTTON                                   (false, false, false, false,  true, false, false, false),
    FRAMED_STONE_BUTTON                             (false, false, false, false,  true, false, false, false),
    FRAMED_LEVER                                    (false, false, false, false,  true, false, false, false),
    FRAMED_SIGN                                     (false, false,  true, false,  true, false, false, false),
    FRAMED_WALL_SIGN                                (false, false,  true, false, false, false, false, false, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB                              ( true, false,  true, false,  true,  true,  true, false, CtmPredicate.Y_AXIS, Shapes.block()),
    FRAMED_DOUBLE_PANEL                             ( true, false,  true, false,  true,  true,  true, false, FramedDoublePanelBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_DOUBLE_SLOPE                             ( true, false,  true, false,  true,  true,  true, false, FramedDoubleSlopeBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_DOUBLE_CORNER                            ( true, false,  true, false,  true,  true,  true, false, FramedDoubleCornerBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_DOUBLE_PRISM_CORNER                      ( true, false,  true, false,  true,  true,  true, false, FramedDoubleThreewayCornerBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_DOUBLE_THREEWAY_CORNER                   ( true, false,  true, false,  true,  true,  true, false, FramedDoubleThreewayCornerBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_TORCH                                    (false, false, false, false,  true, false, false, false),
    FRAMED_WALL_TORCH                               (false, false, false, false, false, false, false, false),
    FRAMED_SOUL_TORCH                               (false, false, false, false,  true, false, false, false),
    FRAMED_SOUL_WALL_TORCH                          (false, false, false, false, false, false, false, false),
    FRAMED_REDSTONE_TORCH                           (false, false, false, false,  true, false, false, false),
    FRAMED_REDSTONE_WALL_TORCH                      (false, false, false, false, false, false, false, false),
    FRAMED_FLOOR_BOARD                              ( true, false, false,  true,  true,  true, false, false, CtmPredicate.TOP, FramedFloorBlock.SKIP_PREDICATE, FramedFloorBlock::generateShapes),
    FRAMED_LATTICE_BLOCK                            (false, false, false,  true,  true,  true, false,  true, CtmPredicate.FALSE, FramedLatticeBlock.SKIP_PREDICATE, FramedLatticeBlock::generateShapes),
    FRAMED_VERTICAL_STAIRS                          ( true, false, false,  true,  true,  true, false,  true, FramedVerticalStairsBlock.CTM_PREDICATE, new VerticalStairsSkipPredicate(), FramedVerticalStairsBlock::generateShapes),
    FRAMED_CHEST                                    (false, false,  true,  true,  true, false, false, false, Shapes.box(1D/16D, 0, 1D/16D, 15D/16D, 14D/16D, 15D/16D)),
    FRAMED_BARS                                     (false, false, false,  true,  true,  true, false,  true),
    FRAMED_PANE                                     (false, false, false,  true,  true,  true, false,  true, CtmPredicate.FALSE, new PaneSkipPredicate()),
    FRAMED_RAIL_SLOPE                               ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock.CTM_PREDICATE, SlopeSkipPredicate.INSTANCE, FramedSlopeBlock::generateShapes),
    FRAMED_POWERED_RAIL_SLOPE                       ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock.CTM_PREDICATE, SlopeSkipPredicate.INSTANCE, FramedSlopeBlock::generateShapes),
    FRAMED_DETECTOR_RAIL_SLOPE                      ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock.CTM_PREDICATE, SlopeSkipPredicate.INSTANCE, FramedSlopeBlock::generateShapes),
    FRAMED_ACTIVATOR_RAIL_SLOPE                     ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock.CTM_PREDICATE, SlopeSkipPredicate.INSTANCE, FramedSlopeBlock::generateShapes),
    FRAMED_FLOWER_POT                               (false, false,  true, false,  true, false, false, false, Shapes.box(5D/16D, 0, 5D/16D, 11D/16D, 6D/16D, 11D/16D)),
    FRAMED_PILLAR                                   (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new PillarSkipPredicate(), FramedPillarBlock::generatePillarShapes),
    FRAMED_HALF_PILLAR                              (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new HalfPillarSkipPredicate(), FramedHalfPillarBlock::generateShapes),
    FRAMED_POST                                     (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new PostSkipPredicate(), FramedPillarBlock::generatePostShapes),
    FRAMED_COLLAPSIBLE_BLOCK                        (false,  true,  true,  true,  true,  true, false, false, FramedCollapsibleBlock.CTM_PREDICATE, new CollapsibleBlockSkipPredicate()),
    FRAMED_HALF_STAIRS                              (false, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, new HalfStairsSkipPredicate(), FramedHalfStairsBlock::generateShapes),
    FRAMED_BOUNCY_CUBE                              ( true, false, false, false,  true, false, false, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_SECRET_STORAGE                           ( true, false,  true, false,  true, false, false, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_REDSTONE_BLOCK                           ( true, false, false, false,  true,  true, false, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_PRISM                                    ( true,  true, false,  true,  true,  true, false, false, FramedPrismBlock.CTM_PREDICATE, FramedPrismBlock.SKIP_PREDICATE, FramedPrismBlock::generateShapes),
    FRAMED_SLOPED_PRISM                             ( true,  true, false,  true,  true,  true, false, false, FramedSlopedPrismBlock.CTM_PREDICATE, FramedSlopedPrismBlock.SKIP_PREDICATE, FramedSlopedPrismBlock::generateShapes),
    FRAMED_SLOPE_SLAB                               ( true,  true, false,  true,  true,  true, false, false, FramedSlopeSlabBlock.CTM_PREDICATE, new SlopeSlabSkipPredicate(), FramedSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_SLOPE_SLAB                      ( true,  true,  true,  true,  true,  true, false, false, FramedElevatedSlopeSlabBlock.CTM_PREDICATE, new ElevatedSlopeSlabSkipPredicate(), FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_SLAB                        ( true, false,  true,  true,  true,  true,  true, false, FramedDoubleSlopeSlabBlock.CTM_PREDICATE),
    FRAMED_INV_DOUBLE_SLOPE_SLAB                    ( true,  true,  true,  true,  true,  true,  true, false, CtmPredicate.FALSE, FramedInverseDoubleSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB               ( true, false,  true, false,  true,  true,  true, false, FramedElevatedDoubleSlopeSlabBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_FLAT_SLOPE_SLAB_CORNER                   ( true,  true, false,  true,  true,  true, false, false, FramedFlatSlopeSlabCornerBlock.CTM_PREDICATE, new FlatSlopeSlabCornerSkipPredicate(), FramedFlatSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER             ( true,  true, false,  true,  true,  true, false, false, FramedFlatSlopeSlabCornerBlock.CTM_PREDICATE, new FlatInnerSlopeSlabCornerSkipPredicate(), FramedFlatSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER              ( true,  true, false,  true,  true,  true, false, false, FramedFlatElevatedSlopeSlabCornerBlock.CTM_PREDICATE, new FlatElevatedSlopeSlabCornerSkipPredicate(), FramedFlatElevatedSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER        ( true,  true, false,  true,  true,  true, false, false, FramedFlatElevatedSlopeSlabCornerBlock.CTM_PREDICATE_INNER, new FlatElevatedInnerSlopeSlabCornerSkipPredicate(), FramedFlatElevatedSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER            ( true, false,  true,  true,  true,  true,  true, false, FramedFlatDoubleSlopeSlabCornerBlock.CTM_PREDICATE, FramedFlatDoubleSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER        ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatInverseDoubleSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER       ( true, false,  true, false,  true,  true,  true, false, CtmPredicate.Y_AXIS, Shapes.block()),
    FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER ( true, false,  true, false,  true,  true,  true, false, FramedFlatElevatedDoubleSlopeSlabCornerBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_VERTICAL_HALF_STAIRS                     (false, false, false,  true,  true, false, false, false, CtmPredicate.FALSE, new VerticalHalfStairsSkipPredicate(), FramedVerticalHalfStairsBlock::generateShapes),
    FRAMED_SLOPE_PANEL                              ( true,  true, false,  true,  true,  true, false, false, FramedSlopePanelBlock.CTM_PREDICATE, new SlopePanelSkipPredicate(), FramedSlopePanelBlock::generateShapes),
    FRAMED_EXTENDED_SLOPE_PANEL                     ( true,  true,  true,  true,  true,  true, false, false, FramedExtendedSlopePanelBlock.CTM_PREDICATE, new ExtendedSlopePanelSkipPredicate(), FramedExtendedSlopePanelBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_PANEL                       ( true, false,  true,  true,  true,  true,  true, false, FramedDoubleSlopePanelBlock.CTM_PREDICATE, FramedDoubleSlopePanelBlock::generateShapes), //Side skip is handled by the single vertical slope slab
    FRAMED_INV_DOUBLE_SLOPE_PANEL                   ( true,  true,  true,  true,  true,  true,  true, false, CtmPredicate.FALSE, FramedInverseDoubleSlopePanelBlock::generateShapes), //Side skip is handled by the single vertical slope slab
    FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL              ( true, false,  true, false,  true,  true,  true, false, FramedExtendedDoubleSlopePanelBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_FLAT_SLOPE_PANEL_CORNER                  ( true,  true, false,  true,  true,  true, false, false, FramedSlopePanelBlock.CTM_PREDICATE, new FlatSlopePanelCornerSkipPredicate(), FramedFlatSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER            ( true,  true, false,  true,  true,  true, false, false, FramedSlopePanelBlock.CTM_PREDICATE, new FlatInnerSlopePanelCornerSkipPredicate(), FramedFlatSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER              ( true,  true, false,  true,  true,  true, false, false, CtmPredicate.HOR_DIR, new FlatExtendedSlopePanelCornerSkipPredicate(), FramedFlatExtendedSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER        ( true,  true, false,  true,  true,  true, false, false, FramedFlatExtendedSlopePanelCornerBlock.CTM_PREDICATE, new FlatExtendedInnerSlopePanelCornerSkipPredicate(), FramedFlatExtendedSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER           ( true, false,  true,  true,  true,  true,  true, false, FramedFlatDoubleSlopePanelCornerBlock.CTM_PREDICATE, FramedFlatDoubleSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER       ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatInverseDoubleSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER       ( true, false,  true, false,  true,  true,  true, false, CtmPredicate.HOR_DIR_AXIS, Shapes.block()),
    FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER ( true, false,  true, false,  true,  true,  true, false, FramedFlatExtendedDoubleSlopePanelCornerBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_DOUBLE_STAIRS                            ( true, false,  true, false,  true,  true,  true, false, FramedDoubleStairsBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_VERTICAL_DOUBLE_STAIRS                   ( true, false,  true, false,  true,  true,  true, false, FramedVerticalDoubleStairsBlock.CTM_PREDICATE, Shapes.block()),
    FRAMED_WALL_BOARD                               ( true, false, false,  true,  true,  true, false, false, CtmPredicate.HOR_DIR, FramedWallBoardBlock.SKIP_PREDICATE, FramedWallBoardBlock::generateShapes),
    FRAMED_GLOWING_CUBE                             ( true, false, false, false,  true,  true, false, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_PYRAMID                                  ( true,  true, false,  true,  true,  true, false, false, CtmPredicate.DIR_OPPOSITE, SideSkipPredicate.CTM, FramedPyramidBlock::generateShapes),
    FRAMED_PYRAMID_SLAB                             ( true,  true, false,  true,  true,  true, false, false, CtmPredicate.DIR_OPPOSITE, SideSkipPredicate.CTM, FramedPyramidBlock::generateSlabShapes),
    FRAMED_LARGE_BUTTON                             (false, false, false, false,  true, false, false, false),
    FRAMED_LARGE_STONE_BUTTON                       (false, false, false, false,  true, false, false, false),
    FRAMED_HORIZONTAL_PANE                          ( true, false, false,  true,  true,  true, false, false, CtmPredicate.FALSE, FramedHorizontalPaneBlock.SKIP_PREDICATE, Shapes.box(0, 7D/16D, 0, 1, 9D/16D, 1)),
    FRAMED_TARGET                                   ( true, false, false, false,  true,  true, false, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_GATE_DOOR                                ( true, false, false, false,  true, false, false, false, FramedDoorBlock.CTM_PREDICATE, new GateSkipPredicate()),
    FRAMED_IRON_GATE_DOOR                           ( true, false, false, false,  true, false, false, false, FramedDoorBlock.CTM_PREDICATE, new GateSkipPredicate()),
    FRAMED_ITEM_FRAME                               (false,  true,  true, false,  true, false, false, false, FramedItemFrameBlock::generateShapes),
    FRAMED_GLOWING_ITEM_FRAME                       (false,  true,  true, false,  true, false, false, false, FramedItemFrameBlock::generateShapes),
    ;

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final boolean canOcclude;
    private final boolean specialHitbox;
    private final boolean specialTile;
    private final boolean waterloggable;
    private final boolean blockItem;
    private final boolean allowIntangible;
    private final boolean doubleBlock;
    private final boolean lockable;
    private final CtmPredicate ctmPredicate;
    private final SideSkipPredicate skipPredicate;
    private final VoxelShapeGenerator shapeGen;

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, CtmPredicate.FALSE, SideSkipPredicate.FALSE, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, CtmPredicate.FALSE, SideSkipPredicate.FALSE, shape);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, VoxelShapeGenerator shapeGen)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, CtmPredicate.FALSE, SideSkipPredicate.FALSE, shapeGen);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, CtmPredicate ctmPredicate)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ctmPredicate, SideSkipPredicate.FALSE, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, CtmPredicate ctmPredicate, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ctmPredicate, SideSkipPredicate.FALSE, shape);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, CtmPredicate ctmPredicate, VoxelShapeGenerator shapeGen)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ctmPredicate, SideSkipPredicate.FALSE, shapeGen);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ctmPredicate, skipPredicate, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ctmPredicate, skipPredicate, VoxelShapeGenerator.singleShape(shape));
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShapeGenerator shapeGen)
    {
        this.canOcclude = canOcclude;
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.allowIntangible = allowIntangible;
        this.doubleBlock = doubleBlock;
        this.lockable = lockable;
        this.ctmPredicate = ctmPredicate;
        this.skipPredicate = skipPredicate;
        this.shapeGen = shapeGen;
    }

    @Override
    public boolean canOccludeWithSolidCamo() { return canOcclude; }

    @Override
    public boolean hasSpecialHitbox() { return specialHitbox; }

    @Override
    public CtmPredicate getCtmPredicate() { return ctmPredicate; }

    @Override
    public SideSkipPredicate getSideSkipPredicate() { return skipPredicate; }

    @Override
    public ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        return shapeGen.generate(states);
    }

    @Override
    public boolean hasSpecialTile() { return specialTile; }

    @Override
    public boolean hasBlockItem() { return blockItem; }

    @Override
    public boolean supportsWaterLogging() { return waterloggable; }

    @Override
    public boolean allowMakingIntangible() { return allowIntangible; }

    @Override
    public boolean isDoubleBlock() { return doubleBlock; }

    @Override
    public boolean canLockState() { return lockable; }

    @Override
    public String getName() { return name; }

    @Override
    public int compareTo(IBlockType other)
    {
        if (!(other instanceof BlockType type))
        {
            return 1;
        }
        return compareTo(type);
    }
}