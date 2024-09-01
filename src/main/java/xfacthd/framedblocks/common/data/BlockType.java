package xfacthd.framedblocks.common.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.common.data.conpreds.ConnectionPredicates;
import xfacthd.framedblocks.common.data.facepreds.FullFacePredicates;
import xfacthd.framedblocks.common.data.shapes.MoreCommonShapes;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.cube.*;
import xfacthd.framedblocks.common.data.shapes.door.*;
import xfacthd.framedblocks.common.data.shapes.interactive.*;
import xfacthd.framedblocks.common.data.shapes.pane.*;
import xfacthd.framedblocks.common.data.shapes.pillar.*;
import xfacthd.framedblocks.common.data.shapes.prism.*;
import xfacthd.framedblocks.common.data.shapes.sign.*;
import xfacthd.framedblocks.common.data.shapes.slab.*;
import xfacthd.framedblocks.common.data.shapes.slope.*;
import xfacthd.framedblocks.common.data.shapes.slopeedge.*;
import xfacthd.framedblocks.common.data.shapes.slopepanel.*;
import xfacthd.framedblocks.common.data.shapes.slopepanelcorner.*;
import xfacthd.framedblocks.common.data.shapes.slopeslab.*;
import xfacthd.framedblocks.common.data.shapes.stairs.standard.*;
import xfacthd.framedblocks.common.data.shapes.stairs.vertical.*;
import xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;

