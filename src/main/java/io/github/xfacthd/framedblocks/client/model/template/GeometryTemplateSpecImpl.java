package io.github.xfacthd.framedblocks.client.model.template;

import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.client.util.CacheCleaner;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class GeometryTemplateSpecImpl extends GeometryTemplateSpec {
    private static final Map<Block, GeometryTemplateSpecImpl> SPECS_BY_BLOCK = new ConcurrentHashMap<>();

    private final BiConsumer<BlockState, SpecEntryBuilder> builderOperator;
    private final Map<BlockState, GeometryTemplateSpecEntry> builtSpecs = new IdentityHashMap<>();

    public static GeometryTemplateSpecImpl createImpl(Holder<Block> block, BiConsumer<BlockState, SpecEntryBuilder> builderOperator) {
        GeometryTemplateSpecImpl templateSpec = new GeometryTemplateSpecImpl(builderOperator);
        SPECS_BY_BLOCK.put(block.value(), templateSpec);
        return templateSpec;
    }

    private GeometryTemplateSpecImpl(BiConsumer<BlockState, SpecEntryBuilder> builderOperator) {
        this.builderOperator = builderOperator;
    }

    GeometryTemplateSpecEntry get(BlockState state) {
        return builtSpecs.get(state);
    }

    private int resolve(Block block, StateMerger stateMerger) {
        ObjectOpenHashSet<GeometryTemplateSpecEntry> dedupe = new ObjectOpenHashSet<>();
        block.getStateDefinition()
                .getPossibleStates()
                .stream()
                .map(stateMerger)
                .distinct()
                .forEach(state -> {
                    GeometryTemplateSpecBuilderImpl builder = new GeometryTemplateSpecBuilderImpl(state);
                    builderOperator.accept(state, builder);
                    builtSpecs.put(state, dedupe.addOrGet(builder.build()));
                });
        return dedupe.size();
    }

    public static int resolveAll() {
        int specCount = 0;
        for (Map.Entry<Block, GeometryTemplateSpecImpl> entry : SPECS_BY_BLOCK.entrySet()) {
            GeometryTemplateSpecImpl templateSpec = entry.getValue();
            specCount += templateSpec.resolve(entry.getKey(), ModelWrappingManager.tryGetStateMerger(entry.getKey()));
            GeometryTemplateManager.registerSourceFiles(
                    templateSpec.builtSpecs.values()
                            .stream()
                            .map(GeometryTemplateSpecEntry::sourceFiles)
                            .flatMap(List::stream)
                            .map(SourceFile::id)
                            .collect(Collectors.toSet())
            );
        }
        return specCount;
    }

    public static int getBlockCount() {
        return SPECS_BY_BLOCK.size();
    }

    public static void reset(CacheCleaner.Reason reason) {
        if (!Utils.PRODUCTION && reason == CacheCleaner.Reason.MANUAL) {
            SPECS_BY_BLOCK.values().forEach(set -> set.builtSpecs.clear());
            resolveAll();
        }
    }
}
