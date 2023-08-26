package xfacthd.framedblocks.common.data.skippreds;

import xfacthd.framedblocks.common.data.BlockType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CullTest
{
    /**
     * The type(s) using this test
     */
    BlockType[] value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface SingleTarget
    {
        /**
         * The type(s) tested against
         */
        BlockType[] value();

        /**
         * Indicate that this test intentionally does not have a reverse test
         */
        boolean oneWay() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(DoubleTargets.class)
    @interface DoubleTarget
    {
        /**
         * The double block type(s) being tested against
         */
        BlockType value();

        /**
         * The part types of the double blocks being tested against
         */
        BlockType[] partTargets();

        /**
         * Part types of the double block being tested against which are intentionally ignored
         */
        BlockType[] ignoredParts() default { };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface DoubleTargets
    {
        DoubleTarget[] value();
    }
}
