package io.github.xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record PaintRollerContents(@Nullable Holder<BlockOverlay> overlay, int count) implements TooltipComponent {
    public static final int MAX_COUNT = 256;
    public static final Codec<PaintRollerContents> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockOverlay.CODEC.optionalFieldOf("overlay").forGetter(PaintRollerContents::overlayForSerialization),
            Codec.intRange(0, MAX_COUNT).fieldOf("count").forGetter(PaintRollerContents::count)
    ).apply(inst, PaintRollerContents::deserialize));
    public static final StreamCodec<RegistryFriendlyByteBuf, PaintRollerContents> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BlockOverlay.STREAM_CODEC),
            PaintRollerContents::overlayForSerialization,
            ByteBufCodecs.VAR_INT,
            PaintRollerContents::count,
            PaintRollerContents::deserialize
    );
    public static final PaintRollerContents NO_OVERLAY = new PaintRollerContents(null, 0);

    private static PaintRollerContents deserialize(Optional<Holder<BlockOverlay>> overlay, int count) {
        return overlay.isEmpty() ? NO_OVERLAY : new PaintRollerContents(overlay.get(), count);
    }

    public static PaintRollerContents get(ItemStack stack) {
        return stack.getOrDefault(FBContent.DC_TYPE_PAINT_ROLLER_CONTENTS, NO_OVERLAY);
    }

    public boolean hasOverlay() {
        return overlay != null;
    }

    public boolean isDepleted() {
        return count == 0;
    }

    public boolean canInsert(ItemStack stack) {
        return (!hasOverlay() || stack.is(sourceItem())) && count < MAX_COUNT;
    }

    public PaintRollerContents insert(BlockOverlayCache cache, ItemStack stack) {
        if (stack.isEmpty()) {
            return this;
        }

        Holder<BlockOverlay> overlay;
        int count;
        if (this.overlay != null) {
            if (!stack.is(sourceItem())) {
                return this;
            }

            overlay = this.overlay;
            count = this.count;
        } else {
            overlay = cache.getOverlay(stack.getItem());
            count = 0;
        }
        int diff = Math.min(stack.getCount(), MAX_COUNT - count);
        stack.shrink(diff);
        return new PaintRollerContents(overlay, count + diff);
    }

    public PaintRollerContents extract(SlotAccess output) {
        if (overlay == null) {
            return this;
        }

        Item srcItem = sourceItem();
        int outCount = Math.min(count, srcItem.getDefaultMaxStackSize());
        if (outCount > 0) {
            output.set(new ItemStack(srcItem, outCount));
        }
        int finalCount = count - outCount;
        Holder<BlockOverlay> overlay = finalCount == 0 ? null : this.overlay;
        return new PaintRollerContents(overlay, finalCount);
    }

    public Item sourceItem() {
        if (overlay == null) {
            return Items.AIR;
        }
        return overlay.value().sourceItem().value();
    }

    public float getFillPercent() {
        return ((float) count) / MAX_COUNT;
    }

    public PaintRollerContents shrink() {
        int newCount = Math.max(count - 1, 0);
        return new PaintRollerContents(overlay, newCount);
    }

    public static float getFillPercent(ItemStack stack) {
        PaintRollerContents contents = get(stack);
        return contents.hasOverlay() ? contents.getFillPercent() : 0F;
    }

    private Optional<Holder<BlockOverlay>> overlayForSerialization() {
        return Optional.ofNullable(overlay);
    }
}
