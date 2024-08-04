package xfacthd.framedblocks.common.apiimpl;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.datagen.loot.objects.SplitCamoLootFunction;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.appearance.AppearanceHelper;
import xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.shapes.ShapeReloader;

public final class InternalApiImpl implements InternalAPI
{
    @Override
    public BlockEntityType<?> getDefaultBlockEntity()
    {
        return FBContent.BE_TYPE_FRAMED_BLOCK.value();
    }

    @Override
    public CamoContainerFactory<EmptyCamoContainer> getEmptyCamoContainerFactory()
    {
        return FBContent.FACTORY_EMPTY.value();
    }

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
        Preconditions.checkState(!FMLEnvironment.production, "Reloading shapes is not supported in production");
        ShapeReloader.addCache(cache);
    }

    @Override
    public void registerReloadableShapeProvider(ReloadableShapeProvider provider)
    {
        Preconditions.checkState(!FMLEnvironment.production, "Reloading shapes is not supported in production");
        ShapeReloader.addProvider(provider);
    }

    @Override
    public LootItemConditionType getNonTrivialCamoLootConditionType()
    {
        return FBContent.NON_TRIVIAL_CAMO_LOOT_CONDITION.value();
    }

    @Override
    public LootItemFunctionType<SplitCamoLootFunction> getSplitCamoLootFunctionType()
    {
        return FBContent.SPLIT_CAMO_LOOT_FUNCTION.value();
    }
}
