package fr.ghostrider584.axiom.metadata;

import net.minestom.server.entity.metadata.EntityMeta;

import java.util.List;

public final class MetadataMappers {

	public static <T extends EntityMeta> MetadataMapperBuilder<T> builder() {
		return MetadataMapperBuilder.create();
	}

	public static <T extends EntityMeta> MetadataMapper<T> metadataCodec(List<? extends MappingEntry<?>> mappings) {
		return new SimpleMetadataMapper<>(mappings);
	}

	public static <T extends EntityMeta> MetadataMapper<T> metadataCodec(MappingEntry<?>... mappings) {
		return metadataCodec(List.of(mappings));
	}

	public static <T extends EntityMeta> MetadataMapper<T> extend(MetadataMapper<? super T> base, List<? extends MappingEntry<?>> additionalMappings) {
		return new CompositeMetadataMapper<>(base, metadataCodec(additionalMappings));
	}

	public static <T extends EntityMeta> MetadataMapper<T> extend(MetadataMapper<? super T> base, MappingEntry<?>... additionalMappings) {
		return extend(base, List.of(additionalMappings));
	}
}