package io.github.xfacthd.framedblocks.common.apiimpl;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import io.github.xfacthd.framedblocks.api.datagen.recipes.builders.FramingSawRecipeBuilder;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.shapes.ReloadableShapeLookup;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.data.appearance.AppearanceHelper;
import io.github.xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import io.github.xfacthd.framedblocks.common.data.camo.block.rotator.BlockCamoRotators;
import io.github.xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import io.github.xfacthd.framedblocks.common.data.shapes.ShapeReloader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class InternalApiImpl implements InternalAPI
{
    @Override
    @Nullable
    public CamoContainerFactory<?> findCamoFactory(ItemStack stack)
    {
        return CamoContainerFactories.findCamoFactory(stack);
    }

    @Override
    public boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack)
    {
        return CamoContainerFactories.isValidRemovalTool(container, stack);
    }

    @Override
    public void enqueueCullingUpdate(Level level, BlockPos pos)
    {
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
    )
    {
        return AppearanceHelper.getAppearance(block, state, level, pos, side, queryState, queryPos);
    }

    @Override
    public void registerShapeCache(ShapeCache<?> cache)
    {
        Preconditions.checkState(!Utils.PRODUCTION, "Reloading shapes is not supported in production");
        ShapeReloader.addCache(cache);
    }

    @Override
    public void registerReloadableShapeLookup(ReloadableShapeLookup lookup)
    {
        Preconditions.checkState(!Utils.PRODUCTION, "Reloading shapes is not supported in production");
        ShapeReloader.addLookup(lookup);
    }

    @Override
    public BlockCamoRotator getCamoRotator(Block block)
    {
        return BlockCamoRotators.get(block);
    }

    @Override
    public Recipe<?> makeFramingSawRecipe(int materialAmount, List<FramingSawRecipeBuilder.Additive> additives, ItemStack result, boolean disabled)
    {
        List<FramingSawRecipeAdditive> builtAdditives = additives.stream().map(FramingSawRecipeAdditive::of).toList();
        return new FramingSawRecipe(materialAmount, builtAdditives, result, disabled);
    }
}
