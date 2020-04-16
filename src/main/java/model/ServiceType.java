package model;

import com.google.common.collect.ImmutableSet;

public enum ServiceType {
    PersonTransport, CargoTransport, SatelliteTransport, Unknown;

    final static ImmutableSet<String> legalArgs = ImmutableSet.of("ct", "pt", "st");

    public static ServiceType fromString(String serviceType) {
        if (ImmutableSet.of("PersonTransport", "pt").contains(serviceType)) {
            return PersonTransport;
        } else if (ImmutableSet.of("CargoTransport", "ct").contains(serviceType)) {
            return CargoTransport;
        } else if (ImmutableSet.of("SatelliteTransport", "st").contains(serviceType)) {
            return SatelliteTransport;
        } else {
            return Unknown;
        }
    }
}
