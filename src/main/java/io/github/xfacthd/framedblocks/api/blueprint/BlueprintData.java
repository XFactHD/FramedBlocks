package io.github.xfacthd.framedblocks.api.blueprint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.CamoPrinter;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.MoreCommonComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/// Describes a framed block being copied by a Framed Blueprint.
///
/// @param block      The block being copied
/// @param camos      The camos applied to the copied block
/// @param overlay    The block overlay applied to the copied block
/// @param glowing    Whether the copied block has the light emission modifier
/// @param intangible Whether the copied block has the intangibibility modifier
/// @param reinforced Whether the copied block has the reinforcement modifier
/// @param emissive   Whether the copied block has the emissivity (fullbright) modifier
/// @param blockState The blockstate properties being copied exactly
/// @param customData Additional data copied from the block's BE
public record BlueprintData(
        Block block,
        CamoList camos,
        Optional<Holder<BlockOverlay>> overlay,
        boolean glowing,
        boolean intangible,
        boolean reinforced,
        boolean emissive,
        BlockItemStateProperties blockState,
        Optional<TypedDataComponent<?>> customData
) implements TooltipProvider {
    private static final Codec<TypedDataComponent<?>> CUSTOM_DATA_CODEC = DataComponentType.PERSISTENT_CODEC
            .dispatch(TypedDataComponent::type, BlueprintData::makeCustomDataValueCodec);
    public static final Codec<BlueprintData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlueprintData::block),
            CamoList.CODEC.optionalFieldOf("camos", CamoList.EMPTY).forGetter(BlueprintData::camos),
            BlockOverlay.CODEC.optionalFieldOf("overlay").forGetter(BlueprintData::overlay),
            Codec.BOOL.optionalFieldOf("glowing", false).forGetter(BlueprintData::glowing),
            Codec.BOOL.optionalFieldOf("intangible", false).forGetter(BlueprintData::intangible),
            Codec.BOOL.optionalFieldOf("reinforced", false).forGetter(BlueprintData::reinforced),
            Codec.BOOL.optionalFieldOf("emissive", false).forGetter(BlueprintData::emissive),
            BlockItemStateProperties.CODEC.optionalFieldOf("blockstate", BlockItemStateProperties.EMPTY).forGetter(BlueprintData::blockState),
            CUSTOM_DATA_CODEC.optionalFieldOf("custom_data").forGetter(BlueprintData::customData)
    ).apply(inst, BlueprintData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK),
            BlueprintData::block,
            CamoList.STREAM_CODEC,
            BlueprintData::camos,
            ByteBufCodecs.optional(BlockOverlay.STREAM_CODEC),
            BlueprintData::overlay,
            ByteBufCodecs.BOOL,
            BlueprintData::glowing,
            ByteBufCodecs.BOOL,
            BlueprintData::intangible,
            ByteBufCodecs.BOOL,
            BlueprintData::reinforced,
            ByteBufCodecs.BOOL,
            BlueprintData::emissive,
            BlockItemStateProperties.STREAM_CODEC,
            BlueprintData::blockState,
            ByteBufCodecs.optional(TypedDataComponent.STREAM_CODEC),
            BlueprintData::customData,
            BlueprintData::new
    );
    public static final BlueprintData EMPTY = new BlueprintData(Blocks.AIR, CamoList.EMPTY, Optional.empty(), false, false, false, false, BlockItemStateProperties.EMPTY, Optional.empty());
    public static final String CONTAINED_BLOCK = "desc.framedblocks.blueprint_block";
    public static final String STORED_OVERLAY = Utils.translationKey("desc", "blueprint.overlay");
    public static final String IS_ILLUMINATED = "desc.framedblocks.blueprint_illuminated";
    public static final String IS_INTANGIBLE = "desc.framedblocks.blueprint_intangible";
    public static final String IS_REINFORCED = "desc.framedblocks.blueprint_reinforced";
    public static final String IS_EMISSIVE = "desc.framedblocks.blueprint_emissive";
    public static final String MISSING_MATERIALS = Utils.translationKey("desc", "blueprint_missing_materials");
    public static final Component BLOCK_INVALID = Utils.translate("desc", "blueprint_invalid").withStyle(ChatFormatting.RED);
    public static final Component OVERLAY_NONE = Utils.translate("desc", "blueprint.overlay.none").withStyle(ChatFormatting.RED);
    public static final Component FALSE = MoreCommonComponents.FALSE;
    public static final Component TRUE = MoreCommonComponents.TRUE;
    public static final Component CANT_COPY = Utils.translate("desc", "blueprint_cant_copy").withStyle(ChatFormatting.RED);

    /// {@return the custom data value if it's present and matches the given type, otherwise the given default}
    ///
    /// @param type     The expected type of the custom data
    /// @param _default The default value to return of custom data is absent or the type does not match
    public <T> T getCustomDataOrDefault(Supplier<DataComponentType<T>> type, T _default) {
        return getCustomDataOrDefault(type.get(), _default);
    }

    /// {@return the custom data value if it's present and matches the given type, otherwise the given default}
    ///
    /// @param type     The expected type of the custom data
    /// @param _default The default value to return of custom data is absent or the type does not match
    @SuppressWarnings("unchecked")
    public <T> T getCustomDataOrDefault(DataComponentType<T> type, T _default) {
        if (customData.isPresent() && customData.get().type() == type) {
            return (T) customData.get().value();
        }
        return _default;
    }

    /// {@return whether this blueprint data is empty}
    public boolean isEmpty() {
        return block.defaultBlockState().isAir();
    }

    /// Create a copy of this blueprint data with the given blockstate properties to copy.
    ///
    /// @param newBlockState The new properties to copy
    /// @return the new blueprint data
    public BlueprintData withBlockState(BlockItemStateProperties newBlockState) {
        return new BlueprintData(block, camos, overlay, glowing, intangible, reinforced, emissive, newBlockState, customData);
    }

    /// Create a copy of this blueprint data with the given custom data.
    ///
    /// @param type The component type of the custom data
    /// @param data The value of the custom data
    /// @return the new blueprint data
    public <T> BlueprintData withCustomData(Supplier<DataComponentType<T>> type, T data) {
        return withCustomData(type.get(), data);
    }

    /// Create a copy of this blueprint data with the given custom data.
    ///
    /// @param type The component type of the custom data
    /// @param data The value of the custom data
    /// @return the new blueprint data
    public <T> BlueprintData withCustomData(DataComponentType<T> type, T data) {
        return new BlueprintData(block, camos, overlay, glowing, intangible, reinforced, emissive, blockState, Optional.of(new TypedDataComponent<>(type, data)));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> appender, TooltipFlag tooltipFlag, DataComponentGetter componentGetter) {
        if (isEmpty()) {
            appender.accept(Component.translatable(CONTAINED_BLOCK, CamoPrinter.BLOCK_NONE).withStyle(ChatFormatting.GOLD));
            return;
        }

        Component blockName = block == Blocks.AIR ? BLOCK_INVALID : block.getName().withStyle(ChatFormatting.WHITE);
        CamoList camos = InternalAPI.INSTANCE.getBlueprintCopyBehavior(block).getCamos(this);
        Component overlayName = overlay.map(BlockOverlay::getName).orElse(OVERLAY_NONE);

        appender.accept(Component.translatable(CONTAINED_BLOCK, blockName).withStyle(ChatFormatting.GOLD));
        CamoPrinter.printCamoList(appender, camos, true);
        appender.accept(Component.translatable(STORED_OVERLAY, overlayName).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(IS_ILLUMINATED, glowing ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(IS_INTANGIBLE, intangible ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(IS_REINFORCED, reinforced ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));
        appender.accept(Component.translatable(IS_EMISSIVE, emissive ? TRUE : FALSE).withStyle(ChatFormatting.GOLD));

        // Prevent printing a wrapped BlueprintData (i.e. from the door)
        if (customData.isPresent() && customData.get().value() instanceof TooltipProvider tooltipProvider && !(tooltipProvider instanceof BlueprintData)) {
            tooltipProvider.addToTooltip(context, appender, tooltipFlag, componentGetter);
        }
    }

    private static <T> MapCodec<TypedDataComponent<T>> makeCustomDataValueCodec(DataComponentType<T> type) {
        return type.codecOrThrow().fieldOf("value").xmap(
                val -> TypedDataComponent.createUnchecked(type, val),
                TypedDataComponent::value
        );
    }
}
