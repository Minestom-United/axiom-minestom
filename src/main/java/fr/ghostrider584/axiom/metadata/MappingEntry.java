package fr.ghostrider584.axiom.metadata;

import net.minestom.server.codec.Codec;
import net.minestom.server.entity.MetadataDef;

public sealed interface MappingEntry<V> permits MetadataMapping, MetadataMapperBuilder.TypedMapping {
	String path();
	MetadataDef.Entry<V> entry();
	Codec<V> codec();
}