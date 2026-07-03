package io.github.xfacthd.framedblocks.api.datagen.loot.objects;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/// Loot condition determining whether the camo is retained on the dropped framed block.
public final class RetainCamoLootCondition implements LootItemCondition {
    public static final RetainCamoLootCondition INSTANCE = new RetainCamoLootCondition();
    public static final MapCodec<RetainCamoLootCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final LootItemCondition.Builder BUILDER = () -> INSTANCE;

    private RetainCamoLootCondition() { }

    @Override
    public boolean test(LootContext ctx) {
        if (ctx.getParameter(LootContextParams.TOOL).has(FramedConstants.Objects.DC_TYPE_RETAIN_CAMO)) {
            return true;
        }
        if (ctx.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IFramedBlockEntity be) {
            return !be.canTriviallyDropAllCamos();
        }
        return false;
    }

    @Override
    public MapCodec<RetainCamoLootCondition> codec() {
        return MAP_CODEC;
    }
}
