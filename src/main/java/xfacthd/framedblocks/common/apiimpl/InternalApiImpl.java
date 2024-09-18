package xfacthd.framedblocks.common.apiimpl;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.shapes.ReloadableShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeCache;
import xfacthd.framedblocks.common.data.appearance.AppearanceHelper;
import xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import xfacthd.framedblocks.common.data.cullupdate.CullingUpdateTracker;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.common.data.shapes.ShapeReloader;

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
        Preconditions.checkState(!FMLEnvironment.production, "Reloading shapes is not supported in production");
        ShapeReloader.addCache(cache);
    }

    @Override
    public void registerReloadableShapeProvider(ReloadableShapeProvider provider)
    {
        Preconditions.checkState(!FMLEnvironment.production, "Reloading shapes is not supported in production");
        ShapeReloader.addProvider(provider);
    }
}
