package xfacthd.framedblocks.codegen.impl.skippreds;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import xfacthd.framedblocks.codegen.impl.skippreds.SkipPredicateGeneratorData.*;

public final class SkipPredicateGeneratorImpl
{
    private static final String TARGET_PATH = "src/main/java/xfacthd/framedblocks/common/data/skippreds/";
    private static final String FILE_NAME_TEMPLATE = "%sSkipPredicate.java";
    private static final String COUNTERPARTS_TEMPLATE = "%sCounterparts.java";
    private static final Map<String, String> STATE_PROP_SOURCES = Map.of(
            "BlockStateProperties", "net.minecraft.world.level.block.state.properties.BlockStateProperties",
            "FramedProperties", "xfacthd.framedblocks.api.block.FramedProperties",
            "PropertyHolder", "xfacthd.framedblocks.common.data.PropertyHolder"
    );
    private static final List<String> STANDARD_IMPORTS = List.of(
            "net.minecraft.core.BlockPos",
            "net.minecraft.core.Direction",
            "net.minecraft.world.level.BlockGetter",
            "net.minecraft.world.level.block.state.BlockState",
            "xfacthd.framedblocks.api.block.IFramedBlock",
            "xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate",
            "xfacthd.framedblocks.common.data.BlockType",
            "xfacthd.framedblocks.common.data.skippreds.CullTest"
    );
    private static final String VANILLA_PROP_TYPES_PKG = "net.minecraft.world.level.block.state.properties.";
    private static final String CUSTOM_PROP_TYPES_PKG = "xfacthd.framedblocks.common.data.property.";
    private static final String CLASS_TEMPLATE = """
            package xfacthd.framedblocks.common.data.skippreds.%s;
            
            %s
            
            @CullTest(BlockType.%s)
            public final class %sSkipPredicate implements SideSkipPredicate
            {
                @Override
                public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
                {
                    if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
                    {
            %s
            
                        return switch (blockType)
                        {
            %s
                            default -> false;
                        };
                    }
                    return false;
                }
            
            %s
            }
            """;
    private static final String COUNTERPARTS_CLASS_TEMPLATE = """
            package xfacthd.framedblocks.common.data.skippreds.%s;
            
            %s
            
            public final class %sCounterparts
            {
            %s
            }
            """;
    private static final String PROP_LOOKUP_TEMPLATE = "            %s %s = state.getValue(%s.%s);";
    private static final String TEST_CASE_TEMPLATE = """
                            case %s -> testAgainst%s(
                                    %s, adjState, side
                            );
            """;
    private static final String TEST_MTH_TEMPLATE = """
                @CullTest.TestTarget(BlockType.%s)
                private static boolean testAgainst%s(
                        %s, BlockState adjState, Direction side
                )
                {
            %s
                    return false;
                }
            """;
    private static final String COUNTERPART_TEST_MTH_TEMPLATE = """
                private static class %sSkipPredicate
                {
                    /*
            %s
                    */
                    @CullTest.TestTarget(BlockType.%s)
                    private static boolean testAgainst%s(
                            %s, BlockState adjState, Direction side
                    )
                    {
            %s
                        return false;
                    }
                }
            """;
    private static final String TEST_MTH_PROP_LOOKUP_TEMPLATE = "        %s adj%s = adjState.getValue(%s.%s);";
    private static final String COUNTERPART_TEST_MTH_PROP_LOOKUP_TEMPLATE = "            %s adj%s = adjState.getValue(%s.%s);";
    private static final String TEST_MTH_PROP_LOOKUP_PLACEHOLDER = "        // Prop lookup placeholder\n";

