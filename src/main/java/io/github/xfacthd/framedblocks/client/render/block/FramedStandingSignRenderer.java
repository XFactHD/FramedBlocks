package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.WallAndGroundTransformations;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.blockentity.state.StandingSignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public final class FramedStandingSignRenderer extends AbstractSignRenderer<StandingSignRenderState> {
    private static final Vector3fc TEXT_OFFSET = new Vector3f(0.0F, 0.33333334F, 0.066666667F);
    private static final WallAndGroundTransformations<SignRenderState.SignTransformations> TRANSFORMATIONS = new WallAndGroundTransformations<>(
            FramedStandingSignRenderer::createWallTransformation, FramedStandingSignRenderer::createGroundTransformation, 16
    );

    public FramedStandingSignRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public StandingSignRenderState createRenderState() {
        return new StandingSignRenderState();
    }

    @Override
    public void extractRenderState(
            SignBlockEntity blockEntity,
            StandingSignRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        BlockState blockState = blockEntity.getBlockState();
        state.attachmentType = PlainSignBlock.getAttachmentPoint(blockState);
        if (blockState.getBlock() instanceof WallSignBlock) {
            state.transformations = TRANSFORMATIONS.wallTransformation(blockState.getValue(WallSignBlock.FACING));
        } else {
            state.transformations = TRANSFORMATIONS.freeTransformations(blockState.getValue(StandingSignBlock.ROTATION));
        }
    }

    private static Transformation textTransformation(PlainSignBlock.Attachment attachmentType, float angle, boolean isFrontText) {
        Matrix4f result = new Matrix4f().translate(0.5F, 0.5F, 0.5F).rotate(Axis.YP.rotationDegrees(-angle));
        if (attachmentType == PlainSignBlock.Attachment.WALL) {
            result.translate(0.0F, -0.3125F, -0.4375F);
        }

        if (!isFrontText) {
            result.rotate(Axis.YP.rotationDegrees(180.0F));
        }

        float scale = 0.010416667F;
        return new Transformation(result.translate(TEXT_OFFSET).scale(scale, -scale, scale));
    }

    private static SignRenderState.SignTransformations createTransformations(PlainSignBlock.Attachment attachmentType, float angle) {
        return new SignRenderState.SignTransformations(textTransformation(attachmentType, angle, true), textTransformation(attachmentType, angle, false));
    }

    private static SignRenderState.SignTransformations createGroundTransformation(int segment) {
        return createTransformations(PlainSignBlock.Attachment.GROUND, RotationSegment.convertToDegrees(segment));
    }

    private static SignRenderState.SignTransformations createWallTransformation(Direction direction) {
        return createTransformations(PlainSignBlock.Attachment.WALL, direction.toYRot());
    }
}
