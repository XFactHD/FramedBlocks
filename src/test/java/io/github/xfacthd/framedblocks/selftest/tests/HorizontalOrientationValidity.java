package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class HorizontalOrientationValidity
{
    public static void checkHorizontalOrientations(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("valid horizontal orientations");

        outer: for (Block block : blocks)
        {
            List<BlockState> states = block.getStateDefinition().getPossibleStates();
            List<Direction> orientations = new ArrayList<>(states.size());
            for (BlockState state : states)
            {
                Direction orientation;
                try
                {
                    orientation = ((IFramedBlock) state.getBlock()).getHorizontalOrientation(state);
                }
                catch (Throwable t)
                {
                    reporter.error("Block '{}' throws exception while querying horizontal orientation", state, t);
                    continue outer;
                }
                if (orientation != null)
                {
                    orientations.add(orientation);
                    if (DirUtils.isY(orientation))
                    {
                        reporter.error("Block '{}' returns {} as horizontal orientation", state, orientation);
                    }
                }
            }
            if (!orientations.isEmpty() && orientations.size() != states.size())
            {
                int nullStates = states.size() - orientations.size();
                reporter.warn("Block '{}' has inconsistent orientations, {} out of {} states return null", block, nullStates, states.size());
            }
        }

        reporter.endTest();
    }

    private HorizontalOrientationValidity() { }
}
