package xfacthd.framedblocks.api.block.blockentity;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.camo.CamoContainer;

public interface IFramedDoubleBlockEntity
{
    ModelProperty<ModelData> DATA_ONE = new ModelProperty<>();
    ModelProperty<ModelData> DATA_TWO = new ModelProperty<>();

    CamoContainer<?, ?> getCamoTwo();
}
