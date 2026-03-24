package io.github.xfacthd.framedblocks.codegen;

import io.github.xfacthd.framedblocks.codegen.impl.skippreds.SkipPredicateGeneratorImpl;

public final class SkipPredicateGenerator
{
    static void main()
    {
        // The type for which the predicate should be generated (must be defined in SkipPredicateGeneratorData.KNOWN_TYPES) or null to regenerate all
        String sourceType = "FRAMED_TYPE";

        SkipPredicateGeneratorImpl.generateAndExportClasses(sourceType);
    }

    private SkipPredicateGenerator() { }
}
