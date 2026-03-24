package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RotateMirrorErrors
{
    private static final RotationDirection[] DIRECTIONS = RotationDirection.values();
    private static final WrenchRotationMode[] MODES = WrenchRotationMode.values();
    private static final Rotation[] ROTATIONS = Rotation.values();
    private static final Mirror[] MIRRORS = Mirror.values();

    @SuppressWarnings("deprecation")
    public static void checkRotateMirrorErrors(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("rotate/mirror correctness");

        Set<Block> knownFaultyWrench = new HashSet<>();
        Set<Block> knownFaultyRotate = new HashSet<>();
        Set<Block> knownFaultyMirror = new HashSet<>();
        blocks.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream)
                .forEach(state ->
                {
                    IFramedBlock block = (IFramedBlock) state.getBlock();
                    for (WrenchRotationMode mode : MODES)
                    {
                        for (RotationDirection direction : DIRECTIONS)
                        {
                            String type = "wrench_rotate_" + mode.getSerializedName();
                            guard(reporter, state, (modState, mod) -> block.rotate(state, mod, mode), direction, type, knownFaultyWrench);
                        }
                    }

                    for (Rotation rot : ROTATIONS)
                    {
                        guard(reporter, state, BlockState::rotate, rot, "rotate", knownFaultyRotate);
                    }

                    for (Mirror mirror : MIRRORS)
                    {
                        guard(reporter, state, BlockState::mirror, mirror, "mirror", knownFaultyMirror);
                    }
                });

        reporter.endTest();
    }

    private static <T extends Enum<T>> void guard(
            SelfTestReporter reporter, BlockState state, Action<T> action, T modifier, String type, Set<Block> knownFaulty
    )
    {
        try
        {
            var ignored = action.perform(state, modifier);
        }
        catch (Throwable t)
        {
            if (!knownFaulty.add(state.getBlock())) return;

            reporter.error(
                    "Action '{}' throws exception on block '{}' with modifier '{}': {}",
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
