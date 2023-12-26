package xfacthd.framedblocks.api.test;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.util.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TestUtils
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BlockPos OCCLUSION_BLOCK_TOP_BOTTOM = new BlockPos(1, 3, 1);
    private static final BlockPos OCCLUSION_BLOCK_SIDE = new BlockPos(1, 2, 2);
    private static final BlockPos OCCLUSION_LIGHT_TOP = new BlockPos(1, 4, 1);
    private static final BlockPos OCCLUSION_LIGHT_BOTTOM = new BlockPos(1, 2, 1);
    private static final BlockPos OCCLUSION_LIGHT_SIDE = new BlockPos(1, 2, 3);
    private static final BlockPos EMISSION_BLOCK = new BlockPos(1, 2, 1);
    private static final BlockPos EMISSION_LIGHT = new BlockPos(1, 3, 1);
    private static final BlockPos INTANGIBILITY_BLOCK = new BlockPos(0, 2, 0);
    private static final BlockPos BEACON_TINT_BLOCK = new BlockPos(0, 2, 0);
    private static final BlockPos BEACON_TINT_BEACON = new BlockPos(0, 0, 0);
    private static final Predicate<float[]> BEACON_PREDICATE_RED = arr -> Arrays.equals(arr, DyeColor.RED.getTextureDiffuseColors());
    private static final String BEACON_COLOR_TEXT_RED = Arrays.toString(DyeColor.RED.getTextureDiffuseColors());

    public static boolean assertFramedBlock(GameTestHelper helper, Block block)
    {
        if (!(block instanceof IFramedBlock))
        {
            helper.fail(String.format("Expected instance of IFramedBlock, got %s", BuiltInRegistries.BLOCK.getKey(block)));
            return false;
        }
        return true;
    }

    public static void applyCamo(GameTestHelper helper, BlockPos pos, Block camo, List<Direction> camoSides)
    {
        Map<Direction, Block> camos = new LinkedHashMap<>();
        camoSides.forEach(side -> camos.put(side, camo));
        applyCamo(helper, pos, camos);
    }

    public static void applyCamo(GameTestHelper helper, BlockPos pos, Map<Direction, Block> camos)
    {
        BlockPos absPos = helper.absolutePos(pos);
        Player player = helper.makeMockPlayer();

        int count = 0;
        for (Map.Entry<Direction, Block> entry : camos.entrySet())
        {
            Direction side = entry.getKey();
            Block camo = entry.getValue();

            Item item = camo == Blocks.AIR ? Utils.FRAMED_HAMMER.value() : camo.asItem();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));

            Vec3 hitVec = switch (count)
            {
                case 0 -> Vec3.atCenterOf(absPos).add(-.1, -.1, -.1);
                case 1 -> Vec3.atCenterOf(absPos).add(.1, .1, .1);
                default -> Vec3.atCenterOf(absPos);
            };
            InteractionResult result = helper.getBlockState(pos).use(
                    helper.getLevel(),
                    player,
                    InteractionHand.MAIN_HAND,
                    new BlockHitResult(hitVec, side, absPos, true)
            );
            count++;

            if (!result.shouldAwardStats())
            {
                helper.fail(String.format(
                        "Camo application on side '%s' of block '%s' failed", side, helper.getBlockState(pos)
                ), pos);
            }
        }
    }

    public static void clickWithItem(GameTestHelper helper, BlockPos pos, ItemLike item)
    {
        clickWithItem(helper, pos, item, Direction.UP, false);
    }

    public static void clickWithItem(GameTestHelper helper, BlockPos pos, ItemLike item, boolean sneak)
    {
        clickWithItem(helper, pos, item, Direction.UP, sneak);
    }

    public static void clickWithItem(GameTestHelper helper, BlockPos pos, ItemLike item, Direction side)
    {
        clickWithItem(helper, pos, item, side, false);
    }

    public static void clickWithItem(GameTestHelper helper, BlockPos pos, ItemLike item, Direction side, boolean sneak)
    {
        BlockPos absPos = helper.absolutePos(pos);

        Player player = helper.makeMockPlayer();
        player.setPos(absPos.relative(side).getCenter());
        player.setShiftKeyDown(sneak);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));

        InteractionResult result = helper.getBlockState(pos).use(
                helper.getLevel(),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absPos), side, absPos, true)
        );

        if (!result.shouldAwardStats())
        {
            helper.fail(String.format("Interaction with block %s failed", helper.getBlockState(pos)), pos);
        }
    }

    public static void attackWithItem(GameTestHelper helper, BlockPos pos, ItemLike item)
    {
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));

        CommonHooks.onLeftClickBlock(player, helper.absolutePos(pos), Direction.UP, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
    }

    /**
     * Chains the given task with a delay of one tick between each task
     * @param helper The GameTestHelper of the running test
     * @param tasks The list of tasks to schedule
     * @return Returns the delay of the last task + 1 tick
     */
    public static int chainTasks(GameTestHelper helper, List<TestRunnable> tasks)
    {
        return chainTasks(helper, tasks, 0);
    }

    /**
     * Chains the given task with a delay of one tick between each task
     * @param helper The GameTestHelper of the running test
     * @param tasks The list of tasks to schedule
     * @param initialDelay The delay in ticks the task chain starts at
     * @return Returns the delay of the last task + 1 tick
     */
    public static int chainTasks(GameTestHelper helper, List<TestRunnable> tasks, int initialDelay)
    {
        int delay = initialDelay;
        for (TestRunnable task : tasks)
        {
            helper.runAfterDelay(delay, task);
            delay += task.getDuration();
        }
        return delay;
    }

    public static <T extends BlockEntity> T getBlockEntity(GameTestHelper helper, BlockPos relPos, Class<T> beClass)
    {
        BlockEntity be = helper.getBlockEntity(relPos);
        if (be == null)
        {
            throw new GameTestAssertPosException(
                    String.format("Expected %s, got null", beClass.getSimpleName()),
                    helper.absolutePos(relPos),
                    relPos,
                    helper.getTick()
            );
        }

        if (!beClass.isInstance(be))
        {
            throw new GameTestAssertPosException(
                    String.format("Expected %s, got %s", beClass.getSimpleName(), be.getClass().getSimpleName()),
                    helper.absolutePos(relPos),
                    relPos,
                    helper.getTick()
            );
        }

        return beClass.cast(be);
    }

    public static void assertTrue(GameTestHelper helper, BlockPos relPos, boolean value, Supplier<String> message)
    {
        if (!value)
        {
            throw new GameTestAssertPosException(message.get(), helper.absolutePos(relPos), relPos, helper.getTick());
        }
    }

    public static void spawnItemCentered(GameTestHelper helper, Item item, BlockPos relPos)
    {
        helper.spawnItem(item, relPos.getX() + .5F, relPos.getY(), relPos.getZ() + .5F);
    }

    // == Occlusion testing ==

    public static boolean assertCanOcclude(GameTestHelper helper, Block block)
    {
        if (!assertFramedBlock(helper, block))
        {
            return false;
        }

        if (!((IFramedBlock) block).getBlockType().canOccludeWithSolidCamo())
        {
            helper.fail(String.format("Block %s can not occlude with a solid camo", BuiltInRegistries.BLOCK.getKey(block)));
            return false;
        }
        return true;
    }

    /**
     * Test whether the given {@link BlockState} occludes the light source placed below the block
     */
    public static void testBlockOccludesLightBelow(GameTestHelper helper, BlockState state)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_TOP_BOTTOM, OCCLUSION_LIGHT_TOP, state, List.of(Direction.UP));
    }

    /**
     * Test whether the given {@link BlockState} occludes the light source placed above the block
     */
    public static void testBlockOccludesLightAbove(GameTestHelper helper, BlockState state)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_TOP_BOTTOM, OCCLUSION_LIGHT_BOTTOM, state, List.of(Direction.DOWN));
    }

    /**
     * Test whether the given {@link BlockState} occludes the light source placed on the north side of the block
     */
    public static void testBlockOccludesLightNorth(GameTestHelper helper, BlockState state)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_SIDE, OCCLUSION_LIGHT_SIDE, state, List.of(Direction.SOUTH));
    }

    /**
     * Test whether the given double block {@link BlockState} occludes the light source placed below the block
     */
    public static void testDoubleBlockOccludesLightBelow(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_TOP_BOTTOM, OCCLUSION_LIGHT_TOP, state, camoSides);
    }

    /**
     * Test whether the given double block {@link BlockState} occludes the light source placed above the block
     */
    public static void testDoubleBlockOccludesLightAbove(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_TOP_BOTTOM, OCCLUSION_LIGHT_BOTTOM, state, camoSides);
    }

    /**
     * Test whether the given {@link BlockState} occludes the light source placed on the north side of the block
     */
    public static void testDoubleBlockOccludesLightNorth(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_SIDE, OCCLUSION_LIGHT_SIDE, state, camoSides);
    }

    private static void testBlockOccludesLight(
            GameTestHelper helper, BlockPos blockPos, BlockPos lightPos, BlockState state, List<Direction> camoSides
    )
    {
        if (!assertCanOcclude(helper, state.getBlock()))
        {
            return;
        }

        //Indirectly validate that the correct structure is used
        helper.assertBlockPresent(Blocks.AIR, blockPos);
        helper.assertBlockPresent(Blocks.GLASS, lightPos);

        chainTasks(helper, List.of(
                () -> helper.setBlock(blockPos, state),
                new TestDelay(5), //Light occlusion changes from BlockState changes may take a few ticks to propagate, apparently
                () ->
                {
                    helper.assertBlockProperty(blockPos, FramedProperties.SOLID, false);
                    assertBlockLight(helper, blockPos, lightPos, 13);
                },
                () -> applyCamo(helper, blockPos, Blocks.GLASS, camoSides),
                new TestDelay(5), //Light occlusion changes from BlockState changes may take a few ticks to propagate, apparently
                () ->
                {
                    helper.assertBlockProperty(blockPos, FramedProperties.SOLID, false);
                    assertBlockLight(helper, blockPos, lightPos, 13);
                },
                () -> applyCamo(helper, blockPos, Blocks.AIR, camoSides),
                () -> applyCamo(helper, blockPos, Blocks.GRANITE, camoSides),
                new TestDelay(5), //Light occlusion changes from BlockState changes may take a few ticks to propagate, apparently
                () ->
                {
                    helper.assertBlockProperty(blockPos, FramedProperties.SOLID, true);
                    assertBlockLight(helper, blockPos, lightPos, 0);
                },
                helper::succeed
        ));
    }

    // == Light emission testing ==

    public static void testBlockLightEmission(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlockLightEmission(helper, state, camoSides, 0);
    }

    public static void testBlockLightEmission(
            GameTestHelper helper, BlockState state, List<Direction> camoSides, int baseEmission
    )
    {
        //noinspection deprecation
        int glowstoneLight = Blocks.GLOWSTONE.defaultBlockState().getLightEmission();

        chainTasks(helper, List.of(
                () -> helper.setBlock(EMISSION_BLOCK, state),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, baseEmission), //Check base emission
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.GLOWSTONE, camoSides),
                new TestDelay(5), //Light changes from BlockState changes may take a few ticks to propagate, apparently
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, glowstoneLight), //Check camo emission
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.AIR, camoSides),
                new TestDelay(5), //Light changes from BlockState changes may take a few ticks to propagate, apparently
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, baseEmission), //Check camo emission reset
                () -> clickWithItem(helper, EMISSION_BLOCK, Items.GLOWSTONE_DUST),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, 15), //Check glowstone dust emission without camo
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.GLASS, camoSides),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, 15), //Check glowstone dust emission with non-solid camo
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.AIR, camoSides),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, 15), //Check glowstone dust emission after non-solid camo removed
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.GRANITE, camoSides),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, 15), //Check glowstone dust emission with solid camo
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.AIR, camoSides),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, 15), //Check glowstone dust emission after solid camo removed
                helper::succeed
        ));
    }

    public static void assertBlockLightEmission(GameTestHelper helper, BlockPos blockPos, BlockPos lightPos, int light)
    {
        int emission = helper.getLevel().getLightEmission(helper.absolutePos(blockPos));
        if (emission != light)
        {
            BlockState state = helper.getBlockState(blockPos);
            throw new GameTestAssertPosException(
                    String.format("Incorrect light emission for %s, expected %d, got %d", state, light, emission),
                    helper.absolutePos(lightPos),
                    lightPos,
                    helper.getTick()
            );
        }

        assertBlockLight(helper, blockPos, lightPos, Math.max(light - 1, 0));
    }

    private static void assertBlockLight(GameTestHelper helper, BlockPos blockPos, BlockPos lightPos, int light)
    {
        int actualLight = helper.getLevel().getLightEngine().getRawBrightness(helper.absolutePos(lightPos), 15);
        if (actualLight != light)
        {
            BlockState state = helper.getBlockState(blockPos);
            throw new GameTestAssertPosException(
                    String.format("Incorrect light level for %s, expected %d, got %d", state, light, actualLight),
                    helper.absolutePos(lightPos),
                    lightPos,
                    helper.getTick()
            );
        }
    }

    // == Intangibility testing ==

    public static void testBlockIntangibility(GameTestHelper helper, BlockState state)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility())
        {
            helper.fail("Intangibility is not enabled in the ServerConfig");
        }

        chainTasks(helper, List.of(
                () -> helper.setBlock(INTANGIBILITY_BLOCK, state),
                () ->
                {
                    FramedBlockEntity be = getBlockEntity(helper, INTANGIBILITY_BLOCK, FramedBlockEntity.class);
                    assertTrue(
                            helper,
                            INTANGIBILITY_BLOCK,
                            !be.isIntangible(CollisionContext.empty()),
                            () -> String.format("Block '%s' is intangible without interaction", state.getBlock())
                    );

                    BlockPos pos = helper.absolutePos(INTANGIBILITY_BLOCK);
                    BlockState currState = helper.getBlockState(INTANGIBILITY_BLOCK);
                    assertTrue(
                            helper,
                            INTANGIBILITY_BLOCK,
                            !currState.getShape(helper.getLevel(), pos, CollisionContext.empty()).isEmpty(),
                            () -> String.format("Block '%s' returns an empty shape when not intangible", state.getBlock())
                    );
                },
                () -> clickWithItem(helper, INTANGIBILITY_BLOCK, ConfigView.Server.INSTANCE.getIntangibilityMarkerItem()),
                () ->
                {
                    FramedBlockEntity be = getBlockEntity(helper, INTANGIBILITY_BLOCK, FramedBlockEntity.class);
                    assertTrue(
                            helper,
                            INTANGIBILITY_BLOCK,
                            be.isIntangible(CollisionContext.empty()),
                            () -> String.format("Block '%s' is not intangible after interaction", state.getBlock())
                    );

                    BlockPos pos = helper.absolutePos(INTANGIBILITY_BLOCK);
                    BlockState currState = helper.getBlockState(INTANGIBILITY_BLOCK);
                    assertTrue(
                            helper,
                            INTANGIBILITY_BLOCK,
                            currState.getShape(helper.getLevel(), pos, CollisionContext.empty()).isEmpty(),
                            () -> String.format("Block '%s' does not return an empty shape when intangible", state.getBlock())
                    );

                    Player player = helper.makeMockPlayer();
                    CollisionContext ctx = CollisionContext.of(player);
                    //noinspection ConstantConditions
                    BuiltInRegistries.ITEM.getTag(Utils.DISABLE_INTANGIBLE)
                            .stream()
                            .flatMap(HolderSet::stream)
                            .forEach(item ->
                            {
                                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));

                                assertTrue(
                                        helper,
                                        INTANGIBILITY_BLOCK,
                                        !be.isIntangible(ctx),
                                        () -> String.format(
                                                "Block '%s' is intangible when targetted by item '%s' which is tagged with 'framedblocks:disable_intangible'",
                                                state.getBlock(),
                                                item
                                        )
                                );

                                assertTrue(
                                        helper,
                                        INTANGIBILITY_BLOCK,
                                        !currState.getShape(helper.getLevel(), pos, ctx).isEmpty(),
                                        () -> String.format(
                                                "Block '%s' is intangible when targetted by item '%s' which is tagged with 'framedblocks:disable_intangible'",
                                                state.getBlock(),
                                                item
                                        )
                                );
                            });

                    if (FMLEnvironment.dist.isClient())
                    {
                        ClientGuard.testHasParticleOverride(helper, state);
                    }
                    else
                    {
                        LOGGER.warn("Can't test particle override of block '{}', running on dedicated server", state.getBlock());
                    }
                },
                () -> clickWithItem(helper, INTANGIBILITY_BLOCK, Utils.FRAMED_SCREWDRIVER.value(), true),
                () ->
                {
                    FramedBlockEntity be = getBlockEntity(helper, INTANGIBILITY_BLOCK, FramedBlockEntity.class);
                    assertTrue(
                            helper,
                            INTANGIBILITY_BLOCK,
                            !be.isIntangible(CollisionContext.empty()),
                            () -> String.format("Block '%s' is intangible after removing marker", state.getBlock())
                    );

                    BlockPos pos = helper.absolutePos(INTANGIBILITY_BLOCK);
                    BlockState currState = helper.getBlockState(INTANGIBILITY_BLOCK);
                    assertTrue(
                            helper,
                            INTANGIBILITY_BLOCK,
                            !currState.getShape(helper.getLevel(), pos, CollisionContext.empty()).isEmpty(),
                            () -> String.format("Block '%s' does returns an empty shape after removing marker", state.getBlock())
                    );
                },
                helper::succeed
        ));
    }

    private static final class ClientGuard
    {
        public static void testHasParticleOverride(GameTestHelper helper, BlockState state)
        {
            IClientBlockExtensions blockExt = IClientBlockExtensions.of(state);
            assertTrue(
                    helper,
                    INTANGIBILITY_BLOCK,
                    blockExt instanceof FramedBlockRenderProperties,
                    () -> String.format("Block '%s' doesn't have required IClientBlockExtensions", state.getBlock())
            );

            BlockPos pos = helper.absolutePos(INTANGIBILITY_BLOCK);
            BlockHitResult miss = BlockHitResult.miss(Vec3.ZERO, Direction.UP, pos);
            boolean hit = false;
            boolean destroy = false;

            try
            {
                ParticleEngine engine = Minecraft.getInstance().particleEngine;
                hit = blockExt.addHitEffects(state, helper.getLevel(), miss, engine);
                destroy = blockExt.addDestroyEffects(state, helper.getLevel(), pos, engine);
            }
            catch (Throwable e)
            {
                helper.fail(String.format("Error while testing particle overrides, likely caused by a misconfigured particle override:\n%s", e));
            }

            assertTrue(helper, INTANGIBILITY_BLOCK, hit, () -> String.format("Block '%s' doesn't handle hit particles", state.getBlock()));
            assertTrue(helper, INTANGIBILITY_BLOCK, destroy, () -> String.format("Block '%s' doesn't handle destroy particles", state.getBlock()));
        }
    }

    // == Beacon beam tint testing ==

    public static void testBeaconBeamTinting(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        chainTasks(helper, List.of(
                () -> helper.setBlock(BEACON_TINT_BLOCK, state),
                () -> assertBeaconTint(helper, Blocks.AIR, Objects::isNull, "null"),
                () -> applyCamo(helper, BEACON_TINT_BLOCK, Blocks.RED_STAINED_GLASS, camoSides),
                () -> assertBeaconTint(helper, Blocks.RED_STAINED_GLASS, BEACON_PREDICATE_RED, BEACON_COLOR_TEXT_RED),
                helper::succeed
        ));
    }

    private static void assertBeaconTint(GameTestHelper helper, Block camo, Predicate<float[]> predicate, String expected)
    {
        BlockState state = helper.getBlockState(BEACON_TINT_BLOCK);
        assertFramedBlock(helper, state.getBlock());

        float[] tint = state.getBeaconColorMultiplier(
                helper.getLevel(),
                helper.absolutePos(BEACON_TINT_BLOCK),
                helper.absolutePos(BEACON_TINT_BEACON)
        );
        assertTrue(helper, BEACON_TINT_BLOCK, predicate.test(tint), () -> String.format(
                "Block '%s' applies incorrect beacon color multiplier for camo '%s', expected %s, got %s",
                state.getBlock(), camo, expected, Arrays.toString(tint)
        ));
    }



    private TestUtils() { }
}
