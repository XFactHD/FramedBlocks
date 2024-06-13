package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.render.util.AnimationSplitterSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FramedSpriteSourceProvider extends SpriteSourceProvider
{
    public static final ResourceLocation SPRITE_SAW_STILL = Utils.rl("block/stonecutter_saw_still");

    public FramedSpriteSourceProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper fileHelper
    )
    {
        super(output, lookup, FramedConstants.MOD_ID, fileHelper);
    }

    @Override
    protected void gather()
    {
        existingFileHelper.trackGenerated(SPRITE_SAW_STILL, ModelProvider.TEXTURE);

        atlas(BLOCKS_ATLAS)
                .addSource(new AnimationSplitterSource(
                        Utils.rl("minecraft", "block/stonecutter_saw"),
                        List.of(new AnimationSplitterSource.Frame(0, SPRITE_SAW_STILL))
                ));
    }
}
