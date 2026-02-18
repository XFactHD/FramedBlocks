package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockModel;
import io.github.xfacthd.framedblocks.api.model.item.AbstractFramedBlockItemModel;
import io.github.xfacthd.framedblocks.api.render.outline.ModelBasedOutlineRenderer;
import io.github.xfacthd.framedblocks.client.model.overlaygen.BlockOverlayGenerator;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayQuadGenerator;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedStandaloneFramedBlockModel;
import io.github.xfacthd.framedblocks.common.data.camo.fluid.FluidCamoClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;

public final class CacheCleaner
{
    public static void clearModelCaches(Reason reason)
    {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();

        modelManager.getBlockModelShaper()
                .framedblocks$getModelByStateCache()
                .values()
                .stream()
                .filter(AbstractFramedBlockModel.class::isInstance)
                .map(AbstractFramedBlockModel.class::cast)
                .forEach(AbstractFramedBlockModel::clearCache);

        modelManager.framedblocks$getBakedItemStackModels()
                .values()
                .stream()
                .filter(AbstractFramedBlockItemModel.class::isInstance)
                .map(AbstractFramedBlockItemModel.class::cast)
                .forEach(AbstractFramedBlockItemModel::clearCache);

        UnbakedStandaloneFramedBlockModel.clearCaches();

        clearExternalGeometryCaches(reason);
    }

    public static void clearExternalGeometryCaches(Reason reason)
    {
        FluidCamoClientHandler.clearModelCache();
        ModelBasedOutlineRenderer.clearCaches();
        OverlayQuadGenerator.clearCaches();
        BlockOverlayGenerator.clearCaches(reason);
    }

    public enum Reason
    {
        RELOAD,
        DISCONNECT,
        SETTINGS_CHANGED,
        MANUAL,
    }

    private CacheCleaner() { }
}
