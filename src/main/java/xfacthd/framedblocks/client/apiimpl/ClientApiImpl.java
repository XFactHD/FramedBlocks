package xfacthd.framedblocks.client.apiimpl;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.data.ConTexDataHandler;
import xfacthd.framedblocks.client.model.QuadTable;
import xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import xfacthd.framedblocks.client.render.special.GhostBlockRenderer;
import xfacthd.framedblocks.client.render.color.FramedBlockColor;
import xfacthd.framedblocks.client.overlaygen.OverlayQuadGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public final class ClientApiImpl implements FramedBlocksClientAPI
{
    @Override
    public BlockColor defaultBlockColor()
    {
        return FramedBlockColor.INSTANCE;
    }

    @Override
    public void registerOutlineRender(IBlockType type, OutlineRenderer render)
    {
        BlockOutlineRenderer.registerOutlineRender(type, render);
    }

    @Override
    public void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Block... blocks)
    {
        GhostBlockRenderer.registerBehaviour(behaviour, blocks);
    }

    @Override
    public void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Item... items)
    {
        GhostBlockRenderer.registerBehaviour(behaviour, items);
    }

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
