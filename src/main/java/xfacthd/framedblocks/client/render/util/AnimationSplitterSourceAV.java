package xfacthd.framedblocks.client.render.util;
/*
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import xfacthd.atlasviewer.client.api.*;

import java.util.List;

final class AnimationSplitterSourceAV extends AnimationSplitterSource implements IPackAwareSpriteSource
{
    private final SpriteSourceMeta meta = new SpriteSourceMeta();

    AnimationSplitterSourceAV(ResourceLocation resource, List<Frame> frames)
    {
        super(resource, frames);
    }

    @Override
    public SpriteSourceMeta atlasviewer$getMeta()
    {
        return meta;
    }

    @Override
    FrameInstance createFrameInstance(Resource res, ResourceLocation texPath, LazyLoadedImage image, Frame frame)
    {
        FrameInstanceAV instance = new FrameInstanceAV(res, texPath, image, frame);
        instance.atlasviewer$getMeta().readFromSpriteSourceMeta(this);
        return instance;
    }



    static final class FrameInstanceAV extends AnimationSplitterSource.FrameInstance implements ISpriteSourcePackAwareSpriteSupplier
    {
        private final SpriteSupplierMeta meta = new SpriteSupplierMeta();

        FrameInstanceAV(Resource resource, ResourceLocation texPath, LazyLoadedImage lazyImage, Frame frame)
        {
            super(resource, texPath, lazyImage, frame);
        }

        @Override
        public SpriteSupplierMeta atlasviewer$getMeta()
        {
            return meta;
        }

        @Override
        SpriteContents postProcess(SpriteContents contents)
        {
            ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$captureMetaFromSpriteSupplier(this, resource);
            return contents;
        }
    }
}*/
