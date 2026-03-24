package io.github.xfacthd.framedblocks.common.data.loot;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public final class LayeredCubeAdditionalItemCountNumberProvider implements NumberProvider
{
    public static final LayeredCubeAdditionalItemCountNumberProvider INSTANCE = new LayeredCubeAdditionalItemCountNumberProvider();

    private LayeredCubeAdditionalItemCountNumberProvider() { }

    @Override
    public float getFloat(LootContext ctx)
    {
        BlockState state = ctx.getParameter(LootContextParams.BLOCK_STATE);
        if (state.hasProperty(BlockStateProperties.LAYERS))
        {
            return state.getValue(BlockStateProperties.LAYERS) - 1;
        }
        return 0;
    }

    @Override
    public MapCodec<LayeredCubeAdditionalItemCountNumberProvider> codec()
    {
        return FBContent.LAYERED_CUBE_ADDITIONAL_ITEM_COUNT_NUMBER_PROVIDER.value();
    }
}
