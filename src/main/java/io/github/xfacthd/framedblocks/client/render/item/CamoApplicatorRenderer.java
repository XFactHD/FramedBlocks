package io.github.xfacthd.framedblocks.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorConfig;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorContent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class CamoApplicatorRenderer implements SpecialModelRenderer<CamoApplicatorRenderer.CamoIcon> {
    private static final Identifier DUMMY_TEXTURE = Utils.id("block/camo_applicator_dummy");

    private final Lazy<CamoIcon> dummyIcon;

    private CamoApplicatorRenderer(SpriteGetter sprites) {
        this.dummyIcon = Lazy.of(() -> {
            TextureAtlasSprite sprite = sprites.get(new SpriteId(ClientUtils.BLOCK_ATLAS, DUMMY_TEXTURE));
            return new CamoIcon(new Material.Baked(sprite, false), -1);
        });
    }

    @Override
    public void submit(
            @Nullable CamoIcon camoIcon,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light,
            int overlay,
            boolean hasFoil,
            int outlineColor
    ) {
        if (camoIcon == null) {
            return;
        }

        TextureAtlasSprite sprite = camoIcon.material.sprite();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        RenderType renderType;
        if (camoIcon.material.forceTranslucent() || sprite.contents().transparency().hasTranslucent()) {
            renderType = Sheets.translucentBlockItemSheet();
        } else {
            renderType = Sheets.cutoutBlockItemSheet();
        }

        for (int i = 0; i < 4; i++) {
            poseStack.pushPose();

            poseStack.translate(.5, .5, .5);
            poseStack.mulPose(Axis.YP.rotationDegrees(90F * i));

            submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                buffer.addVertex(pose, -2.5F/16F,  4.5F/16F, 3.55F/16F).setColor(camoIcon.tint).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(pose, 0, 0, 1);
                buffer.addVertex(pose, -2.5F/16F, -0.5F/16F, 3.55F/16F).setColor(camoIcon.tint).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(pose, 0, 0, 1);
                buffer.addVertex(pose,  2.5F/16F, -0.5F/16F, 3.55F/16F).setColor(camoIcon.tint).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(pose, 0, 0, 1);
                buffer.addVertex(pose,  2.5F/16F,  4.5F/16F, 3.55F/16F).setColor(camoIcon.tint).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(pose, 0, 0, 1);
            });

            poseStack.popPose();
        }
    }

    @Override
    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
    public @Nullable CamoIcon extractArgument(ItemStack stack) {
        CamoApplicatorConfig config = CamoApplicatorConfig.of(stack);
        CamoApplicatorContent.CamoEntry camoEntry = CamoApplicatorContent.of(stack).getCamoEntry(config.selectedSlot());
        if (camoEntry.getStack().isEmpty()) {
            return null;
        }

        CamoApplicatorContent.CamoEntry.DummyContent dummyContent = camoEntry.getDummyContent();
        if (dummyContent == CamoApplicatorContent.CamoEntry.DummyContent.EMPTY) {
            return null;
        }
        if (dummyContent == CamoApplicatorContent.CamoEntry.DummyContent.UNKNOWN) {
            return dummyIcon.get();
        }

        CamoContent content = dummyContent.content();
        CamoContentClientHandler clientHandler = content.getClientHandler();
        return new CamoIcon(
                clientHandler.getOrCreateModel(content).particleMaterial(),
                clientHandler.getParticleTintValue(content)
        );
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) { }

    public record CamoIcon(Material.Baked material, int tint) { }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<CamoIcon> {
        public static final Identifier ID = Utils.id("camo_applicator");
        public static final MapCodec<CamoApplicatorRenderer.Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public CamoApplicatorRenderer bake(BakingContext context) {
            return new CamoApplicatorRenderer(context.sprites());
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
