package xfacthd.framedblocks.client.loader.fallback;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.common.conditions.ICondition;
import xfacthd.framedblocks.api.util.Utils;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public final class FallbackLoader implements IGeometryLoader<FallbackGeometry>
{
    public static final ResourceLocation ID = Utils.rl("fallback");

    @Override
    public FallbackGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        JsonArray conditionArray = GsonHelper.getAsJsonArray(json, "conditions");
        List<ICondition> conditions = ICondition.LIST_CODEC.decode(JsonOps.INSTANCE, conditionArray).getOrThrow(
                err -> new JsonParseException("Failed to parse conditions: " + err)
        ).getFirst();

        if (conditions.stream().allMatch(cond -> cond.test(ICondition.IContext.EMPTY)))
        {
            json.remove("loader");
            return new FallbackGeometry(ctx.deserialize(json, BlockModel.class));
        }

        ResourceLocation fallback = new ResourceLocation(GsonHelper.getAsString(json, "fallback"));
        fallback = ModelBakery.MODEL_LISTER.idToFile(fallback);
        try
        {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(fallback);
            try (Reader reader = resource.openAsReader())
            {
                return new FallbackGeometry(BlockModel.fromStream(reader));
            }
        }
        catch (IOException e)
        {
            throw new JsonParseException("Failed to parse fallback model", e);
        }
    }
}
