package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.util.Utils;

import java.util.concurrent.CompletableFuture;

public final class FramedItemTagProvider extends ItemTagsProvider
{
    public FramedItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider provider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, provider, FramedConstants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() { return super.getName() + ": " + FramedConstants.MOD_ID; }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(ItemTags.SLABS).add(FBContent.blockFramedSlab.get().asItem());
        tag(ItemTags.STAIRS).add(FBContent.blockFramedStairs.get().asItem());
        tag(ItemTags.WALLS).add(FBContent.blockFramedWall.get().asItem());
        tag(ItemTags.FENCES).add(FBContent.blockFramedFence.get().asItem());
        tag(ItemTags.DOORS).add(FBContent.blockFramedDoor.get().asItem(), FBContent.blockFramedIronDoor.get().asItem());
        tag(ItemTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor.get().asItem(), FBContent.blockFramedIronTrapDoor.get().asItem());
        tag(ItemTags.SIGNS).add(FBContent.blockFramedSign.get().asItem());
        tag(Tags.Items.CHESTS).add(FBContent.blockFramedChest.get().asItem());
        tag(Utils.WRENCH).add(FBContent.itemFramedWrench.get());
        tag(Utils.DISABLE_INTANGIBLE).addTag(Utils.WRENCH).add(
                FBContent.itemFramedHammer.get(),
                FBContent.itemFramedBlueprint.get(),
                FBContent.itemFramedScrewdriver.get()
        );
    }
}