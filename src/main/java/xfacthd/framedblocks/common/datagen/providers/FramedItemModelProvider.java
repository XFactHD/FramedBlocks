package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
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

    }
}