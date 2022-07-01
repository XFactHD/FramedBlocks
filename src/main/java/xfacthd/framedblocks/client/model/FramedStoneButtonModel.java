package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;

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
    protected boolean hasAdditionalQuadsInLayer(RenderType layer) { return layer == RenderType.cutout(); }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, IModelData data, RenderType layer)
    {
        BlockState camo = data.getData(FramedBlockData.CAMO);
        if (camo == null || camo.isAir()) { return; }

        List<BakedQuad> quads = frameModel.getQuads(state, null, rand, data);
        addRotatedQuads(quads, quadMap.get(null));

        for (Direction side : Direction.values())
        {
            quads = frameModel.getQuads(state, side, rand, data);
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
    protected BakedModel getCamoModel(BlockState camoState)
    {
        if (camoState.is(FBContent.blockFramedCube.get()))
        {
            return baseModel;
        }
        return super.getCamoModel(camoState);
    }

    @Override
    protected boolean forceUngeneratedBaseModel() { return true; }



    public static void registerFrameModels()
    {
        ForgeModelBakery.addSpecialModel(FRAME_LOCATION);
        ForgeModelBakery.addSpecialModel(FRAME_PRESSED_LOCATION);
    }

    public static void cacheFrameModels(Map<ResourceLocation, BakedModel> registry)
    {
        frameNormalModel = registry.get(FRAME_LOCATION);
        framePressedModel = registry.get(FRAME_PRESSED_LOCATION);
    }
}
