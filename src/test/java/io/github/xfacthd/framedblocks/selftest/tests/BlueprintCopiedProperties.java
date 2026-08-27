package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BlueprintCopiedProperties {
    public static void checkHandlesDefaultProperties(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("blueprint default copied properties");

        for (Block block : blocks) {
            PropertyCollector collector = new PropertyCollector(block);
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                collector.setState(state);

                if (block instanceof SlopeToggleBlock && state.getValue(FramedProperties.ALT_SLOPE)) {
                    collector.check(FramedProperties.ALT_SLOPE);
                }
                if (block instanceof ShapeLockableBlock lockable && lockable.isLocked(state)) {
                    collector.check(FramedProperties.STATE_LOCKED);
                    lockable.getPropertiesToCopy().forEach(collector::check);
                }
                if (block instanceof CopycatStyleBlock.StateDependent copycat && copycat.isCopycatStyle(state)) {
                    collector.check(FramedProperties.COPYCAT_STYLE);
                }
            }
            collector.finish(reporter);
        }

        reporter.endTest();
    }

    private static final class PropertyCollector {
        private final Block block;
        private final BlueprintCopyBehaviour behaviour;
        private final Set<Property<?>> missing = new HashSet<>();
        private final Set<Property<?>> toCopy = new HashSet<>();

        PropertyCollector(Block block) {
            this.block = block;
            this.behaviour = FramedBlueprintItem.getBehaviour(block);
        }

        void setState(BlockState state) {
            toCopy.clear();
            toCopy.addAll(behaviour.getPropertiesToCopy(state));
        }

        void check(Property<?> property) {
            if (!toCopy.contains(property)) {
                missing.add(property);
            }
        }

        void finish(SelfTestReporter reporter) {
            if (!missing.isEmpty()) {
                reporter.warn("Block %s doesn't copy default properties: %s", block, missing);
            }
        }
    }

    private BlueprintCopiedProperties() { }
}
