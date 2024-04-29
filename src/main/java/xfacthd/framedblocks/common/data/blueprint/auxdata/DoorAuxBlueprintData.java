package xfacthd.framedblocks.common.data.blueprint.auxdata;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.common.FBContent;

public record DoorAuxBlueprintData(BlueprintData data) implements AuxBlueprintData<DoorAuxBlueprintData>
{
    public static final MapCodec<DoorAuxBlueprintData> CODEC = BlueprintData.CODEC
            .xmap(DoorAuxBlueprintData::new, DoorAuxBlueprintData::data)
            .fieldOf("data");
    public static final StreamCodec<? super RegistryFriendlyByteBuf, DoorAuxBlueprintData> STREAM_CODEC = BlueprintData.STREAM_CODEC
            .map(DoorAuxBlueprintData::new, DoorAuxBlueprintData::data);
    public static final DoorAuxBlueprintData EMPTY = new DoorAuxBlueprintData(BlueprintData.EMPTY);

    @Override
    public Type<DoorAuxBlueprintData> type()
    {
        return FBContent.AUX_TYPE_DOOR_DATA.value();
    }
}
