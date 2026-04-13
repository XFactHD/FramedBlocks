package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.geometry.QuadListModifier;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.interactive.FramedFlowerPotBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedFlowerPotBlockEntity;
import io.github.xfacthd.framedblocks.common.compat.amendments.AmendmentsCompat;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public class FramedFlowerPotGeometry extends Geometry {
    public static final Identifier HANGING_MODEL_LOCATION = Utils.id("block/hanging_pot_rope");
    public static final String HANGING_MODEL_KEY = "flower_pot_rope";
    private static final BlockState DIRT_STATE = Blocks.DIRT.defaultBlockState();
    private static final Identifier POT_TEXTURE = Utils.id("minecraft", "block/flower_pot");
    private static final Identifier DIRT_TEXTURE = Utils.id("minecraft", "block/dirt");
    private static final QuadListModifier DIRT_UP_MODIFIER = QuadListModifier.replacing(quad ->
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                    .apply(Modifiers.setPosition(4F/16F))
                    .exportDirect()
    );
    private static final QuadListModifier DIRT_DOWN_MODIFIER = QuadListModifier.replacing(quad ->
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                    .apply(Modifiers.setPosition(15F/16F))
                    .exportDirect()
    );
    private static final QuadListModifier DIRT_HOR_MODIFIER = QuadListModifier.replacing(quad ->
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(6F / 16F, 1F / 16F, 10F / 16F, 4F / 16F))
                    .apply(Modifiers.setPosition(10F / 16F))
                    .exportDirect()
    );
    private static final QuadListModifier PLANT_MODIFIER = QuadListModifier.filtering(quad ->
            ClientUtils.isTexture(quad, POT_TEXTURE) || ClientUtils.isTexture(quad, DIRT_TEXTURE)
    );

    private final BlockState state;
    private final boolean hanging;
    @Nullable
    private final BlockStateModel hangingPotModel;

    public FramedFlowerPotGeometry(GeometryFactory.Context ctx) {
        this.state = ctx.state();
        this.hanging = AmendmentsCompat.isLoaded() && ctx.state().getValue(PropertyHolder.HANGING);
        this.hangingPotModel = hanging ? ctx.auxModels().getModel(HANGING_MODEL_KEY) : null;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        if (quad.direction() == Direction.DOWN) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(5F/16F, 5F/16F, 11F/16F, 11F/16F))
                    .export(quadMap, Direction.DOWN);
        } else if (quad.direction() == Direction.UP) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(5F/16F, 5F/16F, 11F/16F, 6F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(5F/16F, 10F/16F, 11F/16F, 11F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(5F/16F, 6F/16F, 6F/16F, 10F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(10F/16F, 6F/16F, 11F/16F, 10F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);
        } else if (!DirUtils.isY(quad.direction())) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(5F/16F, 0, 11F/16F, 6F/16F))
                    .apply(Modifiers.setPosition(11F/16F))
                    .export(quadMap, null);

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(6F/16F, 1F/16F, 10F/16F, 6F/16F))
                    .apply(Modifiers.setPosition(6F/16F))
                    .export(quadMap, null);
        }
    }

    @Override
    public void collectAdditionalPartsCached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        BlockState potState = switch (cacheKeyUserData) {
            case BlockState keyState -> keyState;
            case CompoundKey(BlockState keyState, _) -> keyState;
            case null, default -> null;
        };
        if (potState != null) {
            BlockStateModel potModel = ModelUtils.getModel(potState);
            consumer.acceptAll(potModel, level, pos, random, potState, true, false, false, potState, PLANT_MODIFIER);
        }

        boolean camoOccludes = blockData.getCamoContent().canOcclude();
        BlockStateModel dirtModel = ModelUtils.getModel(DIRT_STATE);
        consumer.acceptAll(dirtModel, level, pos, random, DIRT_STATE, false, true, false, DIRT_STATE, (quadMap, quads, side) -> {
            if ((camoOccludes && side != Direction.UP) || side == null) {
                quads.clear();
                return;
            }

            QuadListModifier mod = switch (side) {
                case UP -> DIRT_UP_MODIFIER;
                case DOWN -> DIRT_DOWN_MODIFIER;
                default -> DIRT_HOR_MODIFIER;
            };
            mod.modify(quadMap, quads, side);
        });

        if (hanging && hangingPotModel != null) {
            consumer.acceptAll(hangingPotModel, level, pos, random, state, true, false, false, null, null);
        }
    }

    @Override
    public @Nullable Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data) {
        Block flower = data.get(FramedFlowerPotBlockEntity.FLOWER_BLOCK);
        if (flower == null || flower == Blocks.AIR) {
            return null;
        }

        BlockState potState = FramedFlowerPotBlock.getFlowerPotState(flower);
        Object geoKey = ModelUtils.getGeometryKeyFiltered(ModelUtils.getModel(potState), level, pos, potState, random);
        return geoKey != null ? new CompoundKey(potState, geoKey) : potState;
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return true;
    }

    private record CompoundKey(BlockState flowerPot, Object geoKey) { }
}
