package io.github.xfacthd.framedblocks.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.component.FramedMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(MapItemSavedData.class)
@SuppressWarnings("MethodMayBeStatic")
public abstract class MixinMapItemSavedData implements FramedMap.MarkerRemover {
    @Unique
    private final Map<String, FramedMap> framedblocks$frameMarkers = new HashMap<>();

    @Shadow
    @Final
    private boolean trackingPosition;

    @Shadow
    @SuppressWarnings("SameParameterValue")
    protected abstract void addDecoration(Holder<MapDecorationType> pType, @Nullable LevelAccessor pLevel, String pDecorationName, double pLevelX, double pLevelZ, double pRotation, @Nullable Component pName);

    @Shadow
    protected abstract void removeDecoration(String pIdentifier);

    @Definition(id = "placedInFrame", local = @Local(name = "placedInFrame", type = ItemFrame.class))
    @Expression("placedInFrame != null")
    @ModifyExpressionValue(method = "tickCarriedBy", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean framedblocks$checkVanillaFramedOrCustomFramed(boolean isFramed, Player player, ItemStack stack) {
        return isFramed || stack.get(FBContent.DC_TYPE_FRAMED_MAP) != null;
    }

    @Definition(id = "placedInFrame", local = @Local(name = "placedInFrame", type = ItemFrame.class))
    @Expression("placedInFrame == null")
    @ModifyExpressionValue(method = "tickCarriedBy", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    private boolean framedblocks$checkNotVanillaFramedAndNotCustomFramed(boolean isNotFramed, Player player, ItemStack stack) {
        return isNotFramed && stack.get(FBContent.DC_TYPE_FRAMED_MAP) == null;
    }

    @Inject(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void framedblocks$updateFramedItemFrameMarker(Player player, ItemStack mapStack, @Nullable ItemFrame placedInFrame, CallbackInfo ci) {
        FramedMap framedMap;
        if (trackingPosition && (framedMap = mapStack.get(FBContent.DC_TYPE_FRAMED_MAP)) != null) {
            String frameId = FramedMap.makeFrameId(framedMap.pos());
            if (!framedblocks$frameMarkers.containsKey(frameId)) {
                framedblocks$addMapMarker(player.level(), frameId, framedMap);
            }
        }
    }

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
            )
    )
    private static Codec<MapItemSavedData> framedblocks$wrapCodec(Codec<MapItemSavedData> originalCodec) {
        if (originalCodec instanceof MapCodec.MapCodecCodec<MapItemSavedData>(MapCodec<MapItemSavedData> codec)) {
            return RecordCodecBuilder.create(inst -> inst.group(
                    codec.forGetter(Function.identity()),
                    FramedMap.CODEC.listOf().optionalFieldOf("framedblocks:frames", List.of()).forGetter(MixinMapItemSavedData::framedblocks$getFramedMaps)
            ).apply(inst, MixinMapItemSavedData::framedblocks$applyFramedMaps));
        } else {
            LogUtils.getLogger().error("[FramedBlocks] Failed to wrap MapItemSavedData.CODEC, map markers for Framed Item Frames will NOT persist!");
            if (!Utils.PRODUCTION) {
                throw new RuntimeException("Failed to wrap MapItemSavedData.CODEC");
            }
            return originalCodec;
        }
    }

    @Unique
    private static List<FramedMap> framedblocks$getFramedMaps(MapItemSavedData mapData) {
        //noinspection DataFlowIssue
        return List.copyOf(((MixinMapItemSavedData) (Object) mapData).framedblocks$frameMarkers.values());
    }

    @Unique
    private static MapItemSavedData framedblocks$applyFramedMaps(MapItemSavedData mapData, List<FramedMap> framedMaps) {
        for (FramedMap map : framedMaps) {
            String frameId = FramedMap.makeFrameId(map.pos());
            //noinspection DataFlowIssue
            ((MixinMapItemSavedData) (Object) mapData).framedblocks$addMapMarker(null, frameId, map);
        }
        return mapData;
    }

    @Override
    public void framedblocks$removeMapMarker(BlockPos pos) {
        String frameId = FramedMap.makeFrameId(pos);
        removeDecoration(frameId);
        framedblocks$frameMarkers.remove(frameId);
    }

    @Unique
    private void framedblocks$addMapMarker(@Nullable LevelAccessor level, String frameId, FramedMap framedMap) {
        BlockPos pos = framedMap.pos();
        int rot = framedMap.yRot();
        addDecoration(MapDecorationTypes.FRAME, level, frameId, pos.getX(), pos.getZ(), rot, null);
        framedblocks$frameMarkers.put(frameId, framedMap);
    }
}
