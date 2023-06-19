package xfacthd.framedblocks.common.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.common.block.interactive.*;
import xfacthd.framedblocks.common.block.pane.*;
import xfacthd.framedblocks.common.block.pillar.*;
import xfacthd.framedblocks.common.block.prism.*;
import xfacthd.framedblocks.common.block.slab.*;
import xfacthd.framedblocks.common.block.slope.*;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.block.stairs.*;
import xfacthd.framedblocks.common.data.facepreds.FullFacePredicates;
import xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;

import java.util.Locale;

@SuppressWarnings("SameParameterValue")
public enum BlockType implements IBlockType
{
    FRAMED_CUBE                                     ( true, false, false, false,  true,  true, false, false, Shapes.block()),
    FRAMED_SLOPE                                    ( true,  true, false,  true,  true,  true, false, false, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE                             ( true,  true, false,  true,  true,  true, false, false, FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE                       ( true,  true, false,  true,  true,  true, false, false, FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER                             ( true,  true, false,  true,  true,  true, false, false, FramedPrismCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER                       ( true,  true, false,  true,  true,  true, false, false, FramedPrismCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER                          ( true,  true, false,  true,  true,  true, false, false, FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER                    ( true,  true, false,  true,  true,  true, false, false, FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                                     ( true, false, false,  true,  true,  true, false, false, FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE                                (false, false, false,  true,  true,  true, false, false, FramedSlabEdgeBlock::generateShapes),
    FRAMED_SLAB_CORNER                              (false, false, false,  true,  true,  true, false, false, FramedSlabCornerBlock::generateShapes),
    FRAMED_DIVIDED_SLAB                             ( true, false,  true,  true,  true,  true,  true, false, FramedSlabBlock::generateShapes),
    FRAMED_PANEL                                    ( true, false, false,  true,  true,  true, false, false, FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR                            (false, false, false,  true,  true,  true, false, false, FramedCornerPillarBlock::generateShapes),
    FRAMED_DIVIDED_PANEL_HORIZONTAL                 ( true, false,  true,  true,  true,  true,  true, false, FramedPanelBlock::generateShapes),
    FRAMED_DIVIDED_PANEL_VERTICAL                   ( true, false,  true,  true,  true,  true,  true, false, FramedPanelBlock::generateShapes),
    FRAMED_STAIRS                                   ( true, false, false,  true,  true,  true, false,  true),
    FRAMED_DOUBLE_STAIRS                            ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_HALF_STAIRS                              (false, false, false,  true,  true,  true, false, false, FramedHalfStairsBlock::generateShapes),
    FRAMED_DIVIDED_STAIRS                           ( true, false,  true,  true,  true,  true,  true, false, FramedDividedStairsBlock::generateShapes),
    FRAMED_VERTICAL_STAIRS                          ( true, false, false,  true,  true,  true, false,  true, FramedVerticalStairsBlock::generateShapes),
    FRAMED_VERTICAL_DOUBLE_STAIRS                   ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_VERTICAL_HALF_STAIRS                     (false, false, false,  true,  true, false, false, false, FramedVerticalHalfStairsBlock::generateShapes),
    FRAMED_VERTICAL_DIVIDED_STAIRS                  ( true, false,  true,  true,  true,  true,  true, false, FramedVerticalDividedStairsBlock::generateShapes),
    FRAMED_WALL                                     (false, false, false,  true,  true, false, false,  true),
    FRAMED_FENCE                                    (false, false, false,  true,  true, false, false,  true),
    FRAMED_FENCE_GATE                               (false, false, false,  true,  true, false, false, false),
    FRAMED_DOOR                                     ( true, false, false, false,  true, false, false, false),
    FRAMED_IRON_DOOR                                ( true, false, false, false,  true, false, false, false),
    FRAMED_TRAPDOOR                                 ( true, false, false,  true,  true, false, false, false),
    FRAMED_IRON_TRAPDOOR                            ( true, false, false,  true,  true, false, false, false),
    FRAMED_PRESSURE_PLATE                           (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_PRESSURE_PLATE             (false, false, false,  true, false, false, false, false),
    FRAMED_STONE_PRESSURE_PLATE                     (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE       (false, false, false,  true, false, false, false, false),
    FRAMED_OBSIDIAN_PRESSURE_PLATE                  (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE    (false, false, false,  true, false, false, false, false),
    FRAMED_GOLD_PRESSURE_PLATE                      (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE        (false, false, false,  true, false, false, false, false),
    FRAMED_IRON_PRESSURE_PLATE                      (false, false, false, false,  true, false, false, false),
    FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE        (false, false, false,  true, false, false, false, false),
    FRAMED_LADDER                                   (false, false, false,  true,  true, false, false, false),
    FRAMED_BUTTON                                   (false, false, false, false,  true, false, false, false),
    FRAMED_STONE_BUTTON                             (false, false, false, false,  true, false, false, false),
    FRAMED_LARGE_BUTTON                             (false, false, false, false,  true, false, false, false),
    FRAMED_LARGE_STONE_BUTTON                       (false, false, false, false,  true, false, false, false),
    FRAMED_LEVER                                    (false, false, false, false,  true, false, false, false),
    FRAMED_SIGN                                     (false, false,  true, false,  true, false, false, false),
    FRAMED_WALL_SIGN                                (false, false,  true, false, false, false, false, false, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB                              ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_DOUBLE_PANEL                             ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_DOUBLE_SLOPE                             ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_DOUBLE_CORNER                            ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_DOUBLE_PRISM_CORNER                      ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_DOUBLE_THREEWAY_CORNER                   ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_TORCH                                    (false, false, false, false,  true, false, false, false),
    FRAMED_WALL_TORCH                               (false, false, false, false, false, false, false, false),
    FRAMED_SOUL_TORCH                               (false, false, false, false,  true, false, false, false),
    FRAMED_SOUL_WALL_TORCH                          (false, false, false, false, false, false, false, false),
    FRAMED_REDSTONE_TORCH                           (false, false, false, false,  true, false, false, false),
    FRAMED_REDSTONE_WALL_TORCH                      (false, false, false, false, false, false, false, false),
    FRAMED_FLOOR_BOARD                              ( true, false, false,  true,  true,  true, false, false, FramedFloorBlock::generateShapes),
    FRAMED_WALL_BOARD                               ( true, false, false,  true,  true,  true, false, false, FramedWallBoardBlock::generateShapes),
    FRAMED_LATTICE_BLOCK                            (false, false, false,  true,  true,  true, false,  true, FramedLatticeBlock::generateShapes),
    FRAMED_CHEST                                    (false, false,  true,  true,  true, false, false, false, Shapes.box(1D/16D, 0, 1D/16D, 15D/16D, 14D/16D, 15D/16D)),
    FRAMED_SECRET_STORAGE                           ( true, false,  true, false,  true, false, false, false, Shapes.block()),
    FRAMED_BARS                                     (false, false, false,  true,  true,  true, false,  true),
    FRAMED_PANE                                     (false, false, false,  true,  true,  true, false,  true),
    FRAMED_HORIZONTAL_PANE                          ( true, false, false,  true,  true,  true, false, false, Shapes.box(0, 7D/16D, 0, 1, 9D/16D, 1)),
    FRAMED_RAIL_SLOPE                               ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock::generateShapes),
    FRAMED_POWERED_RAIL_SLOPE                       ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock::generateShapes),
    FRAMED_DETECTOR_RAIL_SLOPE                      ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock::generateShapes),
    FRAMED_ACTIVATOR_RAIL_SLOPE                     ( true,  true, false,  true,  true, false, false, false, FramedSlopeBlock::generateShapes),
    FRAMED_FLOWER_POT                               (false, false,  true, false,  true, false, false, false, Shapes.box(5D/16D, 0, 5D/16D, 11D/16D, 6D/16D, 11D/16D)),
    FRAMED_PILLAR                                   (false, false, false,  true,  true,  true, false, false, FramedPillarBlock::generatePillarShapes),
    FRAMED_HALF_PILLAR                              (false, false, false,  true,  true,  true, false, false, FramedHalfPillarBlock::generateShapes),
    FRAMED_POST                                     (false, false, false,  true,  true,  true, false, false, FramedPillarBlock::generatePostShapes),
    FRAMED_COLLAPSIBLE_BLOCK                        (false,  true,  true,  true,  true,  true, false, false),
    FRAMED_BOUNCY_CUBE                              ( true, false, false, false,  true, false, false, false, Shapes.block()),
    FRAMED_REDSTONE_BLOCK                           ( true, false, false, false,  true,  true, false, false, Shapes.block()),
    FRAMED_PRISM                                    ( true,  true, false,  true,  true,  true, false, false, FramedPrismBlock::generateShapes),
    FRAMED_INNER_PRISM                              ( true,  true, false,  true,  true,  true, false, false, FramedPrismBlock::generateInnerShapes),
    FRAMED_DOUBLE_PRISM                             ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_SLOPED_PRISM                             ( true,  true, false,  true,  true,  true, false, false, FramedSlopedPrismBlock::generateShapes),
    FRAMED_INNER_SLOPED_PRISM                       ( true,  true, false,  true,  true,  true, false, false, FramedSlopedPrismBlock::generateInnerShapes),
    FRAMED_DOUBLE_SLOPED_PRISM                      ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_SLOPE_SLAB                               ( true,  true, false,  true,  true,  true, false, false, FramedSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_SLOPE_SLAB                      ( true,  true,  true,  true,  true,  true, false, false, FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_SLAB                        ( true, false,  true,  true,  true,  true,  true, false),
    FRAMED_INV_DOUBLE_SLOPE_SLAB                    ( true,  true,  true,  true,  true,  true,  true, false, FramedInverseDoubleSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB               ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_STACKED_SLOPE_SLAB                       ( true,  true,  true,  true,  true,  true,  true, false, FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_FLAT_SLOPE_SLAB_CORNER                   ( true,  true, false,  true,  true,  true, false, false, FramedFlatSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER             ( true,  true, false,  true,  true,  true, false, false, FramedFlatSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER              ( true,  true, false,  true,  true,  true, false, false, FramedFlatElevatedSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER        ( true,  true, false,  true,  true,  true, false, false, FramedFlatElevatedSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER            ( true, false,  true,  true,  true,  true,  true, false, FramedFlatDoubleSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER        ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatInverseDoubleSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER       ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER           ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatElevatedSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER     ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatElevatedSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_SLOPE_PANEL                              ( true,  true, false,  true,  true,  true, false, false, FramedSlopePanelBlock::generateShapes),
    FRAMED_EXTENDED_SLOPE_PANEL                     ( true,  true,  true,  true,  true,  true, false, false, FramedExtendedSlopePanelBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_PANEL                       ( true, false,  true,  true,  true,  true,  true, false, FramedDoubleSlopePanelBlock::generateShapes),
    FRAMED_INV_DOUBLE_SLOPE_PANEL                   ( true,  true,  true,  true,  true,  true,  true, false, FramedInverseDoubleSlopePanelBlock::generateShapes),
    FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL              ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_STACKED_SLOPE_PANEL                      ( true,  true,  true,  true,  true,  true,  true, false, FramedExtendedSlopePanelBlock::generateShapes),
    FRAMED_FLAT_SLOPE_PANEL_CORNER                  ( true,  true, false,  true,  true,  true, false, false, FramedFlatSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER            ( true,  true, false,  true,  true,  true, false, false, FramedFlatSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER              ( true,  true, false,  true,  true,  true, false, false, FramedFlatExtendedSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER        ( true,  true, false,  true,  true,  true, false, false, FramedFlatExtendedSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER           ( true, false,  true,  true,  true,  true,  true, false, FramedFlatDoubleSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER       ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatInverseDoubleSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER       ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER ( true, false,  true, false,  true,  true,  true, false, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER          ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatExtendedSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER    ( true,  true,  true,  true,  true,  true,  true, false, FramedFlatExtendedSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_GLOWING_CUBE                             ( true, false, false, false,  true,  true, false, false, Shapes.block()),
    FRAMED_PYRAMID                                  ( true,  true, false,  true,  true,  true, false, false, FramedPyramidBlock::generateShapes),
    FRAMED_PYRAMID_SLAB                             ( true,  true, false,  true,  true,  true, false, false, FramedPyramidBlock::generateSlabShapes),
    FRAMED_TARGET                                   ( true, false,  true, false,  true,  true, false, false, Shapes.block()),
    FRAMED_GATE                                     ( true, false, false, false,  true, false, false, false),
    FRAMED_IRON_GATE                                ( true, false, false, false,  true, false, false, false),
    FRAMED_ITEM_FRAME                               (false,  true,  true, false,  true, false, false, false, FramedItemFrameBlock::generateShapes),
    FRAMED_GLOWING_ITEM_FRAME                       (false,  true,  true, false,  true, false, false, false, FramedItemFrameBlock::generateShapes),
    FRAMED_FANCY_RAIL                               (false, false, false,  true,  true, false, false, false),
    FRAMED_FANCY_POWERED_RAIL                       (false, false, false,  true,  true, false, false, false),
    FRAMED_FANCY_DETECTOR_RAIL                      (false, false, false,  true,  true, false, false, false),
    FRAMED_FANCY_ACTIVATOR_RAIL                     (false, false, false,  true,  true, false, false, false),
    FRAMED_FANCY_RAIL_SLOPE                         ( true,  true, false,  true,  true, false,  true, false, FramedSlopeBlock::generateShapes),
    FRAMED_FANCY_POWERED_RAIL_SLOPE                 ( true,  true, false,  true,  true, false,  true, false, FramedSlopeBlock::generateShapes),
    FRAMED_FANCY_DETECTOR_RAIL_SLOPE                ( true,  true, false,  true,  true, false,  true, false, FramedSlopeBlock::generateShapes),
    FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE               ( true,  true, false,  true,  true, false,  true, false, FramedSlopeBlock::generateShapes),
    FRAMED_HALF_SLOPE                               (false,  true, false,  true,  true,  true, false, false, FramedHalfSlopeBlock::generateShapes),
    FRAMED_VERTICAL_HALF_SLOPE                      (false,  true, false,  true, false,  true, false, false, FramedVerticalHalfSlopeBlock::generateShapes),
    FRAMED_DIVIDED_SLOPE                            ( true,  true,  true,  true,  true,  true,  true, false, FramedSlopeBlock::generateShapes),
    FRAMED_DOUBLE_HALF_SLOPE                        ( true, false,  true,  true,  true,  true,  true, false, FramedDoubleHalfSlopeBlock::generateShapes),
    FRAMED_VERTICAL_DOUBLE_HALF_SLOPE               ( true, false,  true,  true, false,  true,  true, false, FramedVerticalDoubleHalfSlopeBlock::generateShapes),
    FRAMED_SLOPED_STAIRS                            ( true,  true, false,  true,  true,  true, false, false, FramedSlopedStairsBlock::generateShapes),
    FRAMED_VERTICAL_SLOPED_STAIRS                   ( true,  true, false,  true,  true,  true, false, false, FramedVerticalSlopedStairsBlock::generateShapes),
    FRAMED_MINI_CUBE                                (false, false, false,  true,  true,  true, false, false, Block.box(4, 0, 4, 12, 8, 12)),
    FRAMED_ONE_WAY_WINDOW                           (false, false,  true, false,  true, false, false, false, Shapes.block()),
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
    private final ShapeGenerator shapeGen;

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ShapeGenerator.EMPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, ShapeGenerator.singleShape(shape));
        Preconditions.checkArgument(!waterloggable || !Shapes.join(shape, Shapes.block(), BooleanOp.NOT_SAME).isEmpty(), "Blocks with full cube shape can't be waterloggable");
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, ShapeGenerator shapeGen)
    {
        this.canOcclude = canOcclude;
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.allowIntangible = allowIntangible;
        this.doubleBlock = doubleBlock;
        this.lockable = lockable;
        this.shapeGen = shapeGen;
    }

    @Override
    public boolean canOccludeWithSolidCamo()
    {
        return canOcclude;
    }

    @Override
    public boolean hasSpecialHitbox()
    {
        return specialHitbox;
    }

    @Override
    public FullFacePredicate getFullFacePredicate()
    {
        return FullFacePredicates.PREDICATES.get(this);
    }

    @Override
    public SideSkipPredicate getSideSkipPredicate()
    {
        return SideSkipPredicates.PREDICATES.get(this);
    }

    @Override
    public ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        if (!FMLEnvironment.production)
        {
            return new ReloadableShapeProvider(shapeGen, states);
        }
        return shapeGen.generate(states);
    }

    @Override
    public boolean hasSpecialTile()
    {
        return specialTile;
    }

    @Override
    public boolean hasBlockItem()
    {
        return blockItem;
    }

    @Override
    public boolean supportsWaterLogging()
    {
        return waterloggable;
    }

    @Override
    public boolean allowMakingIntangible()
    {
        return allowIntangible;
    }

    @Override
    public boolean isDoubleBlock()
    {
        return doubleBlock;
    }

    @Override
    public boolean canLockState()
    {
        return lockable;
    }

    @Override
    public String getName()
    {
        return name;
    }

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