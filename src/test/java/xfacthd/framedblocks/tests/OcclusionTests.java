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
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.test.TestUtils;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.util.TestedType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tests that are known to fail should be marked as optional.
 * <br>
 * Known failing tests:
 * <ul>
 * <li>{@link OcclusionTests#testTop_SlopeSlab_BottomHalfTop(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#testTop_SlopeSlab_TopHalfBottom(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#testTop_SlopeSlab_TopHalfTop(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#testBottom_SlopeSlab_BottomHalfTop(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#testBottom_SlopeSlab_TopHalfBottom(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#testBottom_SlopeSlab_TopHalfTop(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_InvDoubleSlopeSlab(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatSlopeSlabCorner_TopHalfBottom(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatSlopeSlabCorner_BottomHalfTop(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatInnerSlopeSlabCorner_TopHalfBottom(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatInnerSlopeSlabCorner_BottomHalfTop(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatInverseDoubleSlopeSlabCorner(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_SlopePanel_FrontHalfNorth(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_SlopePanel_FrontHalfSouth(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_InvDoubleSlopePanel(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatSlopePanelCorner_FrontHalfNorth(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatSlopePanelCorner_FrontHalfSouth(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatInnerSlopePanelCorner_FrontHalfNorth(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatInnerSlopePanelCorner_FrontHalfSouth(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_FlatInverseDoubleSlopePanelCorner(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_InverseDoubleCornerSlopePanel_Bottom(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_InverseDoubleCornerSlopePanel_Top(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_InverseDoubleCornerSlopePanelWall(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_HorizontalPane(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_CenteredSlab(GameTestHelper)}</li>
 * <li>{@link OcclusionTests#test_CenteredPanel(GameTestHelper)}</li>
 * </ul>
 */

@GameTestHolder(FramedConstants.MOD_ID)
public final class OcclusionTests
{
    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CUBE)
    public static void test_Cube(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testTop_Slope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testTop_Slope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testBottom_Slope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void testBottom_Slope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void test_Slope_NorthHorizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE)
    public static void test_Slope_SouthHorizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testTop_CornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testTop_CornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testBottom_CornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void testBottom_CornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_NorthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CORNER_SLOPE)
    public static void test_CornerSlope_SouthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testTop_InnerCornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testTop_InnerCornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testBottom_InnerCornerSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void testBottom_InnerCornerSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_NorthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_CORNER_SLOPE)
    public static void test_InnerCornerSlope_SouthHorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testTop_InnerPrismCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testTop_InnerPrismCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testBottom_InnerPrismCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM_CORNER)
    public static void testBottom_InnerPrismCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testTop_InnerThreewayCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testTop_InnerThreewayCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testBottom_InnerThreewayCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_THREEWAY_CORNER)
    public static void testBottom_InnerThreewayCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testTop_Slab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testTop_Slab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testBottom_Slab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void testBottom_Slab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLAB)
    public static void testTop_DividedSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLAB)
    public static void testTop_DividedSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLAB)
    public static void testBottom_DividedSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLAB)
    public static void testBottom_DividedSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PANEL)
    public static void test_Panel_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PANEL)
    public static void test_Panel_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
    public static void test_DividedPanelHor_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR.get()
                .defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
    public static void test_DividedPanelHor_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_PANEL_VERTICAL)
    public static void test_DividedPanelVert_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.get()
                .defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_PANEL_VERTICAL)
    public static void test_DividedPanelVert_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomInnerLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopInnerLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomInnerRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopInnerRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_RIGHT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomOuterLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_LEFT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopOuterLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_LEFT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_BottomOuterRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_RIGHT);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STAIRS)
    public static void test_Stairs_TopOuterRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.OUTER_RIGHT)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_MASONRY_CORNER)
    public static void test_MasonryCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_MASONRY_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOOR)
    public static void test_Door_Closed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOOR)
    public static void test_Door_Open(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testTop_Trapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testTop_Trapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testBottom_Trapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void testBottom_Trapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_Trapdoor_NorthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_Trapdoor_NorthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_TrapdoorSouthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TRAPDOOR)
    public static void test_Trapdoor_SouthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TRAP_DOOR.get()
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
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PANEL)
    public static void test_DoublePanel_NorthSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PANEL)
    public static void test_DoublePanel_EastWest(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_NE, Direction.EAST);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE)
    public static void test_DoubleSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE)
    public static void test_DoubleSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE)
    public static void test_DoubleSlope_Horizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalBottomLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_LEFT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalBottomRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_BOTTOM_RIGHT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalTopLeft(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_LEFT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_CORNER)
    public static void test_DoubleCorner_HorizontalTopRight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.CORNER_TYPE, CornerType.HORIZONTAL_TOP_RIGHT);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PRISM_CORNER)
    public static void test_DoublePrismCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PRISM_CORNER)
    public static void test_DoublePrismCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
    public static void test_DoubleThreewayCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_THREEWAY_CORNER)
    public static void test_DoubleThreewayCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLOOR_BOARD)
    public static void test_FloorBoard(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLOOR.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLOOR_BOARD)
    public static void testBottom_FloorBoard(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLOOR.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_STAIRS)
    public static void test_VerticalStairs_Vertical(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_RAIL_SLOPE)
    public static void testTop_RailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_RAIL_SLOPE)
    public static void testBottom_RailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_POWERED_RAIL_SLOPE)
    public static void testTop_PoweredRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_POWERED_RAIL_SLOPE)
    public static void testBottom_PoweredRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DETECTOR_RAIL_SLOPE)
    public static void testTop_DetectorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DETECTOR_RAIL_SLOPE)
    public static void testBottom_DetectorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE)
    public static void testTop_ActivatorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE)
    public static void testBottom_ActivatorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_BOUNCY_CUBE)
    public static void test_BouncyCube(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_BOUNCY_CUBE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SECRET_STORAGE)
    public static void test_SecretStorage(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SECRET_STORAGE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testTop_Prism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.DOWN_X);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testTop_Prism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testBottom_Prism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.DOWN_X);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void testBottom_Prism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void test_Prism_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.NORTH_X);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PRISM)
    public static void test_Prism_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.SOUTH_X);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM)
    public static void testTop_InnerPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.DOWN_X);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM)
    public static void testTop_InnerPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM)
    public static void testBottom_InnerPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.DOWN_X);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM)
    public static void testBottom_InnerPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM)
    public static void test_InnerPrism_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.NORTH_X);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_PRISM)
    public static void test_InnerPrism_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.SOUTH_X);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_PRISM)
    public static void test_DoublePrism(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_PRISM.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testTop_SlopedPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.DOWN_NORTH);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testTop_SlopedPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_NORTH);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testBottom_SlopedPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.DOWN_NORTH);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void testBottom_SlopedPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_NORTH);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void test_SlopedPrism_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_PRISM.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_PRISM)
    public static void test_SlopedPrism_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.SOUTH_DOWN);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_SLOPED_PRISM)
    public static void testTop_InnerSlopedPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.DOWN_NORTH);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_SLOPED_PRISM)
    public static void testTop_InnerSlopedPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_NORTH);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_SLOPED_PRISM)
    public static void testBottom_InnerSlopedPrism_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.DOWN_NORTH);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_SLOPED_PRISM)
    public static void testBottom_InnerSlopedPrism_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_NORTH);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_SLOPED_PRISM)
    public static void test_InnerSlopedPrism_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_INNER_SLOPED_PRISM)
    public static void test_InnerSlopedPrism_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.SOUTH_DOWN);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPED_PRISM)
    public static void test_DoubleSlopedPrism(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.DOWN_NORTH);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_BottomHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_BottomHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_TopHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testTop_SlopeSlab_TopHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true)
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_BottomHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_BottomHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_TopHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_SLAB)
    public static void testBottom_SlopeSlab_TopHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true)
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testTop_ElevatedSlopeSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testTop_ElevatedSlopeSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testBottom_ElevatedSlopeSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_SLOPE_SLAB)
    public static void testBottom_ElevatedSlopeSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_SLAB)
    public static void test_DoubleSlopeSlab_BottomHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_SLAB)
    public static void test_DoubleSlopeSlab_TopHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB)
    public static void test_InvDoubleSlopeSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB)
    public static void test_ElevatedDoubleSlopeSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_SLOPE_SLAB)
    public static void test_StackedSlopeSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    public static void test_FlatSlopeSlabCorner_BottomHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    public static void test_FlatSlopeSlabCorner_TopHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    public static void test_FlatSlopeSlabCorner_BottomHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
    public static void test_FlatSlopeSlabCorner_TopHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true)
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatInnerSlopeSlabCorner_BottomHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatInnerSlopeSlabCorner_TopHalfBottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatInnerSlopeSlabCorner_BottomHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatInnerSlopeSlabCorner_TopHalfTop(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true)
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedSlopeSlabCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedSlopeSlabCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedInnerSlopeSlabCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedInnerSlopeSlabCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatDoubleSlopeSlabCorner_BottomHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatDoubleSlopeSlabCorner_TopHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.TOP_HALF, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatInverseDoubleSlopeSlabCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedDoubleSlopeSlabCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedDoubleSlopeSlabCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedInnerDoubleSlopeSlabCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER)
    public static void test_FlatElevatedInnerDoubleSlopeSlabCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER)
    public static void test_FlatStackedSlopeSlabCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER)
    public static void test_FlatStackedSlopeSlabCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatStackedInnerSlopeSlabCorner_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER)
    public static void test_FlatStackedInnerSlopeSlabCorner_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_REDSTONE_BLOCK)
    public static void test_RedstoneBlock(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_REDSTONE_BLOCK.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_BackHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_BackHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_FrontHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLOPE_PANEL)
    public static void test_SlopePanel_FrontHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true)
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXTENDED_SLOPE_PANEL)
    public static void test_ExtendedSlopePanel(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_PANEL)
    public static void test_DoubleSlopePanel_BackHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_SLOPE_PANEL)
    public static void test_DoubleSlopePanel_FrontHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL)
    public static void test_InvDoubleSlopePanel(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL)
    public static void test_ExtendedDoubleSlopePanel(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_SLOPE_PANEL)
    public static void test_StackedSlopePanel(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    public static void test_FlatSlopePanelCorner_BackHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    public static void test_FlatSlopePanelCorner_BackHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    public static void test_FlatSlopePanelCorner_FrontHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
    public static void test_FlatSlopePanelCorner_FrontHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true)
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    public static void test_FlatInnerSlopePanelCorner_BackHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    public static void test_FlatInnerSlopePanelCorner_BackHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    public static void test_FlatInnerSlopePanelCorner_FrontHalfNorth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER)
    public static void test_FlatInnerSlopePanelCorner_FrontHalfSouth(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true)
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
    public static void test_FlatExtendedSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER)
    public static void test_FlatExtendedInnerSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER)
    public static void test_FlatDoubleSlopePanelCorner_BackHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.get()
                .defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER)
    public static void test_FlatDoubleSlopePanelCorner_FrontHalf(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FRONT, true);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER)
    public static void test_FlatInverseDoubleSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER)
    public static void test_FlatExtendedDoubleSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER)
    public static void test_FlatExtendedInnerDoubleSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER)
    public static void test_FlatStackedSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER)
    public static void test_FlatStackedInnerSlopePanelCorner(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    public static void test_LargeInnerCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL)
    public static void test_LargeInnerCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W)
    public static void test_LargeInnerCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    public static void test_ExtendedCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL)
    public static void test_ExtendedCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W)
    public static void test_ExtendedCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    public static void test_ExtendedInnerCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL)
    public static void test_ExtendedInnerCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W)
    public static void test_ExtendedInnerCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_LargeDoubleCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_LargeDoubleCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W)
    public static void test_LargeDoubleCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_InverseDoubleCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_InverseDoubleCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W)
    public static void test_InverseDoubleCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_ExtendedDoubleCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_ExtendedDoubleCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W)
    public static void test_ExtendedDoubleCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_ExtendedInnerDoubleCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL)
    public static void test_ExtendedInnerDoubleCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W)
    public static void test_ExtendedInnerDoubleCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL)
    public static void test_StackedCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL)
    public static void test_StackedCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W)
    public static void test_StackedCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL)
    public static void test_StackedInnerCornerSlopePanel_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL)
    public static void test_StackedInnerCornerSlopePanel_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W)
    public static void test_StackedInnerCornerSlopePanelWall(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL.get()
                .defaultBlockState()
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_STAIRS)
    public static void test_DoubleStairs_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_STAIRS.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_STAIRS)
    public static void test_DoubleStairs_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_STAIRS.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_STAIRS)
    public static void test_DividedStairs_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_STAIRS.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_STAIRS)
    public static void test_DividedStairs_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_STAIRS.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_HALF_STAIRS)
    public static void test_DoubleHalfStairs(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLICED_STAIRS_SLAB)
    public static void test_SlicedStairsSlab_BottomStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLICED_STAIRS_PANEL)
    public static void test_SlicedStairsPanel_BottomStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.SOUTH, Direction.NORTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS)
    public static void test_VerticalDoubleStairs(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS)
    public static void test_VerticalDividedStairs(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_STAIRS)
    public static void test_VerticalDoubleHalfStairs(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_SLICED_STAIRS)
    public static void test_VerticalSlicedStairs(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR)
    public static void test_DoubleThreewayCornerPillar(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_DOOR)
    public static void test_IronDoor_Closed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_DOOR)
    public static void test_IronDoor_Open(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testTop_IronTrapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testTop_IronTrapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testBottom_IronTrapdoor_BottomClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get().defaultBlockState();
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void testBottom_IronTrapdoor_TopClosed(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightAbove(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoor_NorthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoor_NorthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HALF, Half.TOP);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoorSouthBottomOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_TRAPDOOR)
    public static void test_IronTrapdoor_SouthTopOpen(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.OPEN, true)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.HALF, Half.BOTTOM);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_WALL_BOARD)
    public static void test_WallBoard_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_WALL_BOARD.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_WALL_BOARD)
    public static void test_WallBoard_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_WALL_BOARD.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_GLOWING_CUBE)
    public static void test_GlowingCube(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_GLOWING_CUBE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PYRAMID)
    public static void test_Pyramid(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PYRAMID.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_PYRAMID_SLAB)
    public static void test_PyramidSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PYRAMID_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_HORIZONTAL_PANE)
    public static void test_HorizontalPane(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_HORIZONTAL_PANE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_TARGET)
    public static void test_Target(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_TARGET.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_GATE)
    public static void test_Gate(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_GATE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_IRON_GATE)
    public static void test_IronGate(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_IRON_GATE.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_RAIL_SLOPE)
    public static void testTop_FancyRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_RAIL_SLOPE)
    public static void testBottom_FancyRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE)
    public static void testTop_FancyPoweredRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE)
    public static void testBottom_FancyPoweredRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE)
    public static void testTop_FancyDetectorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE)
    public static void testBottom_FancyDetectorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE)
    public static void testTop_FancyActivatorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE)
    public static void testBottom_FancyActivatorRailSlope(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLOPE)
    public static void testTop_DividedSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLOPE)
    public static void testTop_DividedSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLOPE)
    public static void testBottom_DividedSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLOPE)
    public static void testBottom_DividedSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.EAST, Direction.WEST));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLOPE)
    public static void test_DividedSlope_NorthHorizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get()
                .defaultBlockState()
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DIVIDED_SLOPE)
    public static void test_DividedSlope_SouthHorizontal(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE)
    public static void testTop_VerticalDoubleHalfSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get()
                .defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE)
    public static void testTop_VerticalDoubleHalfSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE)
    public static void testBottom_VerticalDoubleHalfSlope_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get()
                .defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE)
    public static void testBottom_VerticalDoubleHalfSlope_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.NORTH, Direction.SOUTH));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_HALF_SLOPE)
    public static void test_DoubleHalfSlope_North(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.EAST);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_DOUBLE_HALF_SLOPE)
    public static void test_DoubleHalfSlope_South(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.UP, Direction.DOWN));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_STAIRS)
    public static void test_SlopedStairs_BottomStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_STAIRS.get()
                .defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_SLOPED_STAIRS)
    public static void test_SlopedStairs_TopStraight(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_SLOPED_STAIRS.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_VERTICAL_SLOPED_STAIRS)
    public static void test_VerticalSlopedStairs_Vertical(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS.get()
                .defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_SLAB)
    public static void test_CenteredSlab(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CENTERED_SLAB.get().defaultBlockState();
        TestUtils.testBlockOccludesLightBelow(helper, state);
    }

    @GameTest(template = "box_side", batch = "occlusion", required = false)
    @TestedType(type = BlockType.FRAMED_PANEL)
    public static void test_CenteredPanel(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CENTERED_PANEL.get().defaultBlockState();
        TestUtils.testBlockOccludesLightNorth(helper, state);
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CHECKERED_CUBE)
    public static void test_CheckeredCube(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CHECKERED_CUBE.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.DOWN, Direction.UP));
    }

    @GameTest(template = "box_top", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CHECKERED_SLAB)
    public static void test_CheckeredSlab_Bottom(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CHECKERED_SLAB.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightBelow(helper, state, List.of(Direction.NORTH, Direction.WEST));
    }

    @GameTest(template = "box_bottom", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CHECKERED_SLAB)
    public static void test_CheckeredSlab_Top(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CHECKERED_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, true);
        TestUtils.testDoubleBlockOccludesLightAbove(helper, state, List.of(Direction.SOUTH, Direction.UP));
    }

    @GameTest(template = "box_side", batch = "occlusion")
    @TestedType(type = BlockType.FRAMED_CHECKERED_PANEL)
    public static void test_CheckeredPanel(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_CHECKERED_PANEL.get().defaultBlockState();
        TestUtils.testDoubleBlockOccludesLightNorth(helper, state, List.of(Direction.DOWN, Direction.WEST));
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
