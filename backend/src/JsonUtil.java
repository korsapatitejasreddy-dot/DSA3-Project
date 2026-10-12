import java.util.*;

public class JsonUtil {
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public static String patient(Patient p) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"priority\":%d,\"duration\":%d,\"type\":\"%s\",\"doctor\":\"%s\",\"admissionDate\":\"%s\",\"deadline\":\"%s\"}",
            p.id, esc(p.name), p.priority, p.duration, esc(p.type), esc(p.doctor),
            esc(p.admissionDate), esc(p.deadline)
        );
    }

    public static String resource(Resource r) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\",\"doctor\":\"%s\",\"capacity\":%d,\"availableFrom\":%d,\"availableTo\":%d,\"available\":%s}",
            r.id, esc(r.name), esc(r.type), esc(r.doctor), r.capacity,
            r.availableFrom, r.availableTo, r.available
        );
    }

    public static String schedule(ScheduleEntry e) {
        return String.format(
            "{\"patientId\":%d,\"patientName\":\"%s\",\"resourceName\":\"%s\",\"resourceType\":\"%s\",\"doctor\":\"%s\",\"start\":%d,\"end\":%d,\"priority\":%d,\"status\":\"%s\"}",
            e.patientId, esc(e.patientName), esc(e.resourceName), esc(e.resourceType),
            esc(e.doctor), e.start, e.end, e.priority, esc(e.status)
        );
    }

    public static String listPatients(List<Patient> list) {
        return array(list.stream().map(JsonUtil::patient).toList());
    }

    public static String listResources(List<Resource> list) {
        return array(list.stream().map(JsonUtil::resource).toList());
    }

    public static String listSchedule(List<ScheduleEntry> list) {
        return array(list.stream().map(JsonUtil::schedule).toList());
    }

    public static String array(List<String> values) {
        return "[" + String.join(",", values) + "]";
    }
}
