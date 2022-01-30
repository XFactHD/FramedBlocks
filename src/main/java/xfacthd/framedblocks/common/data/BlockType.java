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
    FRAMED_CUBE                   (false, false, false,  true,  true, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_SLOPE                  ( true, false,  true,  true,  true, FramedSlopeBlock.CTM_PREDICATE, new SlopeSkipPredicate(), FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE           ( true, false,  true,  true,  true, FramedCornerSlopeBlock.CTM_PREDICATE, new CornerSkipPredicate(), FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE     ( true, false,  true,  true,  true, FramedCornerSlopeBlock.CTM_PREDICATE_INNER, new InnerCornerSkipPredicate(), FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER           ( true, false,  true,  true,  true, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER     ( true, false,  true,  true,  true, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedPrismCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER        ( true, false,  true,  true,  true, CtmPredicate.FALSE, new ThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER  ( true, false,  true,  true,  true, FramedThreewayCornerBlock.CTM_PREDICATE, new InnerThreewayCornerSkipPredicate(), FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                   (false, false,  true,  true,  true, FramedSlabBlock.CTM_PREDICATE, new SlabSkipPredicate(), FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE              (false, false,  true,  true,  true, CtmPredicate.FALSE, new SlabEdgeSkipPredicate(), FramedSlabEdgeBlock::generateShapes),
    FRAMED_SLAB_CORNER            (false, false,  true,  true,  true, CtmPredicate.FALSE, new SlabCornerSkipPredicate(), FramedSlabCornerBlock::generateShapes),
    FRAMED_PANEL                  (false, false,  true,  true,  true, FramedPanelBlock.CTM_PREDICATE, new PanelSkipPredicate(), FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR          (false, false,  true,  true,  true, CtmPredicate.FALSE, new CornerPillarSkipPredicate(), FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS                 (false, false,  true,  true,  true, FramedStairsBlock.CTM_PREDICATE, new StairsSkipPredicate()),
    FRAMED_WALL                   (false, false,  true,  true, false, CtmPredicate.FALSE, FramedWallBlock.SKIP_PREDICATE),
    FRAMED_FENCE                  (false, false,  true,  true, false, CtmPredicate.FALSE, FramedFenceBlock.SKIP_PREDICATE),
    FRAMED_GATE                   (false, false,  true,  true, false, CtmPredicate.FALSE, FramedGateBlock.SKIP_PREDICATE),
    FRAMED_DOOR                   (false, false, false,  true, false, FramedDoorBlock.CTM_PREDICATE, new DoorSkipPredicate()),
    FRAMED_TRAPDOOR               (false, false,  true,  true, false, FramedTrapDoorBlock.CTM_PREDICATE, SideSkipPredicate.CTM),
    FRAMED_PRESSURE_PLATE         (false, false, false,  true, false),
    FRAMED_LADDER                 (false, false,  true,  true, false),
    FRAMED_BUTTON                 (false, false, false,  true, false),
    FRAMED_LEVER                  (false, false, false,  true, false),
    FRAMED_SIGN                   (false,  true, false,  true, false),
    FRAMED_WALL_SIGN              (false,  true, false, false, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB            (false,  true, false,  true,  true, CtmPredicate.Y_AXIS, SideSkipPredicate.FALSE, Shapes.block()), //Side skip is handled by the single slab
    FRAMED_DOUBLE_PANEL           (false,  true, false,  true,  true, FramedDoublePanelBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, Shapes.block()), //Side skip is handled by the single panel
    FRAMED_DOUBLE_SLOPE           (false,  true, false,  true,  true, FramedDoubleSlopeBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, Shapes.block()), //Side skip is handled by the single slope
    FRAMED_DOUBLE_CORNER          (false,  true, false,  true,  true, FramedDoubleCornerBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, Shapes.block()),
    FRAMED_DOUBLE_PRISM_CORNER    (false,  true, false,  true,  true, FramedDoubleThreewayCornerBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, Shapes.block()),
    FRAMED_DOUBLE_THREEWAY_CORNER (false,  true, false,  true,  true, FramedDoubleThreewayCornerBlock.CTM_PREDICATE, SideSkipPredicate.FALSE, Shapes.block()),
    FRAMED_TORCH                  (false, false, false,  true, false),
    FRAMED_WALL_TORCH             (false, false, false, false, false),
    FRAMED_SOUL_TORCH             (false, false, false,  true, false),
    FRAMED_SOUL_WALL_TORCH        (false, false, false, false, false),
	FRAMED_FLOOR_BOARD            (false, false,  true,  true,  true, FramedFloorBlock.CTM_PREDICATE, FramedFloorBlock.SKIP_PREDICATE, Shapes.box(0, 0, 0, 1, 1D/16D, 1)),
    FRAMED_LATTICE_BLOCK          (false, false,  true,  true,  true, CtmPredicate.FALSE, FramedLatticeBlock.SKIP_PREDICATE, FramedLatticeBlock::generateShapes),
    FRAMED_VERTICAL_STAIRS        (false, false,  true,  true,  true, FramedVerticalStairs.CTM_PREDICATE, new VerticalStairsSkipPredicate(), FramedVerticalStairs::generateShapes),
    FRAMED_CHEST                  (false,  true,  true,  true, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, Shapes.box(1D/16D, 0, 1D/16D, 15D/16D, 14D/16D, 15D/16D)),
    FRAMED_BARS                   (false, false,  true,  true,  true, CtmPredicate.FALSE, SideSkipPredicate.FALSE),
    FRAMED_PANE                   (false, false,  true,  true,  true, CtmPredicate.FALSE, new PaneSkipPredicate()),
    FRAMED_RAIL_SLOPE             ( true, false,  true,  true, false, FramedSlopeBlock.CTM_PREDICATE, new SlopeSkipPredicate(), FramedSlopeBlock::generateShapes),
    FRAMED_FLOWER_POT             (false,  true, false,  true, false, CtmPredicate.FALSE, SideSkipPredicate.FALSE, Shapes.box(5D/16D, 0, 5D/16D, 11D/16D, 6D/16D, 11D/16D)),
    FRAMED_PILLAR                 (false, false,  true,  true,  true, CtmPredicate.FALSE, new PillarSkipPredicate(), FramedPillarBlock::generatePillarShapes),
    FRAMED_HALF_PILLAR            (false, false,  true,  true,  true, CtmPredicate.FALSE, new HalfPillarSkipPredicate(), FramedHalfPillarBlock::generateShapes),
    FRAMED_POST                   (false, false,  true,  true,  true, CtmPredicate.FALSE, new PostSkipPredicate(), FramedPillarBlock::generatePostShapes),
    FRAMED_COLLAPSIBLE_BLOCK      ( true,  true, false,  true,  true, FramedCollapsibleBlock.CTM_PREDICATE, new CollapsibleBlockSkipPredicate()),
    FRAMED_HALF_STAIRS            (false, false,  true,  true,  true, CtmPredicate.FALSE, new HalfStairsSkipPredicate(), FramedHalfStairsBlock::generateShapes),
    FRAMED_BOUNCY_CUBE            (false, false, false,  true, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block()),
    FRAMED_SECRET_STORAGE         (false,  true, false,  true, false, CtmPredicate.TRUE, SideSkipPredicate.CTM, Shapes.block());

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final boolean specialHitbox;
    private final boolean specialTile;
    private final boolean waterloggable;
    private final boolean blockItem;
    private final boolean allowPassthrough;
    private final CtmPredicate ctmPredicate;
    private final SideSkipPredicate skipPredicate;
    private final VoxelShapeGenerator shapeGen;

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowPassthrough)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, allowPassthrough, CtmPredicate.FALSE, SideSkipPredicate.FALSE, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowPassthrough, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, allowPassthrough, ctmPredicate, skipPredicate, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowPassthrough, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShape shape)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, allowPassthrough, ctmPredicate, skipPredicate, VoxelShapeGenerator.singleShape(shape));
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowPassthrough, CtmPredicate ctmPredicate, SideSkipPredicate skipPredicate, VoxelShapeGenerator shapeGen)
    {
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.allowPassthrough = allowPassthrough;
        this.ctmPredicate = ctmPredicate;
        this.skipPredicate = skipPredicate;
        this.shapeGen = shapeGen;
    }

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
    public boolean allowPassthrough() { return allowPassthrough; }

    @Override
    public String getName() { return name; }

    @Override
    public int compareTo(IBlockType other)
    {
        if (!(other instanceof BlockType type))
        {
            return 0;
        }
        return compareTo(type);
    }
}