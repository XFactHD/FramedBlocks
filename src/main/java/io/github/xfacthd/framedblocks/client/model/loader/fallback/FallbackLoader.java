package io.github.xfacthd.framedblocks.client.model.loader.fallback;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.neoforged.neoforge.client.model.UnbakedModelParser;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public final class FallbackLoader implements UnbakedModelLoader<UnbakedModel>
{
    public static final Identifier ID = Utils.id("fallback");
    private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
    public static final Identifier EMPTY_FALLBACK = Utils.id("builtin/empty");

    @Override
    public UnbakedModel read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        JsonArray conditionArray = GsonHelper.getAsJsonArray(json, ConditionalOps.DEFAULT_CONDITIONS_KEY);
        List<ICondition> conditions = ICondition.LIST_CODEC.decode(JsonOps.INSTANCE, conditionArray).getOrThrow(
                err -> new JsonParseException("Failed to parse conditions: " + err)
        ).getFirst();

        if (conditions.stream().allMatch(cond -> cond.test(ICondition.IContext.EMPTY)))
        {
            json.remove("loader");
            return ctx.deserialize(json, BlockModel.class);
        }

        Identifier fallback = Identifier.parse(GsonHelper.getAsString(json, "fallback"));
        // Missing model cannot be used as fallback due to sprite resolution of minecraft:missingno incorrectly selecting the item atlas
        if (fallback.equals(MissingCuboidModel.LOCATION))
        {
            throw new JsonParseException("Cannot use minecraft:builtin/missing as fallback model");
        }
        if (fallback.equals(EMPTY_FALLBACK))
        {
            return EmptyModel.INSTANCE;
        }
        fallback = MODEL_LISTER.idToFile(fallback);
        try
        {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(fallback);
            try (Reader reader = resource.openAsReader())
            {
                return UnbakedModelParser.parse(reader);
            }
        }
        catch (IOException e)
        {
            throw new JsonParseException("Failed to parse fallback model", e);
        }
    }

    private static final class EmptyModel implements UnbakedModel
    {
        static final EmptyModel INSTANCE = new EmptyModel();
        private static final TextureSlots.Data TEXTURE_SLOTS = new TextureSlots.Data.Builder()
                .addTexture("particle", new Material(Utils.id("neoforge", "white")))
                .build();

        @Override
        public UnbakedGeometry geometry()
        {
            return UnbakedGeometry.EMPTY;
        }

        @Override
        public TextureSlots.Data textureSlots()
        {
            return TEXTURE_SLOTS;
        }
    }
}
