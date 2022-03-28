package xfacthd.framedblocks.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;

import java.util.*;

public class TestUtils
{
    private static final RegistryObject<Item> FRAMED_HAMMER = RegistryObject.of(new ResourceLocation(FramedBlocksAPI.getInstance().modid(), "framed_hammer"), ForgeRegistries.ITEMS);
    private static final BlockPos OCCLUSION_BLOCK_TOP_BOTTOM = new BlockPos(1, 3, 1);
    private static final BlockPos OCCLUSION_BLOCK_SIDE = new BlockPos(1, 2, 2);
    private static final BlockPos OCCLUSION_LIGHT_TOP = new BlockPos(1, 4, 1);
    private static final BlockPos OCCLUSION_LIGHT_BOTTOM = new BlockPos(1, 2, 1);
    private static final BlockPos OCCLUSION_LIGHT_SIDE = new BlockPos(1, 2, 3);
    private static final BlockPos EMISSION_BLOCK = new BlockPos(1, 2, 1);
    private static final BlockPos EMISSION_LIGHT = new BlockPos(1, 3, 1);

    public static boolean assertFramedBlock(GameTestHelper helper, Block block)
    {
        if (!(block instanceof IFramedBlock))
        {
            helper.fail(String.format("Expected instance of IFramedBlock, got %s", block.getRegistryName()));
            return false;
        }
        return true;
    }

    public static void applyCamo(GameTestHelper helper, BlockPos pos, Block camo, List<Direction> camoSides)
    {
        Map<Direction, Block> camos = new HashMap<>();
        camoSides.forEach(side -> camos.put(side, camo));
        applyCamo(helper, pos, camos);
    }

    public static void applyCamo(GameTestHelper helper, BlockPos pos, Map<Direction, Block> camos)
    {
        BlockPos absPos = helper.absolutePos(pos);
        Player player = helper.makeMockPlayer();

        camos.forEach((side, camo) ->
        {
            Item item = camo == Blocks.AIR ? FRAMED_HAMMER.get() : camo.asItem();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));

            InteractionResult result = helper.getBlockState(pos).use(
                    helper.getLevel(),
                    player,
                    InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(absPos), side, absPos, true)
            );

            if (!result.shouldAwardStats())
            {
                helper.fail(String.format("Interaction with block %s failed", helper.getBlockState(pos)));
            }
        });
    }

    public static void clickWithItem(GameTestHelper helper, BlockPos pos, Item item)
    {
        BlockPos absPos = helper.absolutePos(pos);

        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));

        InteractionResult result = helper.getBlockState(pos).use(
                helper.getLevel(),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absPos), Direction.UP, absPos, true)
        );

        if (!result.shouldAwardStats())
        {
            helper.fail(String.format("Interaction with block %s failed", helper.getBlockState(pos)));
        }
    }

    /**
     * Chains the given task with a delay of one tick between each task
     * @param helper The GameTestHelper of the running test
     * @param tasks The list of tasks to schedule
     */
    public static void chainTasks(GameTestHelper helper, List<Runnable> tasks)
    {
        int delay = 0;
        for (Runnable task : tasks)
        {
            helper.runAfterDelay(delay, task);
            delay++;
        }
    }

    // == Occlusion testing ==

    public static boolean assertCanOcclude(GameTestHelper helper, Block block)
    {
        if (!assertFramedBlock(helper, block)) { return false; }

        if (!((IFramedBlock) block).getBlockType().canOccludeWithSolidCamo())
        {
            helper.fail(String.format("Block %s can not occlude with a solid camo", block.getRegistryName()));
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
     * Test whether the given double block {@link BlockState} occludes the light source placed belowthe block
     */
    public static void testDoubleBlockOccludesLightBelow(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlockOccludesLight(helper, OCCLUSION_BLOCK_TOP_BOTTOM, OCCLUSION_LIGHT_TOP, state, camoSides);
    }

    private static void testBlockOccludesLight(GameTestHelper helper, BlockPos blockPos, BlockPos lightPos, BlockState state, List<Direction> camoSides)
    {
        if (!assertCanOcclude(helper, state.getBlock())) { return; }

        //Indirectly validate that the correct structure is used
        helper.assertBlockPresent(Blocks.AIR, blockPos);
        helper.assertBlockPresent(Blocks.GLASS, lightPos);

        chainTasks(helper, List.of(
                () -> helper.setBlock(blockPos, state),
                () ->
                {
                    helper.assertBlockProperty(blockPos, FramedProperties.SOLID, false);
                    assertBlockLight(helper, blockPos, lightPos, 13);
                },
                () -> applyCamo(helper, blockPos, Blocks.GLASS, camoSides),
                () ->
                {
                    helper.assertBlockProperty(blockPos, FramedProperties.SOLID, false);
                    assertBlockLight(helper, blockPos, lightPos, 13);
                },
                () -> applyCamo(helper, blockPos, Blocks.AIR, camoSides),
                () -> applyCamo(helper, blockPos, Blocks.GRANITE, camoSides),
                () ->
                {
                    helper.assertBlockProperty(blockPos, FramedProperties.SOLID, true);
                    assertBlockLight(helper, blockPos, lightPos, 0);
                },
                helper::succeed
        ));
    }

    public static void testBlockLightEmission(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlockLightEmission(helper, state, camoSides, 0);
    }

    public static void testBlockLightEmission(GameTestHelper helper, BlockState state, List<Direction> camoSides, int baseEmission)
    {
        //noinspection deprecation
        int glowstoneLight = Blocks.GLOWSTONE.defaultBlockState().getLightEmission();

        chainTasks(helper, List.of(
                () -> helper.setBlock(EMISSION_BLOCK, state),
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, baseEmission), //Check base emission
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.GLOWSTONE, camoSides),
                () -> {}, //Light changes from BlockState changes may take two ticks to propagate, apparently
                () -> assertBlockLightEmission(helper, EMISSION_BLOCK, EMISSION_LIGHT, glowstoneLight), //Check camo emission
                () -> applyCamo(helper, EMISSION_BLOCK, Blocks.AIR, camoSides),
                () -> {}, //Light changes from BlockState changes may take two ticks to propagate, apparently
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
}
