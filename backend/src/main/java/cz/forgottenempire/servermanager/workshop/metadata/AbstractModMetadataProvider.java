package cz.forgottenempire.servermanager.workshop.metadata;

import java.util.Optional;

abstract class AbstractModMetadataProvider {
    Optional<ModMetadata> fetchModMetadata(long modId) {
        PropertyProvider propertyProvider = createPropertyProvider(modId);
        if (propertyProvider == null) {
            return Optional.empty();
        }

        String modName = propertyProvider.findName();
        String consumerAppId = propertyProvider.findConsumerAppId();
        if (modName == null || consumerAppId == null) {
            return Optional.empty();
        }

        return Optional.of(new ModMetadata(modName, consumerAppId));
    }

    abstract PropertyProvider createPropertyProvider(long modId);
}
