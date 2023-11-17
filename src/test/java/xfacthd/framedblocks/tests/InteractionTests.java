package xfacthd.framedblocks.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.registries.RegistryObject;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.test.TestUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.*;
import xfacthd.framedblocks.common.data.property.LatchType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;

@GameTestHolder(FramedConstants.MOD_ID)
public final class InteractionTests
{
    private static final BlockPos POS_ABOVE_FLOOR = new BlockPos(0, 2, 0);

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSignDying(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_SIGN.get()),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().getColor() == DyeColor.BLACK,
                            () -> String.format("Expected sign text color to be 'black', got '%s'", be.getFrontText().getColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.getFrontText().hasGlowingText(),
                            () -> "Sign text should not be glowing"
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.isWaxed(),
                            () -> "Sign should not be waxed"
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.RED_DYE, Direction.SOUTH),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().getColor() == DyeColor.RED,
                            () -> String.format("Expected sign text color to be 'red', got '%s'", be.getFrontText().getColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.getFrontText().hasGlowingText(),
                            () -> "Sign text should not be glowing"
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.isWaxed(),
                            () -> "Sign should not be waxed"
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.GLOW_INK_SAC, Direction.SOUTH),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().getColor() == DyeColor.RED,
                            () -> String.format("Expected sign text color to be 'red', got '%s'", be.getFrontText().getColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().hasGlowingText(),
                            () -> "Sign text should be glowing"
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.isWaxed(),
                            () -> "Sign should not be waxed"
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.HONEYCOMB, Direction.SOUTH),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().getColor() == DyeColor.RED,
                            () -> String.format("Expected sign text color to be 'red', got '%s'", be.getFrontText().getColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().hasGlowingText(),
                            () -> "Sign text should be glowing"
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.isWaxed(),
                            () -> "Sign should be waxed"
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.WOODEN_AXE, Direction.SOUTH),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().getColor() == DyeColor.RED,
                            () -> String.format("Expected sign text color to be 'red', got '%s'", be.getFrontText().getColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFrontText().hasGlowingText(),
                            () -> "Sign text should be glowing"
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.isWaxed(),
                            () -> "Sign should not be waxed"
                    );
                },
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testButtonPress(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_BUTTON.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, true)
        ));

        helper.runAfterDelay(33, () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false));
        helper.runAfterDelay(34, helper::succeed);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testStoneButtonPress(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_STONE_BUTTON.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, true)
        ));

        helper.runAfterDelay(23, () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false));
        helper.runAfterDelay(24, helper::succeed);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testLeverFlip(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_LEVER.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, LeverBlock.POWERED, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, LeverBlock.POWERED, true),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, LeverBlock.POWERED, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testDoorInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_DOOR.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, DoorBlock.OPEN, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, DoorBlock.OPEN, true),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, DoorBlock.OPEN, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testTrapDoorInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_TRAP_DOOR.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, true),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testFenceGateInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_FENCE_GATE.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FenceGateBlock.OPEN, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FenceGateBlock.OPEN, true),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FenceGateBlock.OPEN, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testPressurePlateInteract(GameTestHelper helper)
    {
        helper.setBlock(POS_ABOVE_FLOOR.below(), Blocks.REDSTONE_LAMP);
        int delay = TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_PRESSURE_PLATE.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PressurePlateBlock.POWERED, false),
                () -> TestUtils.spawnItemCentered(helper, Items.IRON_INGOT, POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PressurePlateBlock.POWERED, true),
                helper::killAllEntities
        ));

        helper.runAfterDelay(delay + 20, () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PressurePlateBlock.POWERED, false));
        helper.runAfterDelay(delay + 21, helper::succeed);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testBuildDoubleSlab(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_SLAB.get()),
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_SLAB.get()),
                () -> helper.succeedWhenBlockPresent(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get(), POS_ABOVE_FLOOR)
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testBuildDoublePanelNorthSouth(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_PANEL.get()),
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_PANEL.get(), Direction.SOUTH),
                () -> helper.assertBlockPresent(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get(), POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.FACING_NE, Direction.NORTH),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testBuildDoublePanelEastWest(GameTestHelper helper)
    {
        BlockState state = FBContent.BLOCK_FRAMED_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.EAST);

        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, state),
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_PANEL.get(), Direction.WEST),
                () -> helper.assertBlockPresent(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get(), POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.FACING_NE, Direction.EAST),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x2", batch = "interaction")
    public static void testBuildRailSlope(GameTestHelper helper)
    {
        helper.setBlock(POS_ABOVE_FLOOR, Blocks.STONE);

        BlockPos posSouth = POS_ABOVE_FLOOR.south();

        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(posSouth, FBContent.BLOCK_FRAMED_SLOPE.get()),
                () -> TestUtils.clickWithItem(helper, posSouth, Items.RAIL),
                () -> helper.succeedWhenBlockPresent(FBContent.BLOCK_FRAMED_RAIL_SLOPE.get(), posSouth)
        ));
    }

    @GameTest(template = "floor_1x2", batch = "interaction")
    public static void testBuildPoweredRailSlope(GameTestHelper helper)
    {
        helper.setBlock(POS_ABOVE_FLOOR, Blocks.STONE);

        BlockPos posSouth = POS_ABOVE_FLOOR.south();

        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(posSouth, FBContent.BLOCK_FRAMED_SLOPE.get()),
                () -> TestUtils.clickWithItem(helper, posSouth, Items.POWERED_RAIL),
                () -> helper.succeedWhenBlockPresent(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.get(), posSouth)
        ));
    }

    @GameTest(template = "floor_1x2", batch = "interaction")
    public static void testBuildDetectorRailSlope(GameTestHelper helper)
    {
        helper.setBlock(POS_ABOVE_FLOOR, Blocks.STONE);

        BlockPos posSouth = POS_ABOVE_FLOOR.south();

        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(posSouth, FBContent.BLOCK_FRAMED_SLOPE.get()),
                () -> TestUtils.clickWithItem(helper, posSouth, Items.DETECTOR_RAIL),
                () -> helper.succeedWhenBlockPresent(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.get(), posSouth)
        ));
    }

    @GameTest(template = "floor_1x2", batch = "interaction")
    public static void testBuildActivatorRailSlope(GameTestHelper helper)
    {
        helper.setBlock(POS_ABOVE_FLOOR, Blocks.STONE);

        BlockPos posSouth = POS_ABOVE_FLOOR.south();

        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(posSouth, FBContent.BLOCK_FRAMED_SLOPE.get()),
                () -> TestUtils.clickWithItem(helper, posSouth, Items.ACTIVATOR_RAIL),
                () -> helper.succeedWhenBlockPresent(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.get(), posSouth)
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testRotateCamo(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_CUBE.get()),
                () -> TestUtils.applyCamo(helper, POS_ABOVE_FLOOR, Blocks.OAK_LOG, List.of(Direction.UP)),
                () ->
                {
                    FramedBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getCamo().getState() == Blocks.OAK_LOG.defaultBlockState(),
                            () -> String.format("Expected oak log default state as camo, got %s", be.getCamo().getState())
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.ITEM_FRAMED_SCREWDRIVER.get()),
                () ->
                {
                    FramedBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getCamo().getState() == Blocks.OAK_LOG.defaultBlockState().cycle(RotatedPillarBlock.AXIS),
                            () -> String.format("Expected oak log rotated once as camo, got %s", be.getCamo().getState())
                    );
                },
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchChestLock(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_CHEST.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.DEFAULT),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.ITEM_FRAMED_HAMMER.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.CAMO),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.ITEM_FRAMED_HAMMER.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.NONE),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.ITEM_FRAMED_HAMMER.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.DEFAULT),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchOffsetPrism(GameTestHelper helper)
    {
        testSwitchOffset(helper, FBContent.BLOCK_FRAMED_PRISM_CORNER);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchOffsetInnerPrism(GameTestHelper helper)
    {
        testSwitchOffset(helper, FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchOffsetDoublePrism(GameTestHelper helper)
    {
        testSwitchOffset(helper, FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER);
    }

    private static void testSwitchOffset(GameTestHelper helper, RegistryObject<Block> block)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, block.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.OFFSET, false),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.ITEM_FRAMED_HAMMER.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.OFFSET, true),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.ITEM_FRAMED_HAMMER.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.OFFSET, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testFlowerPotInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_FLOWER_POT.get()),
                () -> TestUtils.applyCamo(helper, POS_ABOVE_FLOOR, Blocks.POLISHED_GRANITE, List.of(Direction.UP)),
                () ->
                {
                    FramedFlowerPotBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedFlowerPotBlockEntity.class);

                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.hasFlowerBlock(),
                            () -> String.format("Expected empty flower pot, got %s planted in the pot", be.getFlowerBlock())
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Blocks.CACTUS),
                () ->
                {
                    FramedFlowerPotBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedFlowerPotBlockEntity.class);

                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.hasFlowerBlock(),
                            () -> "Expected filled flower pot, got empty pot"
                    );

                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getFlowerBlock() == Blocks.CACTUS,
                            () -> String.format("Expected %s in flower pot, got %s instead", Blocks.CACTUS, be.getFlowerBlock())
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.AIR),
                () ->
                {
                    FramedFlowerPotBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedFlowerPotBlockEntity.class);

                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.hasFlowerBlock(),
                            () -> String.format("Expected empty flower pot, got %s planted in the pot", be.getFlowerBlock())
                    );
                },
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testIronDoorInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_IRON_DOOR.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, DoorBlock.OPEN, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                //No, this is not a typo, the door must not budge ;)
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, DoorBlock.OPEN, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testIronTrapDoorInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                //No, this is not a typo, the trapdoor must not budge ;)
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testLargeButtonPress(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_LARGE_BUTTON.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, true)
        ));

        helper.runAfterDelay(33, () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false));
        helper.runAfterDelay(34, helper::succeed);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testLargeStoneButtonPress(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, true)
        ));

        helper.runAfterDelay(23, () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false));
        helper.runAfterDelay(24, helper::succeed);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testTargetColoring(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.BLOCK_FRAMED_TARGET.get()),
                () ->
                {
                    FramedTargetBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedTargetBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getOverlayColor() == DyeColor.RED.getTextColor(),
                            () -> String.format("Expected target color to be '16711680' (red), got '%s'", be.getOverlayColor())
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.LIME_DYE),
                () ->
                {
                    FramedTargetBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedTargetBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getOverlayColor() == DyeColor.LIME.getTextColor(),
                            () -> String.format("Expected target color to be '12582656' (lime), got '%s'", be.getOverlayColor())
                    );
                },
                helper::succeed
        ));
    }



    private InteractionTests() { }
}
