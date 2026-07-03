package io.github.xfacthd.framedblocks.api.model.data;

import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

/// Represents a single data entry of [ModelData].
///
/// @param property The property to store the data with
/// @param data     The value to store in the model data
public record ModelDataEntry<T>(ModelProperty<T> property, T data) {
    public void apply(ModelData.Builder builder) {
        builder.with(property, data);
    }
}
