package io.github.xfacthd.framedblocks.common.data.skippreds;

import io.github.xfacthd.framedblocks.common.data.BlockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CullTest {
    /// The type(s) using this test.
    BlockType[] value();

    boolean noSelfTest() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface TestTarget {
        /// The type(s) tested against.
        BlockType[] value();

        /// Indicate that this test intentionally does not have a reverse test.
        boolean oneWay() default false;
    }
}
