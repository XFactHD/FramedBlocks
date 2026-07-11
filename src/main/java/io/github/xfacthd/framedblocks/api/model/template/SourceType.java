package io.github.xfacthd.framedblocks.api.model.template;

/// Indicates where a template source file should be loaded from.
public enum SourceType {
    /// The source file is a template file and will only be loaded from `/resources/assets/<modid>/framed_templates/`.
    TEMPLATE,
    /// The source file is a vanilla model file and will be loaded from `/resources/assets/<modid>/models/` unless
    /// a file with an identical relative path exists in `/resources/assets/<modid>/framed_templates/`.
    MODEL,
}
