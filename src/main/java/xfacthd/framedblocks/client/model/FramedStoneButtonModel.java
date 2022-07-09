package xfacthd.framedblocks.client.model;

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
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.*;

public class FramedStoneButtonModel extends FramedButtonModel
{
    private static final ResourceLocation FRAME_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/framed_stone_button_frame");
    private static final ResourceLocation FRAME_PRESSED_LOCATION = new ResourceLocation(FramedConstants.MOD_ID, "block/framed_stone_button_pressed_frame");

    private static BakedModel frameNormalModel;
    private static BakedModel framePressedModel;

    private final BakedModel frameModel;

    public FramedStoneButtonModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.frameModel = pressed ? framePressedModel : frameNormalModel;
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
        if (fbData != null && fbData.getCamoState().isAir()) { return; }

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

        int rotX;
        int rotY;

        if (face == AttachFace.WALL)
        {
            rotX = 90;
            if (Utils.isX(dir))
            {
                rotY = (int) (dir.toYRot() + 180) % 360;
            }
            else
            {
                rotY = (int)dir.toYRot();
            }
        }
        else
        {
            rotX = face == AttachFace.CEILING ? 180 : 0;
            rotY = (int)dir.toYRot();
        }

        for (BakedQuad quad : source)
        {
            quad = ModelUtils.duplicateQuad(quad);

            BakedQuadTransformer.rotateQuadAroundAxisCentered(quad, Direction.Axis.X, rotX, false);
            BakedQuadTransformer.rotateQuadAroundAxisCentered(quad, Direction.Axis.Y, rotY, false);

            dest.add(quad);
        }
    }

    @Override
    protected boolean forceUngeneratedBaseModel() { return true; }



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
