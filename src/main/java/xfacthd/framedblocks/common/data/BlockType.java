package xfacthd.framedblocks.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.util.*;

public enum BlockType
{
    FRAMED_CUBE                 (false, false, false,  true, CtmPredicate.TRUE, VoxelShapes.fullCube()),
    FRAMED_SLOPE                ( true, false,  true,  true, FramedSlopeBlock.CTM_PREDICATE/*, FramedSlopeBlock.SKIP_PREDICATE*/, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE         ( true, false,  true,  true, FramedCornerSlopeBlock.CTM_PREDICATE/*, FramedCornerSlopeBlock.SKIP_PREDICATE*/, FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE   ( true, false,  true,  true, FramedCornerSlopeBlock.CTM_PREDICATE_INNER/*, FramedCornerSlopeBlock.SKIP_PREDICATE_INNER*/, FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER         ( true, false,  true,  true, /*FramedThreewayCornerBlock.SKIP_PREDICATE, */FramedThreewayCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER   ( true, false,  true,  true, FramedThreewayCornerBlock.CTM_PREDICATE/*, FramedThreewayCornerBlock.SKIP_PREDICATE_INNER*/, FramedThreewayCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER      ( true, false,  true,  true, /*FramedThreewayCornerBlock.SKIP_PREDICATE, */FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER( true, false,  true,  true, FramedThreewayCornerBlock.CTM_PREDICATE/*, FramedThreewayCornerBlock.SKIP_PREDICATE_INNER*/, FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                 (false, false,  true,  true, FramedSlabBlock.CTM_PREDICATE, /*FramedSlabBlock.SKIP_PREDICATE, */FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE            (false, false,  true,  true, /*FramedSlabEdgeBlock.SKIP_PREDICATE, */FramedSlabEdgeBlock::generateShapes),
    FRAMED_PANEL                (false, false,  true,  true, FramedPanelBlock.CTM_PREDICATE, /*FramedPanelBlock.SKIP_PREDICATE, */FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR        (false, false,  true,  true, /*FramedCornerPillarBlock.SKIP_PREDICATE, */FramedCornerPillarBlock::generateShapes),
    FRAMED_STAIRS               (false, false,  true,  true, /*FramedSlabBlock.SKIP_PREDICATE, */FramedStairsBlock.CTM_PREDICATE),
    FRAMED_WALL                 (false, false,  true,  true/*, FramedWallBlock.SKIP_PREDICATE*/),
    FRAMED_FENCE                (false, false,  true,  true/*, FramedFenceBlock.SKIP_PREDICATE*/),
    FRAMED_GATE                 (false, false,  true,  true/*, FramedGateBlock.SKIP_PREDICATE*/),
    FRAMED_DOOR                 (false, false,  true,  true, FramedDoorBlock.CTM_PREDICATE),
    FRAMED_TRAPDOOR             (false, false,  true,  true, FramedTrapDoorBlock.CTM_PREDICATE),
    FRAMED_PRESSURE_PLATE       (false, false, false,  true),
    FRAMED_LADDER               (false, false,  true,  true),
    FRAMED_BUTTON               (false, false, false,  true),
    FRAMED_LEVER                (false, false, false,  true),
    FRAMED_SIGN                 (false,  true, false,  true),
    FRAMED_WALL_SIGN            (false,  true, false, false, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB          (false,  true, false, false, CtmPredicate.Y_AXIS/*, FramedDoubleSlabBlock.SKIP_PREDICATE*/, VoxelShapes.fullCube()),
    FRAMED_DOUBLE_PANEL         (false,  true, false, false, FramedDoublePanelBlock.CTM_PREDICATE_PANEL/*, FramedDoublePanelBlock.SKIP_PREDICATE*/, VoxelShapes.fullCube()),
    FRAMED_DOUBLE_SLOPE         (false,  true, false,  true, FramedDoubleSlopeBlock.CTM_PREDICATE_SLOPE/*, FramedDoubleSlopeBlock.SKIP_PREDICATE*/, VoxelShapes.fullCube()),
    FRAMED_TORCH                (false, false, false,  true),
    FRAMED_WALL_TORCH           (false, false, false, false),
    FRAMED_COLLAPSIBLE_BLOCK    ( true,  true, false,  true);

    private final boolean specialHitbox;
    private final boolean specialTile;
    private final boolean waterloggable;
    private final boolean blockItem;
    private final CtmPredicate ctmPredicate;
    private final SideSkipPredicate skipPredicate;
    private final VoxelShapeGenerator shapeGen;

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, CtmPredicate.FALSE);
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, CtmPredicate ctmPredicate)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, ctmPredicate, VoxelShapeGenerator.EMTPTY);
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, VoxelShapeGenerator shapeGen)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, CtmPredicate.FALSE, shapeGen);
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, CtmPredicate ctmPredicate, VoxelShape shape)
    {
        this(specialHitbox, specialTile, waterloggable, blockItem, ctmPredicate, VoxelShapeGenerator.singleShape(shape));
    }

    BlockType(boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, CtmPredicate ctmPredicate, VoxelShapeGenerator shapeGen)
    {
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.ctmPredicate = ctmPredicate;
        this.skipPredicate = SideSkipPredicate.CTM; //TODO: actually implement the predicates for blocks where the behaviour is not equivalent to the CTM predicate
        this.shapeGen = shapeGen;
    }

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
}