import com.google.common.collect.ImmutableSet;

import java.util.Arrays;

public enum ServiceType {
    PersonTransport, CargoTransport, SatelliteTransport;

    static ServiceType fromString(String serviceType) {
        if (ImmutableSet.of("PersonTransport", "pt").contains(serviceType)) {
            return PersonTransport;
        } else if (ImmutableSet.of("CargoTransport", "ct").contains(serviceType)) {
            return CargoTransport;
        } else if (ImmutableSet.of("SatelliteTransport", "st").contains(serviceType)) {
            return SatelliteTransport;
        } else {
            throw new IllegalStateException(String.format("Service type: %s is not supported, try: ", serviceType)
                    + Arrays.toString(ServiceType.values()));
        }
    }
}
