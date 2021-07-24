package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.FramedBlocks;

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
    }
}