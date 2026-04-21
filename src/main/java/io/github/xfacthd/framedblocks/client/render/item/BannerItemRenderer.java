package io.github.xfacthd.framedblocks.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.render.block.FramedBannerRenderer;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.SpecialBlockModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BannerItemRenderer implements SpecialModelRenderer<CamoContainer<?, ?>> {
    private static final Vector3fc EXTENT_MIN = new Vector3f(0, 0, 0);
    private static final Vector3fc EXTENT_MAX = new Vector3f(0, 2, 1);

    private final Supplier<FramedBannerRenderer> renderer;

    public BannerItemRenderer(Supplier<FramedBannerRenderer> renderer) {
        this.renderer = Lazy.of(renderer);
    }

    @Override
    public void submit(@Nullable CamoContainer<?, ?> argument, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        argument = Objects.requireNonNullElse(argument, EmptyCamoContainer.EMPTY);
        renderer.get().submitSpecial(argument, poseStack, collector, lightCoords, overlayCoords, outlineColor);
    }

    @Override
    public CamoContainer<?, ?> extractArgument(ItemStack stack) {
        return CamoList.get(stack).getCamo(0);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(EXTENT_MIN);
        output.accept(EXTENT_MAX);
    }

    public static SpecialBlockModelWrapper.Unbaked<CamoContainer<?, ?>> createBlockModel(BlockState state) {
        return new SpecialBlockModelWrapper.Unbaked<>(new Unbaked(state), Optional.empty());
    }

    public record Unbaked(BlockState state) implements SpecialModelRenderer.Unbaked<CamoContainer<?, ?>> {
        public static final Identifier ID = Utils.id("banner");
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        public Unbaked() {
            this(FBContent.BLOCK_FRAMED_BANNER.value().defaultBlockState());
        }

        @Override
        public BannerItemRenderer bake(BakingContext context) {
            return new BannerItemRenderer(() -> new FramedBannerRenderer(context, state));
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
