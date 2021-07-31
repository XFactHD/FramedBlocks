package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.BlueprintPropertyOverride;

public class FramedItemModelProvider extends ItemModelProvider
{
    public FramedItemModelProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, FramedBlocks.MODID, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        singleTexture("framed_hammer", mcLoc("item/generated"), "layer0", modLoc("item/framed_hammer"));
        singleTexture("framed_wrench", mcLoc("item/generated"), "layer0", modLoc("item/framed_wrench"));

        ItemModelBuilder modelNormal = singleTexture("framed_blueprint", mcLoc("item/generated"), "layer0", modLoc("item/framed_blueprint"));
        ModelFile modelWritten = singleTexture("framed_blueprint_written", mcLoc("item/generated"), "layer0", modLoc("item/framed_blueprint_written"));

        modelNormal.override()
                    .predicate(BlueprintPropertyOverride.HAS_DATA, 0)
                    .model(modelNormal)
                    .end()
                .override()
                    .predicate(BlueprintPropertyOverride.HAS_DATA, 1)
                    .model(modelWritten)
                    .end();
    }
}