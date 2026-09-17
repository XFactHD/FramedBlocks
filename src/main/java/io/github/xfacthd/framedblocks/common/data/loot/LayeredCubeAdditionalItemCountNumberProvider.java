package io.github.xfacthd.framedblocks.common.data.loot;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

public final class LayeredCubeAdditionalItemCountNumberProvider implements ContextIntProvider {
    @Override
    public int getIntUnsafe(LootContext ctx) {
        BlockState state = ctx.getOptional(LootContextParams.BLOCK_STATE);
        if (state != null && state.hasProperty(BlockStateProperties.LAYERS)) {
            return state.getValue(BlockStateProperties.LAYERS) - 1;
        }
        return 0;
    }

    @Override
    public void validate(ValidationContext context) { }

    @Override
    public MapCodec<LayeredCubeAdditionalItemCountNumberProvider> codec() {
        return FBContent.LAYERED_CUBE_ADDITIONAL_ITEM_COUNT_NUMBER_PROVIDER.value();
    }
}
