package xfacthd.framedblocks.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.skippreds.*;
import xfacthd.framedblocks.common.util.*;

import java.util.Locale;

public enum BlockType
{
    FRAMED_CUBE                   ( true, false, false, false,  true,  true, CtmPredicate.TRUE, SideSkipPredicate.CTM, VoxelShapes.block()),
    FRAMED_SLOPE                  ( true,  true, false,  true,  true,  true, FramedSlopeBlock.CTM_PREDICATE, new SlopeSkipPredicate(), FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE           ( true,  true, false,  true,  true,  true, FramedCornerSlopeBlock.CTM_PREDICATE, new CornerSkipPredicate(), FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE     ( true,  true, false,  true,  true,  true, FramedCornerSlopeBlock.CTM_PREDICATE_INNER, new InnerCornerSkipPredicate(), FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER           ( true,  true, false,  true,  true,  true, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER     ( true,  true, false,  true,  true,  true, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER        ( true,  true, false,  true,  true,  true, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER  ( true,  true, false,  true,  true,  true, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                   ( true, false, false,  true,  true,  true, FramedSlabBlock.CTM_PREDICATE, new SlabSkipPredicate(), FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE              (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new SlabEdgeSkipPredicate(), FramedSlabEdgeBlock::generateShapes),
    FRAMED_SLAB_CORNER            (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new SlabCornerSkipPredicate(), FramedSlabCornerBlock::generateShapes),
    FRAMED_PANEL                  ( true, false, false,  true,  true,  true, FramedPanelBlock.CTM_PREDICATE, new PanelSkipPredicate(), FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR          (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new CornerPillarSkipPredicate(), FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS                 ( true, false, false,  true,  true,  true, FramedStairsBlock.CTM_PREDICATE, new StairsSkipPredicate()),
    FRAMED_WALL                   (false, false, false,  true,  true, false, CtmPredicate.FALSE, new WallSkipPredicate()),
    FRAMED_FENCE                  (false, false, false,  true,  true, false, CtmPredicate.FALSE, FramedFenceBlock.SKIP_PREDICATE),
    FRAMED_GATE                   (false, false, false,  true,  true, false, CtmPredicate.FALSE, FramedGateBlock.SKIP_PREDICATE),
    FRAMED_DOOR                   ( true, false, false, false,  true, false, FramedDoorBlock.CTM_PREDICATE, new DoorSkipPredicate()),
    FRAMED_TRAPDOOR               ( true, false, false,  true,  true, false, FramedTrapDoorBlock.CTM_PREDICATE, SideSkipPredicate.CTM),
    FRAMED_PRESSURE_PLATE         (false, false, false, false,  true, false),
    FRAMED_LADDER                 (false, false, false,  true,  true, false),
    FRAMED_BUTTON                 (false, false, false, false,  true, false),
    FRAMED_LEVER                  (false, false, false, false,  true, false),
    FRAMED_SIGN                   (false, false,  true, false,  true, false),
    FRAMED_WALL_SIGN              (false, false,  true, false, false, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB            ( true, false,  true, false,  true,  true, CtmPredicate.Y_AXIS, SideSkipPredicate.FALSE, VoxelShapes.block()), //Side skip is handled by the single slab
    FRAMED_DOUBLE_PANEL           ( true, false,  true, false,  true,  true, FramedDoublePanelBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, VoxelShapes.block()), //Side skip is handled by the single panel
    FRAMED_DOUBLE_SLOPE           ( true, false,  true, false,  true,  true, FramedDoubleSlopeBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, VoxelShapes.block()), //Side skip is handled by the single slope
    FRAMED_DOUBLE_CORNER          ( true, false,  true, false,  true,  true, FramedDoubleCornerBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, VoxelShapes.block()),
    FRAMED_DOUBLE_PRISM_CORNER    ( true, false,  true, false,  true,  true, FramedDoubleThreewayCornerBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, VoxelShapes.block()),
    FRAMED_DOUBLE_THREEWAY_CORNER ( true, false,  true, false,  true,  true, FramedDoubleThreewayCornerBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, VoxelShapes.block()),
    FRAMED_TORCH                  (false, false, false, false,  true, false),
    FRAMED_WALL_TORCH             (false, false, false, false, false, false),
    FRAMED_SOUL_TORCH             (false, false, false, false,  true, false),
    FRAMED_SOUL_WALL_TORCH        (false, false, false, false, false, false),
	FRAMED_FLOOR_BOARD            ( true, false, false,  true,  true,  true, FramedFloorBlock.CTM_PREDICATE, FramedFloorBlock.SKIP_PREDICATE, VoxelShapes.box(0, 0, 0, 1, 1D/16D, 1)),
    FRAMED_LATTICE_BLOCK          (false, false, false,  true,  true,  true, CtmPredicate.FALSE, FramedLatticeBlock.SKIP_PREDICATE, FramedLatticeBlock::generateShapes),
    FRAMED_VERTICAL_STAIRS        ( true, false, false,  true,  true,  true, FramedVerticalStairsBlock.CTM_PREDICATE, new VerticalStairsSkipPredicate(), FramedVerticalStairsBlock::generateShapes),
    FRAMED_CHEST                  (false, false,  true,  true,  true, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, VoxelShapes.box(1D/16D, 0, 1D/16D, 15D/16D, 14D/16D, 15D/16D)),
    FRAMED_BARS                   (false, false, false,  true,  true,  true, CtmPredicate.FALSE, SideSkipPredicate.FALSE),
    FRAMED_PANE                   (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new PaneSkipPredicate()),
    FRAMED_RAIL_SLOPE             ( true,  true, false,  true,  true, false, FramedSlopeBlock.CTM_PREDICATE, new SlopeSkipPredicate(), FramedSlopeBlock::generateShapes),
    FRAMED_FLOWER_POT             (false, false,  true, false,  true, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, VoxelShapes.box(5D/16D, 0, 5D/16D, 11D/16D, 6D/16D, 11D/16D)),
    FRAMED_PILLAR                 (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new PillarSkipPredicate(), FramedPillarBlock::generatePillarShapes),
    FRAMED_HALF_PILLAR            (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new HalfPillarSkipPredicate(), FramedHalfPillarBlock::generateShapes),
    FRAMED_POST                   (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new PostSkipPredicate(), FramedPillarBlock::generatePostShapes),
    FRAMED_COLLAPSIBLE_BLOCK      (false,  true,  true,  true,  true,  true, FramedCollapsibleBlock.CTM_PREDICATE, new CollapsibleBlockSkipPredicate()),
    FRAMED_HALF_STAIRS            (false, false, false,  true,  true,  true, CtmPredicate.FALSE, new HalfStairsSkipPredicate(), FramedHalfStairsBlock::generateShapes),
    FRAMED_BOUNCY_CUBE            ( true, false, false, false,  true, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, VoxelShapes.block()),
    FRAMED_SECRET_STORAGE         ( true, false,  true, false,  true, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, VoxelShapes.block()),
    FRAMED_REDSTONE_BLOCK         ( true, false, false, false,  true,  true, CtmPredicate.TRUE, SideSkipPredicate.CTM, VoxelShapes.block()),
    FRAMED_PRISM                  ( true,  true, false,  true,  true,  true, FramedPrismBlock.CTM_PREDICATE, FramedPrismBlock.SKIP_PREDICATE, FramedPrismBlock::generateShapes),
    FRAMED_SLOPED_PRISM           ( true,  true, false,  true,  true,  true, FramedSlopedPrismBlock.CTM_PREDICATE, FramedSlopedPrismBlock.SKIP_PREDICATE, FramedSlopedPrismBlock::generateShapes),
    FRAMED_SLOPE_SLAB             ( true,  true, false,  true,  true,  true, FramedSlopeSlabBlock.CTM_PREDICATE, new SlopeSlabSkipPredicate(), FramedSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_SLOPE_SLAB    ( true,  true,  true,  true,  true,  true, FramedElevatedSlopeSlabBlock.CTM_PREDICATE, new ElevatedSlopeSlabSkipPredicate(), FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_SLAB      ( true, false,  true,  true,  true,  true, FramedDoubleSlopeSlabBlock.CTM_PREDICATE, SideSkipPredicate.FALSE), //Side skip is handled by the single slope slab
    FRAMED_INV_DOUBLE_SLOPE_SLAB  ( true,  true,  true,  true,  true,  true, CtmPredicate.FALSE, SideSkipPredicate.FALSE, FramedInverseDoubleSlopeSlabBlock::generateShapes), //Side skip is handled by the single slope slab
    FRAMED_VERTICAL_HALF_STAIRS   (false, false, false,  true,  true, false, CtmPredicate.FALSE, new VerticalHalfStairsSkipPredicate(), FramedVerticalHalfStairsBlock::generateShapes),
    FRAMED_SLOPE_PANEL            ( true,  true, false,  true,  true,  true, FramedSlopePanelBlock.CTM_PREDICATE, new SlopePanelSkipPredicate(), FramedSlopePanelBlock::generateShapes),
    FRAMED_EXTENDED_SLOPE_PANEL   ( true,  true,  true,  true,  true,  true, FramedExtendedSlopePanelBlock.CTM_PREDICATE, new ExtendedSlopePanelSkipPredicate(), FramedExtendedSlopePanelBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_PANEL     ( true, false,  true,  true,  true,  true, FramedDoubleSlopePanelBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, FramedDoubleSlopePanelBlock::generateShapes), //Side skip is handled by the single vertical slope slab
    FRAMED_INV_DOUBLE_SLOPE_PANEL ( true,  true,  true,  true,  true,  true, CtmPredicate.FALSE, SideSkipPredicate.FALSE, FramedInverseDoubleSlopePanelBlock::generateShapes); //Side skip is handled by the single vertical slope slab

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final boolean canOcclude;
    private final boolean specialHitbox;
    private final boolean specialTile;
    private final boolean waterloggable;
    private final boolean blockItem;
    private final boolean allowIntangible; //If this is true, the block must suppress hit and destroy particles to avoid crashing when hit
    private final CtmPredicate ctmPredicate;
    private final SideSkipPredicate skipPredicate;
    private final VoxelShapeGenerator shapeGen;

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, CtmPredicate.FALSE, SideSkipPredicate.FALSE, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, ctmPredicate, skipPredicate, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, ctmPredicate, skipPredicate, VoxelShapeGenerator.singleShape(shape));
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShapeGenerator shapeGen)
    {
        this.canOcclude = canOcclude;
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.allowIntangible = allowIntangible;
        this.ctmPredicate = ctmPredicate;
        this.skipPredicate = skipPredicate;
        this.shapeGen = shapeGen;
    }

    public boolean canOccludeWithSolidCamo() { return canOcclude; }

    public boolean hasSpecialHitbox() { return specialHitbox; }

    public CtmPredicate getCtmPredicate() { return ctmPredicate; }

    public SideSkipPredicate getSideSkipPredicate() { return skipPredicate; }

    public ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        return shapeGen.generate(states);
    }

    public boolean hasSpecialTile() { return specialTile; }

    public boolean hasBlockItem() { return blockItem; }

    public boolean supportsWaterLogging() { return waterloggable; }

    public boolean allowMakingIntangible() { return allowIntangible; }

    public String getName() { return name; }
}