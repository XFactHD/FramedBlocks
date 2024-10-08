package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import xfacthd.framedblocks.api.model.FramedBlockModel;
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
                .filter(FramedBlockModel.class::isInstance)
                .map(FramedBlockModel.class::cast)
                .forEach(FramedBlockModel::clearCache);

        FramedChestRenderer.clearModelCaches();
    }



    private FramedClientUtils() { }
}
