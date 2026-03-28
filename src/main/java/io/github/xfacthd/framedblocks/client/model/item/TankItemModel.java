package io.github.xfacthd.framedblocks.client.model.item;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public final class TankItemModel<T> implements ItemModel {
    private final FramedBlockItemModel baseModel;
    private final SpecialModelRenderer<T> renderer;

    private TankItemModel(FramedBlockItemModel baseModel, SpecialModelRenderer<T> renderer) {
        this.baseModel = baseModel;
        this.renderer = renderer;
    }

    @Override
    public void update(
            ItemStackRenderState state,
            ItemStack stack,
            ItemModelResolver resolver,
            ItemDisplayContext ctx,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed
    ) {
        baseModel.update(state, stack, resolver, ctx, level, owner, seed);

        CamoList camoList = stack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        if (camoList.getCamo(0).getContent().isSolid()) {
            return;
        }

        SimpleFluidContent fluid = stack.getOrDefault(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY);
        if (!fluid.isEmpty()) {
            state.setAnimated(); // Assume that all fluids use animated textures

            ItemStackRenderState.LayerRenderState specialLayer = state.newLayer();
            if (stack.hasFoil()) {
                specialLayer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            }
            specialLayer.setUsesBlockLight(true);
            specialLayer.setItemTransform(baseModel.getItemTransforms().getTransform(ctx));
            specialLayer.setupSpecialModel(renderer, renderer.extractArgument(stack));

            state.appendModelIdentityElement(fluid);
        }
    }

    public record Unbaked(FramedBlockItemModel.Unbaked base, SpecialModelRenderer.Unbaked<?> specialModel) implements ItemModel.Unbaked {
        public static final Identifier ID = Utils.id("tank");
        public static final MapCodec<TankItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ItemModels.CODEC.fieldOf("base").flatXmap(TankItemModel.Unbaked::validateBaseModel, DataResult::success).forGetter(TankItemModel.Unbaked::base),
                SpecialModelRenderers.CODEC.fieldOf("renderer").forGetter(TankItemModel.Unbaked::specialModel)
        ).apply(inst, TankItemModel.Unbaked::new));

        @Override
        public ItemModel bake(BakingContext ctx, Matrix4fc transformation) {
            SpecialModelRenderer<?> renderer = specialModel.bake(ctx);
            if (renderer != null) {
                return new TankItemModel<>(base.bake(ctx, transformation), renderer);
            }
            return ctx.missingItemModel();
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            base.resolveDependencies(resolver);
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        private static DataResult<FramedBlockItemModel.Unbaked> validateBaseModel(ItemModel.Unbaked baseModel) {
            if (baseModel instanceof FramedBlockItemModel.Unbaked unbaked) {
                return DataResult.success(unbaked);
            }
            return DataResult.error(() -> "Base model must be a FramedBlockItemModel");
        }
    }
}
