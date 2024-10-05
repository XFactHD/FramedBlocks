package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import xfacthd.framedblocks.api.model.AbstractFramedBlockModel;
import xfacthd.framedblocks.client.render.block.FramedChestRenderer;

public final class FramedClientUtils
{
    public static void clearModelCaches()
    {
        Minecraft.getInstance()
                .getModelManager()
                .getModelBakery()
                .getBakedTopLevelModels()
                .values()
                .stream()
                .filter(AbstractFramedBlockModel.class::isInstance)
                .map(AbstractFramedBlockModel.class::cast)
                .forEach(AbstractFramedBlockModel::clearCache);

        FramedChestRenderer.clearModelCaches();
    }



    private FramedClientUtils() { }
}
