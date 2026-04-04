package io.github.xfacthd.framedblocks.common.data;

import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.common.block.slopepanel.FramedDoubleSlopePanelBlock;
import io.github.xfacthd.framedblocks.common.block.slopeslab.FramedDoubleSlopeSlabBlock;
import io.github.xfacthd.framedblocks.common.block.slopeslab.FramedFlatDoubleSlopeSlabCornerBlock;
import io.github.xfacthd.framedblocks.common.util.BlockTypeMap;

public final class NullCullPredicates extends BlockTypeMap<NullCullPredicate> {
    public static final NullCullPredicates PREDICATES = new NullCullPredicates();

    private NullCullPredicates() {
        super(NullCullPredicate.NEVER, type -> !type.isDoubleBlock());
    }

    @Override
    protected void fill() {
        put(BlockType.FRAMED_DOUBLE_SLOPE, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DIVIDED_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_HALF_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DOUBLE_PRISM_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DOUBLE_THREEWAY_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_STACKED_SLOPE_EDGE, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_ELEV_DOUBLE_CORNER_SLOPE_EDGE, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ELEV_DOUBLE_INNER_CORNER_SLOPE_EDGE, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_STACKED_CORNER_SLOPE_EDGE, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_DOUBLE_SLAB, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ADJ_DOUBLE_SLAB, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_SLAB, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DIVIDED_SLAB, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_PANEL, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ADJ_DOUBLE_PANEL, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_PANEL, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DIVIDED_PANEL_VERTICAL, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DIVIDED_STAIRS, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_HALF_STAIRS, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_SLICED_STAIRS_SLAB, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_SLICED_STAIRS_PANEL, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_SLOPED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLAB, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_VERTICAL_DOUBLE_HALF_STAIRS, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_VERTICAL_SLICED_STAIRS, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DIVIDED_BOARD, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_DOUBLE_CORNER_BOARD, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_FANCY_RAIL_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_SPLIT_PILLAR_SOCKET, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_ELEVATED_INNER_DOUBLE_PRISM, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_DOUBLE_SLOPE_SLAB, FramedDoubleSlopeSlabBlock.NULL_CULL_PREDICATE);
        put(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_STACKED_SLOPE_SLAB, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, FramedFlatDoubleSlopeSlabCornerBlock.NULL_CULL_PREDICATE);
        put(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_DOUBLE_SLOPE_PANEL, FramedDoubleSlopePanelBlock.NULL_CULL_PREDICATE);
        put(BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_STACKED_SLOPE_PANEL, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, FramedDoubleSlopePanelBlock.NULL_CULL_PREDICATE);
        put(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_PART_TWO);
        put(BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W, NullCullPredicate.ONLY_PART_TWO);
        put(BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_STACKED_PYRAMID_SLAB, NullCullPredicate.ONLY_PART_ONE);
        put(BlockType.FRAMED_MASONRY_CORNER, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_CHECKERED_CUBE, NullCullPredicate.ALWAYS);
        put(BlockType.FRAMED_CHECKERED_SLAB, NullCullPredicate.NEVER);
        put(BlockType.FRAMED_CHECKERED_PANEL, NullCullPredicate.NEVER);
    }
}
