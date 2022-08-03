package xfacthd.framedblocks.client.loader.overlay;

import com.google.gson.*;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import xfacthd.framedblocks.api.util.Utils;

public class OverlayLoader implements IGeometryLoader<OverlayGeometry>
{
    public static final ResourceLocation ID = Utils.rl("overlay");
    private static final Vector3f CENTER = new Vector3f(.5F, .5F, .5F);
    private static final Vector3f DEFAULT_SCALE = new Vector3f(1.001F, 1.001F, 1.001F);

    @Override
    public OverlayGeometry read(JsonObject obj, JsonDeserializationContext ctx) throws JsonParseException
    {
        BlockModel model = ctx.deserialize(GsonHelper.getAsJsonObject(obj, "model"), BlockModel.class);

        Vector3f offset = Vector3f.ZERO;
        Vector3f scale = DEFAULT_SCALE.copy();

        if (obj.has("center"))
        {
            JsonArray arr = GsonHelper.getAsJsonArray(obj, "center");
            if (arr.size() != 3)
            {
                throw new JsonSyntaxException("Invalid center array, expected exactly three elements");
            }

            Vector3f center = new Vector3f(
                    GsonHelper.convertToFloat(arr.get(0), "center[0]") / 16F,
                    GsonHelper.convertToFloat(arr.get(1), "center[1]") / 16F,
                    GsonHelper.convertToFloat(arr.get(2), "center[2]") / 16F
            );

            offset = CENTER.copy();
            offset.sub(center);

            // Slightly skew the scale to avoid z-fighting on off-center models
            Vector3f scaleAdd = new Vector3f(.01F, .01F, .01F);
            scaleAdd.mul(Mth.abs(offset.x()), Mth.abs(offset.y()), Mth.abs(offset.z()));
            scale.add(scaleAdd);
        }

        return new OverlayGeometry(model, offset, scale);
    }
}
