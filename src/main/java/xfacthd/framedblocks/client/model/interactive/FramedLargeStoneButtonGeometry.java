package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.List;

public class FramedLargeStoneButtonGeometry extends FramedLargeButtonGeometry
{
    private static final ResourceLocation FRAME_LOCATION = Utils.rl("block/framed_large_stone_button_frame");
    private static final ResourceLocation FRAME_PRESSED_LOCATION = Utils.rl("block/framed_large_stone_button_pressed_frame");

    private final BakedModel frameModel;
    private final int rotX;
    private final int rotY;

    public FramedLargeStoneButtonGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
        this.frameModel = ctx.modelAccessor().get(pressed ? FRAME_PRESSED_LOCATION : FRAME_LOCATION);

        this.rotX = switch (face)
        {
            case FLOOR -> 0;
            case WALL -> 90;
            case CEILING -> 180;
        };
        this.rotY = (int)-dir.toYRot();
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType layer
    )
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
    public boolean useBaseModel()
    {
        return true;
    }



    public static void registerFrameModels(ModelEvent.RegisterAdditional event)
    {
        event.register(FRAME_LOCATION);
        event.register(FRAME_PRESSED_LOCATION);
    }
}
