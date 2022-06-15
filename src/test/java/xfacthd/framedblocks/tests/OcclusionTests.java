package xfacthd.framedblocks.tests;

import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.gametest.GameTestHolder;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.util.test.TestUtils;
import xfacthd.framedblocks.util.TestedType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tests that are known to fail should be marked as optional.
 *
 * Known failing tests:
 * {@link OcclusionTests#testTop_SlopeSlab_BottomHalfTop(GameTestHelper)}
 * {@link OcclusionTests#testTop_SlopeSlab_TopHalfBottom(GameTestHelper)}
 * {@link OcclusionTests#testTop_SlopeSlab_TopHalfTop(GameTestHelper)}
 * {@link OcclusionTests#testBottom_SlopeSlab_BottomHalfTop(GameTestHelper)}
 * {@link OcclusionTests#testBottom_SlopeSlab_TopHalfBottom(GameTestHelper)}
 * {@link OcclusionTests#testBottom_SlopeSlab_TopHalfTop(GameTestHelper)}
 * {@link OcclusionTests#test_InvDoubleSlopeSlab(GameTestHelper)}
 * {@link OcclusionTests#test_SlopePanel_FrontHalfNorth(GameTestHelper)}
 * {@link OcclusionTests#test_SlopePanel_FrontHalfSouth(GameTestHelper)}
 * {@link OcclusionTests#test_InvDoubleSlopePanel(GameTestHelper)}
 */

@GameTestHolder(FramedConstants.MOD_ID)
public final class OcclusionTests
{
    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CUBE)
    public static void test_Cube(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCube.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testTop_Slope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testTop_Slope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testBottom_Slope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testBottom_Slope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void test_Slope_NorthHorizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void test_Slope_SouthHorizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testTop_CornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testTop_CornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testBottom_CornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testBottom_CornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testTop_InnerCornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testTop_InnerCornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testBottom_InnerCornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testBottom_InnerCornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerCornerSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testTop_InnerPrismCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerPrismCorner.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testTop_InnerPrismCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerPrismCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testBottom_InnerPrismCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerPrismCorner.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testBottom_InnerPrismCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerPrismCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testTop_InnerThreewayCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerThreewayCorner.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testTop_InnerThreewayCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerThreewayCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testBottom_InnerThreewayCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerThreewayCorner.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testBottom_InnerThreewayCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInnerThreewayCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testTop_Slab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlab.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testTop_Slab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testBottom_Slab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlab.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testBottom_Slab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PANEL)
    public static void test_Panel_North(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPanel.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PANEL)
    public static void test_Panel_South(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPanel.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomInnerLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopInnerLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomInnerRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopInnerRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_RIGHT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomOuterLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopOuterLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_LEFT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomOuterRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopOuterRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedStairs.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_RIGHT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOOR)
    public static void test_Door_Closed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOOR)
    public static void test_Door_Open(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testTop_Trapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testTop_Trapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testBottom_Trapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testBottom_Trapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_Trapdoor_NorthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_Trapdoor_NorthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_TrapdoorSouthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_Trapdoor_SouthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.HALF, Half.BOTTOM);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLAB)
    public static void test_DoubleSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlab.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PANEL)
    public static void test_DoublePanel_NorthSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoublePanel.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PANEL)
    public static void test_DoublePanel_EastWest(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoublePanel.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_NE, Direction.EAST);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE)
    public static void test_DoubleSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlope.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE)
    public static void test_DoubleSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE)
    public static void test_DoubleSlope_Horizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlope.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleCorner.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleCorner.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleCorner.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleCorner.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleCorner.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleCorner.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PRISM_CORNER)
    public static void test_DoublePrismCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoublePrismCorner.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PRISM_CORNER)
    public static void test_DoublePrismCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoublePrismCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
    public static void test_DoubleThreewayCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleThreewayCorner.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
    public static void test_DoubleThreewayCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleThreewayCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLOOR_BOARD)
    public static void test_FloorBoard(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedFloor.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLOOR_BOARD)
    public static void testBottom_FloorBoard(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedFloor.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_STAIRS)
    public static void test_VerticalStairs_Vertical(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedVerticalStairs.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_RAIL_SLOPE)
    public static void testTop_RailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedRailSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_RAIL_SLOPE)
    public static void testBottom_RailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedRailSlope.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_BOUNCY_CUBE)
    public static void test_BouncyCube(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedBouncyCube.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SECRET_STORAGE)
    public static void test_SecretStorage(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSecretStorage.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testTop_Prism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.DOWN);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testTop_Prism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testBottom_Prism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.DOWN);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testBottom_Prism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void test_Prism_North(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPrism.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void test_Prism_South(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testTop_SlopedPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.DOWN)
                .setValue(PropertyHolder.ORIENTATION, Direction.NORTH);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testTop_SlopedPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP)
                .setValue(PropertyHolder.ORIENTATION, Direction.NORTH);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testBottom_SlopedPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.DOWN)
                .setValue(PropertyHolder.ORIENTATION, Direction.NORTH);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testBottom_SlopedPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP)
                .setValue(PropertyHolder.ORIENTATION, Direction.NORTH);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void test_SlopedPrism_North(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopedPrism.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void test_SlopedPrism_South(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopedPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_BottomHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_BottomHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_TopHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_TopHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true)
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_BottomHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_BottomHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_TopHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_TopHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopeSlab.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true)
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testTop_ElevatedSlopeSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedElevatedSlopeSlab.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testTop_ElevatedSlopeSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedElevatedSlopeSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testBottom_ElevatedSlopeSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedElevatedSlopeSlab.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testBottom_ElevatedSlopeSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedElevatedSlopeSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_SLAB)
    public static void test_DoubleSlopeSlab_BottomHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlopeSlab.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_SLAB)
    public static void test_DoubleSlopeSlab_TopHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlopeSlab.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB)
    public static void test_InvDoubleSlopeSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInverseDoubleSlopeSlab.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_REDSTONE_BLOCK)
    public static void test_RedstoneBlock(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedRedstoneBlock.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_BackHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopePanel.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_BackHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopePanel.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_FrontHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopePanel.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_FrontHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedSlopePanel.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true)
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    public static void test_ExtendedSlopePanel(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedExtendedSlopePanel.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_PANEL)
    public static void test_DoubleSlopePanel_BackHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlopePanel.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_PANEL)
    public static void test_DoubleSlopePanel_FrontHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedDoubleSlopePanel.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL)
    public static void test_InvDoubleSlopePanel(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedInverseDoubleSlopePanel.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_DOOR)
    public static void test_IronDoor_Closed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_DOOR)
    public static void test_IronDoor_Open(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testTop_IronTrapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testTop_IronTrapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testBottom_IronTrapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testBottom_IronTrapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoor_NorthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoor_NorthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoorSouthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoor_SouthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedIronTrapDoor.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.HALF, Half.BOTTOM);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }



    private static boolean firstBatch = true;

    @BeforeBatch(batch = "occlusion")
    public static void validateAllBlocksHaveTest(ServerLevel level)
    {
        if (!firstBatch) { return; }
        firstBatch = false;

        Set<BlockType> types = Arrays.stream(OcclusionTests.class.getMethods())
                .map(mth -> mth.getAnnotation(TestedType.class))
                .filter(Objects::nonNull)
                .map(TestedType::type)
                .collect(Collectors.toSet());

        List<BlockType> missing = Arrays.stream(BlockType.values())
                .filter(IBlockType::canOccludeWithSolidCamo)
                .filter(type -> !types.contains(type))
                .toList();

        if (!missing.isEmpty())
        {
            FramedBlocks.LOGGER.warn("Found blocks missing occlusion test {}", missing);
        }
    }

    private OcclusionTests() { }
}
