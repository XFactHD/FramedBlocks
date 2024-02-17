package xfacthd.framedblocks.selftest.tests;

import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class SkipPredicatePresenceConsistency
{
    private static final Map<BlockType, Test> TESTS = new EnumMap<>(BlockType.class);

    public static void checkSkipPredicateConsistency(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking skip predicate consistency");

        Map<BlockType, Set<BlockType>> doubleBlockPartTypes = new EnumMap<>(BlockType.class);
        blocks.stream().filter(b -> ((IFramedBlock) b).getBlockType().isDoubleBlock()).forEach(block ->
        {
            BlockType type = (BlockType) ((IFramedBlock) block).getBlockType();
            Set<BlockType> partTypes = EnumSet.noneOf(BlockType.class);
            block.getStateDefinition().getPossibleStates().forEach(state ->
            {
                Tuple<BlockState, BlockState> pair = AbstractFramedDoubleBlock.getStatePair(state);
                partTypes.add((BlockType) ((IFramedBlock) pair.getA().getBlock()).getBlockType());
                partTypes.add((BlockType) ((IFramedBlock) pair.getB().getBlock()).getBlockType());
            });
            doubleBlockPartTypes.put(type, partTypes);
        });

        SideSkipPredicates.PREDICATES.forEach((type, pred) ->
        {
            Class<?> clazz = pred.getClass();
            CullTest cullTest = clazz.getAnnotation(CullTest.class);
            if (cullTest == null)
            {
                return;
            }

            Method[] methods = clazz.getDeclaredMethods();
            Test test = new Test(clazz.getSimpleName(), EnumSet.noneOf(BlockType.class), EnumSet.noneOf(BlockType.class), new HashSet<>());
            Set<BlockType> doubleTypes = EnumSet.noneOf(BlockType.class);
            for (Method mth : methods)
            {
                CullTest.SingleTarget singleTarget = mth.getAnnotation(CullTest.SingleTarget.class);
                CullTest.DoubleTarget doubleTarget = mth.getAnnotation(CullTest.DoubleTarget.class);
                CullTest.DoubleTargets doubleTargets = mth.getAnnotation(CullTest.DoubleTargets.class);

                if (singleTarget != null)
                {
                    collectSingleTarget(test, mth, singleTarget);
                }

                if (doubleTarget != null)
                {
                    collectDoubleTarget(doubleBlockPartTypes, type, test, doubleTypes, mth, doubleTarget);
                }

                if (doubleTargets != null)
                {
                    if (doubleTargets.value().length <= 1)
                    {
                        FramedBlocks.LOGGER.error(
                                "    DoubleTargets container annotation on method '{}' in class '{}' should be replaced with DoubleTarget",
                                mth.getName(), test.clazzName
                        );
                    }
                    for (CullTest.DoubleTarget target : doubleTargets.value())
                    {
                        collectDoubleTarget(doubleBlockPartTypes, type, test, doubleTypes, mth, target);
                    }
                }

                if (Modifier.isStatic(mth.getModifiers()) && mth.getReturnType() == Boolean.TYPE && mth.getName().contains("test"))
                {
                    if (singleTarget == null && doubleTarget == null && doubleTargets == null)
                    {
                        FramedBlocks.LOGGER.error(
                                "    Method '{}' in class '{}' is missing test target annotation",
                                mth.getName(), test.clazzName
                        );
                    }
                }
            }
            for (BlockType testType : cullTest.value())
            {
                TESTS.put(testType, test);
            }
        });

        TESTS.forEach((type, test) ->
        {
            if (!test.targets.contains(type))
            {
                FramedBlocks.LOGGER.warn("    Type '{}' is missing a test against itself", type);
            }
            test.targets.forEach(target ->
            {
                if (test.oneWayTargets.contains(target))
                {
                    return;
                }

                Test reverse = TESTS.get(target);
                if (reverse != null && !reverse.targets.contains(type))
                {
                    FramedBlocks.LOGGER.warn(
                            "    Type '{}' has a test against type '{}' in class '{}' but class '{}' is missing the reverse test",
                            type, target, test.clazzName, reverse.clazzName
                    );
                }
            });
            test.doubleTargets.forEach(target ->
            {
                for (BlockType part : target.testedParts)
                {
                    Test reverse = TESTS.get(part);
                    if (reverse != null && !reverse.targets.contains(type))
                    {
                        FramedBlocks.LOGGER.warn(
                                "    Type '{}' has a test against type '{}' as part of the double type " +
                                        "'{}' in class '{}' but class '{}' is missing the reverse test against the part",
                                type, part, target.target, test.clazzName, reverse.clazzName
                        );
                    }
                }

                for (BlockType part : doubleBlockPartTypes.get(target.target))
                {
                    Test reverse = TESTS.get(part);
                    if (reverse != null && reverse.targets.contains(type) && !target.testedParts.contains(part) && !target.ignoredParts.contains(part))
                    {
                        FramedBlocks.LOGGER.warn(
                                "    Type '{}' (part of double type '{}') has a test against type '{}' in class " +
                                        "'{}' but class '{}' is missing the reverse test in the test against the double type",
                                part, target.target, type, reverse.clazzName, test.clazzName
                        );
                    }
                }
            });
        });
    }

    private static void collectSingleTarget(Test test, Method mth, CullTest.SingleTarget singleTarget)
    {
        for (BlockType targetType : singleTarget.value())
        {
            if (targetType.isDoubleBlock())
            {
                String msg = "SingleTarget must not handle double blocks, method '%s' in class '%s' specifies type '%s'";
                FramedBlocks.LOGGER.error("    " + msg.formatted(mth.getName(), test.clazzName, targetType));
                continue;
            }

            if (!test.targets.add(targetType))
            {
                FramedBlocks.LOGGER.error(
                        "    Class '{}' has duplicate test against type '{}'", test.clazzName, targetType
                );
            }
            if (singleTarget.oneWay())
            {
                test.oneWayTargets.add(targetType);
            }
        }
    }

    private static void collectDoubleTarget(
            Map<BlockType, Set<BlockType>> doubleBlockPartTypes,
            BlockType type,
            Test test,
            Set<BlockType> doubleTypes,
            Method mth,
            CullTest.DoubleTarget doubleTarget
    )
    {
        BlockType targetType = doubleTarget.value();
        if (!targetType.isDoubleBlock())
        {
            FramedBlocks.LOGGER.error(
                    "    DoubleTarget must not handle single blocks, method '{}' in class '{}' specifies type '{}'",
                    mth.getName(), test.clazzName, targetType
            );
            return;
        }

        if (!doubleTypes.add(targetType))
        {
            FramedBlocks.LOGGER.error(
                    "    Class '{}' has duplicate test against double type '{}'", test.clazzName, targetType
            );
        }

        BlockType[] partTargets = doubleTarget.partTargets();
        if (partTargets.length < 1)
        {
            FramedBlocks.LOGGER.error(
                    "    DoubleTarget must specify at least one target type, method '{}' in class '{}' specifies none",
                    mth.getName(), test.clazzName
            );
            return;
        }
        Set<BlockType> typeParts = doubleBlockPartTypes.get(targetType);
        boolean error = false;
        for (BlockType part : partTargets)
        {
            if (part.isDoubleBlock())
            {
                FramedBlocks.LOGGER.error(
                        "    DoubleTarget target parts must be single blocks, method '{}' in class '{}' specifies type '{}' as a part",
                        mth.getName(), test.clazzName, part
                );
                error = true;
            }
            else if (!typeParts.contains(part))
            {
                FramedBlocks.LOGGER.error(
                        "    Type '{}' specifies test against type '{}' as part of test against double " +
                                "type '{}' in class '{}' which does not have such a part",
                        type, part, targetType, test.clazzName
                );
                error = true;
            }
        }
        BlockType[] ignoredParts = doubleTarget.ignoredParts();
        for (BlockType ignoredPart : ignoredParts)
        {
            if (ignoredPart.isDoubleBlock())
            {
                FramedBlocks.LOGGER.error(
                        "    DoubleTarget ignored parts must be single blocks, method '{}' in class '{}' specifies type '{}' as an ignored part",
                        mth.getName(), test.clazzName, ignoredPart
                );
                error = true;
            }
            if (!typeParts.contains(ignoredPart))
            {
                FramedBlocks.LOGGER.error(
                        "    Type '{}' specifies type '{}' as ignored on test against double " +
                                "type '{}' in class '{}' which does not have such a part",
                        type, ignoredPart, targetType, test.clazzName
                );
                error = true;
            }
        }

        if (!error)
        {
            test.doubleTargets.add(new DoubleTarget(targetType, Set.of(partTargets), Set.of(ignoredParts)));
        }
    }

    public static Test getTestOf(BlockType type)
    {
        return TESTS.get(type);
    }



    public record DoubleTarget(BlockType target, Set<BlockType> testedParts, Set<BlockType> ignoredParts) { }

    public record Test(String clazzName, Set<BlockType> targets, Set<BlockType> oneWayTargets, Set<DoubleTarget> doubleTargets) { }



    private SkipPredicatePresenceConsistency() { }
}
