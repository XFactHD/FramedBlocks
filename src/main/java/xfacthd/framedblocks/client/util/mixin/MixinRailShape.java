package xfacthd.framedblocks.client.util.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.RailState;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xfacthd.framedblocks.common.FBContent;

@Mixin(RailState.class)
public class MixinRailShape
{
    @Final
    @Shadow
    private AbstractRailBlock block;

    @Inject(
            method = "connectTo",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/block/BlockState;setValue(Lnet/minecraft/state/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void framedblocks_connectFilterInvalidState(RailState state, CallbackInfo ci, BlockPos blockpos, BlockPos blockpos1, BlockPos blockpos2, BlockPos blockpos3, boolean flag, boolean flag1, boolean flag2, boolean flag3, RailShape railshape)
    {
        if (this.block == FBContent.blockFramedRailSlope.get() && !railshape.isAscending())
        {
            ci.cancel();
        }
    }

    @ModifyVariable(
            method = "place",
            at = @At(
                    value = "LOAD",
                    ordinal = 3
            ),
            ordinal = 1
    )
    private RailShape framedblocks_placeFilterInvalidState(RailShape shape)
    {
        if (block == FBContent.blockFramedRailSlope.get() && shape != null && !shape.isAscending())
        {
            return null;
        }
        return shape;
    }
}