package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.render.util.AnimationSplitterSource;

import java.util.List;

public final class FramedSpriteSourceProvider extends SpriteSourceProvider
{
    private static final ExistingFileHelper.ResourceType TYPE_TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");
    public static final ResourceLocation SPRITE_SAW_STILL = Utils.rl("block/stonecutter_saw_still");

    public FramedSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, fileHelper, FramedConstants.MOD_ID);
    }

    @Override
    protected void addSources()
    {
        AnimationSplitterSource.register(); // Minecraft is not instantiated in datagen

        existingFileHelper.trackGenerated(SPRITE_SAW_STILL, TYPE_TEXTURE);

        atlas(BLOCKS_ATLAS)
                .addSource(new AnimationSplitterSource(
                        new ResourceLocation("block/stonecutter_saw"),
                        List.of(new AnimationSplitterSource.Frame(0, SPRITE_SAW_STILL))
                ));
    }
}
