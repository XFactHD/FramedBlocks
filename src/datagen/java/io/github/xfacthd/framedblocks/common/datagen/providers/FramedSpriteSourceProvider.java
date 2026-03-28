package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.render.util.AnimationSplitterSource;
import io.github.xfacthd.framedblocks.client.render.util.AreaMaskSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FramedSpriteSourceProvider extends SpriteSourceProvider {
    public static final Identifier SPRITE_SAW_STILL = Utils.id("block/stonecutter_saw_still");

    public FramedSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, FramedConstants.MOD_ID);
    }

    @Override
    protected void gather() {
        atlas(AtlasIds.BLOCKS)
                .addSource(new AnimationSplitterSource(
                        Utils.id("minecraft", "block/stonecutter_saw"),
                        List.of(new AnimationSplitterSource.Frame(0, SPRITE_SAW_STILL))
                ));

        SourceList sources = atlas(AtlasIds.BLOCKS);
        for (DyeColor color : DyeColor.values()) {
            String colorName = color.getName();
            sources.addSource(new AreaMaskSource(
                    Utils.id("minecraft", "block/" + colorName + "_wool"),
                    Utils.id("block/overlay/" + colorName + "_carpet_edge"),
                    0,
                    15,
                    16,
                    1,
                    0,
                    -15
            ));
        }
    }
}
