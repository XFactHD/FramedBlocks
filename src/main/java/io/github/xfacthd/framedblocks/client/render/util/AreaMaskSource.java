package io.github.xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record AreaMaskSource(
        Identifier resource,
        Identifier sprite,
        int x,
        int y,
        int w,
        int h,
        int offX,
        int offY
) implements SpriteSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<AreaMaskSource> CODEC = RecordCodecBuilder.<AreaMaskSource>mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("resource").forGetter(AreaMaskSource::resource),
            Identifier.CODEC.fieldOf("sprite").forGetter(AreaMaskSource::sprite),
            Codec.intRange(0, 15).fieldOf("x").forGetter(AreaMaskSource::x),
            Codec.intRange(0, 15).fieldOf("y").forGetter(AreaMaskSource::y),
            Codec.intRange(1, 16).fieldOf("width").forGetter(AreaMaskSource::w),
            Codec.intRange(1, 16).fieldOf("height").forGetter(AreaMaskSource::h),
            Codec.intRange(-15, 15).optionalFieldOf("offset_x", 0).forGetter(AreaMaskSource::offX),
            Codec.intRange(-15, 15).optionalFieldOf("offset_y", 0).forGetter(AreaMaskSource::offY)
    ).apply(inst, AreaMaskSource::new)).validate(res -> {
        if (res.x + res.w > 16) return DataResult.error(() -> "x + width must be <= 16!");
        if (res.y + res.h > 16) return DataResult.error(() -> "y + height must be <= 16!");
        if (res.x + res.offX < 0) return DataResult.error(() -> "x + offset_x must be >= 0");
        if (res.y + res.offY < 0) return DataResult.error(() -> "y + offset_y must be >= 0");
        if (res.x + res.w + res.offX > 16) return DataResult.error(() -> "x + width + offset_x must be <= 16");
        if (res.y + res.h + res.offY > 16) return DataResult.error(() -> "y + height + offset_y must be <= 16");
        return DataResult.success(res);
    });
    public static final Identifier ID = Utils.id("mask");

    @Override
    public void run(ResourceManager manager, Output out) {
        run(manager, out, Set.of());
    }

    @Override
    public void run(ResourceManager manager, Output out, Set<MetadataSectionType<?>> additionalMetadata) {
        Identifier srcPath = TEXTURE_ID_CONVERTER.idToFile(resource);
        Optional<Resource> optSource = manager.getResource(srcPath);
        if (optSource.isEmpty()) {
            LOGGER.warn("Missing source texture: {}", srcPath);
            return;
        }

        Resource srcRes = optSource.get();
        Rect2i rect = new Rect2i(x, y, w - 1, h - 1);
        out.add(sprite, new AreaMaskInstance(srcPath, srcRes, new LazyLoadedImage(srcPath, srcRes, 1), rect, offX, offY, sprite, additionalMetadata));
    }

    @Override
    public MapCodec<AreaMaskSource> codec() {
        return CODEC;
    }

    public record AreaMaskInstance(
            Identifier srcPath,
            Resource srcRes,
            LazyLoadedImage srcImg,
            Rect2i rect,
            int offX,
            int offY,
            Identifier sprite,
            Set<MetadataSectionType<?>> additionalMetadata
    ) implements DiscardableLoader {
        @Override
        public @Nullable SpriteContents get(SpriteResourceLoader loader) {
            try {
                NativeImage source = srcImg.get();

                ResourceMetadata srcMeta = srcRes.metadata();
                AnimationMetadataSection sourceAnim = srcMeta
                        .getSection(AnimationMetadataSection.TYPE)
                        .orElse(null);
                FrameSize frameSize = calculateFrameSize(source, sourceAnim);
                int factorX = frameSize.width() / 16;
                int factorY = frameSize.height() / 16;
                rect.setPosition(factorX * rect.getX(), factorY * rect.getY());
                rect.setWidth(rect.getWidth() * factorX);
                rect.setHeight(rect.getHeight() * factorY);
                int offX = this.offX * factorX;
                int offY = this.offY * factorY;

                NativeImage imageOut = new NativeImage(NativeImage.Format.RGBA, source.getWidth(), source.getHeight(), false);
                List<FrameInfo> frames = collectFrames(source, frameSize, sourceAnim);
                buildOutputImage(frames, source, rect, offX, offY, imageOut, frameSize);
                List<MetadataSectionType.WithValue<?>> metadata = srcMeta.getTypedSections(additionalMetadata);
                Optional<TextureMetadataSection> texMeta = srcMeta.getSection(TextureMetadataSection.TYPE);
                return new SpriteContents(sprite, frameSize, imageOut, Optional.ofNullable(sourceAnim), metadata, texMeta);
            } catch (Exception e) {
                LOGGER.error("Failed to create masked texture '{}' from source texture'{}'", sprite, srcPath, e);
            } finally {
                srcImg.release();
            }
            return null;
        }

        private static FrameSize calculateFrameSize(NativeImage source, @Nullable AnimationMetadataSection sourceAnim) {
            if (sourceAnim != null) {
                return sourceAnim.calculateFrameSize(source.getWidth(), source.getHeight());
            }
            return new FrameSize(source.getWidth(), source.getHeight());
        }

        private static List<FrameInfo> collectFrames(NativeImage image, FrameSize size, @Nullable AnimationMetadataSection animation) {
            List<FrameInfo> frames = new ArrayList<>();
            int rowCount = image.getWidth() / size.width();
            // Collect explicitly specified frames
            if (animation != null && animation.frames().isPresent()) {
                animation.frames().get().forEach(frame -> {
                    int idx = frame.index();
                    int frameX = (idx % rowCount) * size.width();
                    int frameY = (idx / rowCount) * size.height();
                    frames.add(new FrameInfo(idx, frameX, frameY));
                });
            }
            // Collect implicit frames if no explicit ones are specified in the animation or no animation is present
            if (frames.isEmpty()) {
                int frameCount = rowCount * (image.getHeight() / size.height());
                for (int idx = 0; idx < frameCount; idx++) {
                    int frameX = (idx % rowCount) * size.width();
                    int frameY = (idx / rowCount) * size.height();
                    frames.add(new FrameInfo(idx, frameX, frameY));
                }
            }
            return frames;
        }

        private static void buildOutputImage(List<FrameInfo> frames, NativeImage source, Rect2i rect, int offX, int offY, NativeImage imageOut, FrameSize frameSize) {
            frames.forEach(frame -> {
                int fx = frame.x();
                int fy = frame.y();
                int fw = frameSize.width();
                int fh = frameSize.height();

                for (int y = 0; y < fh; y++) {
                    for (int x = 0; x < fw; x++) {
                        int absX = fx + x;
                        int absY = fy + y;
                        int color = 0;
                        if (rect.contains(x, y)) {
                            color = source.getPixel(absX, absY);
                        }
                        imageOut.setPixel(Mth.positiveModulo(absX + offX, fw), Mth.positiveModulo(absY + offY, fh), color);
                    }
                }
            });
        }

        @Override
        public void discard() {
            srcImg.release();
        }
    }

    private record FrameInfo(int idx, int x, int y) { }
}
