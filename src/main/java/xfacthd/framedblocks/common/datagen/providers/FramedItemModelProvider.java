package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.util.BlueprintPropertyOverride;
import xfacthd.framedblocks.common.FBContent;

@SuppressWarnings({ "SameParameterValue", "UnusedReturnValue" })
public final class FramedItemModelProvider extends ItemModelProvider
{
    public FramedItemModelProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, FramedConstants.MOD_ID, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        handheldItem(FBContent.itemFramedHammer, "cutout");
        handheldItem(FBContent.itemFramedWrench, "cutout");
        handheldItem(FBContent.itemFramedKey, "cutout");
        handheldItem(FBContent.itemFramedScrewdriver, "cutout");

        simpleItem(FBContent.itemFramedReinforcement, "cutout");

        ItemModelBuilder modelNormal = simpleItem(FBContent.itemFramedBlueprint, "cutout");
        ModelFile modelWritten = simpleItem("framed_blueprint_written", "cutout");

        modelNormal.override()
                    .predicate(BlueprintPropertyOverride.HAS_DATA, 0)
                    .model(modelNormal)
                    .end()
                .override()
                    .predicate(BlueprintPropertyOverride.HAS_DATA, 1)
                    .model(modelWritten)
                    .end();
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item, String renderType)
    {
        String name = item.getId().getPath();
        return singleTexture(name, mcLoc("item/handheld"), "layer0", modLoc("item/" + name)).renderType(renderType);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item, String renderType)
    {
        return simpleItem(item.getId().getPath(), renderType);
    }

    private ItemModelBuilder simpleItem(String name, String renderType)
    {
        return singleTexture(name, mcLoc("item/generated"), "layer0", modLoc("item/" + name)).renderType(renderType);
    }
}