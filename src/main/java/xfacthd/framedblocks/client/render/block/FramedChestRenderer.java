package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.wrapping.TextureLookup;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.client.model.cube.FramedChestLidGeometry;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.cube.FramedChestBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.data.property.LatchType;

import java.util.Map;

public class FramedChestRenderer implements BlockEntityRenderer<FramedChestBlockEntity>
{
    private static final int DIRECTIONS = 4;
    private static final int CHEST_TYPES = ChestType.values().length;
    private static final int LATCH_TYPES = LatchType.values().length;
    private static final BakedModel[] LID_MODELS = new BakedModel[DIRECTIONS * CHEST_TYPES * LATCH_TYPES];
    private static final RandomSource RANDOM = RandomSource.create();

    @SuppressWarnings("unused")
    public FramedChestRenderer(BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(
            FramedChestBlockEntity be,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        BlockState state = be.getBlockState();

        var result = FramedChestBlock.combine(be, true);
        ChestState chestState = result.apply(FramedChestBlock.STATE_COMBINER);

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
        LatchType latch = state.getValue(PropertyHolder.LATCH_TYPE);

        long lastChange = result.apply(FramedChestBlock.OPENNESS_COMBINER).orElse(0L);

        BakedModel model = LID_MODELS[makeModelIndex(dir, type, latch)];
        //noinspection ConstantConditions
        ModelData data = model.getModelData(be.getLevel(), be.getBlockPos(), state, be.getModelData());

        float angle = calculateAngle(be, chestState, dir, lastChange, partialTicks);

        float xOff = Utils.isX(dir) ? (Utils.isPositive(dir) ? 1F/16F : 15F/16F) : 0;
        float zOff = Utils.isZ(dir) ? (Utils.isPositive(dir) ? 1F/16F : 15F/16F) : 0;

        poseStack.pushPose();

        poseStack.translate(xOff, 9F/16F, zOff);
        poseStack.mulPose(Utils.isX(dir) ? Axis.ZP.rotationDegrees(angle) : Axis.XN.rotationDegrees(angle));
        poseStack.translate(-xOff, -9F/16F, -zOff);

        renderLidModel(be, state, poseStack, buffer, model, data);

        poseStack.popPose();
    }

    private static void renderLidModel(
            FramedChestBlockEntity be,
            BlockState state,
            PoseStack matrix,
            MultiBufferSource buffer,
            BakedModel model,
            ModelData data
    )
    {
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

        int color = Minecraft.getInstance().getBlockColors().getColor(state, be.getLevel(), be.getBlockPos(), 0);
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        //noinspection ConstantConditions
        int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        RANDOM.setSeed(42);
        for (RenderType type : model.getRenderTypes(state, RANDOM, data))
        {
            RenderType bufferType = RenderTypeHelper.getEntityRenderType(type, false);

            renderer.renderModel(
                    matrix.last(),
                    buffer.getBuffer(bufferType),
                    state,
                    model,
                    red, green, blue,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    data,
                    type
            );
        }
    }

    private static float calculateAngle(
            FramedChestBlockEntity be, ChestState chestState, Direction dir, long lastChange, float partialTicks
    )
    {
        //noinspection ConstantConditions
        float diff = (float) (be.getLevel().getGameTime() - lastChange) + partialTicks;

        float factor = Mth.lerp(diff / 10F, 0, 1);
        if (chestState == ChestState.CLOSING) { factor = 1F - factor; }

        factor = 1.0F - factor;
        factor = 1.0F - factor * factor * factor;

        float angle = Mth.clamp(factor * 90F, 0F, 90F);
        if (!Utils.isPositive(dir)) { angle *= -1F; }

        return angle;
    }

    @Override
    public boolean shouldRender(FramedChestBlockEntity be, Vec3 camera)
    {
        ChestState state = FramedChestBlock.combine(be, true).apply(FramedChestBlock.STATE_COMBINER);
        return state != ChestState.CLOSED && BlockEntityRenderer.super.shouldRender(be, camera);
    }

    @Override
    public AABB getRenderBoundingBox(FramedChestBlockEntity blockEntity)
    {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - .25, pos.getY() + .5625, pos.getZ() - .25, pos.getX() + 1.25, pos.getY() + 1.5, pos.getZ() + 1.25);
    }



    public static void onModelsLoaded(Map<ModelResourceLocation, BakedModel> registry)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            for (ChestType type : ChestType.values())
            {
                for (LatchType latch : LatchType.values())
                {
                    BlockState state = FBContent.BLOCK_FRAMED_CHEST.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(BlockStateProperties.CHEST_TYPE, type)
                            .setValue(PropertyHolder.LATCH_TYPE, latch);

                    ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);

                    BakedModel model = registry.get(location);
                    if (model instanceof FramedBlockModel fbModel)
                    {
                        model = fbModel.getBaseModel();
                    }
                    GeometryFactory.Context ctx = new GeometryFactory.Context(state, model, registry::get, TextureLookup.runtime());
                    LID_MODELS[makeModelIndex(dir, type, latch)] = new FramedBlockModel(ctx, new FramedChestLidGeometry(ctx));
                }
            }
        }
    }

    private static int makeModelIndex(Direction dir, ChestType type, LatchType latch)
    {
        return dir.get2DDataValue() + (type.ordinal() * DIRECTIONS) + (latch.ordinal() * DIRECTIONS * CHEST_TYPES);
    }
}
