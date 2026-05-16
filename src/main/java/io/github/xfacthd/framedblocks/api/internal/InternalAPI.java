package io.github.xfacthd.framedblocks.api.internal;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpecBuilder;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.FramingSawRecipeBuilder;
import io.github.xfacthd.framedblocks.api.shapes.ReloadableShapeLookup;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.util.Utils;
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
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.SequencedMap;

@ApiStatus.Internal
public interface InternalAPI {
    InternalAPI INSTANCE = Utils.loadService(InternalAPI.class);

    @Nullable CamoContainerFactory<?> findCamoFactory(ItemStack stack);

    boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack);

    void enqueueCullingUpdate(Level level, BlockPos pos);

    BlockState getAppearance(
            IFramedBlock framedBlock,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    );

    void registerShapeCache(ShapeCache<?> cache);

    void registerReloadableShapeLookup(ReloadableShapeLookup lookup);

    BlockCamoRotator getCamoRotator(Block block);

    Recipe<?> makeFramingSawRecipe(int materialAmount, List<FramingSawRecipeBuilder.Additive> additives, ItemStackTemplate result, boolean disabled);

    BlueprintCopyBehaviour getBlueprintCopyBehavior(Block block);

    StateCycleSpec buildSingleBlockStateCycleSpec(StateCycleSpecBuilder builder, StateCycleSpecAssembler assembler);

    StateCycleSpec buildMultiBlockStateCycleSpec(SequencedMap<Block, StateCycleSpecBuilder> entries, StateCycleSpecAssembler assembler);

    boolean isStateCyclingActive(Player player, BlockItem item);
}
