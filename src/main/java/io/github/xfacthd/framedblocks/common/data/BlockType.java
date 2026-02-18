package io.github.xfacthd.framedblocks.common.data;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.common.data.conpreds.ConnectionPredicates;
import io.github.xfacthd.framedblocks.common.data.facepreds.FullFacePredicates;
import io.github.xfacthd.framedblocks.common.data.shapes.MoreCommonShapes;
import io.github.xfacthd.framedblocks.common.data.shapes.cube.*;
import io.github.xfacthd.framedblocks.common.data.shapes.door.*;
import io.github.xfacthd.framedblocks.common.data.shapes.interactive.*;
import io.github.xfacthd.framedblocks.common.data.shapes.pane.*;
import io.github.xfacthd.framedblocks.common.data.shapes.pillar.*;
import io.github.xfacthd.framedblocks.common.data.shapes.prism.*;
import io.github.xfacthd.framedblocks.common.data.shapes.sign.*;
import io.github.xfacthd.framedblocks.common.data.shapes.slab.*;
import io.github.xfacthd.framedblocks.common.data.shapes.slope.*;
import io.github.xfacthd.framedblocks.common.data.shapes.slopeedge.*;
import io.github.xfacthd.framedblocks.common.data.shapes.slopepanel.*;
import io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner.*;
import io.github.xfacthd.framedblocks.common.data.shapes.slopeslab.*;
import io.github.xfacthd.framedblocks.common.data.shapes.stairs.standard.*;
import io.github.xfacthd.framedblocks.common.data.shapes.stairs.vertical.*;
import io.github.xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public enum BlockType implements IBlockType
{
    FRAMED_CUBE                                     ( true, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_SLOPE                                    ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, SlopeShapes.INSTANCE),
    FRAMED_DOUBLE_SLOPE                             ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_HALF_SLOPE                               (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, new HalfSlopeShapes()),
    FRAMED_VERTICAL_HALF_SLOPE                      (false, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, new VerticalHalfSlopeShapes()),
    FRAMED_DIVIDED_SLOPE                            ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, SlopeShapes.INSTANCE),
    FRAMED_DOUBLE_HALF_SLOPE                        ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, DoubleHalfSlopeShapes::generate),
    FRAMED_VERTICAL_DOUBLE_HALF_SLOPE               ( true,  true,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CommonShapes.SLAB_GENERATOR),
    FRAMED_CORNER_SLOPE                             ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, CornerSlopeShapes.OUTER),
    FRAMED_INNER_CORNER_SLOPE                       ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, CornerSlopeShapes.INNER),
    FRAMED_DOUBLE_CORNER                            ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_PRISM_CORNER                             (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, PrismCornerShapes.OUTER),
    FRAMED_INNER_PRISM_CORNER                       ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PrismCornerShapes.INNER),
    FRAMED_DOUBLE_PRISM_CORNER                      ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_THREEWAY_CORNER                          (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, ThreewayCornerShapes.OUTER),
    FRAMED_INNER_THREEWAY_CORNER                    ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ThreewayCornerShapes.INNER),
    FRAMED_DOUBLE_THREEWAY_CORNER                   ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_SLOPE_EDGE                               (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, new SlopeEdgeShapes()),
    FRAMED_ELEVATED_SLOPE_EDGE                      ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedSlopeEdgeShapes.INSTANCE),
    FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE               ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_STACKED_SLOPE_EDGE                       ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedSlopeEdgeShapes.INSTANCE),
    FRAMED_CORNER_SLOPE_EDGE                        (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.MODEL, CornerSlopeEdgeShapes.OUTER),
    FRAMED_INNER_CORNER_SLOPE_EDGE                  (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, CornerSlopeEdgeShapes.INNER),
    FRAMED_ELEVATED_CORNER_SLOPE_EDGE               ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, ElevatedCornerSlopeEdgeShapes.OUTER),
    FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE         ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedCornerSlopeEdgeShapes.INNER),
    FRAMED_ELEV_DOUBLE_CORNER_SLOPE_EDGE            ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, Shapes.block()),
    FRAMED_ELEV_DOUBLE_INNER_CORNER_SLOPE_EDGE      ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_STACKED_CORNER_SLOPE_EDGE                ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, ElevatedCornerSlopeEdgeShapes.OUTER),
    FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE          ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedCornerSlopeEdgeShapes.INNER),
    FRAMED_THREEWAY_CORNER_SLOPE_EDGE               (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.MODEL, ThreewayCornerSlopeEdgeShapes.OUTER),
    FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE         (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, ThreewayCornerSlopeEdgeShapes.INNER),
    FRAMED_SLOPE_EDGE_SLAB                          ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, new SlopeEdgeSlabShapes()),
    FRAMED_SLOPE_EDGE_PANEL                         ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, new SlopeEdgePanelShapes()),
    FRAMED_SLAB                                     ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, CommonShapes.SLAB_GENERATOR),
    FRAMED_DOUBLE_SLAB                              ( true, false, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_SLAB                          ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_COPYCAT_SLAB                  ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_DIVIDED_SLAB                             ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CommonShapes.SLAB_GENERATOR),
    FRAMED_SLAB_EDGE                                (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, SlabEdgeShapes::generate),
    FRAMED_SLAB_CORNER                              (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, SlabCornerShapes::generate),
    FRAMED_PANEL                                    ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, CommonShapes.PANEL_GENERATOR),
    FRAMED_DOUBLE_PANEL                             ( true, false, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_PANEL                         ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_ADJ_DOUBLE_COPYCAT_PANEL                 ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_DIVIDED_PANEL_HORIZONTAL                 ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CommonShapes.PANEL_GENERATOR),
    FRAMED_DIVIDED_PANEL_VERTICAL                   ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CommonShapes.PANEL_GENERATOR),
    FRAMED_CORNER_PILLAR                            (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CornerPillarShapes::generate),
    FRAMED_STAIRS                                   ( true, false,  true,  true,  true, false,  true, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_DOUBLE_STAIRS                            ( true, false, false,  true,  true,  true,  true, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_HALF_STAIRS                              (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, HalfStairsShapes::generate),
    FRAMED_DIVIDED_STAIRS                           ( true, false,  true,  true,  true,  true,  true, ConTexMode.FULL_EDGE, Outline.SIMPLE),
    FRAMED_DOUBLE_HALF_STAIRS                       ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, DoubleHalfStairsShapes::generate),
    FRAMED_SLICED_STAIRS_SLAB                       ( true, false,  true,  true,  true,  true,  true, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_SLICED_STAIRS_PANEL                      ( true, false,  true,  true,  true,  true,  true, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_SLOPED_STAIRS                            ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, SlopedStairsShapes.INSTANCE),
    FRAMED_SLOPED_DOUBLE_STAIRS                     ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_SLICED_SLOPED_STAIRS_SLAB                ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, SlopedStairsShapes.INSTANCE),
    FRAMED_SLICED_SLOPED_STAIRS_SLOPE               ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, SlopedStairsShapes.INSTANCE),
    FRAMED_VERTICAL_STAIRS                          ( true, false,  true,  true,  true, false,  true, ConTexMode.FULL_FACE, Outline.SIMPLE, VerticalStairsShapes::generate),
    FRAMED_VERTICAL_DOUBLE_STAIRS                   ( true, false, false,  true,  true,  true,  true, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_VERTICAL_HALF_STAIRS                     (false, false,  true,  true, false, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, VerticalHalfStairsShapes::generate),
    FRAMED_VERTICAL_DIVIDED_STAIRS                  ( true, false,  true,  true,  true,  true,  true, ConTexMode.FULL_EDGE, Outline.SIMPLE, VerticalStairsShapes::generate),
    FRAMED_VERTICAL_DOUBLE_HALF_STAIRS              ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CommonShapes.SLAB_GENERATOR),
    FRAMED_VERTICAL_SLICED_STAIRS                   ( true, false,  true,  true,  true,  true,  true, ConTexMode.FULL_FACE, Outline.SIMPLE, VerticalStairsShapes::generate),
    FRAMED_VERTICAL_SLOPED_STAIRS                   ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, VerticalSlopedStairsShapes.INSTANCE),
    FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS            ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL      ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, VerticalSlopedStairsShapes.INSTANCE),
    FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE      ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, VerticalSlopedStairsShapes.INSTANCE),
    FRAMED_THREEWAY_CORNER_PILLAR                   (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, ThreewayCornerPillarShapes::generate),
    FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR            ( true, false, false,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, Shapes.block()),
    FRAMED_WALL                                     (false, false,  true,  true, false, false,  true, ConTexMode.DETAILED,  Outline.SIMPLE, new WallShapes()),
    FRAMED_FENCE                                    (false, false,  true,  true, false, false,  true, ConTexMode.DETAILED,  Outline.SIMPLE),
    FRAMED_FENCE_GATE                               (false, false, false,  true, false, false, false, ConTexMode.DETAILED,  Outline.SIMPLE),
    FRAMED_DOOR                                     ( true,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_IRON_DOOR                                ( true,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_TRAPDOOR                                 ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_IRON_TRAPDOOR                            ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_PRESSURE_PLATE                           (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WATERLOGGABLE_PRESSURE_PLATE             (false, false,  true, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_STONE_PRESSURE_PLATE                     (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE       (false, false,  true, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_OBSIDIAN_PRESSURE_PLATE                  (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE    (false, false,  true, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_GOLD_PRESSURE_PLATE                      (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE        (false, false,  true, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_IRON_PRESSURE_PLATE                      (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE        (false, false,  true, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_LADDER                                   (false, false,  true,  true, false, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, new LadderShapes()),
    FRAMED_BUTTON                                   (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_STONE_BUTTON                             (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_LARGE_BUTTON                             (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_LARGE_STONE_BUTTON                       (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_LEVER                                    (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_SIGN                                     (false,  true,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WALL_SIGN                                (false,  true,  true, false, false, false, false, null,                 Outline.SIMPLE, WallSignShapes::generate),
    FRAMED_HANGING_SIGN                             (false,  true,  true,  true, false, false, false, null,                 Outline.SIMPLE, CeilingHangingSignShapes::generate),
    FRAMED_WALL_HANGING_SIGN                        (false,  true,  true, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_TORCH                                    (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_WALL_TORCH                               (false, false, false, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_SOUL_TORCH                               (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_SOUL_WALL_TORCH                          (false, false, false, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_COPPER_TORCH                             (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_COPPER_WALL_TORCH                        (false, false, false, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_REDSTONE_TORCH                           (false, false, false,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_REDSTONE_WALL_TORCH                      (false, false, false, false, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_BOARD                                    ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, BoardShapes::generate),
    FRAMED_CORNER_STRIP                             (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CornerStripShapes::generate),
    FRAMED_LATTICE_BLOCK                            (false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED,  Outline.SIMPLE, LatticeShapes.THIN),
    FRAMED_THICK_LATTICE                            (false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED,  Outline.SIMPLE, LatticeShapes.THICK),
    FRAMED_CHEST                                    (false,  true,  true,  true, false, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, ChestShapes::generate),
    FRAMED_SECRET_STORAGE                           ( true,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_TANK                                     ( true,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_BARS                                     (false, false,  true,  true,  true, false,  true, null,                 Outline.SIMPLE),
    FRAMED_PANE                                     (false, false,  true,  true,  true, false,  true, ConTexMode.DETAILED,  Outline.SIMPLE),
    FRAMED_HORIZONTAL_PANE                          ( true, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, Block.box(0, 7, 0, 16, 9, 16)),
    FRAMED_RAIL_SLOPE                               ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_POWERED_RAIL_SLOPE                       ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_DETECTOR_RAIL_SLOPE                      ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_ACTIVATOR_RAIL_SLOPE                     ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_FANCY_RAIL                               (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_FANCY_POWERED_RAIL                       (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_FANCY_DETECTOR_RAIL                      (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_FANCY_ACTIVATOR_RAIL                     (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_FANCY_RAIL_SLOPE                         ( true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_FANCY_POWERED_RAIL_SLOPE                 ( true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_FANCY_DETECTOR_RAIL_SLOPE                ( true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE               ( true,  true,  true,  true, false,  true, false, ConTexMode.FULL_FACE, Outline.CUSTOM, SlopeShapes.INSTANCE),
    FRAMED_FLOWER_POT                               (false,  true, false,  true, false, false, false, null,                 Outline.SIMPLE, Block.box(5, 0, 5, 11, 6, 11)),
    FRAMED_PILLAR                                   (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, PillarShapes.PILLAR),
    FRAMED_HALF_PILLAR                              (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, HalfPillarShapes::generate),
    FRAMED_PILLAR_SOCKET                            ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, PillarSocketShapes::generate),
    FRAMED_SPLIT_PILLAR_SOCKET                      ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, PillarSocketShapes::generate),
    FRAMED_POST                                     (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, PillarShapes.POST),
    FRAMED_COLLAPSIBLE_BLOCK                        (false,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.CUSTOM),
    FRAMED_COLLAPSIBLE_COPYCAT_BLOCK                (false,  true,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE),
    FRAMED_BOUNCY_CUBE                              ( true, false, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_REDSTONE_BLOCK                           ( true, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_PRISM                                    ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PrismShapes.OUTER),
    FRAMED_ELEVATED_INNER_PRISM                     ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedPrismShapes.INNER),
    FRAMED_ELEVATED_INNER_DOUBLE_PRISM              ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_SLOPED_PRISM                             ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, SlopedPrismShapes.OUTER),
    FRAMED_ELEVATED_INNER_SLOPED_PRISM              ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedSlopedPrismShapes.INNER),
    FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM       ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_SLOPE_SLAB                               ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, new SlopeSlabShapes()),
    FRAMED_ELEVATED_SLOPE_SLAB                      ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedSlopeSlabShapes.INSTANCE),
    FRAMED_COMPOUND_SLOPE_SLAB                      ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, InverseDoubleSlopeSlabShapes.INSTANCE),
    FRAMED_DOUBLE_SLOPE_SLAB                        ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, MoreCommonShapes.TOP_HALF_SLAB_GENERATOR),
    FRAMED_INV_DOUBLE_SLOPE_SLAB                    ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, InverseDoubleSlopeSlabShapes.INSTANCE),
    FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB               ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_STACKED_SLOPE_SLAB                       ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, ElevatedSlopeSlabShapes.INSTANCE),
    FRAMED_FLAT_SLOPE_SLAB_CORNER                   ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatSlopeSlabCornerShapes.OUTER),
    FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER             ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatSlopeSlabCornerShapes.INNER),
    FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER              ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatElevatedSlopeSlabCornerShapes.OUTER),
    FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER        ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatElevatedSlopeSlabCornerShapes.INNER),
    FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER            ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, MoreCommonShapes.TOP_HALF_SLAB_GENERATOR),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER        ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, new FlatInverseDoubleSlopeSlabCornerShapes()),
    FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER       ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER           ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatElevatedSlopeSlabCornerShapes.OUTER),
    FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER     ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatElevatedSlopeSlabCornerShapes.INNER),
    FRAMED_SLOPE_PANEL                              ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, new SlopePanelShapes()),
    FRAMED_EXTENDED_SLOPE_PANEL                     ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedSlopePanelShapes.INSTANCE),
    FRAMED_COMPOUND_SLOPE_PANEL                     ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, InverseDoubleSlopePanelShapes.INSTANCE),
    FRAMED_DOUBLE_SLOPE_PANEL                       ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, MoreCommonShapes.FRONT_INV_PANEL_GENERATOR),
    FRAMED_INV_DOUBLE_SLOPE_PANEL                   ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, InverseDoubleSlopePanelShapes.INSTANCE),
    FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL              ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_STACKED_SLOPE_PANEL                      ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedSlopePanelShapes.INSTANCE),
    FRAMED_FLAT_SLOPE_PANEL_CORNER                  ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatSlopePanelCornerShapes.OUTER),
    FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER            ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatSlopePanelCornerShapes.INNER),
    FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER              ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatExtendedSlopePanelCornerShapes.OUTER),
    FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER        ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatExtendedSlopePanelCornerShapes.INNER),
    FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER           ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, MoreCommonShapes.FRONT_INV_PANEL_GENERATOR),
    FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER       ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, new FlatInverseDoubleSlopePanelCornerShapes()),
    FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER       ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER          ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatExtendedSlopePanelCornerShapes.OUTER),
    FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER    ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, FlatExtendedSlopePanelCornerShapes.INNER),
    FRAMED_SMALL_CORNER_SLOPE_PANEL                 (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, CornerSlopePanelShapes.SMALL_OUTER),
    FRAMED_SMALL_CORNER_SLOPE_PANEL_W               (false, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, new CornerSlopePanelWallShapes.SmallOuter()),
    FRAMED_LARGE_CORNER_SLOPE_PANEL                 (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, CornerSlopePanelShapes.LARGE_OUTER),
    FRAMED_LARGE_CORNER_SLOPE_PANEL_W               (false, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, new CornerSlopePanelWallShapes.LargeOuter()),
    FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL           (false, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, CornerSlopePanelShapes.SMALL_INNER),
    FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W         (false, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, new CornerSlopePanelWallShapes.SmallInner()),
    FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL           ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, CornerSlopePanelShapes.LARGE_INNER),
    FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W         ( true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, new CornerSlopePanelWallShapes.LargeInner()),
    FRAMED_EXT_CORNER_SLOPE_PANEL                   ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedCornerSlopePanelShapes.OUTER),
    FRAMED_EXT_CORNER_SLOPE_PANEL_W                 ( true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedCornerSlopePanelWallShapes.OUTER),
    FRAMED_EXT_INNER_CORNER_SLOPE_PANEL             ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedCornerSlopePanelShapes.INNER),
    FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W           ( true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedCornerSlopePanelWallShapes.INNER),
    FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL          (false,  true,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, CornerPillarShapes::generate),
    FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W        (false,  true,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, DoubleCornerSlopePanelWallShapes::generateSmall),
    FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL          ( true,  true,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, DoubleCornerSlopePanelShapes::generate),
    FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W        ( true,  true,  true, false,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, DoubleCornerSlopePanelWallShapes::generateLarge),
    FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL            ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, new InverseDoubleCornerSlopePanelShapes()),
    FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W          ( true, false,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, new InverseDoubleCornerSlopePanelWallShapes()),
    FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL            ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W          ( true,  true, false, false,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL      ( true,  true, false,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W    ( true,  true, false, false,  true,  true, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_STACKED_CORNER_SLOPE_PANEL               ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, ExtendedCornerSlopePanelShapes.OUTER),
    FRAMED_STACKED_CORNER_SLOPE_PANEL_W             ( true, false,  true, false,  true,  true, false, ConTexMode.FULL_EDGE, Outline.MODEL, ExtendedCornerSlopePanelWallShapes.OUTER),
    FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL         ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedCornerSlopePanelShapes.INNER),
    FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W       ( true, false,  true, false,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, ExtendedCornerSlopePanelWallShapes.INNER),
    FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL           (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.MODEL, PrismCornerSlopePanelShapes.SMALL_OUTER),
    FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W         (false, false,  true, false,  true, false, false, ConTexMode.DETAILED,  Outline.MODEL, PrismCornerSlopePanelWallShapes.SMALL_OUTER),
    FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL           ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, PrismCornerSlopePanelShapes.LARGE_OUTER),
    FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W         ( true, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, Outline.MODEL, PrismCornerSlopePanelWallShapes.LARGE_OUTER),
    FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL     ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PrismCornerSlopePanelShapes.SMALL_INNER),
    FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W   ( true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PrismCornerSlopePanelWallShapes.SMALL_INNER),
    FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL     ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PrismCornerSlopePanelShapes.LARGE_INNER),
    FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W   ( true, false,  true, false,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PrismCornerSlopePanelWallShapes.LARGE_INNER),
    FRAMED_PYRAMID                                  ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PyramidShapes.FULL),
    FRAMED_PYRAMID_SLAB                             ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PyramidShapes.SLAB),
    FRAMED_ELEVATED_PYRAMID_SLAB                    ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.MODEL, PyramidShapes.ELEVATED_SLAB),
    FRAMED_UPPER_PYRAMID_SLAB                       ( true, false,  true, false,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, PyramidShapes.UPPER_SLAB),
    FRAMED_STACKED_PYRAMID_SLAB                     ( true, false,  true,  true,  true,  true, false, ConTexMode.FULL_FACE, Outline.MODEL, PyramidShapes.ELEVATED_SLAB),
    FRAMED_TARGET                                   ( true,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_GATE                                     ( true, false, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, GateShapes::generate),
    FRAMED_IRON_GATE                                ( true, false, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, GateShapes::generate),
    FRAMED_ITEM_FRAME                               (false,  true,  true,  true, false, false, false, null,                 Outline.CUSTOM, ItemFrameShapes::generate),
    FRAMED_GLOWING_ITEM_FRAME                       (false,  true,  true,  true, false, false, false, null,                 Outline.CUSTOM, ItemFrameShapes::generate),
    FRAMED_MINI_CUBE                                (false, false,  true,  true,  true, false, false, null,                 Outline.SIMPLE, MiniCubeShapes::generate),
    FRAMED_ONE_WAY_WINDOW                           (false,  true, false,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_BOOKSHELF                                ( true, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_CHISELED_BOOKSHELF                       ( true,  true, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Shapes.block()),
    FRAMED_CENTERED_SLAB                            ( true, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, Block.box(0, 4, 0, 16, 12, 16)),
    FRAMED_CENTERED_PANEL                           ( true, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, CenteredPanelShapes::generate),
    FRAMED_MASONRY_CORNER_SEGMENT                   (false, false,  true, false,  true, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, MasonryCornerSegmentShapes::generate),
    FRAMED_MASONRY_CORNER                           ( true, false, false,  true,  true,  true, false, ConTexMode.FULL_EDGE, Outline.SIMPLE, Shapes.block()),
    FRAMED_CHECKERED_CUBE_SEGMENT                   (false, false,  true, false,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, CheckeredCubeSegmentShapes::generate),
    FRAMED_CHECKERED_CUBE                           ( true, false, false,  true,  true,  true, false, ConTexMode.DETAILED,  Outline.SIMPLE, Shapes.block()),
    FRAMED_CHECKERED_SLAB_SEGMENT                   (false, false,  true, false,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, CheckeredSlabSegmentShapes::generate),
    FRAMED_CHECKERED_SLAB                           ( true, false,  true,  true,  true,  true, false, ConTexMode.DETAILED,  Outline.SIMPLE, CommonShapes.SLAB_GENERATOR),
    FRAMED_CHECKERED_PANEL_SEGMENT                  (false, false,  true, false,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, CheckeredPanelSegmentShapes::generate),
    FRAMED_CHECKERED_PANEL                          ( true, false,  true,  true,  true,  true, false, ConTexMode.DETAILED,  Outline.SIMPLE, CommonShapes.PANEL_GENERATOR),
    FRAMED_TUBE                                     ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, TubeShapes::generate),
    FRAMED_CORNER_TUBE                              ( true, false,  true,  true, false, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, CornerTubeShapes::generate),
    FRAMED_CHAIN                                    (false, false,  true,  true,  true, false, false, ConTexMode.DETAILED,  Outline.SIMPLE, ChainShapes::generate),
    FRAMED_LANTERN                                  (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_SOUL_LANTERN                             (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_COPPER_LANTERN                           (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_HOPPER                                   (false,  true, false,  true, false, false, false, ConTexMode.FULL_EDGE, Outline.SIMPLE),
    FRAMED_LAYERED_CUBE                             ( true, false,  true,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, LayeredCubeShapes::generate),
    FRAMED_LIGHTNING_ROD                            (false, false,  true,  true, false, false, false, null,                 Outline.SIMPLE),
    FRAMED_PATH                                     ( true, false, false,  true,  true, false, false, ConTexMode.FULL_FACE, Outline.SIMPLE, Block.box(0, 0, 0, 16, 15, 16))
    ;

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final boolean canOcclude;
    private final boolean specialOutline;
    private final boolean modelBasedOutline;
    private final boolean specialTile;
    private final boolean waterloggable;
    private final boolean blockItem;
    private final boolean allowIntangible;
    private final boolean doubleBlock;
    private final boolean lockable;
    private final boolean supportsCT;
    private final ConTexMode minCTMode;
    private final ShapeGenerator shapeGen;

    BlockType(boolean canOcclude, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode, Outline outline)
    {
        this(canOcclude, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, minCTMode, outline, ShapeGenerator.EMPTY);
    }

    BlockType(boolean canOcclude, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode, Outline outline, VoxelShape shape)
    {
        this(canOcclude, specialTile, waterloggable, blockItem, allowIntangible, doubleBlock, lockable, minCTMode, outline, ShapeGenerator.singleShape(shape, null));
        Preconditions.checkArgument(!waterloggable || !Shapes.joinUnoptimized(shape, Shapes.block(), BooleanOp.NOT_SAME).isEmpty(), "Blocks with full cube shape can't be waterloggable");
    }

    BlockType(boolean canOcclude, boolean specialTile, boolean waterloggable, boolean blockItem, boolean allowIntangible, boolean doubleBlock, boolean lockable, @Nullable ConTexMode minCTMode, Outline outline, ShapeGenerator shapeGen)
    {
        this.canOcclude = canOcclude;
        this.specialOutline = outline != Outline.SIMPLE;
        this.modelBasedOutline = outline == Outline.MODEL;
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
    public boolean hasSpecialOutline()
    {
        return specialOutline;
    }

    public boolean useModelBasedOutline()
    {
        return modelBasedOutline;
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
    public BlockOverlayPredicate getBlockOverlayPredicate()
    {
        return BlockOverlayPredicate.NEVER;
    }

    @Override
    public ShapeGenerator getShapeGenerator()
    {
        return shapeGen;
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
    public boolean supportsBlockOverlays()
    {
        return false;
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

    private enum Outline
    {
        SIMPLE,
        MODEL,
        CUSTOM,
    }
}