    public static void generateAndExportClass(String subPackage, String sourceTypeName, List<String> targetTypeNames)
    {
        Type sourceType = resolveType(sourceTypeName);
        List<Type> targetTypes = targetTypeNames.stream().map(SkipPredicateGeneratorImpl::resolveType).toList();

        String shortName = getShortTypeName(sourceType);
        String predicateClazz = generateClass(subPackage, sourceType, targetTypes);
        String counterpartsClazz = generateCounterpartsClass(subPackage, sourceType, targetTypes);

        Path dirPath = Path.of("./").resolve(TARGET_PATH).resolve(subPackage);
        try
        {
            Files.createDirectories(dirPath);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Failed to create directories", e);
        }
        exportClass(dirPath, FILE_NAME_TEMPLATE.formatted(shortName), predicateClazz, "Predicate");
        exportClass(dirPath, COUNTERPARTS_TEMPLATE.formatted(shortName), counterpartsClazz, "Counterparts");
    }

    private static Type resolveType(String typeName)
    {
        Type type = SkipPredicateGeneratorData.KNOWN_TYPES.get(typeName);
        if (type == null)
        {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return type;
    }

    private static void exportClass(Path dirPath, String fileName, String clazz, String type)
    {
        Path clazzPath = dirPath.resolve(fileName);
        System.out.println(clazzPath.toAbsolutePath().normalize());
        try
        {
            Files.writeString(clazzPath, clazz, StandardOpenOption.CREATE_NEW);
        }
        catch (IOException e)
        {
            System.err.println(type + " class not exported: " + e.getMessage());
        }
    }

    private static String generateClass(String subPackage, Type sourceType, List<Type> targetTypes)
    {
        Set<String> imports = new HashSet<>(STANDARD_IMPORTS);

        String propertyList = buildPropertyLookupList(sourceType, imports);
        String testCaseList = buildTestCaseList(sourceType, targetTypes);
        String testMthList = buildTestMethodList(sourceType, targetTypes, imports);
        String importList = imports.stream().sorted().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));

