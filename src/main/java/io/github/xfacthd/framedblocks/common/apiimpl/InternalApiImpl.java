package io.github.xfacthd.framedblocks.common.apiimpl;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpecBuilder;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.FramingSawRecipeBuilder;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.internal.StateCycleSpecAssembler;
import io.github.xfacthd.framedblocks.api.shapes.ReloadableShapeLookup;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.data.appearance.AppearanceHelper;
import io.github.xfacthd.framedblocks.common.data.attachment.PlacementStateCycleStorage;
import io.github.xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import io.github.xfacthd.framedblocks.common.data.camo.block.rotator.BlockCamoRotators;
import io.github.xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import io.github.xfacthd.framedblocks.common.data.shapes.ShapeReloader;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import io.github.xfacthd.framedblocks.common.item.block.placement.MultiBlockStateCycleSpec;
import io.github.xfacthd.framedblocks.common.item.block.placement.SingleBlockStateCycleSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

public final class InternalApiImpl implements InternalAPI {
    @Override
    public @Nullable CamoContainerFactory<?> findCamoFactory(ItemStack stack) {
        return CamoContainerFactories.findCamoFactory(stack);
    }

    @Override
    public boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack) {
        return CamoContainerFactories.isValidRemovalTool(container, stack);
    }

    @Override
    public void enqueueCullingUpdate(Level level, BlockPos pos) {
        CullingUpdateTracker.enqueueCullingUpdate(level, pos);
    }

    @Override
    public BlockState getAppearance(
            IFramedBlock block,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    ) {
        return AppearanceHelper.getAppearance(block, state, level, pos, side, queryState, queryPos);
    }

    @Override
    public void registerShapeCache(ShapeCache<?> cache) {
        Preconditions.checkState(!Utils.PRODUCTION, "Reloading shapes is not supported in production");
        ShapeReloader.addCache(cache);
    }

    @Override
    public void registerReloadableShapeLookup(ReloadableShapeLookup lookup) {
        Preconditions.checkState(!Utils.PRODUCTION, "Reloading shapes is not supported in production");
        ShapeReloader.addLookup(lookup);
    }

    @Override
    public BlockCamoRotator getCamoRotator(Block block) {
        return BlockCamoRotators.get(block);
    }

    @Override
    public Recipe<?> makeFramingSawRecipe(int materialAmount, List<FramingSawRecipeBuilder.Additive> additives, ItemStackTemplate result, boolean disabled) {
        List<FramingSawRecipeAdditive> builtAdditives = additives.stream().map(FramingSawRecipeAdditive::of).toList();
        return new FramingSawRecipe(materialAmount, builtAdditives, result, disabled);
    }

    @Override
    public BlueprintCopyBehaviour getBlueprintCopyBehavior(Block block) {
        return FramedBlueprintItem.getBehaviour(block);
    }

    @Override
    public StateCycleSpec buildSingleBlockStateCycleSpec(StateCycleSpecBuilder builder, StateCycleSpecAssembler assembler) {
        return assembler.assemble(builder, SingleBlockStateCycleSpec::new);
    }

    @Override
    public StateCycleSpec buildMultiBlockStateCycleSpec(SequencedMap<Block, StateCycleSpecBuilder> entries, StateCycleSpecAssembler assembler) {
        SequencedMap<Block, SingleBlockStateCycleSpec> specs = new LinkedHashMap<>(entries.size());
        for (Map.Entry<Block, StateCycleSpecBuilder> entry : entries.sequencedEntrySet()) {
            specs.put(entry.getKey(), assembler.assemble(entry.getValue(), SingleBlockStateCycleSpec::new));
        }
        return new MultiBlockStateCycleSpec(specs);
    }

    @Override
    public boolean isStateCyclingActive(Player player, BlockItem item) {
        return PlacementStateCycleStorage.isActive(player, item);
    }
}
