package io.github.xfacthd.framedblocks.common.data.collapsible;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record HammerTarget(Direction face, @Nullable Direction oldFace, Vec3 pos)
{
    public HammerTarget(Direction face, @Nullable Direction oldFace, Vec3 hitLoc, boolean relative)
    {
        this(face, oldFace, relative ? Utils.fraction(hitLoc) : hitLoc);
    }
}
