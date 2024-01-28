package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public final class ReinforcementModel
{
    public static final ResourceLocation LOCATION = Utils.rl("block/framed_reinforcement");
    private static final RandomSource RAND = RandomSource.create();
    private static final BakedQuad[] QUADS_PER_FACE = new BakedQuad[6];
    private static final List<BakedQuad> ALL_QUADS = new ArrayList<>(6);

    public static BakedQuad getQuad(Direction side)
    {
        return QUADS_PER_FACE[side.ordinal()];
    }

    public static List<BakedQuad> getAllQuads()
    {
        return ALL_QUADS;
    }

    public static void reload(Map<ResourceLocation, BakedModel> models)
    {
        ALL_QUADS.clear();
        BakedModel model = Objects.requireNonNull(models.get(LOCATION));
        Utils.forAllDirections(false, dir ->
        {
            RAND.setSeed(42);
            List<BakedQuad> quads = model.getQuads(null, dir, RAND, ModelData.EMPTY, RenderType.cutout());
            if (quads.isEmpty())
            {
                throw new IllegalStateException("Expected at least one quad on side %s, got none".formatted(dir));
            }

            BakedQuad quad = quads.get(0);
            QUADS_PER_FACE[dir.ordinal()] = quad;
            ALL_QUADS.add(quad);
        });
    }



    private ReinforcementModel() { }
}
