package xfacthd.framedblocks.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;

import java.util.*;

public class TestUtils
{
    private static final RegistryObject<Item> FRAMED_HAMMER = RegistryObject.of(new ResourceLocation(FramedBlocksAPI.getInstance().modid(), "framed_hammer"), ForgeRegistries.ITEMS);
    private static final Set<IBlockType> TESTED_TYPES = new HashSet<>();
    private static final BlockPos BLOCK_TOP_BOTTOM = new BlockPos(1, 3, 1);
    private static final BlockPos BLOCK_SIDE = new BlockPos(1, 2, 2);
    private static final BlockPos LIGHT_TOP = new BlockPos(1, 4, 1);
    private static final BlockPos LIGHT_BOTTOM = new BlockPos(1, 2, 1);
    private static final BlockPos LIGHT_SIDE = new BlockPos(1, 2, 3);

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
        testBlock(helper, BLOCK_TOP_BOTTOM, LIGHT_TOP, state, List.of(Direction.UP));
    }

    /**
     * Test whether the given {@link BlockState} occludes the light source placed above the block
     */
    public static void testBlockOccludesLightAbove(GameTestHelper helper, BlockState state)
    {
        testBlock(helper, BLOCK_TOP_BOTTOM, LIGHT_BOTTOM, state, List.of(Direction.DOWN));
    }

    /**
     * Test whether the given {@link BlockState} occludes the light source placed on the north side of the block
     */
    public static void testBlockOccludesLightNorth(GameTestHelper helper, BlockState state)
    {
        testBlock(helper, BLOCK_SIDE, LIGHT_SIDE, state, List.of(Direction.SOUTH));
    }

    /**
     * Test whether the given double block {@link BlockState} occludes the light source placed belowthe block
     */
    public static void testDoubleBlockOccludesLightBelow(GameTestHelper helper, BlockState state, List<Direction> camoSides)
    {
        testBlock(helper, BLOCK_TOP_BOTTOM, LIGHT_TOP, state, camoSides);
    }

    private static void testBlock(GameTestHelper helper, BlockPos blockPos, BlockPos lightPos, BlockState state, List<Direction> camoSides)
    {
        if (!assertCanOcclude(helper, state.getBlock())) { return; }

        TESTED_TYPES.add(((IFramedBlock) state.getBlock()).getBlockType());

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

    public static boolean isMissingType(IBlockType type) { return !TESTED_TYPES.contains(type); }
}
