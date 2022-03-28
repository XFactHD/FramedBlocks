package xfacthd.framedblocks.util;

import xfacthd.framedblocks.common.data.BlockType;

import java.lang.annotation.*;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestedType
{
    BlockType type();
}
