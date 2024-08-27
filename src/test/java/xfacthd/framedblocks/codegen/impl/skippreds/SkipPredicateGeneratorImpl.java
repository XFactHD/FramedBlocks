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
    private static final String SKIP_PREDS_ROOT_PKG = "xfacthd.framedblocks.common.data.skippreds.";
    private static final String CLASS_TEMPLATE = """
            package %s.%s;
            
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
            
            
            
            %s
            }
            """;
    private static final String COUNTERPARTS_CLASS_TEMPLATE = """
            package %s.%s;
            
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
            %s
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
            %s
                    }
                }
            """;
    private static final String TEST_MTH_PROP_LOOKUP_TEMPLATE = "        %s adj%s = adjState.getValue(%s.%s);";
    private static final String COUNTERPART_TEST_MTH_PROP_LOOKUP_TEMPLATE = "            %s adj%s = adjState.getValue(%s.%s);";
    private static final String TEST_MTH_PROP_LOOKUP_PLACEHOLDER = "        // Prop lookup placeholder\n";
    private static final String DIR_COMPUTE_MTH_TEMPLATE = """
                public static %s get%sDir(%s, Direction side)
                {
                    // TODO: implement
                    return %s.NULL;
                }
            """;
    private static final String DIR_TEST_TEMPLATE_FIRST = "return get%sDir(%s).isEqualTo(%sget%sDir(%s))";
    private static final String DIR_TEST_TEMPLATE_OTHER = "       get%sDir(%s).isEqualTo(%sget%sDir(%s))";

    public static void generateAndExportClass(String sourceTypeName, List<String> targetTypeNames)
    {
        Type sourceType = resolveType(sourceTypeName);
        List<Type> targetTypes = targetTypeNames.stream().map(SkipPredicateGeneratorImpl::resolveType).toList();

        String shortName = getShortTypeName(sourceType);
        String predicateClazz = generateClass(sourceType, targetTypes);
        String counterpartsClazz = generateCounterpartsClass(sourceType, targetTypes);

        Path dirPath = Path.of("./").resolve(TARGET_PATH).resolve(sourceType.subPackage());
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
            System.err.println(type + " class not exported: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
    }

    private static String generateClass(Type sourceType, List<Type> targetTypes)
    {
        Set<String> imports = new HashSet<>(STANDARD_IMPORTS);
        Map<Type, Set<Property>> propsByTestTarget = new HashMap<>();

        String propertyList = buildPropertyLookupList(sourceType, imports);
        String testMthList = buildTestMethodList(sourceType, targetTypes, imports, propsByTestTarget);
        String testCaseList = buildTestCaseList(sourceType, targetTypes, propsByTestTarget);
        String dirComputeMthList = buildDirComputeMethodList(sourceType, imports);
        String importList = imports.stream().sorted().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));

        return CLASS_TEMPLATE.formatted(
                SKIP_PREDS_ROOT_PKG.substring(0, SKIP_PREDS_ROOT_PKG.length() - 1),
                sourceType.subPackage(),
                importList,
                sourceType.type(),
                getShortTypeName(sourceType),
                propertyList,
                testCaseList,
                testMthList,
                dirComputeMthList
        );
    }

    private static String generateCounterpartsClass(Type sourceType, List<Type> targetTypes)
    {
        Set<String> imports = new HashSet<>(STANDARD_IMPORTS);

        String testMthList = buildCounterpartTestMethodList(sourceType, targetTypes, imports);
        String importList = imports.stream().sorted().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));

        return COUNTERPARTS_CLASS_TEMPLATE.formatted(
                SKIP_PREDS_ROOT_PKG.substring(0, SKIP_PREDS_ROOT_PKG.length() - 1),
                sourceType.subPackage(),
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

    private static String buildTestCaseList(Type sourceType, List<Type> targetTypes, Map<Type, Set<Property>> propsByTestTarget)
    {
        StringBuilder builder = new StringBuilder();
        String selfPropArgsList = buildTestCaseArgList(sourceType, propsByTestTarget.get(sourceType));
        builder.append(buildTestCase(sourceType, selfPropArgsList));
        for (Type type : targetTypes)
        {
            String propArgsList = buildTestCaseArgList(sourceType, propsByTestTarget.get(type));
            builder.append(buildTestCase(type, propArgsList));
        }
        return builder.toString().stripTrailing();
    }

    private static String buildTestCaseArgList(Type type, Set<Property> usedProps)
    {
        return type.properties()
                .stream()
                .filter(usedProps::contains)
                .map(Property::name)
                .collect(Collectors.joining(", "));
    }

    private static String buildTestCase(Type type, String propArgsList)
    {
        return TEST_CASE_TEMPLATE.formatted(type.type(), getShortTypeName(type), propArgsList);
    }

    private static String buildTestMethodList(Type sourceType, List<Type> targetTypes, Set<String> imports, Map<Type, Set<Property>> propsByTestTarget)
    {
        StringBuilder builder = new StringBuilder();

        Set<Property> selfUsedProps = new HashSet<>();
        String selfTestExec = buildTestExecution(sourceType, sourceType, selfUsedProps, new HashSet<>(), 2);
        String selfPropParamsList = sourceType.properties()
                .stream()
                .filter(selfUsedProps::contains)
                .map(prop -> prop.typeName() + " " + prop.name())
                .collect(Collectors.joining(", "));
        List<Property> selfProperties = sourceType.properties().stream().filter(selfUsedProps::contains).toList();
        String selfPropLookupList = buildTestPropertyLookupList(TEST_MTH_PROP_LOOKUP_TEMPLATE, selfProperties, imports);
        builder.append(buildTestMethod(sourceType, selfPropParamsList, selfPropLookupList, selfTestExec));
        propsByTestTarget.put(sourceType, selfUsedProps);

        for (Type type : targetTypes)
        {
            Set<Property> srcUsedProps = new HashSet<>();
            Set<Property> targetUsedProps = new HashSet<>();
            String testExec = buildTestExecution(sourceType, type, srcUsedProps, targetUsedProps, 2);

            String propParamsList = sourceType.properties()
                    .stream()
                    .filter(srcUsedProps::contains)
                    .map(prop -> prop.typeName() + " " + prop.name())
                    .collect(Collectors.joining(", "));

            String propLookupList = TEST_MTH_PROP_LOOKUP_PLACEHOLDER;
            if (!type.properties().isEmpty())
            {
                List<Property> properties = type.properties().stream().filter(targetUsedProps::contains).toList();
                propLookupList = buildTestPropertyLookupList(TEST_MTH_PROP_LOOKUP_TEMPLATE, properties, imports);
            }

            builder.append("\n");
            builder.append(buildTestMethod(type, propParamsList, propLookupList, testExec));

            propsByTestTarget.put(type, srcUsedProps);
            if (!type.subPackage().equals(sourceType.subPackage()))
            {
                imports.add(SKIP_PREDS_ROOT_PKG + type.subPackage() + "." + getShortTypeName(type) + "SkipPredicate");
            }
        }
        return builder.toString().stripTrailing();
    }

    private static String buildTestExecution(Type sourceType, Type type, Set<Property> srcUsedProps, Set<Property> targetUsedProps, int indentLevel)
    {
        record DirPair(TestDir first, TestDir second) { }

        List<DirPair> commonDirs = new ArrayList<>();
        if (sourceType == type)
        {
            for (TestDir dir : sourceType.testDirs())
            {
                commonDirs.add(new DirPair(dir, dir));
                List<Property> usedProps = dir.props().stream().map(sourceType.propertyMap()::get).toList();
                srcUsedProps.addAll(usedProps);
                targetUsedProps.addAll(usedProps);
            }
        }
        else if (!sourceType.testDirs().isEmpty() && !type.testDirs().isEmpty())
        {
            for (TestDir dir : sourceType.testDirs())
            {
                for (TestDir otherDir : type.testDirs())
                {
                    for (String id : dir.identifiers())
                    {
                        if (otherDir.identifiers().contains(id))
                        {
                            commonDirs.add(new DirPair(dir, otherDir));
                            srcUsedProps.addAll(dir.props().stream().map(sourceType.propertyMap()::get).toList());
                            targetUsedProps.addAll(otherDir.props().stream().map(type.propertyMap()::get).toList());
                            break;
                        }
                    }
                }
            }
        }

        String indent = "    ".repeat(indentLevel);
        if (commonDirs.isEmpty())
        {
            srcUsedProps.addAll(sourceType.properties());
            targetUsedProps.addAll(type.properties());
            return indent + "// TODO: implement\n" + indent + "return false;";
        }

        String secondTarget = sourceType == type ? "" : (getShortTypeName(type) + "SkipPredicate.");

        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (DirPair pair : commonDirs)
        {
            if (!first) builder.append(" ||\n");

            String template = first ? DIR_TEST_TEMPLATE_FIRST : DIR_TEST_TEMPLATE_OTHER;
            builder.append(indent).append(template.formatted(
                    pair.first.name(),
                    buildTestExecParams(sourceType, pair.first, false),
                    secondTarget,
                    pair.second.name(),
                    buildTestExecParams(type, pair.second, true)
            ));

            first = false;
        }

        return builder.append(";").toString();
    }

    private static String buildTestExecParams(Type type, TestDir dir, boolean opposite)
    {
        String params = dir.props()
                .stream()
                .map(prop -> type.propertyMap().get(prop))
                .map(Property::name)
                .map(prop -> opposite ? ("adj" + capitalize(prop, false)) : prop)
                .collect(Collectors.joining(", ", "", ", "));
        params += opposite ? "side.getOpposite()" : "side";
        return params;
    }

    private static String buildDirComputeMethodList(Type sourceType, Set<String> imports)
    {
        StringBuilder builder = new StringBuilder();

        for (TestDir dir : sourceType.testDirs())
        {
            imports.add(SKIP_PREDS_ROOT_PKG + dir.type());

            String params = dir.props()
                    .stream()
                    .map(prop -> sourceType.propertyMap().get(prop))
                    .map(prop -> prop.typeName() + " " + prop.name())
                    .collect(Collectors.joining(", "));

            builder.append(DIR_COMPUTE_MTH_TEMPLATE.formatted(
                    dir.type(),
                    dir.name(),
                    params,
                    dir.type()
            ));
            builder.append("\n");
        }

        return builder.toString().stripTrailing();
    }

    private static String buildCounterpartTestMethodList(Type sourceType, List<Type> targetTypes, Set<String> imports)
    {
        StringBuilder builder = new StringBuilder();
        for (Type type : targetTypes)
        {
            Set<Property> usedProps = new HashSet<>();
            Set<Property> srcUsedProps = new HashSet<>();
            String testExec = buildTestExecution(type, sourceType, usedProps, srcUsedProps, 3);

            String propParamsList = type.properties()
                    .stream()
                    .filter(usedProps::contains)
                    .peek(prop -> collectPropertyImports(prop, imports))
                    .map(prop -> prop.typeName() + " " + prop.name())
                    .collect(Collectors.joining(", "));
            String propArgsList = type.properties()
                    .stream()
                    .filter(usedProps::contains)
                    .map(Property::name)
                    .collect(Collectors.joining(", "));

            List<Property> srcProperties = sourceType.properties().stream().filter(srcUsedProps::contains).toList();
            String propLookupList = buildTestPropertyLookupList(COUNTERPART_TEST_MTH_PROP_LOOKUP_TEMPLATE, srcProperties, imports);

            builder.append(COUNTERPART_TEST_MTH_TEMPLATE.formatted(
                    getShortTypeName(type),
                    buildTestCase(sourceType, propArgsList).stripTrailing(),
                    sourceType.type(),
                    getShortTypeName(sourceType),
                    propParamsList,
                    propLookupList,
                    testExec
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

    private static String buildTestMethod(Type type, String propParamsList, String propLookup, String testExec)
    {
        return TEST_MTH_TEMPLATE.formatted(type.type(), getShortTypeName(type), propParamsList, propLookup, testExec);
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
            builder.append(switch (part)
            {
                case "EXT" -> "Extended";
                case "ELEV" -> "Elevated";
                case "W" -> "Wall";
                default -> capitalize(part, true);
            });
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
