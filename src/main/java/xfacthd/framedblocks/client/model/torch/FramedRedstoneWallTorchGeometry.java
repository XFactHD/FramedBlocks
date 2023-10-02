package xfacthd.framedblocks.client.model.torch;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.model.util.ModelCache;

import java.util.List;

public class FramedRedstoneWallTorchGeometry implements Geometry
{
    private static final float MIN = 7F/16F;
    private static final float MAX = 9F/16F;
    private static final float TOP = 11.5F/16F;
    private static final float BOTTOM = 12.5F/16F;

    private final BakedModel baseModel;
    private final float yAngle;
    private final boolean lit;

    public FramedRedstoneWallTorchGeometry(GeometryFactory.Context ctx)
    {
        this.baseModel = ctx.baseModel();
        this.yAngle = 270F - ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        this.lit = ctx.state().getValue(BlockStateProperties.LIT);
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.REDSTONE_WALL_TORCH.defaultBlockState(), rand, extraData);
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
            BlockState state,
            RandomSource rand,
            ModelData extraData,
            RenderType renderType
    )
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, extraData, renderType);
        for (BakedQuad quad : quads)
        {
            if (!ClientUtils.isDummyTexture(quad))
            {
                quadMap.get(null).add(quad);
            }
        }
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            float top = lit ? (TOP - (1F/16F)) : TOP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(MIN, MIN, MAX, MAX))
                    .apply(Modifiers.setPosition(quadDir == Direction.UP ? top : BOTTOM))
                    .apply(Modifiers.offset(Direction.WEST, .5F))
                    .apply(FramedWallTorchGeometry.applyRotation(yAngle))
                    .export(quadMap.get(null));
        }
        else
        {
            boolean xAxis = Utils.isX(quadDir);
            boolean east = quadDir == Direction.EAST;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(MIN, 0, MAX, lit ? (7F/16F) : .5F))
                    .applyIf(Modifiers.setPosition(east ? 1F/16F : 17F/16F), xAxis)
                    .applyIf(Modifiers.setPosition(MAX), !xAxis)
                    .applyIf(Modifiers.offset(Direction.WEST, .5F), !xAxis)
                    .apply(Modifiers.offset(Direction.UP, 3.5F/16F))
                    .apply(FramedWallTorchGeometry.applyRotation(yAngle))
                    .export(quadMap.get(null));
        }
    }
}