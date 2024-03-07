package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.FramedBlocks;

import java.util.*;

public final class RotateMirrorErrors
{
    private static final Rotation[] ROTATIONS = Rotation.values();
    private static final Mirror[] MIRRORS = Mirror.values();

    @SuppressWarnings("deprecation")
    public static void checkRotateMirrorErrors(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking rotate/mirror correctness");

        Set<Block> knownFaultyRotate = new HashSet<>();
        Set<Block> knownFaultyMirror = new HashSet<>();
        blocks.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream)
                .forEach(state ->
                {
                    for (Rotation rot : ROTATIONS)
                    {
                        if (rot == Rotation.NONE) continue;
                        guard(state, BlockState::rotate, rot, "rotate", knownFaultyRotate);
                    }

                    for (Mirror mirror : MIRRORS)
                    {
                        if (mirror == Mirror.NONE) continue;
                        guard(state, BlockState::mirror, mirror, "mirror", knownFaultyMirror);
                    }
                });
    }

    private static <T extends Enum<T>> void guard(
            BlockState state, Action<T> action, T modifier, String type, Set<Block> knownFaulty
    )
    {
        try
        {
            var ignored = action.perform(state, modifier);
        }
        catch (Throwable t)
        {
            if (!knownFaulty.add(state.getBlock())) return;

            FramedBlocks.LOGGER.error(
                    "    Action '{}' throws exception on block '{}' with modifier '{}': {}",
                    type, state.getBlock(), modifier, t.getMessage()
            );
        }
    }

    @FunctionalInterface
    private interface Action<T extends Enum<T>>
    {
        BlockState perform(BlockState state, T modifier);
    }



    private RotateMirrorErrors() { }
}
