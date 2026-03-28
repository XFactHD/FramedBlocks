package io.github.xfacthd.framedblocks.common.compat.atlasviewer;

import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import io.github.xfacthd.framedblocks.client.render.util.AreaMaskSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;

public final class AtlasViewerCompat {
    public static final Component LABEL_TEXTURE = Utils.translate("label", "source_tooltip.anim_splitter.texture");
    public static final Component LABEL_FRAMES = Utils.translate("label", "source_tooltip.anim_splitter.frames");
    public static final Component LABEL_MASK_TEXTURE = Utils.translate("label", "source_tooltip.area_mask.texture");
    public static final Component LABEL_MASK_SPRITE = Utils.translate("label", "source_tooltip.area_mask.sprite");
    public static final Component LABEL_MASK_AREA = Utils.translate("label", "source_tooltip.area_mask.area");
    public static final String VALUE_MASK_AREA = Utils.translationKey("value", "source_tooltip.area_mask.area");
    public static final Component LABEL_MASK_OFFSET = Utils.translate("label", "source_tooltip.area_mask.offset");
    public static final String VALUE_MASK_OFFSET = Utils.translationKey("value", "source_tooltip.area_mask.offset");

    public static void init(IEventBus modBus) {
        if (ModList.get().isLoaded("atlasviewer")) {
            try {
                if (Utils.CLIENT_DIST) {
                    GuardedClientAccess.init(modBus);
                }
            } catch (Throwable e) {
                FramedBlocks.LOGGER.warn("An error occured while initializing AtlasViewer integration!", e);
            }
        }
    }

    private static final class GuardedClientAccess {
        public static void init(IEventBus modBus) {
            modBus.addListener(GuardedClientAccess::onRegisterSpriteSourceDetails);
        }

        private static void onRegisterSpriteSourceDetails(RegisterSpriteSourceDetailsEvent event) {
            event.registerPrimaryResourceGetter(
                    AnimationSplitterSource.FrameInstance.class,
                    AnimationSplitterSource.FrameInstance::resource
            );
            event.registerSourceTooltipAppender(AnimationSplitterSource.class, (src, consumer) -> {
                consumer.accept(AtlasViewerCompat.LABEL_TEXTURE, Component.literal(src.resource().toString()));
                consumer.accept(AtlasViewerCompat.LABEL_FRAMES, Component.empty());
                src.frames().forEach(frame -> consumer.accept(
                        null, Component.literal("  - ")
                                .append(Component.literal(Integer.toString(frame.frameIdx())).withStyle(ChatFormatting.ITALIC))
                                .append(": ")
                                .append(Component.literal(frame.outLoc().toString()))
                ));
            });

            event.registerPrimaryResourceGetter(
                    AreaMaskSource.AreaMaskInstance.class,
                    AreaMaskSource.AreaMaskInstance::srcRes
            );
            event.registerSourceTooltipAppender(AreaMaskSource.class, (src, appender) -> {
                appender.accept(AtlasViewerCompat.LABEL_MASK_TEXTURE, Component.literal(src.resource().toString()));
                appender.accept(AtlasViewerCompat.LABEL_MASK_SPRITE, Component.literal(src.sprite().toString()));
                appender.accept(AtlasViewerCompat.LABEL_MASK_AREA, Component.translatable(VALUE_MASK_AREA, src.x(), src.y(), src.w(), src.h()));
                if (src.offX() != 0 || src.offY() != 0) {
                    appender.accept(AtlasViewerCompat.LABEL_MASK_OFFSET, Component.translatable(VALUE_MASK_OFFSET, src.offX(), src.offY()));
                }
            });
        }

        private GuardedClientAccess() { }
    }

    private AtlasViewerCompat() { }
}