import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public enum BlockType implements IBlockType
{
    FRAMED_CUBE                                     ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPE                                    ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_DOUBLE_SLOPE                             ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_HALF_SLOPE                               (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, new HalfSlopeShapes()),
    FRAMED_VERTICAL_HALF_SLOPE                      (false,  true, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, new VerticalHalfSlopeShapes()),
    FRAMED_DIVIDED_SLOPE                            ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, SlopeShapes.INSTANCE),
    FRAMED_DOUBLE_HALF_SLOPE                        ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, DoubleHalfSlopeShapes::generate),
    FRAMED_VERTICAL_DOUBLE_HALF_SLOPE               ( true, false,  true,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, CommonShapes.SLAB_GENERATOR),
    FRAMED_CORNER_SLOPE                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, CornerSlopeShapes.OUTER),
    FRAMED_INNER_CORNER_SLOPE                       ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, CornerSlopeShapes.INNER),
    FRAMED_DOUBLE_CORNER                            ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_PRISM_CORNER                             (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, PrismCornerShapes.OUTER),
    FRAMED_INNER_PRISM_CORNER                       ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, PrismCornerShapes.INNER),
    FRAMED_DOUBLE_PRISM_CORNER                      ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_THREEWAY_CORNER                          (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, ThreewayCornerShapes.OUTER),
    FRAMED_INNER_THREEWAY_CORNER                    ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ThreewayCornerShapes.INNER),
    FRAMED_DOUBLE_THREEWAY_CORNER                   ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPE_EDGE                               (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, new SlopeEdgeShapes()),
    FRAMED_ELEVATED_SLOPE_EDGE                      ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ElevatedSlopeEdgeShapes.INSTANCE),
    FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE               ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_STACKED_SLOPE_EDGE                       ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, ElevatedSlopeEdgeShapes.INSTANCE),
    FRAMED_CORNER_SLOPE_EDGE                        (false,  true, false,  true,  true,  true, false, false, ConTexMode.DETAILED, CornerSlopeEdgeShapes.OUTER),
    FRAMED_INNER_CORNER_SLOPE_EDGE                  (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, CornerSlopeEdgeShapes.INNER),
    FRAMED_ELEVATED_CORNER_SLOPE_EDGE               ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, ElevatedCornerSlopeEdgeShapes.OUTER),
    FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE         ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ElevatedCornerSlopeEdgeShapes.INNER),
    FRAMED_SLAB                                     ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, CommonShapes.SLAB_GENERATOR),
    FRAMED_DOUBLE_SLAB                              ( true, false, false, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_SLAB                          ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_COPYCAT_SLAB                  ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DIVIDED_SLAB                             ( true, false, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, CommonShapes.SLAB_GENERATOR),
    FRAMED_SLAB_EDGE                                (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, SlabEdgeShapes::generate),
    FRAMED_SLAB_CORNER                              (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, SlabCornerShapes::generate),
    FRAMED_PANEL                                    ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, CommonShapes.PANEL_GENERATOR),
    FRAMED_DOUBLE_PANEL                             ( true, false, false, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_PANEL                         ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_COPYCAT_PANEL                 ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_DIVIDED_PANEL_HORIZONTAL                 ( true, false, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, CommonShapes.PANEL_GENERATOR),
    FRAMED_DIVIDED_PANEL_VERTICAL                   ( true, false, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, CommonShapes.PANEL_GENERATOR),
    FRAMED_CORNER_PILLAR                            (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, CornerPillarShapes::generate),
    FRAMED_STAIRS                                   ( true, false, false,  true,  true,  true, false,  true, ConTexMode.FULL_FACE),
    FRAMED_DOUBLE_STAIRS                            ( true, false, false, false,  true,  true,  true,  true, ConTexMode.FULL_FACE),
    FRAMED_HALF_STAIRS                              (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, HalfStairsShapes::generate),
    FRAMED_DIVIDED_STAIRS                           ( true, false, false,  true,  true,  true,  true,  true, ConTexMode.FULL_EDGE),
    FRAMED_DOUBLE_HALF_STAIRS                       ( true, false, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, DoubleHalfStairsShapes::generate),
    FRAMED_SLICED_STAIRS_SLAB                       ( true, false, false,  true,  true,  true,  true,  true, ConTexMode.FULL_FACE),
    FRAMED_SLICED_STAIRS_PANEL                      ( true, false, false,  true,  true,  true,  true,  true, ConTexMode.FULL_FACE),
    FRAMED_SLOPED_STAIRS                            ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, new SlopedStairsShapes()),
    FRAMED_VERTICAL_STAIRS                          ( true, false, false,  true,  true,  true, false,  true, ConTexMode.FULL_FACE, VerticalStairsShapes::generate),
    FRAMED_VERTICAL_DOUBLE_STAIRS                   ( true, false, false, false,  true,  true,  true,  true, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_VERTICAL_HALF_STAIRS                     (false, false, false,  true,  true, false, false, false, ConTexMode.FULL_EDGE, VerticalHalfStairsShapes::generate),
    FRAMED_VERTICAL_DIVIDED_STAIRS                  ( true, false, false,  true,  true,  true,  true,  true, ConTexMode.FULL_EDGE, VerticalStairsShapes::generate),
    FRAMED_VERTICAL_DOUBLE_HALF_STAIRS              ( true, false, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, CommonShapes.SLAB_GENERATOR),
    FRAMED_VERTICAL_SLICED_STAIRS                   ( true, false, false,  true,  true,  true,  true,  true, ConTexMode.FULL_FACE, VerticalStairsShapes::generate),
    FRAMED_VERTICAL_SLOPED_STAIRS                   ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, new VerticalSlopedStairsShapes()),
    FRAMED_THREEWAY_CORNER_PILLAR                   (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, ThreewayCornerPillarShapes::generate),
    FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR            ( true, false, false, false,  true,  true,  true, false, ConTexMode.FULL_EDGE, Shapes.block()),
    FRAMED_WALL                                     (false, false, false,  true,  true, false, false,  true, ConTexMode.DETAILED),
    FRAMED_FENCE                                    (false, false, false,  true,  true, false, false,  true, ConTexMode.DETAILED),
    FRAMED_FENCE_GATE                               (false, false, false, false,  true, false, false, false, ConTexMode.DETAILED),
    FRAMED_DOOR                                     ( true, false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE),
    FRAMED_IRON_DOOR                                ( true, false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE),
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
    FRAMED_SIGN                                     (false, false,  true,  true,  true, false, false, false, null),
    FRAMED_WALL_SIGN                                (false, false,  true,  true, false, false, false, false, null, WallSignShapes::generate),
    FRAMED_HANGING_SIGN                             (false, false,  true,  true,  true, false, false, false, null, CeilingHangingSignShapes::generate),
    FRAMED_WALL_HANGING_SIGN                        (false, false,  true,  true, false, false, false, false, null),
    FRAMED_TORCH                                    (false, false, false, false,  true, false, false, false, null),
    FRAMED_WALL_TORCH                               (false, false, false, false, false, false, false, false, null),
    FRAMED_SOUL_TORCH                               (false, false, false, false,  true, false, false, false, null),
    FRAMED_SOUL_WALL_TORCH                          (false, false, false, false, false, false, false, false, null),
    FRAMED_REDSTONE_TORCH                           (false, false, false, false,  true, false, false, false, null),
    FRAMED_REDSTONE_WALL_TORCH                      (false, false, false, false, false, false, false, false, null),
    FRAMED_FLOOR_BOARD                              ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FloorBoardShapes::generate),
    FRAMED_WALL_BOARD                               ( true, false, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, WallBoardShapes::generate),
    FRAMED_CORNER_STRIP                             (false, false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, CornerStripShapes::generate),
    FRAMED_LATTICE_BLOCK                            (false, false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED, LatticeShapes.THIN),
    FRAMED_THICK_LATTICE                            (false, false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED, LatticeShapes.THICK),
    FRAMED_CHEST                                    (false, false,  true,  true,  true, false, false, false, null, Block.box(1, 0, 1, 15, 14, 15)),
    FRAMED_SECRET_STORAGE                           ( true, false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_TANK                                     ( true, false,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_BARS                                     (false, false, false,  true,  true,  true, false,  true, null),
    FRAMED_PANE                                     (false, false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED),
    FRAMED_HORIZONTAL_PANE                          ( true, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, Block.box(0, 7, 0, 16, 9, 16)),
    FRAMED_RAIL_SLOPE                               ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_POWERED_RAIL_SLOPE                       ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_DETECTOR_RAIL_SLOPE                      ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_ACTIVATOR_RAIL_SLOPE                     ( true,  true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_FANCY_RAIL                               (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_POWERED_RAIL                       (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_DETECTOR_RAIL                      (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_ACTIVATOR_RAIL                     (false, false, false,  true,  true, false, false, false, null),
    FRAMED_FANCY_RAIL_SLOPE                         ( true,  true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_FANCY_POWERED_RAIL_SLOPE                 ( true,  true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_FANCY_DETECTOR_RAIL_SLOPE                ( true,  true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE               ( true,  true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, SlopeShapes.INSTANCE),
    FRAMED_FLOWER_POT                               (false, false,  true, false,  true, false, false, false, null, Block.box(5, 0, 5, 11, 6, 11)),
    FRAMED_PILLAR                                   (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, PillarShapes.PILLAR),
    FRAMED_HALF_PILLAR                              (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, HalfPillarShapes::generate),
    FRAMED_POST                                     (false, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, PillarShapes.POST),
    FRAMED_COLLAPSIBLE_BLOCK                        (false,  true,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE),
    FRAMED_COLLAPSIBLE_COPYCAT_BLOCK                (false, false,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE),
    FRAMED_BOUNCY_CUBE                              ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_REDSTONE_BLOCK                           ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_PRISM                                    ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, PrismShapes.OUTER),
    FRAMED_ELEVATED_INNER_PRISM                     ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ElevatedPrismShapes.INNER),
    FRAMED_ELEVATED_INNER_DOUBLE_PRISM              ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPED_PRISM                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, SlopedPrismShapes.OUTER),
    FRAMED_ELEVATED_INNER_SLOPED_PRISM              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ElevatedSlopedPrismShapes.INNER),
    FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM       ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_SLOPE_SLAB                               ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, new SlopeSlabShapes()),
    FRAMED_ELEVATED_SLOPE_SLAB                      ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ElevatedSlopeSlabShapes.INSTANCE),
    FRAMED_COMPOUND_SLOPE_SLAB                      ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, InverseDoubleSlopeSlabShapes.INSTANCE),
    FRAMED_DOUBLE_SLOPE_SLAB                        ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, MoreCommonShapes.TOP_HALF_SLAB_GENERATOR),
    FRAMED_INV_DOUBLE_SLOPE_SLAB                    ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, InverseDoubleSlopeSlabShapes.INSTANCE),
    FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB               ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_STACKED_SLOPE_SLAB                       ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, ElevatedSlopeSlabShapes.INSTANCE),
    FRAMED_FLAT_SLOPE_SLAB_CORNER                   ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatSlopeSlabCornerShapes.OUTER),
    FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatSlopeSlabCornerShapes.INNER),
    FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatElevatedSlopeSlabCornerShapes.OUTER),
    FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER        ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatElevatedSlopeSlabCornerShapes.INNER),
    FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER            ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, MoreCommonShapes.TOP_HALF_SLAB_GENERATOR),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER        ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, new FlatInverseDoubleSlopeSlabCornerShapes()),
    FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER       ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER           ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FlatElevatedSlopeSlabCornerShapes.OUTER),
    FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER     ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FlatElevatedSlopeSlabCornerShapes.INNER),
    FRAMED_SLOPE_PANEL                              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, new SlopePanelShapes()),
    FRAMED_EXTENDED_SLOPE_PANEL                     ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ExtendedSlopePanelShapes.INSTANCE),
    FRAMED_COMPOUND_SLOPE_PANEL                     ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, InverseDoubleSlopePanelShapes.INSTANCE),
    FRAMED_DOUBLE_SLOPE_PANEL                       ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, MoreCommonShapes.FRONT_INV_PANEL_GENERATOR),
    FRAMED_INV_DOUBLE_SLOPE_PANEL                   ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, InverseDoubleSlopePanelShapes.INSTANCE),
    FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL              ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_STACKED_SLOPE_PANEL                      ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, ExtendedSlopePanelShapes.INSTANCE),
    FRAMED_FLAT_SLOPE_PANEL_CORNER                  ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatSlopePanelCornerShapes.OUTER),
    FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER            ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatSlopePanelCornerShapes.INNER),
    FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER              ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatExtendedSlopePanelCornerShapes.OUTER),
    FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER        ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, FlatExtendedSlopePanelCornerShapes.INNER),
    FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER           ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, MoreCommonShapes.FRONT_INV_PANEL_GENERATOR),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER       ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, new FlatInverseDoubleSlopePanelCornerShapes()),
    FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER       ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER          ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FlatExtendedSlopePanelCornerShapes.OUTER),
    FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER    ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, FlatExtendedSlopePanelCornerShapes.INNER),
    FRAMED_SMALL_CORNER_SLOPE_PANEL                 (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, CornerSlopePanelShapes.SMALL_OUTER),
    FRAMED_SMALL_CORNER_SLOPE_PANEL_W               (false,  true, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, new CornerSlopePanelWallShapes.SmallOuter()),
    FRAMED_LARGE_CORNER_SLOPE_PANEL                 (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, CornerSlopePanelShapes.LARGE_OUTER),
    FRAMED_LARGE_CORNER_SLOPE_PANEL_W               (false,  true, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, new CornerSlopePanelWallShapes.LargeOuter()),
    FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL           (false,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, CornerSlopePanelShapes.SMALL_INNER),
    FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W         (false,  true, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, new CornerSlopePanelWallShapes.SmallInner()),
    FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL           ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, CornerSlopePanelShapes.LARGE_INNER),
    FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W         ( true,  true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, new CornerSlopePanelWallShapes.LargeInner()),
    FRAMED_EXT_CORNER_SLOPE_PANEL                   ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ExtendedCornerSlopePanelShapes.OUTER),
    FRAMED_EXT_CORNER_SLOPE_PANEL_W                 ( true,  true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, ExtendedCornerSlopePanelWallShapes.OUTER),
    FRAMED_EXT_INNER_CORNER_SLOPE_PANEL             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, ExtendedCornerSlopePanelShapes.INNER),
    FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W           ( true,  true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, ExtendedCornerSlopePanelWallShapes.INNER),
    FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL          (false, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, CornerPillarShapes::generate),
    FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W        (false, false,  true,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, DoubleCornerSlopePanelWallShapes::generateSmall),
    FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL          ( true, false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, DoubleCornerSlopePanelShapes::generate),
    FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W        ( true, false,  true,  true, false,  true,  true, false, ConTexMode.FULL_FACE, DoubleCornerSlopePanelWallShapes::generateLarge),
    FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL            ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, new InverseDoubleCornerSlopePanelShapes()),
    FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W          ( true,  true, false,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, new InverseDoubleCornerSlopePanelWallShapes()),
    FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL            ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W          ( true, false,  true, false, false,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL      ( true, false,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W    ( true, false,  true, false, false,  true,  true, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_STACKED_CORNER_SLOPE_PANEL               ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, ExtendedCornerSlopePanelShapes.OUTER),
    FRAMED_STACKED_CORNER_SLOPE_PANEL_W             ( true,  true, false,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, ExtendedCornerSlopePanelWallShapes.OUTER),
    FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL         ( true,  true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, ExtendedCornerSlopePanelShapes.INNER),
    FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W       ( true,  true, false,  true, false,  true,  true, false, ConTexMode.FULL_FACE, ExtendedCornerSlopePanelWallShapes.INNER),
    FRAMED_GLOWING_CUBE                             ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_PYRAMID                                  ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, PyramidShapes.FULL),
    FRAMED_PYRAMID_SLAB                             ( true,  true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, PyramidShapes.SLAB),
    FRAMED_TARGET                                   ( true, false,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_GATE                                     ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE, GateShapes::generate),
    FRAMED_IRON_GATE                                ( true, false, false, false,  true, false, false, false, ConTexMode.FULL_FACE, GateShapes::generate),
    FRAMED_ITEM_FRAME                               (false,  true,  true, false,  true, false, false, false, null, ItemFrameShapes::generate),
    FRAMED_GLOWING_ITEM_FRAME                       (false,  true,  true, false,  true, false, false, false, null, ItemFrameShapes::generate),
    FRAMED_MINI_CUBE                                (false, false, false,  true,  true,  true, false, false, null, Block.box(4, 0, 4, 12, 8, 12)),
    FRAMED_ONE_WAY_WINDOW                           (false, false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_BOOKSHELF                                ( true, false, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_CHISELED_BOOKSHELF                       ( true, false,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Shapes.block()),
    FRAMED_CENTERED_SLAB                            ( true, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, Block.box(0, 4, 0, 16, 12, 16)),
    FRAMED_CENTERED_PANEL                           ( true, false, false,  true,  true,  true, false, false, ConTexMode.DETAILED, CenteredPanelShapes::generate),
    FRAMED_MASONRY_CORNER_SEGMENT                   (false, false, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, MasonryCornerSegmentShapes::generate),
    FRAMED_MASONRY_CORNER                           ( true, false, false, false,  true,  true,  true, false, ConTexMode.FULL_EDGE, Shapes.block()),
    FRAMED_CHECKERED_CUBE_SEGMENT                   (false, false, false,  true, false,  true, false, false, ConTexMode.DETAILED, CheckeredCubeSegmentShapes::generate),
    FRAMED_CHECKERED_CUBE                           ( true, false, false, false,  true,  true,  true, false, ConTexMode.DETAILED, Shapes.block()),
    FRAMED_CHECKERED_SLAB_SEGMENT                   (false, false, false,  true, false,  true, false, false, ConTexMode.DETAILED, CheckeredSlabSegmentShapes::generate),
    FRAMED_CHECKERED_SLAB                           ( true, false, false,  true,  true,  true,  true, false, ConTexMode.DETAILED, CommonShapes.SLAB_GENERATOR),
    FRAMED_CHECKERED_PANEL_SEGMENT                  (false, false, false,  true, false,  true, false, false, ConTexMode.DETAILED, CheckeredPanelSegmentShapes::generate),
    FRAMED_CHECKERED_PANEL                          ( true, false, false,  true,  true,  true,  true, false, ConTexMode.DETAILED, CommonShapes.PANEL_GENERATOR),
    FRAMED_TUBE                                     ( true, false, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, TubeShapes::generate),
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
    private final boolean separateOcclusionShapes;

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, minCTMode, ShapeGenerator.EMPTY);
    }

    BlockType(boolean canOcclude, boolean specialHitbox, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode, VoxelShape shape)
    {
        this(canOcclude, specialHitbox, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, minCTMode, ShapeGenerator.singleShape(shape));
        Preconditions.checkArgument(!waterloggable || !Shapes.joinUnoptimized(shape, Shapes.block(), BooleanOp.NOT_SAME).isEmpty(), "Blocks with full cube shape can't be waterloggable");
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
        this.separateOcclusionShapes = shapeGen instanceof SplitShapeGenerator;
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
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states, ShapeProvider shapes)
    {
        if (separateOcclusionShapes)
        {
            SplitShapeGenerator splitShapeGen = (SplitShapeGenerator) shapeGen;
            if (!FMLEnvironment.production)
            {
                return new ReloadableShapeProvider(splitShapeGen::generateOcclusionShapes, states);
            }
            return splitShapeGen.generateOcclusionShapes(states);
        }
        return shapes;
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
    public boolean consumesTwoCamosInCamoApplicationRecipe()
    {
        return doubleBlock || this == FRAMED_DOOR || this == FRAMED_IRON_DOOR;
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