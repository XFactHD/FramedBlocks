package xfacthd.framedblocks.common.compat.buildinggadgets;

/*import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileDataSerializer;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileEntityData;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.MaterialList;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

final class FramedBlockEntityDataSerializer implements ITileDataSerializer
{
    FramedBlockEntityDataSerializer() { }

    @Override
    public CompoundTag serialize(ITileEntityData data, boolean persisted)
    {
        Preconditions.checkArgument(data instanceof FramedBlockEntityData);
        FramedBlockEntityData tileData = (FramedBlockEntityData) data;
        CompoundTag res = new CompoundTag();
        res.put("data", tileData.getNBT());
        if (tileData.getRequiredMaterials() != null)
        {
            res.put("materials", tileData.getRequiredMaterials().serialize(persisted));
        }

        return res;
    }

    @Override
    public ITileEntityData deserialize(CompoundTag tagCompound, boolean persisted)
    {
        CompoundTag data = tagCompound.getCompound("data");
        MaterialList materialList = null;
        if (tagCompound.contains("materials", Tag.TAG_COMPOUND))
        {
            CompoundTag materialNbt = tagCompound.getCompound("materials");
            if (materialNbt.getList("data", Tag.TAG_COMPOUND).isEmpty())
            {
                materialNbt.getCompound("data").putString("serializer", materialNbt.getString("serializer"));
                materialNbt = materialNbt.getCompound("data");
            }
            materialList = MaterialList.deserialize(materialNbt, persisted);
        }

        return new FramedBlockEntityData(data, materialList);
    }
}*/
