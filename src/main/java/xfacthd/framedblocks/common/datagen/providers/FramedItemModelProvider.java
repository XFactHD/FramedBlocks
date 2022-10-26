package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.util.BlueprintPropertyOverride;
import xfacthd.framedblocks.common.FBContent;

@SuppressWarnings({ "SameParameterValue", "UnusedReturnValue" })
public class FramedItemModelProvider extends ItemModelProvider
{
    public FramedItemModelProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, FramedConstants.MOD_ID, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        handheldItem(FBContent.itemFramedHammer);
        handheldItem(FBContent.itemFramedWrench);
        handheldItem(FBContent.itemFramedKey);
        handheldItem(FBContent.itemFramedScrewdriver);

        ItemModelBuilder modelNormal = simpleItem(FBContent.itemFramedBlueprint);
        ModelFile modelWritten = simpleItem("framed_blueprint_written");

        modelNormal.override()
                    .predicate(BlueprintPropertyOverride.HAS_DATA, 0)
                    .model(modelNormal)
                    .end()
                .override()
                    .predicate(BlueprintPropertyOverride.HAS_DATA, 1)
                    .model(modelWritten)
                    .end();
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item)
    {
        String name = item.getId().getPath();
        return singleTexture(name, mcLoc("item/handheld"), "layer0", modLoc("item/" + name));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item)
    {
        return simpleItem(item.getId().getPath());
    }

    private ItemModelBuilder simpleItem(String name)
    {
        return singleTexture(name, mcLoc("item/generated"), "layer0", modLoc("item/" + name));
    }
}