        return CLASS_TEMPLATE.formatted(
                subPackage,
                importList,
                sourceType.type(),
                getShortTypeName(sourceType),
                propertyList,
                testCaseList,
                testMthList
        );
    }

    private static String generateCounterpartsClass(String subPackage, Type sourceType, List<Type> targetTypes)
    {
        Set<String> imports = new HashSet<>(STANDARD_IMPORTS);

        String propLookupList = buildTestPropertyLookupList(COUNTERPART_TEST_MTH_PROP_LOOKUP_TEMPLATE, sourceType.properties(), imports);
        String testMthList = buildCounterpartTestMethodList(sourceType, targetTypes, propLookupList, imports);
        String importList = imports.stream().sorted().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));

        return COUNTERPARTS_CLASS_TEMPLATE.formatted(
                subPackage,
                importList,
                getShortTypeName(sourceType),
                testMthList
        );
    }

    private static String buildPropertyLookupList(Type sourceType, Set<String> imports)
    {
        StringBuilder builder = new StringBuilder();
        for (Property prop : sourceType.properties())
        {
            collectPropertyImports(prop, imports);

            builder.append(PROP_LOOKUP_TEMPLATE.formatted(
                    prop.typeName(),
                    prop.name(),
                    prop.propHolder(),
                    prop.propName()
            )).append("\n");
        }
        return builder.toString().stripTrailing();
    }

    private static String buildTestCaseList(Type sourceType, List<Type> targetTypes)
    {
        String propArgsList = sourceType.properties().stream().map(Property::name).collect(Collectors.joining(", "));

        StringBuilder builder = new StringBuilder();
        builder.append(buildTestCase(sourceType, propArgsList));
        for (Type type : targetTypes)
        {
            builder.append(buildTestCase(type, propArgsList));
        }
        return builder.toString().stripTrailing();
    }

    private static String buildTestCase(Type type, String propArgsList)
    {
        return TEST_CASE_TEMPLATE.formatted(type.type(), getShortTypeName(type), propArgsList);
    }

    private static String buildTestMethodList(Type sourceType, List<Type> targetTypes, Set<String> imports)
    {
        String propParamsList = sourceType.properties().stream().map(prop -> prop.typeName() + " " + prop.name()).collect(Collectors.joining(", "));

        StringBuilder builder = new StringBuilder();

        String propLookupList = buildTestPropertyLookupList(TEST_MTH_PROP_LOOKUP_TEMPLATE, sourceType.properties(), imports);
        builder.append(buildTestMethod(sourceType, propParamsList, propLookupList));

        for (Type type : targetTypes)
        {
            String body = TEST_MTH_PROP_LOOKUP_PLACEHOLDER;
            if (!type.properties().isEmpty())
            {
                body = buildTestPropertyLookupList(TEST_MTH_PROP_LOOKUP_TEMPLATE, type.properties(), imports);
            }

            builder.append("\n");
            builder.append(buildTestMethod(type, propParamsList, body));
        }
        return builder.toString().stripTrailing();
    }

    private static String buildCounterpartTestMethodList(Type sourceType, List<Type> targetTypes, String propLookupList, Set<String> imports)
    {
        StringBuilder builder = new StringBuilder();
        for (Type type : targetTypes)
        {
            String propParamsList = type.properties()
                    .stream()
                    .peek(prop -> collectPropertyImports(prop, imports))
                    .map(prop -> prop.typeName() + " " + prop.name())
                    .collect(Collectors.joining(", "));
            String propArgsList = type.properties()
                    .stream()
                    .map(Property::name)
                    .collect(Collectors.joining(", "));

            builder.append(COUNTERPART_TEST_MTH_TEMPLATE.formatted(
                    getShortTypeName(type),
                    buildTestCase(sourceType, propArgsList).stripTrailing(),
                    sourceType.type(),
                    getShortTypeName(sourceType),
                    propParamsList,
                    propLookupList
            )).append("\n");
        }
        return builder.toString().stripTrailing();
    }

    private static String buildTestPropertyLookupList(String template, List<Property> properties, Set<String> imports)
    {
        StringBuilder builder = new StringBuilder();
        for (Property prop : properties)
        {
            collectPropertyImports(prop, imports);

            builder.append(template.formatted(
                    prop.typeName(),
                    capitalize(prop.name(), false),
                    prop.propHolder(),
                    prop.propName()
            )).append("\n");
        }
        String list = builder.toString();
        if (properties.size() == 1)
        {
            list = list.stripTrailing();
        }
        return list;
    }

    private static String buildTestMethod(Type type, String propParamsList, String body)
    {
        return TEST_MTH_TEMPLATE.formatted(type.type(), getShortTypeName(type), propParamsList, body);
    }

    private static void collectPropertyImports(Property property, Set<String> imports)
    {
        String holderImport = STATE_PROP_SOURCES.get(property.propHolder());
        if (holderImport == null)
        {
            throw new IllegalArgumentException("Invalid prop holder: " + property.propHolder());
        }
        imports.add(holderImport);

        switch (property.type())
        {
            case VANILLA -> imports.add(VANILLA_PROP_TYPES_PKG + property.typeName());
            case CUSTOM -> imports.add(CUSTOM_PROP_TYPES_PKG + property.typeName());
        }
    }

    private static String getShortTypeName(Type type)
    {
        StringBuilder builder = new StringBuilder();
        for (String part : type.type().replace("FRAMED_", "").split("_"))
        {
            if (part.equals("EXT"))
            {
                builder.append("Extended");
                continue;
            }
            if (part.equals("ELEV"))
            {
                builder.append("Elevated");
                continue;
            }

            builder.append(capitalize(part, true));
        }
        return builder.toString();
    }

    private static String capitalize(String text, boolean lowerPartTwo)
    {
        String partOne = text.substring(0, 1).toUpperCase(Locale.ROOT);
        String partTwo = text.substring(1);
        if (lowerPartTwo)
        {
            partTwo = partTwo.toLowerCase(Locale.ROOT);
        }
        return partOne + partTwo;
    }



    private SkipPredicateGeneratorImpl() { }
}
