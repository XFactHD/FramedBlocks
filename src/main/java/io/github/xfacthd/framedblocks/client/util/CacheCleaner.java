package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.item.AbstractFramedBlockItemModel;
import io.github.xfacthd.framedblocks.client.model.FluidCubeModel;
import io.github.xfacthd.framedblocks.client.model.RuntimeMaterialBaker;
import io.github.xfacthd.framedblocks.client.model.overlaygen.BlockOverlayGenerator;
import io.github.xfacthd.framedblocks.client.model.overlaygen.OverlayQuadGenerator;
import io.github.xfacthd.framedblocks.client.model.unbaked.UnbakedStandaloneFramedBlockModel;
import io.github.xfacthd.framedblocks.client.render.special.ModelBasedOutlineRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;

public final class CacheCleaner
{
    public static void clearModelCaches(Reason reason)
    {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();

        modelManager.getBlockStateModelSet()
                .framedblocks$getModelByState()
                .values()
                .stream()
                .filter(AbstractFramedBlockStateModel.class::isInstance)
                .map(AbstractFramedBlockStateModel.class::cast)
                .forEach(AbstractFramedBlockStateModel::clearCache);

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
        FluidCubeModel.clearCaches();
        RuntimeMaterialBaker.clear(reason);
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
