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
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.model.util.ModelCache;

import java.util.List;
import java.util.Map;

public class FramedRedstoneWallTorchModel extends FramedBlockModel
{
    private static final Vector3f ROTATION_ORIGIN = new Vector3f(0, 3.5F/16F, 8F/16F);
    private static final float MIN = 7F/16F;
    private static final float MAX = 9F/16F;
    private static final float TOP = 11.5F/16F;
    private static final float BOTTOM = 12.5F/16F;

    private final Direction dir;
    private final boolean lit;

    public FramedRedstoneWallTorchModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.lit = state.getValue(BlockStateProperties.LIT);
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.REDSTONE_WALL_TORCH.defaultBlockState(), rand, extraData);
    }

    @Override
    protected void getAdditionalQuads(
            Map<Direction, List<BakedQuad>> quadMap,
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
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        /*
        "from": [-1, 3.5, 7],
		"to": [1, 11.5, 9],
        "rotation": {"angle": -22.5, "axis": "z", "origin": [0, 3.5, 8]},
        */

        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            float top = lit ? (TOP - (1F/16F)) : TOP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(MIN, MIN, MAX, MAX))
                    .apply(Modifiers.setPosition(quadDir == Direction.UP ? top : BOTTOM))
                    .apply(Modifiers.offset(Direction.WEST, .5F))
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
                    .export(quadMap.get(null));
        }
    }

    @Override
    protected void postProcessQuads(Map<Direction, List<BakedQuad>> quadMap)
    {
        float yAngle = 270F - dir.toYRot();
        quadMap.get(null).forEach(q ->
                QuadModifier.geometry(q)
                        .apply(Modifiers.rotate(Direction.Axis.Z, ROTATION_ORIGIN, -22.5F, false))
                        .apply(Modifiers.rotateCentered(Direction.Axis.Y, yAngle, false))
                        .modifyInPlace()
        );
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return false;
    }
}