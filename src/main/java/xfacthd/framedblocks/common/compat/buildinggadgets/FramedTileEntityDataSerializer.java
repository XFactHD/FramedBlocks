package xfacthd.framedblocks.common.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileDataSerializer;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileEntityData;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.MaterialList;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

class FramedTileEntityDataSerializer extends ForgeRegistryEntry<ITileDataSerializer> implements ITileDataSerializer
{
    FramedTileEntityDataSerializer() { }

    public CompoundNBT serialize(ITileEntityData data, boolean persisted)
    {
        Preconditions.checkArgument(data instanceof FramedTileEntityData);
        FramedTileEntityData tileData = (FramedTileEntityData) data;
        CompoundNBT res = new CompoundNBT();
        res.put("data", tileData.getNBT());
        if (tileData.getRequiredMaterials() != null)
        {
            res.put("materials", tileData.getRequiredMaterials().serialize(persisted));
        }

        return res;
    }

    public ITileEntityData deserialize(CompoundNBT tagCompound, boolean persisted)
    {
        CompoundNBT data = tagCompound.getCompound("data");
        MaterialList materialList = null;
        if (tagCompound.contains("materials", Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT materialNbt = tagCompound.getCompound("materials");
            if (materialNbt.getList("data", Constants.NBT.TAG_COMPOUND).isEmpty())
            {
                materialNbt.getCompound("data").putString("serializer", materialNbt.getString("serializer"));
                materialNbt = materialNbt.getCompound("data");
            }
            materialList = MaterialList.deserialize(materialNbt, persisted);
        }

        return new FramedTileEntityData(data, materialList);
    }
}