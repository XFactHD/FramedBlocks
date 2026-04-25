package io.github.xfacthd.framedblocks.codegen.impl.skippreds;

import io.github.xfacthd.framedblocks.codegen.impl.skippreds.SkipPredicateGeneratorData.Property;
import io.github.xfacthd.framedblocks.codegen.impl.skippreds.SkipPredicateGeneratorData.SpecialPropLookup;
import io.github.xfacthd.framedblocks.codegen.impl.skippreds.SkipPredicateGeneratorData.TestDir;
import io.github.xfacthd.framedblocks.codegen.impl.skippreds.SkipPredicateGeneratorData.Type;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class SkipPredicateGeneratorImpl {
    private static final String TARGET_PATH = "src/main/java/io/github/xfacthd/framedblocks/common/data/skippreds/";
    private static final String FILE_NAME_TEMPLATE = "%sSkipPredicate.java";
    private static final Map<String, String> STATE_PROP_SOURCES = Map.of(
            "BlockStateProperties", "net.minecraft.world.level.block.state.properties.BlockStateProperties",
            "FramedProperties", "io.github.xfacthd.framedblocks.api.block.FramedProperties",
            "PropertyHolder", "io.github.xfacthd.framedblocks.common.data.PropertyHolder"
    );
    private static final List<String> STANDARD_IMPORTS = List.of(
            "net.minecraft.core.BlockPos",
            "net.minecraft.core.Direction",
            "net.minecraft.world.level.BlockGetter",
            "net.minecraft.world.level.block.state.BlockState",
            "io.github.xfacthd.framedblocks.api.block.IFramedBlock",
            "io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate",
            "io.github.xfacthd.framedblocks.common.data.BlockType",
            "io.github.xfacthd.framedblocks.common.data.skippreds.CullTest"
    );
    private static final String VANILLA_PROP_TYPES_PKG = "net.minecraft.world.level.block.state.properties.";
    private static final String CUSTOM_PROP_TYPES_PKG = "io.github.xfacthd.framedblocks.common.data.property.";
    private static final String SKIP_PREDS_ROOT_PKG = "io.github.xfacthd.framedblocks.common.data.skippreds.";
    private static final String CLASS_TEMPLATE = """
            package %s%s;
            
            %s
            
            /**
             This class is machine-generated, any manual changes to this class will be overwritten.
             */
            @CullTest(%s)
            public final class %sSkipPredicate implements SideSkipPredicate {%s
                @Override
                public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
            %s        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            %s            return switch (blockType) {
            %s
                            default -> false;
                        };
                    }
                    return false;
                }
            
            %s
            }
            """;
    private static final String INST_FIELD_TEMPLATE = """
            
                public static final %sSkipPredicate INSTANCE = new %sSkipPredicate();
            
                private %sSkipPredicate() { }
            """;
    private static final String PRE_FILTER_TEST_TEMPLATE = """
                    if (%s.testEarlyExit(%s, side)) {
                        return false;
                    }
            """;
    private static final String PRE_FILTER_SPECIAL_LOOKUP_VAR_TEMPLATE = "        %s %s = (%s) state.getBlock();";
    private static final String PRE_FILTER_PROP_LOOKUP_TEMPLATE = "        %s %s = state.getValue(%s.%s);";
    private static final String PRE_FILTER_SPECIAL_PROP_LOOKUP_TEMPLATE = "        %s %s = %s.%s(state);";
    private static final String SPECIAL_LOOKUP_VAR_TEMPLATE = "            %s %s = (%s) state.getBlock();";
    private static final String SPECIAL_LOOKUP_INLINE_TEMPLATE = "((%s) state.getBlock())";
    private static final String PROP_LOOKUP_TEMPLATE = "            %s %s = state.getValue(%s.%s);";
    private static final String SPECIAL_PROP_LOOKUP_TEMPLATE = "            %s %s = %s.%s(state);";
    private static final String TEST_CASE_TEMPLATE = """
                            case %s -> testAgainst%s(
                                    %sside
                            );
            """;
    private static final String TEST_MTH_TEMPLATE = """
                @CullTest.TestTarget(%s)
                private static boolean testAgainst%s(
                        %sDirection side
                ) {
            %s%s
                }
            """;
    private static final String TEST_MTH_SPECIAL_LOOKUP_VAR_TEMPLATE = "        %s %s = (%s) adjState.getBlock();";
    private static final String TEST_MTH_SPECIAL_LOOKUP_INLINE_TEMPLATE = "((%s) adjState.getBlock())";
    private static final String TEST_MTH_PROP_LOOKUP_TEMPLATE = "        %s adj%s = adjState.getValue(%s.%s);";
    private static final String TEST_MTH_SPECIAL_PROP_LOOKUP_TEMPLATE = "        %s adj%s = %s.%s(adjState);";
    private static final String DIR_TEST_TEMPLATE_FIRST = "return %s.get%sDir(%s).isEqualTo(%s.get%sDir(%s))";
    private static final String DIR_TEST_TEMPLATE_OTHER = "       %s.get%sDir(%s).isEqualTo(%s.get%sDir(%s))";
    private static final String BOOLEAN_DIR_TEST_TEMPLATE_FIRST = "return (%s.is%sDir(%s) && %s.is%sDir(%s))";
    private static final String BOOLEAN_DIR_TEST_TEMPLATE_OTHER = "       (%s.is%sDir(%s) && %s.is%sDir(%s))";
    private static final String IDENTITY_DIR_TEST_TEMPLATE_FIRST = "return %s.get%sDir(%s) == %s.get%sDir(%s)";
    private static final String IDENTITY_DIR_TEST_TEMPLATE_OTHER = "       %s.get%sDir(%s) == %s.get%sDir(%s)";
    private static final String EQUALS_DIR_TEST_TEMPLATE_FIRST = "return %s.get%sDir(%s).equals(%s.get%sDir(%s))";
    private static final String EQUALS_DIR_TEST_TEMPLATE_OTHER = "       %s.get%sDir(%s).equals(%s.get%sDir(%s))";
    private static final String SPECIAL_DIR_TEST_TEMPLATE_FIRST = "return %s.test%sDir(%s)";
    private static final String SPECIAL_DIR_TEST_TEMPLATE_OTHER = "       %s.test%sDir(%s)";

    public static void generateAndExportClasses(@Nullable String sourceTypeName) {
        Type sourceType = resolveType(sourceTypeName);
        List<Type> targetTypes = findTargetTypes(sourceType);

        Path rootPath = Path.of("./").resolve(TARGET_PATH);

        Map<String, @Nullable String> predicateClazzes = new HashMap<>();
        Map<String, Path> dirPaths = new HashMap<>();

        if (sourceType != null) {
            String shortName = sourceType.shortName();
            predicateClazzes.put(shortName, generateClass(sourceType, targetTypes));
            dirPaths.put(shortName, rootPath.resolve(sourceType.subPackage()));
        }
        for (Type type : targetTypes) {
            String shortName = type.shortName();
            predicateClazzes.put(shortName, generateClass(type, findTargetTypes(type)));
            dirPaths.put(shortName, rootPath.resolve(type.subPackage()));
        }

        try {
            for (Path value : dirPaths.values()) {
                Files.createDirectories(value);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create directories", e);
        }
        predicateClazzes.forEach((name, clazz) ->
                exportClass(dirPaths.get(name), FILE_NAME_TEMPLATE.formatted(name), clazz)
        );
    }

    @Contract("null -> null")
    private static @Nullable Type resolveType(@Nullable String typeName) {
        if (typeName == null) {
            return null;
        }
        Type type = SkipPredicateGeneratorData.KNOWN_TYPES.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return type;
    }

    private static List<Type> findTargetTypes(@Nullable Type sourceType) {
        if (sourceType == null) {
            return List.copyOf(SkipPredicateGeneratorData.KNOWN_TYPES.values());
        }

        return SkipPredicateGeneratorData.KNOWN_TYPES.values()
                .stream()
                .filter(type -> type != sourceType)
                .filter(type -> {
                    if (sourceType.hasOneWayTestAgainst(type)) {
                        return true;
                    }

                    for (TestDir srcDir : sourceType.testDirs()) {
                        if (srcDir.isExcluded(type)) {
                            continue;
                        }

                        for (TestDir dir : type.testDirs()) {
                            if (dir.isExcluded(sourceType)) {
                                continue;
                            }

                            for (String id : srcDir.identifiers()) {
                                if (dir.identifiers().contains(id)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                })
                .toList();
    }

    private static void exportClass(Path dirPath, String fileName, @Nullable String clazz) {
        if (clazz == null) {
            return;
        }

        Path clazzPath = dirPath.resolve(fileName);
        System.out.println(clazzPath.toAbsolutePath().normalize());
        try {
            Files.writeString(clazzPath, clazz, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Predicate class not exported: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
    }

    private static @Nullable String generateClass(Type sourceType, List<Type> targetTypes) {
        // Types which are only one-way targets need to be in KNOWN_TYPES so we need to filter these here instead
        if (sourceType.testDirs().isEmpty() && sourceType.oneWayTests().isEmpty()) {
            return null;
        }

        Set<String> imports = new HashSet<>(STANDARD_IMPORTS);
        Map<Type, Set<Property>> propsByTestTarget = new HashMap<>();
        Map<Type, List<TestDir>> sourceTestDirsByType = new HashMap<>();
        Set<Type> noPropsTypes = new HashSet<>();

        String preFilterPropertyList = buildPropertyLookupList(sourceType, sourceType.properties(), imports, PropertyLookupLocation.PRE_FILTER);
        String propertyList = buildPropertyLookupList(sourceType, sourceType.properties(), imports, PropertyLookupLocation.MAIN);
        String testMthList = buildTestMethodList(sourceType, targetTypes, imports, propsByTestTarget, sourceTestDirsByType, noPropsTypes);
        String testCaseList = buildTestCaseList(sourceType, targetTypes, propsByTestTarget, sourceTestDirsByType, noPropsTypes);
        String importList = imports.stream().sorted().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));

        String shortName = sourceType.shortName();
        String instField = "";
        if (sourceType.allTypes(null).size() > 1) {
            instField = INST_FIELD_TEMPLATE.formatted(shortName, shortName, shortName);
        }
        return CLASS_TEMPLATE.formatted(
                SKIP_PREDS_ROOT_PKG,
                sourceType.subPackage(),
                importList,
                buildAnnotationTypeList(sourceType, "", null, false, false),
                shortName,
                instField,
                preFilterPropertyList,
                propertyList,
                testCaseList,
                testMthList
        );
    }

    private static String buildPropertyLookupList(Type type, List<Property> properties, Set<String> imports, PropertyLookupLocation location) {
        boolean inPreFilter = location == PropertyLookupLocation.PRE_FILTER;
        boolean inMain = location == PropertyLookupLocation.MAIN;
        boolean inTestMth = location == PropertyLookupLocation.TEST_METHOD;

        StringBuilder builder = new StringBuilder();

        Set<String> specialLookupVarsFoundTwice = new HashSet<>();
        SequencedMap<String, SequencedSet<Property>> specialLookupVars = new LinkedHashMap<>();
        for (Property prop : properties) {
            if (!prop.hasSpecialLookup()) {
                continue;
            }

            SpecialPropLookup lookup = Objects.requireNonNull(prop.specialPropLookup());
            SequencedSet<Property> varProps = specialLookupVars.computeIfAbsent(lookup.varName(), _ -> new LinkedHashSet<>());
            if (!varProps.isEmpty()) {
                specialLookupVarsFoundTwice.add(lookup.varName());
            }
            varProps.add(prop);
        }
        specialLookupVars.forEach((varName, props) -> {
            if (!specialLookupVarsFoundTwice.contains(varName)) {
                return;
            }

            if (!inTestMth) {
                boolean anyEarlyExit = props.stream().anyMatch(Property::earlyExit);
                if ((inPreFilter && !anyEarlyExit) || (inMain && anyEarlyExit)) {
                    return;
                }
            }

            SpecialPropLookup lookup = Objects.requireNonNull(props.getFirst().specialPropLookup());
            builder.append(location.lookupVarTemplate.formatted(
                    lookup.varType(),
                    lookup.varName(),
                    lookup.varType()
            )).append("\n");
        });

        List<String> propVars = new ArrayList<>();
        for (Property prop : properties) {
            if ((inPreFilter && !prop.earlyExit()) || (inMain && prop.earlyExit())) {
                continue;
            }

            collectPropertyImports(prop, imports);

            String varName = prop.name();
            if (inTestMth) {
                varName = capitalize(varName, false);
            }
            propVars.add(varName);

            String lookupLine;
            if (prop.hasSpecialLookup()) {
                SpecialPropLookup lookup = Objects.requireNonNull(prop.specialPropLookup());
                imports.add(lookup.classImport());

                String ifaceVarName = lookup.varName();
                String callTarget = ifaceVarName;
                if (!specialLookupVarsFoundTwice.contains(ifaceVarName)) {
                    callTarget = location.lookupInlineTemplate.formatted(lookup.varType());
                }
                lookupLine = location.specialLookupTemplate.formatted(
                        prop.typeName(),
                        varName,
                        callTarget,
                        lookup.method()
                );
            } else {
                lookupLine = location.lookupTemplate.formatted(
                        prop.typeName(),
                        varName,
                        prop.propHolder(),
                        prop.propName()
                );
            }
            builder.append(lookupLine).append("\n");
        }
        if (!builder.isEmpty() && location == PropertyLookupLocation.PRE_FILTER) {
            String preFilterArgs = String.join(", ", propVars);
            builder.append(PRE_FILTER_TEST_TEMPLATE.formatted(getTestDirComputeClass(type, true), preFilterArgs));
        }
        if (!builder.isEmpty() && (!inTestMth || properties.size() > 1)) {
            builder.append("\n");
        }
        return builder.toString();
    }

    private static String buildTestCaseList(
            Type sourceType,
            List<Type> targetTypes,
            Map<Type, Set<Property>> propsByTestTarget,
            Map<Type, List<TestDir>> sourceTestDirsByType,
            Set<Type> noPropsTypes
    ) {
        StringBuilder builder = new StringBuilder();
        boolean hasSpecialSelfTest = sourceType.hasSpecialTests();
        Set<Property> selfProps = propsByTestTarget.get(sourceType);
        if (selfProps != null) {
            String selfPropArgsList = buildTestCaseArgList(sourceType, selfProps, hasSpecialSelfTest, hasSpecialSelfTest || !noPropsTypes.contains(sourceType));
            builder.append(buildTestCase(sourceType, selfPropArgsList, sourceTestDirsByType.get(sourceType)));
        }
        for (Type type : targetTypes) {
            Set<Property> props = propsByTestTarget.get(type);
            if (props == null) {
                continue;
            }

            boolean hasSpecialTest = sourceType.hasOneWayTestAgainst(type);
            String propArgsList = buildTestCaseArgList(sourceType, props, hasSpecialTest, hasSpecialTest || !noPropsTypes.contains(type));
            builder.append(buildTestCase(type, propArgsList, sourceTestDirsByType.get(type)));
        }
        return builder.toString().stripTrailing();
    }

    private static String buildTestCaseArgList(Type type, Set<Property> usedProps, boolean needsState, boolean needAdjState) {
        String args = type.properties()
                .stream()
                .filter(usedProps::contains)
                .map(Property::name)
                .collect(Collectors.joining(", "));
        if (!args.isEmpty()) {
            args += ", ";
        }
        if (needsState) {
            args = "state, " + args;
        }
        if (needAdjState) {
            args += "adjState, ";
        }
        return args;
    }

    private static String buildTestCase(Type type, String propArgsList, @Nullable List<TestDir> sourceTestDirs) {
        return TEST_CASE_TEMPLATE.formatted(
                String.join(",\n" + " ".repeat(21), type.allTypes(sourceTestDirs)),
                type.shortName(),
                propArgsList
        );
    }

    private static String buildTestMethodList(
            Type sourceType,
            List<Type> targetTypes,
            Set<String> imports,
            Map<Type, Set<Property>> propsByTestTarget,
            Map<Type, List<TestDir>> sourceTestDirsByType,
            Set<Type> noPropsTypes
    ) {
        StringBuilder builder = new StringBuilder();

        Set<Property> selfUsedProps = new HashSet<>();
        List<TestDir> selfSourceTestDirs = new ArrayList<>();
        String selfTestExec = buildTestExecution(sourceType, sourceType, selfUsedProps, new HashSet<>(), noPropsTypes, selfSourceTestDirs);
        sourceTestDirsByType.put(sourceType, selfSourceTestDirs);
        if (selfTestExec != null) {
            String selfPropParamsList = sourceType.properties()
                    .stream()
                    .filter(selfUsedProps::contains)
                    .map(prop -> prop.typeName() + " " + prop.name())
                    .collect(Collectors.joining(", "));
            if (!selfPropParamsList.isEmpty()) {
                selfPropParamsList += ", ";
            }
            boolean hasSpecialSelfTest = sourceType.hasSpecialTests();
            if (hasSpecialSelfTest) {
                selfPropParamsList = "BlockState state, " + selfPropParamsList;
            }
            if (!noPropsTypes.contains(sourceType) || hasSpecialSelfTest) {
                selfPropParamsList += "BlockState adjState, ";
            }
            List<Property> selfProperties = sourceType.properties().stream().filter(selfUsedProps::contains).toList();
            String selfPropLookupList = buildPropertyLookupList(sourceType, selfProperties, imports, PropertyLookupLocation.TEST_METHOD);
            builder.append(buildTestMethod(sourceType, selfPropParamsList, selfPropLookupList, selfTestExec, selfSourceTestDirs, false));
            propsByTestTarget.put(sourceType, selfUsedProps);
        }

        boolean omitLeadingLinebreak = selfTestExec == null;
        for (Type type : targetTypes) {
            Set<Property> srcUsedProps = new HashSet<>();
            Set<Property> targetUsedProps = new HashSet<>();
            List<TestDir> sourceTestDirs = new ArrayList<>();
            String testExec = buildTestExecution(sourceType, type, srcUsedProps, targetUsedProps, noPropsTypes, sourceTestDirs);
            sourceTestDirsByType.put(type, sourceTestDirs);
            if (testExec == null) {
                continue;
            }

            String propParamsList = sourceType.properties()
                    .stream()
                    .filter(srcUsedProps::contains)
                    .map(prop -> prop.typeName() + " " + prop.name())
                    .collect(Collectors.joining(", "));
            if (!propParamsList.isEmpty()) {
                propParamsList += ", ";
            }
            boolean isOneWayTest = sourceType.hasOneWayTestAgainst(type);
            if (isOneWayTest) {
                propParamsList = "BlockState state, " + propParamsList;
            }
            if (!noPropsTypes.contains(type) || isOneWayTest) {
                propParamsList += "BlockState adjState, ";
            }

            String propLookupList = "";
            if (!type.properties().isEmpty()) {
                List<Property> properties = type.properties().stream().filter(targetUsedProps::contains).toList();
                propLookupList = buildPropertyLookupList(type, properties, imports, PropertyLookupLocation.TEST_METHOD);
            }

            if (!omitLeadingLinebreak) {
                builder.append("\n");
            }
            builder.append(buildTestMethod(type, propParamsList, propLookupList, testExec, sourceTestDirs, isOneWayTest));

            propsByTestTarget.put(type, srcUsedProps);
            if (!isOneWayTest && !type.subPackage().equals(sourceType.subPackage())) {
                imports.add(SKIP_PREDS_ROOT_PKG + type.subPackage() + "." + getTestDirComputeClass(type, false));
            }

            omitLeadingLinebreak = false;
        }
        return builder.toString().stripTrailing();
    }

    private static @Nullable String buildTestExecution(
            Type sourceType,
            Type type,
            Set<Property> srcUsedProps,
            Set<Property> targetUsedProps,
            Set<Type> noPropsTypes,
            List<TestDir> sourceTestDirs
    ) {
        record DirPair(TestDir first, @Nullable TestDir second) { }

        List<DirPair> commonDirs = new ArrayList<>();
        if (sourceType == type) {
            for (TestDir dir : sourceType.testDirs()) {
                if (dir.isExcluded(sourceType)) {
                    continue;
                }

                commonDirs.add(new DirPair(dir, dir));
                List<Property> usedProps = dir.getProps().stream().map(sourceType.propertyMap()::get).toList();
                srcUsedProps.addAll(usedProps);
                targetUsedProps.addAll(usedProps);
            }
        } else if (!sourceType.testDirs().isEmpty() && !type.testDirs().isEmpty()) {
            for (TestDir dir : sourceType.testDirs()) {
                if (dir.isExcluded(type)) {
                    continue;
                }

                for (TestDir otherDir : type.testDirs()) {
                    if (otherDir.isExcluded(sourceType)) {
                        continue;
                    }

                    for (String id : dir.identifiers()) {
                        if (otherDir.identifiers().contains(id)) {
                            commonDirs.add(new DirPair(dir, otherDir));
                            srcUsedProps.addAll(dir.getProps().stream().map(sourceType.propertyMap()::get).toList());
                            targetUsedProps.addAll(otherDir.getProps().stream().map(type.propertyMap()::get).toList());
                            break;
                        }
                    }
                }
            }
        } else if (sourceType.hasOneWayTestAgainst(type)) {
            TestDir testDir = sourceType.oneWayTests().get(type.type());
            commonDirs.add(new DirPair(testDir, null));
        }

        String indent = "        ";
        if (commonDirs.isEmpty()) {
            return null;
        }

        commonDirs.stream()
                .map(DirPair::first)
                .forEach(sourceTestDirs::add);

        String firstTarget = getTestDirComputeClass(sourceType, true);
        String secondTarget = sourceType == type ? firstTarget : getTestDirComputeClass(type, true);

        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (DirPair pair : commonDirs) {
            if (!first) {
                builder.append(" ||\n");
            }

            boolean special = pair.first.isSpecial();
            String template = switch (pair.first.comparison()) {
                case TEST_DIR -> first ? DIR_TEST_TEMPLATE_FIRST : DIR_TEST_TEMPLATE_OTHER;
                case BOOLEAN -> first ? BOOLEAN_DIR_TEST_TEMPLATE_FIRST : BOOLEAN_DIR_TEST_TEMPLATE_OTHER;
                case IDENTITY -> first ? IDENTITY_DIR_TEST_TEMPLATE_FIRST : IDENTITY_DIR_TEST_TEMPLATE_OTHER;
                case EQUALS -> first ? EQUALS_DIR_TEST_TEMPLATE_FIRST : EQUALS_DIR_TEST_TEMPLATE_OTHER;
                case SPECIAL -> first ? SPECIAL_DIR_TEST_TEMPLATE_FIRST : SPECIAL_DIR_TEST_TEMPLATE_OTHER;
            };
            String dirTestExec;
            if (special) {
                dirTestExec = template.formatted(
                        firstTarget,
                        pair.first.name(),
                        buildSpecialTestExecParams(sourceType, type, pair.first, pair.second, noPropsTypes)
                );
            } else {
                Objects.requireNonNull(pair.second);
                dirTestExec = template.formatted(
                        firstTarget,
                        pair.first.name(),
                        buildSimpleTestExecParams(sourceType, pair.first, false, noPropsTypes),
                        secondTarget,
                        pair.second.name(),
                        buildSimpleTestExecParams(type, pair.second, true, noPropsTypes)
                );
            }
            builder.append(indent).append(dirTestExec);

            first = false;
        }

        return builder.append(";").toString();
    }

    private static String buildSimpleTestExecParams(Type type, TestDir dir, boolean opposite, Set<Type> noPropsTypes) {
        return buildPropertyValueParamList(type, dir, opposite, noPropsTypes) + (opposite ? "side.getOpposite()" : "side");
    }

    private static String buildSpecialTestExecParams(Type typeOne, Type typeTwo, TestDir dirOne, @Nullable TestDir dirTwo, Set<Type> noPropsTypes) {
        String firstParams = buildPropertyValueParamList(typeOne, dirOne, false, noPropsTypes);
        String params = "state, " + firstParams + "adjState, ";
        if (dirTwo != null) {
            params += buildPropertyValueParamList(typeTwo, dirTwo, true, noPropsTypes);
        }
        return params + "side";
    }

    private static String buildPropertyValueParamList(Type type, TestDir dir, boolean opposite, Set<Type> noPropsTypes) {
        String params = dir.getProps()
                .stream()
                .map(prop -> type.propertyMap().get(prop))
                .map(Property::name)
                .map(prop -> opposite ? ("adj" + capitalize(prop, false)) : prop)
                .collect(Collectors.joining(", "));
        if (!params.isEmpty()) {
            params += ", ";
        } else {
            noPropsTypes.add(type);
        }
        return params;
    }

    private static String buildTestMethod(
            Type type,
            String propParamsList,
            String propLookup,
            String testExec,
            @Nullable List<TestDir> sourceTestDirs,
            boolean oneWayTest
    ) {
        return TEST_MTH_TEMPLATE.formatted(
                buildAnnotationTypeList(type, "    ", sourceTestDirs, true, oneWayTest),
                type.shortName(),
                propParamsList,
                propLookup,
                testExec
        );
    }

    private static void collectPropertyImports(Property property, Set<String> imports) {
        String holderImport = STATE_PROP_SOURCES.get(property.propHolder());
        if (holderImport == null) {
            throw new IllegalArgumentException("Invalid prop holder: " + property.propHolder());
        }
        imports.add(holderImport);

        switch (property.type()) {
            case VANILLA -> imports.add(VANILLA_PROP_TYPES_PKG + property.typeName());
            case CUSTOM -> imports.add(CUSTOM_PROP_TYPES_PKG + property.typeName());
        }
    }

    private static String getTestDirComputeClass(Type type, boolean includeInner) {
        String pkg = type.subPackage();
        String name = SkipPredicateGeneratorData.TEST_DIR_COMPUTE_CLASS_NAME_OVERRIDES.get(pkg);
        if (name == null) {
            name = capitalize(pkg, false);
        }
        name += "Dirs";
        if (includeInner) {
            name += "." + type.shortName();
        }
        return name;
    }

    static String capitalize(String text, boolean lowerPartTwo) {
        String partOne = text.substring(0, 1).toUpperCase(Locale.ROOT);
        String partTwo = text.substring(1);
        if (lowerPartTwo) {
            partTwo = partTwo.toLowerCase(Locale.ROOT);
        }
        return partOne + partTwo;
    }

    private static String buildAnnotationTypeList(Type type, String indent, @Nullable List<TestDir> sourceTestDirs, boolean testTarget, boolean oneWayTest) {
        List<String> types = type.allTypes(sourceTestDirs);
        String typeList;
        if (types.size() == 1) {
            typeList = "BlockType." + types.getFirst();
        } else {
            String innerIndent = indent + "        ";
            typeList = types.stream()
                    .map("BlockType."::concat)
                    .collect(Collectors.joining(",\n" + innerIndent, "{\n" + innerIndent, "\n" + indent + "}"));
        }
        if (!testTarget && !type.hasSelfTest()) {
            typeList = "value = " + typeList + ", noSelfTest = true";
        } else if (testTarget && oneWayTest) {
            typeList = "value = " + typeList + ", oneWay = true";
        }
        return typeList;
    }

    private enum PropertyLookupLocation {
        PRE_FILTER(
                PRE_FILTER_SPECIAL_LOOKUP_VAR_TEMPLATE,
                SPECIAL_LOOKUP_INLINE_TEMPLATE,
                PRE_FILTER_PROP_LOOKUP_TEMPLATE,
                PRE_FILTER_SPECIAL_PROP_LOOKUP_TEMPLATE
        ),
        MAIN(
                SPECIAL_LOOKUP_VAR_TEMPLATE,
                SPECIAL_LOOKUP_INLINE_TEMPLATE,
                PROP_LOOKUP_TEMPLATE,
                SPECIAL_PROP_LOOKUP_TEMPLATE
        ),
        TEST_METHOD(
                TEST_MTH_SPECIAL_LOOKUP_VAR_TEMPLATE,
                TEST_MTH_SPECIAL_LOOKUP_INLINE_TEMPLATE,
                TEST_MTH_PROP_LOOKUP_TEMPLATE,
                TEST_MTH_SPECIAL_PROP_LOOKUP_TEMPLATE
        ),
        ;

        private final String lookupVarTemplate;
        private final String lookupInlineTemplate;
        private final String lookupTemplate;
        private final String specialLookupTemplate;

        PropertyLookupLocation(String lookupVarTemplate, String lookupInlineTemplate, String lookupTemplate, String specialLookupTemplate) {
            this.lookupVarTemplate = lookupVarTemplate;
            this.lookupInlineTemplate = lookupInlineTemplate;
            this.lookupTemplate = lookupTemplate;
            this.specialLookupTemplate = specialLookupTemplate;
        }
    }

    private SkipPredicateGeneratorImpl() { }
}
