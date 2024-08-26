package xfacthd.framedblocks.client.apiimpl;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;
import xfacthd.framedblocks.client.model.QuadTable;
import xfacthd.framedblocks.client.overlaygen.OverlayQuadGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public final class ClientApiImpl implements FramedBlocksClientAPI
{
    @Override
    public void addConTexProperty(ModelProperty<?> ctProperty)
    {
        ConTexDataHandler.addConTexProperty(ctProperty);
    }

    @Override
    public void generateOverlayQuads(QuadMap quadMap, Direction side, TextureAtlasSprite sprite)
    {
        generateOverlayQuads(quadMap, side, sprite, dir -> true);
    }

    @Override
    public void generateOverlayQuads(QuadMap quadMap, Direction side, TextureAtlasSprite sprite, Predicate<Direction> filter)
    {
        generateOverlayQuads(quadMap, side, dir -> sprite, filter);
    }

    @Override
    public void generateOverlayQuads(QuadMap quadMap, Direction side, Function<Direction, TextureAtlasSprite> spriteGetter, Predicate<Direction> filter)
    {
        QuadTable quadTable = (QuadTable) quadMap;

        List<BakedQuad> allQuads = quadTable.getAllQuads(side);
        if (allQuads.isEmpty()) return;

        List<BakedQuad> generatedQuads = OverlayQuadGenerator.generate(allQuads, spriteGetter, filter);
        if (generatedQuads.isEmpty()) return;

        List<BakedQuad> existingQuads = quadTable.get(side);
        ArrayList<BakedQuad> targetQuads = new ArrayList<>(existingQuads.size() + generatedQuads.size());
        Utils.copyAll(existingQuads, targetQuads);
        Utils.copyAll(generatedQuads, targetQuads);
        quadTable.put(side, targetQuads);
    }
}
