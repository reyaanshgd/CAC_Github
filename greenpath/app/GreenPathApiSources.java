package org.greenpath.app;

/**
 * GreenPath discovery-source endpoints.
 *
 * SECURITY:
 * - These constants contain only public/no-secret endpoints.
 * - DO NOT put Idealist, Ticketmaster, or any other private API key in the APK.
 *   Keyed APIs should be called by a GreenPath backend, which returns normalized
 *   opportunity JSON to the Android app.
 */
public final class GreenPathApiSources {
    private GreenPathApiSources() {}

    // Confirmed public JSON endpoint (The Events Calendar REST API).
    public static final String SAVE_THE_BAY_EVENTS =
            "https://savesfbay.org/wp-json/tribe/events/v1/events";

    // Legistar civic-meeting feeds. Public GET access should be verified at runtime.
    public static final String SAN_JOSE_LEGISTAR =
            "https://webapi.legistar.com/v1/sanjose/events";
    public static final String CUPERTINO_LEGISTAR =
            "https://webapi.legistar.com/v1/cupertino/events";
    public static final String SANTA_CLARA_LEGISTAR =
            "https://webapi.legistar.com/v1/santaclara/events";

    // Public iCalendar feeds.
    public static final String SUNNYVALE_PUBLIC_MEETINGS_ICS =
            "https://calendar.google.com/calendar/ical/cityclerksunnyvale%40gmail.com/public/basic.ics";
    public static final String MILPITAS_CITY_ICS =
            "https://www.milpitas.gov/common/modules/iCalendar/iCalendar.aspx?catID=14&feed=calendar";
    public static final String MILPITAS_RECREATION_ICS =
            "https://www.milpitas.gov/common/modules/iCalendar/iCalendar.aspx?catID=26&feed=calendar";

    // Backend-only APIs. Keep keys on server; these strings are endpoint bases only.
    public static final String IDEALIST_VOLUNTEER_BACKEND_SOURCE =
            "https://www.idealist.org/api/v1/listings/volops";
    public static final String TICKETMASTER_BACKEND_SOURCE =
            "https://app.ticketmaster.com/discovery/v2/events.json";
}
