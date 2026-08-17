package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.level.block.state.BlockState;

final class CompoundStateMerger extends StateMerger {
    private final StateMerger mergerOne;
    private final StateMerger mergerTwo;

    private CompoundStateMerger(StateMerger mergerOne, StateMerger mergerTwo) {
        super(Utils.concat(mergerOne.handledProperties, mergerTwo.handledProperties));
        this.mergerOne = mergerOne;
        this.mergerTwo = mergerTwo;
    }

    @Override
    public BlockState apply(BlockState state) {
        return mergerTwo.apply(mergerOne.apply(state));
    }

    static StateMerger of(StateMerger mergerOne, StateMerger mergerTwo) {
        if (mergerOne instanceof IgnoreAllStateMerger) {
            return mergerOne;
        }
        if (mergerTwo instanceof IgnoreAllStateMerger) {
            return mergerTwo;
        }
        if (mergerOne == StateMergers.PASSTHROUGH) {
            return mergerTwo;
        }
        if (mergerTwo == StateMergers.PASSTHROUGH) {
            return mergerOne;
        }
        if (mergerOne instanceof IgnoringStateMerger ignoreOne && mergerTwo instanceof IgnoringStateMerger ignoreTwo) {
            return StateMergers.ignoring(Utils.concat(ignoreOne.handledProperties, ignoreTwo.handledProperties));
        }
        return new CompoundStateMerger(mergerOne, mergerTwo);
    }
}
