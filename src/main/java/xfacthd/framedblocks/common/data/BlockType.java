package xfacthd.framedblocks.common.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.*;
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
import xfacthd.framedblocks.common.data.conpreds.ConnectionPredicates;
import xfacthd.framedblocks.common.data.facepreds.FullFacePredicates;
import xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;

import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public enum BlockType implements IBlockType
{
    FRAMED_CUBE                                     ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPE                                    ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_CORNER_SLOPE                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedCornerSlopeBlock::generateCornerShapes),
    FRAMED_INNER_CORNER_SLOPE                       ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedCornerSlopeBlock::generateInnerCornerShapes),
    FRAMED_PRISM_CORNER                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, FramedPrismCornerBlock::generatePrismShapes),
    FRAMED_INNER_PRISM_CORNER                       ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedPrismCornerBlock::generateInnerPrismShapes),
    FRAMED_THREEWAY_CORNER                          ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, FramedThreewayCornerBlock::generateThreewayShapes),
    FRAMED_INNER_THREEWAY_CORNER                    ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedThreewayCornerBlock::generateInnerThreewayShapes),
    FRAMED_SLAB                                     ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlabBlock::generateShapes),
    FRAMED_SLAB_EDGE                                (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, FramedSlabEdgeBlock::generateShapes),
    FRAMED_SLAB_CORNER                              (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, FramedSlabCornerBlock::generateShapes),
    FRAMED_DIVIDED_SLAB                             ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedSlabBlock::generateShapes),
    FRAMED_PANEL                                    ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedPanelBlock::generateShapes),
    FRAMED_CORNER_PILLAR                            (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, FramedCornerPillarBlock::generateShapes),
    FRAMED_DIVIDED_PANEL_HORIZONTAL                 ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedPanelBlock::generateShapes),
    FRAMED_DIVIDED_PANEL_VERTICAL                   ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedPanelBlock::generateShapes),
    FRAMED_STAIRS                                   ( true, false, false,  true,  true,  true, false,  true, ConTexMode.FULL_FACE),
    FRAMED_DOUBLE_STAIRS                            ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_HALF_STAIRS                              (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, FramedHalfStairsBlock::generateShapes),
    FRAMED_DIVIDED_STAIRS                           ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedDividedStairsBlock::generateShapes),
    FRAMED_VERTICAL_STAIRS                          ( true, false, false,  true,  true,  true, false,  true, ConTexMode.FULL_FACE, FramedVerticalStairsBlock::generateShapes),
    FRAMED_VERTICAL_DOUBLE_STAIRS                   ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_VERTICAL_HALF_STAIRS                     (false, false, false,  true,  true, false, false, false, ConTexMode.FULL_EDGE, FramedVerticalHalfStairsBlock::generateShapes),
    FRAMED_VERTICAL_DIVIDED_STAIRS                  ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedVerticalDividedStairsBlock::generateShapes),
    FRAMED_WALL                                     (false, false, false,  true,  true, false, false,  true, ConTexMode.DETAILED),
    FRAMED_FENCE                                    (false, false, false,  true,  true, false, false,  true, ConTexMode.DETAILED),
    FRAMED_FENCE_GATE                               (false, false, false,  true,  true, false, false, false, ConTexMode.DETAILED),
    FRAMED_DOOR                                     ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_IRON_DOOR                                ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_TRAPDOOR                                 ( true, false, false,  true,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_IRON_TRAPDOOR                            ( true, false, false,  true,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_PRESSURE_PLATE                           (false, false, false, false,  true, false, false, false, null),
    FRAMED_WATERLOGGABLE_PRESSURE_PLATE             (false, false, false,  true, false, false, false, false, null),
    FRAMED_STONE_PRESSURE_PLATE                     (false, false, false, false,  true, false, false, false, null),
    FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE       (false, false, false,  true, false, false, false, false, null),
    FRAMED_OBSIDIAN_PRESSURE_PLATE                  (false, false, false, false,  true, false, false, false, null),
    FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE    (false, false, false,  true, false, false, false, false, null),
    FRAMED_GOLD_PRESSURE_PLATE                      (false, false, false, false,  true, false, false, false, null),
    FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE        (false, false, false,  true, false, false, false, false, null),
    FRAMED_IRON_PRESSURE_PLATE                      (false, false, false, false,  true, false, false, false, null),
    FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE        (false, false, false,  true, false, false, false, false, null),
    FRAMED_LADDER                                   (false, false, false,  true,  true, false, false, false, ConTexMode.DETAILED),
    FRAMED_BUTTON                                   (false, false, false, false,  true, false, false, false, null),
    FRAMED_STONE_BUTTON                             (false, false, false, false,  true, false, false, false, null),
    FRAMED_LARGE_BUTTON                             (false, false, false, false,  true, false, false, false, null),
    FRAMED_LARGE_STONE_BUTTON                       (false, false, false, false,  true, false, false, false, null),
    FRAMED_LEVER                                    (false, false, false, false,  true, false, false, false, null),
    FRAMED_SIGN                                     (false, false,  true, false,  true, false, false, false, null),
    FRAMED_WALL_SIGN                                (false, false,  true, false, false, false, false, false, null, FramedWallSignBlock::generateShapes),
    FRAMED_DOUBLE_SLAB                              ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DOUBLE_PANEL                             ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DOUBLE_SLOPE                             ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DOUBLE_CORNER                            ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DOUBLE_PRISM_CORNER                      ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DOUBLE_THREEWAY_CORNER                   ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_TORCH                                    (false, false, false, false,  true, false, false, false, null),
    FRAMED_WALL_TORCH                               (false, false, false, false, false, false, false, false, null),
    FRAMED_SOUL_TORCH                               (false, false, false, false,  true, false, false, false, null),
    FRAMED_SOUL_WALL_TORCH                          (false, false, false, false, false, false, false, false, null),
    FRAMED_REDSTONE_TORCH                           (false, false, false, false,  true, false, false, false, null),
    FRAMED_REDSTONE_WALL_TORCH                      (false, false, false, false, false, false, false, false, null),
    FRAMED_FLOOR_BOARD                              ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFloorBlock::generateShapes),
    FRAMED_WALL_BOARD                               ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedWallBoardBlock::generateShapes),
    FRAMED_LATTICE_BLOCK                            (false, false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED, FramedLatticeBlock::generateShapes),
    FRAMED_CHEST                                    (false, false,  true,  true,  true, false, false, false, null, Shapes.box(1D/16D, 0, 1D/16D, 15D/16D, 14D/16D, 15D/16D)),
    FRAMED_SECRET_STORAGE                           ( true, false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_BARS                                     (false, false, false,  true,  true,  true, false,  true, null),
    FRAMED_PANE                                     (false, false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED),
    FRAMED_HORIZONTAL_PANE                          ( true, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, Shapes.box(0, 7D/16D, 0, 1, 9D/16D, 1)),
    FRAMED_RAIL_SLOPE                               ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_POWERED_RAIL_SLOPE                       ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_DETECTOR_RAIL_SLOPE                      ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_ACTIVATOR_RAIL_SLOPE                     ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_FLOWER_POT                               (false, false,  true, false,  true, false, false, false, null, Shapes.box(5D/16D, 0, 5D/16D, 11D/16D, 6D/16D, 11D/16D)),
    FRAMED_PILLAR                                   (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, FramedPillarBlock::generatePillarShapes),
    FRAMED_HALF_PILLAR                              (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, FramedHalfPillarBlock::generateShapes),
    FRAMED_POST                                     (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, FramedPillarBlock::generatePostShapes),
    FRAMED_COLLAPSIBLE_BLOCK                        (false,  true,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE),
    FRAMED_BOUNCY_CUBE                              ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_REDSTONE_BLOCK                           ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_PRISM                                    ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedPrismBlock::generateShapes),
    FRAMED_INNER_PRISM                              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedPrismBlock::generateInnerShapes),
    FRAMED_DOUBLE_PRISM                             ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPED_PRISM                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlopedPrismBlock::generateShapes),
    FRAMED_INNER_SLOPED_PRISM                       ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlopedPrismBlock::generateInnerShapes),
    FRAMED_DOUBLE_SLOPED_PRISM                      ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPE_SLAB                               ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_SLOPE_SLAB                      ( true,  true,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_SLAB                        ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE),
    FRAMED_INV_DOUBLE_SLOPE_SLAB                    ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedInverseDoubleSlopeSlabBlock::generateShapes),
    FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB               ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_STACKED_SLOPE_SLAB                       ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedElevatedSlopeSlabBlock::generateShapes),
    FRAMED_FLAT_SLOPE_SLAB_CORNER                   ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatElevatedSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER        ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatElevatedSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER            ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedFlatDoubleSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER        ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedFlatInverseDoubleSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER       ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER           ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedFlatElevatedSlopeSlabCornerBlock::generateShapes),
    FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER     ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedFlatElevatedSlopeSlabCornerBlock::generateInnerShapes),
    FRAMED_SLOPE_PANEL                              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlopePanelBlock::generateShapes),
    FRAMED_EXTENDED_SLOPE_PANEL                     ( true,  true,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedExtendedSlopePanelBlock::generateShapes),
    FRAMED_DOUBLE_SLOPE_PANEL                       ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedDoubleSlopePanelBlock::generateShapes),
    FRAMED_INV_DOUBLE_SLOPE_PANEL                   ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedInverseDoubleSlopePanelBlock::generateShapes),
    FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL              ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_STACKED_SLOPE_PANEL                      ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedExtendedSlopePanelBlock::generateShapes),
    FRAMED_FLAT_SLOPE_PANEL_CORNER                  ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER            ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatExtendedSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER        ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedFlatExtendedSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER           ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedFlatDoubleSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER       ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedFlatInverseDoubleSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER       ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER          ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedFlatExtendedSlopePanelCornerBlock::generateShapes),
    FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER    ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FramedFlatExtendedSlopePanelCornerBlock::generateInnerShapes),
    FRAMED_GLOWING_CUBE                             ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_PYRAMID                                  ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedPyramidBlock::generateShapes),
    FRAMED_PYRAMID_SLAB                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedPyramidBlock::generateSlabShapes),
    FRAMED_TARGET                                   ( true, false,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_GATE                                     ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_IRON_GATE                                ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_ITEM_FRAME                               (false,  true,  true, false,  true, false, false, false, null, FramedItemFrameBlock::generateShapes),
    FRAMED_GLOWING_ITEM_FRAME                       (false,  true,  true, false,  true, false, false, false, null, FramedItemFrameBlock::generateShapes),
    FRAMED_FANCY_RAIL                               (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_POWERED_RAIL                       (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_DETECTOR_RAIL                      (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_ACTIVATOR_RAIL                     (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_RAIL_SLOPE                         ( true,  true, false,  true,  true, false,  true, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_FANCY_POWERED_RAIL_SLOPE                 ( true,  true, false,  true,  true, false,  true, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_FANCY_DETECTOR_RAIL_SLOPE                ( true,  true, false,  true,  true, false,  true, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE               ( true,  true, false,  true,  true, false,  true, false, ConTexMode.FULL_FACE, FramedSlopeBlock::generateShapes),
    FRAMED_HALF_SLOPE                               (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, FramedHalfSlopeBlock::generateShapes),
    FRAMED_VERTICAL_HALF_SLOPE                      (false,  true, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, FramedVerticalHalfSlopeBlock::generateShapes),
    FRAMED_DIVIDED_SLOPE                            ( true,  true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedSlopeBlock::generateShapes),
    FRAMED_DOUBLE_HALF_SLOPE                        ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, FramedDoubleHalfSlopeBlock::generateShapes),
    FRAMED_VERTICAL_DOUBLE_HALF_SLOPE               ( true, false,  true,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, FramedVerticalDoubleHalfSlopeBlock::generateShapes),
    FRAMED_SLOPED_STAIRS                            ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedSlopedStairsBlock::generateShapes),
    FRAMED_VERTICAL_SLOPED_STAIRS                   ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FramedVerticalSlopedStairsBlock::generateShapes),
    FRAMED_MINI_CUBE                                (false, false, false,  true,  true,  true, false, false, null, Block.box(4, 0, 4, 12, 8, 12)),
    FRAMED_ONE_WAY_WINDOW                           (false, false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Shapes.block()),
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
    private final boolean supportsCT;
    private final ConTexMode minCTMode;
    private final ShapeGenerator shapeGen;

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, minCTMode, ShapeGenerator.EMPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, minCTMode, ShapeGenerator.singleShape(shape));
        Preconditions.checkArgument(!waterloggable || !Shapes.join(shape, Shapes.block(), BooleanOp.NOT_SAME).isEmpty(), "Blocks with full cube shape can't be waterloggable");
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode, ShapeGenerator shapeGen)
    {
        this.canOcclude = canOcclude;
        this.specialHitbox = specialHitbox;
        this.specialTile = specialTile;
        this.waterloggable = waterloggable;
        this.blockItem = blockItem;
        this.allowIntangible = allowIntangible;
        this.doubleBlock = doubleBlock;
        this.lockable = lockable;
        this.supportsCT = minCTMode != null;
        this.minCTMode = Objects.requireNonNullElse(minCTMode, ConTexMode.NONE);
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
    public ConnectionPredicate getConnectionPredicate()
    {
        return ConnectionPredicates.PREDICATES.get(this);
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
    public boolean supportsConnectedTextures()
    {
        return supportsCT;
    }

    @Override
    public ConTexMode getMinimumConTexMode()
    {
        return minCTMode;
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