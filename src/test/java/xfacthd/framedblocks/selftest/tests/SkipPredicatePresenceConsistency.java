package xfacthd.framedblocks.selftest.tests;

import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.SideSkipPredicates;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class SkipPredicatePresenceConsistency
{
    private static final Map<BlockType, Test> TESTS = new EnumMap<>(BlockType.class);

    public static void checkSkipPredicateConsistency(SelfTestReporter reporter)
    {
        reporter.startTest("skip predicate consistency");

        SideSkipPredicates.PREDICATES.forEach((type, pred) ->
        {
            Class<?> clazz = pred.getClass();
            CullTest cullTest = clazz.getAnnotation(CullTest.class);
            if (cullTest == null)
            {
                return;
            }

            Method[] methods = clazz.getDeclaredMethods();
            Test test = new Test(clazz.getSimpleName(), EnumSet.noneOf(BlockType.class), EnumSet.noneOf(BlockType.class));
            for (Method mth : methods)
            {
                CullTest.TestTarget target = mth.getAnnotation(CullTest.TestTarget.class);

                if (target != null)
                {
                    collectTarget(reporter, test, mth, target);
                }

                if (Modifier.isStatic(mth.getModifiers()) && mth.getReturnType() == Boolean.TYPE && mth.getName().contains("test"))
                {
                    if (target == null)
                    {
                        reporter.error(
                                "Method '{}' in class '{}' is missing test target annotation",
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
                reporter.warn("Type '{}' is missing a test against itself", type);
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
                    reporter.warn(
                            "Type '{}' has a test against type '{}' in class '{}' but class '{}' is missing the reverse test",
                            type, target, test.clazzName, reverse.clazzName
                    );
                }
            });
        });

        reporter.endTest();
    }

    private static void collectTarget(SelfTestReporter reporter, Test test, Method mth, CullTest.TestTarget target)
    {
        for (BlockType targetType : target.value())
        {
            if (targetType.isDoubleBlock())
            {
                reporter.error(
                        "SingleTarget must not handle double blocks, method '{}' in class '{}' specifies type '{}'",
                        mth.getName(), test.clazzName, targetType
                );
                continue;
            }

            if (!test.targets.add(targetType))
            {
                reporter.error("Class '{}' has duplicate test against type '{}'", test.clazzName, targetType);
            }
            if (target.oneWay())
            {
                test.oneWayTargets.add(targetType);
            }
        }
    }

    public static Test getTestOf(BlockType type)
    {
        return TESTS.get(type);
    }



    public record Test(String clazzName, Set<BlockType> targets, Set<BlockType> oneWayTargets) { }



    private SkipPredicatePresenceConsistency() { }
}
