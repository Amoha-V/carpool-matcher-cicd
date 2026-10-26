package com.example.carpool.model;

/**
 * Pick-up / drop-off areas around Chennai with approximate coordinates.
 * Distances between areas are computed with the haversine formula.
 */
public enum Place {
    SNU_CHENNAI("Shiv Nadar University Chennai", 12.7430, 80.1930),
    KELAMBAKKAM("Kelambakkam", 12.7843, 80.2192),
    SIRUSERI("Siruseri", 12.8300, 80.2210),
    NAVALUR("Navalur", 12.8460, 80.2270),
    SHOLINGANALLUR("Sholinganallur", 12.9010, 80.2279),
    PERUNGUDI("Perungudi", 12.9650, 80.2461),
    VELACHERY("Velachery", 12.9815, 80.2180),
    ADYAR("Adyar", 13.0012, 80.2565),
    GUINDY("Guindy", 13.0067, 80.2206),
    TAMBARAM("Tambaram", 12.9249, 80.1000),
    CHENGALPATTU("Chengalpattu", 12.6819, 79.9888),
    T_NAGAR("T. Nagar", 13.0418, 80.2341),
    ANNA_NAGAR("Anna Nagar", 13.0850, 80.2101),
    CHENNAI_CENTRAL("Chennai Central", 13.0827, 80.2707);

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final String label;
    private final double latitude;
    private final double longitude;

    Place(String label, double latitude, double longitude) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLabel() {
        return label;
    }

    /** Great-circle distance to another place in kilometres (haversine formula). */
    public double distanceKm(Place other) {
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(other.longitude - longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
