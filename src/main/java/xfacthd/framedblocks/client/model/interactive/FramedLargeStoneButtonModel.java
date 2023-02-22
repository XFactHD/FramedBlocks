package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedLargeStoneButtonModel extends FramedLargeButtonModel
{
    private static final ResourceLocation FRAME_LOCATION = Utils.rl("block/framed_large_stone_button_frame");
    private static final ResourceLocation FRAME_PRESSED_LOCATION = Utils.rl("block/framed_large_stone_button_pressed_frame");

    private static BakedModel frameNormalModel;
    private static BakedModel framePressedModel;

    private final BakedModel frameModel;
    private final int rotX;
    private final int rotY;

    public FramedLargeStoneButtonModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.frameModel = pressed ? framePressedModel : frameNormalModel;

        this.rotX = switch (face)
        {
            case FLOOR -> 0;
            case WALL -> 90;
            case CEILING -> 180;
        };
        this.rotY = (int)-dir.toYRot();
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType layer)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData == null || fbData.getCamoState().isAir()) { return; }

        List<BakedQuad> quads = frameModel.getQuads(state, null, rand, data, layer);
        addRotatedQuads(quads, quadMap.get(null));

        for (Direction side : Direction.values())
        {
            quads = frameModel.getQuads(state, side, rand, data, layer);
            addRotatedQuads(quads, quadMap.get(side));
        }
    }

    private void addRotatedQuads(List<BakedQuad> source, List<BakedQuad> dest)
    {
        if (face == AttachFace.FLOOR && Utils.isZ(dir))
        {
            dest.addAll(source);
            return;
        }

        for (BakedQuad quad : source)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.rotateCentered(Direction.Axis.X, rotX, false))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotY, false))
                    .export(dest);
        }
    }

    @Override
    protected boolean useBaseModel() { return true; }



    public static void registerFrameModels(ModelEvent.RegisterAdditional event)
    {
        event.register(FRAME_LOCATION);
        event.register(FRAME_PRESSED_LOCATION);
    }

    public static void cacheFrameModels(Map<ResourceLocation, BakedModel> registry)
    {
        frameNormalModel = registry.get(FRAME_LOCATION);
        framePressedModel = registry.get(FRAME_PRESSED_LOCATION);
    }
}
