package io.github.xfacthd.framedblocks.client.model.item;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelDataProvider;
import io.github.xfacthd.framedblocks.api.model.item.RegisterItemModelDataProvidersEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.fml.ModLoader;

public final class ItemModelDataProviders {
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, ItemModelDataProvider> DATA_PROVIDERS = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<ItemModelDataProvider> CODEC = DATA_PROVIDERS.codec(Identifier.CODEC);

    public static void init() {
        ModLoader.postEvent(new RegisterItemModelDataProvidersEvent(DATA_PROVIDERS::put));
    }

    private ItemModelDataProviders() { }
}
