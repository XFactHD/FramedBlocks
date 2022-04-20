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

public enum BlockType implements IBlockType
{
    FRAMED_CUBE                   ( true, false, false, false,  true,  true, false, SideSkipPredicate.CTM, Shapes.block(), CtmPredicate.TRUE),
    FRAMED_SLOPE                  ( true,  true, false,  true,  true,  true, false, FramedSlopeBlock.CTM_PREDICATE, new SlopeSkipPredicate(), FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE           ( true,  true, false,  true,  true,  true, false, FramedCornerSlopeBlock.CTM_PREDICATE, new CornerSkipPredicate(), FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE     ( true,  true, false,  true,  true,  true, false, FramedCornerSlopeBlock.CTM_PREDICATE_INNER, new InnerCornerSkipPredicate(), FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER           ( true,  true, false,  true,  true,  true, false, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER     ( true,  true, false,  true,  true,  true, false, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER        ( true,  true, false,  true,  true,  true, false, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER  ( true,  true, false,  true,  true,  true, false, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                   ( true, false, false,  true,  true,  true, false, FramedSlabBlock.CTM_PREDICATE, new SlabSkipPredicate(), FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE              (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new SlabEdgeSkipPredicate(), FramedSlabEdgeBlock::generateShapes),
    FRAMED_SLAB_CORNER            (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new SlabCornerSkipPredicate(), FramedSlabCornerBlock::generateShapes),
    FRAMED_PANEL                  ( true, false, false,  true,  true,  true, false, FramedPanelBlock.CTM_PREDICATE, new PanelSkipPredicate(), FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR          (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new CornerPillarSkipPredicate(), FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS                 ( true, false, false,  true,  true,  true, false, new StairsSkipPredicate(), FramedStairsBlock.CTM_PREDICATE),
    FRAMED_WALL                   (false, false, false,  true,  true, false, false, FramedWallBlock.SKIP_PREDICATE, CtmPredicate.FALSE),
    FRAMED_FENCE                  (false, false, false,  true,  true, false, false, FramedFenceBlock.SKIP_PREDICATE, CtmPredicate.FALSE),
    FRAMED_GATE                   (false, false, false,  true,  true, false, false, FramedGateBlock.SKIP_PREDICATE, CtmPredicate.FALSE),
    FRAMED_DOOR                   ( true, false, false, false,  true, false, false, new DoorSkipPredicate(), FramedDoorBlock.CTM_PREDICATE),
    FRAMED_TRAPDOOR               ( true, false, false,  true,  true, false, false, SideSkipPredicate.CTM, FramedTrapDoorBlock.CTM_PREDICATE),
    FRAMED_PRESSURE_PLATE         (false, false, false, false,  true, false, false),
    FRAMED_LADDER                 (false, false, false,  true,  true, false, false),
    FRAMED_BUTTON                 (false, false, false, false,  true, false, false),
    FRAMED_LEVER                  (false, false, false, false,  true, false, false),
    FRAMED_SIGN                   (false, false,  true, false,  true, false, false),
    FRAMED_WALL_SIGN              (false, false,  true, false, false, false, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB            ( true, false,  true, false,  true,  true,  true, SideSkipPredicate.FALSE, Shapes.block(), CtmPredicate.Y_AXIS), //Side skip is handled by the single slab
    FRAMED_DOUBLE_PANEL           ( true, false,  true, false,  true,  true,  true, SideSkipPredicate.FALSE, Shapes.block(), FramedDoublePanelBlock.CTM_PREDICATE), //Side skip is handled by the single panel
    FRAMED_DOUBLE_SLOPE           ( true, false,  true, false,  true,  true,  true, SideSkipPredicate.FALSE, Shapes.block(), FramedDoubleSlopeBlock.CTM_PREDICATE), //Side skip is handled by the single slope
    FRAMED_DOUBLE_CORNER          ( true, false,  true, false,  true,  true,  true, SideSkipPredicate.FALSE, Shapes.block(), FramedDoubleCornerBlock.CTM_PREDICATE),
    FRAMED_DOUBLE_PRISM_CORNER    ( true, false,  true, false,  true,  true,  true, SideSkipPredicate.FALSE, Shapes.block(), FramedDoubleThreewayCornerBlock.CTM_PREDICATE),
    FRAMED_DOUBLE_THREEWAY_CORNER ( true, false,  true, false,  true,  true,  true, SideSkipPredicate.FALSE, Shapes.block(), FramedDoubleThreewayCornerBlock.CTM_PREDICATE),
    FRAMED_TORCH                  (false, false, false, false,  true, false, false),
    FRAMED_WALL_TORCH             (false, false, false, false, false, false, false),
    FRAMED_SOUL_TORCH             (false, false, false, false,  true, false, false),
    FRAMED_SOUL_WALL_TORCH        (false, false, false, false, false, false, false),
	FRAMED_FLOOR_BOARD            ( true, false, false,  true,  true,  true, false, FramedFloorBlock.SKIP_PREDICATE, Shapes.box(0, 0, 0, 1, 1D/16D, 1), FramedFloorBlock.CTM_PREDICATE),
    FRAMED_LATTICE_BLOCK          (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, FramedLatticeBlock.SKIP_PREDICATE, FramedLatticeBlock::generateShapes),
    FRAMED_VERTICAL_STAIRS        ( true, false, false,  true,  true,  true, false, FramedVerticalStairs.CTM_PREDICATE, new VerticalStairsSkipPredicate(), FramedVerticalStairs::generateShapes),
    FRAMED_CHEST                  (false, false,  true,  true,  true, false, false, SideSkipPredicate.FALSE, Shapes.box(1D/16D, 0, 1D/16D, 15D/16D, 14D/16D, 15D/16D), CtmPredicate.FALSE),
    FRAMED_BARS                   (false, false, false,  true,  true,  true, false, SideSkipPredicate.FALSE, CtmPredicate.FALSE),
    FRAMED_PANE                   (false, false, false,  true,  true,  true, false, new PaneSkipPredicate(), CtmPredicate.FALSE),
    FRAMED_RAIL_SLOPE             ( true,  true, false,  true,  true, false, false, FramedSlopeBlock.CTM_PREDICATE, new SlopeSkipPredicate(), FramedSlopeBlock::generateShapes),
    FRAMED_FLOWER_POT             (false, false,  true, false,  true, false, false, SideSkipPredicate.FALSE, Shapes.box(5D/16D, 0, 5D/16D, 11D/16D, 6D/16D, 11D/16D), CtmPredicate.FALSE),
    FRAMED_PILLAR                 (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new PillarSkipPredicate(), FramedPillarBlock::generatePillarShapes),
    FRAMED_HALF_PILLAR            (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new HalfPillarSkipPredicate(), FramedHalfPillarBlock::generateShapes),
    FRAMED_POST                   (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new PostSkipPredicate(), FramedPillarBlock::generatePostShapes),
    FRAMED_COLLAPSIBLE_BLOCK      (false,  true,  true,  true,  true,  true, false, new CollapsibleBlockSkipPredicate(), FramedCollapsibleBlock.CTM_PREDICATE),
    FRAMED_HALF_STAIRS            (false, false, false,  true,  true,  true, false, CtmPredicate.FALSE, new HalfStairsSkipPredicate(), FramedHalfStairsBlock::generateShapes),
    FRAMED_BOUNCY_CUBE            ( true, false, false, false,  true, false, false, SideSkipPredicate.CTM, Shapes.block(), CtmPredicate.TRUE),
    FRAMED_SECRET_STORAGE         ( true, false,  true, false,  true, false, false, SideSkipPredicate.CTM, Shapes.block(), CtmPredicate.TRUE),
    FRAMED_PRISM                  ( true,  true, false,  true,  true,  true, false, FramedPrismBlock.CTM_PREDICATE, FramedPrismBlock.SKIP_PREDICATE, FramedPrismBlock::generateShapes),
    FRAMED_SLOPED_PRISM           ( true,  true, false,  true,  true,  true, false, FramedSlopedPrismBlock.CTM_PREDICATE, FramedSlopedPrismBlock.SKIP_PREDICATE, FramedSlopedPrismBlock::generateShapes),
    FRAMED_SLOPE_SLAB             ( true,  true, false,  true,  true,  true, false, FramedSlopeSlabBlock.CTM_PREDICATE, new SlopeSlabSkipPredicate(), FramedSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_SLOPE_SLAB    ( true,  true,  true,  true,  true,  true, false, FramedElevatedSlopeSlabBlock.CTM_PREDICATE, new ElevatedSlopeSlabSkipPredicate(), FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_SLAB      ( true, false,  true,  true,  true,  true,  true, SideSkipPredicate.FALSE, FramedDoubleSlopeSlabBlock.CTM_PREDICATE), //Side skip is handled by the single slope slab
    FRAMED_INV_DOUBLE_SLOPE_SLAB  ( true,  true,  true,  true,  true,  true,  true, CtmPredicate.FALSE, SideSkipPredicate.FALSE, FramedInverseDoubleSlopeSlabBlock::generateShapes), //Side skip is handled by the single slope slab
    FRAMED_VERTICAL_HALF_STAIRS   (false, false, false,  true,  true, false, false, CtmPredicate.FALSE, new VerticalHalfStairsSkipPredicate(), FramedVerticalHalfStairsBlock::generateShapes);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final boolean canOcclude;
    private final boolean specialHitbox;
    private final boolean specialTile;
    private final boolean waterloggable;
    private final boolean blockItem;
    private final boolean allowIntangible;
    private final boolean doubleBlock;
    private final CtmPredicate ctmPredicate;
    private final SideSkipPredicate skipPredicate;
    private final VoxelShapeGenerator shapeGen;

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, CtmPredicate.FALSE, SideSkipPredicate.FALSE, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, SideSkipPredicate skipPredicate, CtmPredicate ctmPredicate)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, ctmPredicate, skipPredicate, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, SideSkipPredicate skipPredicate, VoxelShape shape, CtmPredicate ctmPredicate)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, ctmPredicate, skipPredicate, VoxelShapeGenerator.singleShape(shape));
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShapeGenerator shapeGen)
    {
        this.canOcclude = canOcclude;
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.allowIntangible = allowIntangible;
        this.doubleBlock = doubleBlock;
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