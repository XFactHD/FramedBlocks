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
    @interface TestTarget
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
}
