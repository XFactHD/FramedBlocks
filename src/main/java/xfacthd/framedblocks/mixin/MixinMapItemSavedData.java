package xfacthd.framedblocks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.component.FramedMap;

import java.util.HashMap;
import java.util.Map;

@Mixin(MapItemSavedData.class)
@SuppressWarnings("MethodMayBeStatic")
public abstract class MixinMapItemSavedData implements FramedMap.MarkerRemover
{
    @Unique
    private final Map<String, FramedMap> framedblocks$frameMarkers = new HashMap<>();

    @Shadow @Final private boolean trackingPosition;

    @Shadow protected abstract void addDecoration(Holder<MapDecorationType> pType, @Nullable LevelAccessor pLevel, String pDecorationName, double pLevelX, double pLevelZ, double pRotation, @Nullable Component pName);
    @Shadow protected abstract void removeDecoration(String pIdentifier);

    @ModifyExpressionValue(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isFramed()Z", ordinal = 0))
    private boolean framedblocks$checkVanillaFramedOrCustomFramed(boolean isFramed, Player player, ItemStack stack)
    {
        return isFramed || stack.get(FBContent.DC_TYPE_FRAMED_MAP) != null;
    }

    @ModifyExpressionValue(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isFramed()Z", ordinal = 1))
    private boolean framedblocks$checkNotVanillaFramedAndNotCustomFramed(boolean isFramed, Player player, ItemStack stack)
    {
        return isFramed || stack.get(FBContent.DC_TYPE_FRAMED_MAP) != null;
    }

    @Inject(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void framedblocks$updateFramedItemFrameMarker(Player player, ItemStack mapStack, CallbackInfo ci)
    {
        FramedMap framedMap;
        //noinspection ConstantConditions
        if (trackingPosition && (framedMap = mapStack.get(FBContent.DC_TYPE_FRAMED_MAP)) != null)
        {
            String frameId = FramedMap.makeFrameId(framedMap.pos());
            if (!framedblocks$frameMarkers.containsKey(frameId))
            {
                framedblocks$addMapMarker(player.level(), frameId, framedMap);
            }
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private static void framedblocks$loadCustomMapMarkers(CompoundTag tag, HolderLookup.Provider lookupProvider, CallbackInfoReturnable<MapItemSavedData> cir)
    {
        ListTag frames = tag.getList("framedblocks:frames", Tag.TAG_COMPOUND);
        for (int i = 0; i < frames.size(); i++)
        {
            CompoundTag frameTag = frames.getCompound(i);
            FramedMap.CODEC.decode(NbtOps.INSTANCE, frameTag).ifSuccess(pair ->
            {
                FramedMap map = pair.getFirst();
                String frameId = FramedMap.makeFrameId(map.pos());
                ((MixinMapItemSavedData)(Object) cir.getReturnValue()).framedblocks$addMapMarker(null, frameId, map);
            });
        }
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void framedblocks$saveCustomMapMarkers(CompoundTag tag, HolderLookup.Provider lookupProvider, CallbackInfoReturnable<CompoundTag> cir)
    {
        if (!framedblocks$frameMarkers.isEmpty())
        {
            ListTag frames = new ListTag();
            for (FramedMap map : framedblocks$frameMarkers.values())
            {
                FramedMap.CODEC.encodeStart(NbtOps.INSTANCE, map).ifSuccess(frames::add);
            }
            tag.put("framedblocks:frames", frames);
        }
    }

    @Override
    public void framedblocks$removeMapMarker(BlockPos pos)
    {
        String frameId = FramedMap.makeFrameId(pos);
        removeDecoration(frameId);
        framedblocks$frameMarkers.remove(frameId);
    }

    @Unique
    private void framedblocks$addMapMarker(
            LevelAccessor level, String frameId, FramedMap framedMap
    )
    {
        BlockPos pos = framedMap.pos();
        int rot = framedMap.yRot();
        addDecoration(MapDecorationTypes.FRAME, level, frameId, pos.getX(), pos.getZ(), rot, null);
        framedblocks$frameMarkers.put(frameId, framedMap);
    }
}
