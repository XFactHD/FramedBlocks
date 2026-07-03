package io.github.xfacthd.framedblocks.api.model.item;

import io.github.xfacthd.framedblocks.api.model.CachingModel;
import net.minecraft.client.renderer.item.ItemModel;

/// Base class for custom item models for framed blocks.
public abstract class AbstractFramedBlockItemModel implements ItemModel, CachingModel {
    protected AbstractFramedBlockItemModel() {
        CachingModel.register(this);
    }
}
