package xfacthd.framedblocks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
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
import xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;

import java.util.HashMap;
import java.util.Map;

@Mixin(MapItemSavedData.class)
@SuppressWarnings("MethodMayBeStatic")
public abstract class MixinMapItemSavedData implements FramedItemFrameBlockEntity.MapMarkerRemover
{
    @Unique
    private final Map<String, FramedItemFrameBlockEntity.FramedMap> framedblocks$frameMarkers = new HashMap<>();

    @Shadow @Final private boolean trackingPosition;

    @Shadow protected abstract void addDecoration(MapDecoration.Type pType, @Nullable LevelAccessor pLevel, String pDecorationName, double pLevelX, double pLevelZ, double pRotation, @Nullable Component pName);
    @Shadow protected abstract void removeDecoration(String pIdentifier);

    @ModifyExpressionValue(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isFramed()Z", ordinal = 0))
    private boolean framedblocks$checkVanillaFramedOrCustomFramed(boolean isFramed, Player player, ItemStack stack)
    {
        //noinspection ConstantConditions
        return isFramed || (stack.hasTag() && stack.getTag().contains(FramedItemFrameBlockEntity.NBT_KEY_FRAMED_MAP));
    }

    @ModifyExpressionValue(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isFramed()Z", ordinal = 1))
    private boolean framedblocks$checkNotVanillaFramedAndNotCustomFramed(boolean isFramed, Player player, ItemStack stack)
    {
        //noinspection ConstantConditions
        return isFramed || (stack.hasTag() && stack.getTag().contains(FramedItemFrameBlockEntity.NBT_KEY_FRAMED_MAP));
    }

    @Inject(method = "tickCarriedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTag()Lnet/minecraft/nbt/CompoundTag;"))
    private void framedblocks$updateFramedItemFrameMarker(Player player, ItemStack mapStack, CallbackInfo ci)
    {
        CompoundTag tag;
        //noinspection ConstantConditions
        if (trackingPosition && mapStack.hasTag() && (tag = mapStack.getTag().getCompound(FramedItemFrameBlockEntity.NBT_KEY_FRAMED_MAP)) != null)
        {
            BlockPos pos = BlockPos.of(tag.getLong("pos"));
            String frameId = FramedItemFrameBlockEntity.FramedMap.makeFrameId(pos);
            if (!framedblocks$frameMarkers.containsKey(frameId))
            {
                int rot = tag.getByte("y_rot") * 90;
                FramedItemFrameBlockEntity.FramedMap framedMap = new FramedItemFrameBlockEntity.FramedMap(pos, rot);
                framedblocks$addMapMarker(player.level(), frameId, framedMap);
            }
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private static void framedblocks$loadCustomMapMarkers(CompoundTag tag, CallbackInfoReturnable<MapItemSavedData> cir)
    {
        ListTag frames = tag.getList("framedblocks:frames", Tag.TAG_COMPOUND);
        for (int i = 0; i < frames.size(); i++)
        {
            CompoundTag frameTag = frames.getCompound(i);
            FramedItemFrameBlockEntity.FramedMap map = FramedItemFrameBlockEntity.FramedMap.load(frameTag);
            String frameId = FramedItemFrameBlockEntity.FramedMap.makeFrameId(map.pos());
            ((MixinMapItemSavedData)(Object) cir.getReturnValue()).framedblocks$addMapMarker(null, frameId, map);
        }
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void framedblocks$saveCustomMapMarkers(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir)
    {
        if (!framedblocks$frameMarkers.isEmpty())
        {
            ListTag frames = new ListTag();
            framedblocks$frameMarkers.forEach((id, marker) -> frames.add(marker.save()));
            tag.put("framedblocks:frames", frames);
        }
    }

    @Override
    public void framedblocks$removeMapMarker(BlockPos pos)
    {
        String frameId = FramedItemFrameBlockEntity.FramedMap.makeFrameId(pos);
        removeDecoration(frameId);
        framedblocks$frameMarkers.remove(frameId);
    }

    @Unique
    private void framedblocks$addMapMarker(
            LevelAccessor level, String frameId, FramedItemFrameBlockEntity.FramedMap framedMap
    )
    {
        BlockPos pos = framedMap.pos();
        int rot = framedMap.yRot();
        addDecoration(MapDecoration.Type.FRAME, level, frameId, pos.getX(), pos.getZ(), rot, null);
        framedblocks$frameMarkers.put(frameId, framedMap);
    }
}
