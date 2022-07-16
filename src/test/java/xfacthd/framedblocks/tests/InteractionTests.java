package xfacthd.framedblocks.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.test.TestUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedSign.get()),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getTextColor() == DyeColor.BLACK,
                            () -> String.format("Expected sign text color to be 'black', got '%s'", be.getTextColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.hasGlowingText(),
                            () -> "Sign text glowing unexpectedly"
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.RED_DYE),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getTextColor() == DyeColor.RED,
                            () -> String.format("Expected sign text color to be 'red', got '%s'", be.getTextColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            !be.hasGlowingText(),
                            () -> "Sign text should not be glowing"
                    );
                },
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, Items.GLOW_INK_SAC),
                () ->
                {
                    FramedSignBlockEntity be = TestUtils.getBlockEntity(helper, POS_ABOVE_FLOOR, FramedSignBlockEntity.class);
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.getTextColor() == DyeColor.RED,
                            () -> String.format("Expected sign text color to be 'red', got '%s'", be.getTextColor())
                    );
                    TestUtils.assertTrue(
                            helper,
                            POS_ABOVE_FLOOR,
                            be.hasGlowingText(),
                            () -> "Sign text should be glowing"
                    );
                },
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testButtonPress(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedButton.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, true)
        ));

        helper.runAfterDelay(33, () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, ButtonBlock.POWERED, false));
        helper.runAfterDelay(34, helper::succeed);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testLeverFlip(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedLever.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedDoor.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedTrapDoor.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedGate.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedPressurePlate.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedSlab.get()),
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.blockFramedSlab.get()),
                () -> helper.succeedWhenBlockPresent(FBContent.blockFramedDoubleSlab.get(), POS_ABOVE_FLOOR)
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testBuildDoublePanelNorthSouth(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedPanel.get()),
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.blockFramedPanel.get(), Direction.SOUTH),
                () -> helper.assertBlockPresent(FBContent.blockFramedDoublePanel.get(), POS_ABOVE_FLOOR),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.FACING_NE, Direction.NORTH),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testBuildDoublePanelEastWest(GameTestHelper helper)
    {
        BlockState state = FBContent.blockFramedPanel.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.EAST);

        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, state),
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.blockFramedPanel.get(), Direction.WEST),
                () -> helper.assertBlockPresent(FBContent.blockFramedDoublePanel.get(), POS_ABOVE_FLOOR),
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
                () -> helper.setBlock(posSouth, FBContent.blockFramedSlope.get()),
                () -> TestUtils.clickWithItem(helper, posSouth, Items.RAIL),
                () -> helper.succeedWhenBlockPresent(FBContent.blockFramedRailSlope.get(), posSouth)
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testRotateCamo(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedCube.get()),
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
                () -> TestUtils.clickWithItem(helper, POS_ABOVE_FLOOR, FBContent.itemFramedScrewdriver.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedChest.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.DEFAULT),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.itemFramedHammer.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.CAMO),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.itemFramedHammer.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.NONE),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.itemFramedHammer.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, PropertyHolder.LATCH_TYPE, LatchType.DEFAULT),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchOffsetPrism(GameTestHelper helper)
    {
        testSwitchOffset(helper, FBContent.blockFramedPrismCorner);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchOffsetInnerPrism(GameTestHelper helper)
    {
        testSwitchOffset(helper, FBContent.blockFramedInnerPrismCorner);
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testSwitchOffsetDoublePrism(GameTestHelper helper)
    {
        testSwitchOffset(helper, FBContent.blockFramedDoublePrismCorner);
    }

    private static void testSwitchOffset(GameTestHelper helper, RegistryObject<Block> block)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, block.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.OFFSET, false),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.itemFramedHammer.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.OFFSET, true),
                () -> TestUtils.attackWithItem(helper, POS_ABOVE_FLOOR, FBContent.itemFramedHammer.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, FramedProperties.OFFSET, false),
                helper::succeed
        ));
    }

    @GameTest(template = "floor_1x1", batch = "interaction")
    public static void testFlowerPotInteract(GameTestHelper helper)
    {
        TestUtils.chainTasks(helper, List.of(
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedFlowerPot.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedIronDoor.get()),
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
                () -> helper.setBlock(POS_ABOVE_FLOOR, FBContent.blockFramedIronTrapDoor.get()),
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, false),
                () -> helper.useBlock(POS_ABOVE_FLOOR),
                //No, this is not a typo, the trapdoor must not budge ;)
                () -> helper.assertBlockProperty(POS_ABOVE_FLOOR, TrapDoorBlock.OPEN, false),
                helper::succeed
        ));
    }



    private InteractionTests() { }
}
