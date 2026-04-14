package io.github.xfacthd.framedblocks.client.model.item;

import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.component.PaintRollerContents;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PaintRollerItemModel implements ItemModel {
    private static final CuboidModelElement ELEMENT_UNTINTED = makeCuboidElement(false);
    private static final CuboidModelElement ELEMENT_TINTED = makeCuboidElement(true);

    private final ItemModel baseModel;
    private final ModelRenderProperties properties;
    private final Matrix4fc transformation;
    private final ModelBaker baker;
    private final Map<BlockOverlay, QuadCollection> rollerQuads = new Reference2ObjectOpenHashMap<>();

    public PaintRollerItemModel(ItemModel baseModel, ModelRenderProperties properties, Matrix4fc transformation, ModelBaker baker) {
        this.baseModel = baseModel;
        this.properties = properties;
        this.transformation = transformation;
        this.baker = baker;
    }

    @Override
    public void update(
            ItemStackRenderState output,
            ItemStack item,
            ItemModelResolver resolver,
            ItemDisplayContext context,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed
    ) {
        baseModel.update(output, item, resolver, context, level, owner, seed);

        PaintRollerContents contents = PaintRollerContents.get(item);
        if (contents.hasOverlay()) {
            BlockOverlay overlay = Objects.requireNonNull(contents.overlay()).value();
            ItemStackRenderState.LayerRenderState layer = output.newLayer();

            QuadCollection quads = rollerQuads.computeIfAbsent(overlay, this::bakeRollerOverlayQuads);
            layer.prepareQuadList().addAll(quads.getAll());
            if (quads.hasMaterialFlag(BakedQuad.FLAG_ANIMATED)) {
                output.setAnimated();
            }

            int overlayTint = TintUtils.getOverlayDefaultTint(overlay);
            if (overlayTint != -1) {
                layer.tintLayers().add(overlayTint);
                output.appendModelIdentityElement(overlayTint);
            }

            layer.setLocalTransform(transformation);
            properties.applyToLayer(layer, context);
            output.appendModelIdentityElement(overlay);
        }
    }

    private QuadCollection bakeRollerOverlayQuads(BlockOverlay overlay) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        CuboidModelElement element = overlay.tintSource() != null ? ELEMENT_TINTED : ELEMENT_UNTINTED;
        Material.Baked material = baker.materials().get(new Material(overlay.solidTexture(), overlay.translucent()), () -> "");
        for (Map.Entry<Direction, CuboidFace> entry : element.faces().entrySet()) {
            builder.addUnculledFace(FaceBakery.bakeQuad(
                    baker,
                    element.from(),
                    element.to(),
                    entry.getValue(),
                    material,
                    entry.getKey(),
                    BlockModelRotation.IDENTITY,
                    element.rotation(),
                    true,
                    0
            ));
        }
        return builder.build();
    }

    private static CuboidModelElement makeCuboidElement(boolean tinted) {
        int tintIdx = tinted ? 0 : -1;
        CuboidFace.UVs uvs = new CuboidFace.UVs(0, 5, 16, 8);
        return new CuboidModelElement(
                new Vector3f( 0, 11, 6.5F),
                new Vector3f(16, 14F, 9.5F),
                Map.of(
                        Direction.NORTH, new CuboidFace(null, tintIdx, "overlay", uvs, Quadrant.R0),
                        Direction.SOUTH, new CuboidFace(null, tintIdx, "overlay", uvs, Quadrant.R0),
                        Direction.UP, new CuboidFace(null, tintIdx, "overlay", uvs, Quadrant.R0),
                        Direction.DOWN, new CuboidFace(null, tintIdx, "overlay", uvs, Quadrant.R0)
                )
        );
    }

    public record Unbaked(Identifier model) implements ItemModel.Unbaked {
        public static final Identifier ID = Utils.id("paint_roller");
        public static final MapCodec<Unbaked> CODEC = Identifier.CODEC.fieldOf("base_model")
                .xmap(Unbaked::new, Unbaked::model);

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, resolvedModel.getTopTextureSlots());
            ItemModel baseModel = new CuboidItemModelWrapper.Unbaked(this.model, Optional.empty(), List.of()).bake(context, transformation);
            return new PaintRollerItemModel(baseModel, properties, transformation, baker);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(model);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
