package io.github.xfacthd.framedblocks.client.model.template;

import com.mojang.math.OctahedralGroup;
import net.minecraft.resources.Identifier;

record SourceFile(FileId id, OctahedralGroup transform) {
    SourceFile(Identifier id, boolean model, OctahedralGroup rotation) {
        this(new FileId(id, model), rotation);
    }

    record FileId(Identifier id, boolean model) { }
}
