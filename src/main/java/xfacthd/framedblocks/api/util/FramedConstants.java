package xfacthd.framedblocks.api.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;

public final class FramedConstants
{
    public static final String MOD_ID = "framedblocks";
    public static final ResourceLocation CAMO_CONTAINER_FACTORY_REGISTRY_NAME = Utils.rl("camo_containers");
    public static final ResourceKey<Registry<CamoContainerFactory<?>>> CAMO_CONTAINER_FACTORY_REGISTRY_KEY = ResourceKey.createRegistryKey(CAMO_CONTAINER_FACTORY_REGISTRY_NAME);
    public static final ResourceLocation AUX_BLUEPRINT_DATA_TYPE_REGISTRY_NAME = Utils.rl("aux_blueprint_data");
    public static final ResourceKey<Registry<AuxBlueprintData.Type<?>>> AUX_BLUEPRINT_DATA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(AUX_BLUEPRINT_DATA_TYPE_REGISTRY_NAME);
    public static final String IMC_METHOD_ADD_PROPERTY = "add_ct_property";



    private FramedConstants() { }
}
