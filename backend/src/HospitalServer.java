import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HospitalServer {
    static HospitalDataStore db = new HospitalDataStore();
    static SchedulingEngine engine = new SchedulingEngine();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/dashboard", HospitalServer::dashboard);
        server.createContext("/api/patients", HospitalServer::patients);
        server.createContext("/api/resources", HospitalServer::resources);
        server.createContext("/api/schedule", HospitalServer::schedule);
        server.createContext("/api/generate", HospitalServer::generate);
        server.createContext("/api/reset", HospitalServer::reset);

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        System.out.println("Hospital Scheduling Backend running at http://localhost:8080");
        System.out.println("Open frontend/index.html in your browser.");
        server.start();
    }

    static void patients(HttpExchange ex) throws IOException {
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 200, JsonUtil.listPatients(db.patients));
            return;
        }

        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String,String> f = form(ex);
            int id = Integer.parseInt(f.getOrDefault("id", "0"));
            if (id == 0) {
                id = db.patients.stream().mapToInt(p -> p.id).max().orElse(100) + 1;
            }

            Patient p = new Patient(
                id,
                f.getOrDefault("name", "New Patient"),
                Integer.parseInt(f.getOrDefault("priority", "1")),
                Integer.parseInt(f.getOrDefault("duration", "2")),
                f.getOrDefault("type", "Planned"),
                f.getOrDefault("doctor", "Dr. Rao"),
                f.getOrDefault("admissionDate", "2026-09-24"),
                f.getOrDefault("deadline", "2026-09-26")
            );
            db.patients.add(p);
            send(ex, 201, "{\"message\":\"Patient added\",\"patient\":" + JsonUtil.patient(p) + "}");
            return;
        }

        send(ex, 405, "{\"error\":\"Method not allowed\"}");
    }

    static void resources(HttpExchange ex) throws IOException {
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 200, JsonUtil.listResources(db.resources));
            return;
        }
        send(ex, 405, "{\"error\":\"Method not allowed\"}");
    }

    static void schedule(HttpExchange ex) throws IOException {
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 200, JsonUtil.listSchedule(db.schedule));
            return;
        }
        send(ex, 405, "{\"error\":\"Method not allowed\"}");
    }

    static void generate(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"POST required\"}");
            return;
        }

        db.schedule.clear();
        db.schedule.addAll(engine.generate(db.patients, db.resources));

        double utilization = engine.utilization(db.schedule, db.resources);
        double waiting = engine.averageWaitingTime(db.schedule);
        int makespan = engine.makespan(db.schedule);
        long assigned = db.schedule.stream().filter(s -> s.status.equals("SCHEDULED")).count();

        String response = String.format(
            "{\"message\":\"Schedule generated\",\"assigned\":%d,\"total\":%d,\"utilization\":%.2f,\"averageWaiting\":%.2f,\"makespan\":%d}",
            assigned, db.schedule.size(), utilization, waiting, makespan
        );
        send(ex, 200, response);
    }

    static void dashboard(HttpExchange ex) throws IOException {
        double utilization = engine.utilization(db.schedule, db.resources);
        double waiting = engine.averageWaitingTime(db.schedule);
        int makespan = engine.makespan(db.schedule);
        long assigned = db.schedule.stream().filter(s -> s.status.equals("SCHEDULED")).count();
        long waitingPatients = db.schedule.stream().filter(s -> s.status.equals("WAITING")).count();

        String response = String.format(
            "{\"patients\":%d,\"resources\":%d,\"scheduled\":%d,\"waiting\":%d,\"utilization\":%.2f,\"averageWaiting\":%.2f,\"makespan\":%d}",
            db.patients.size(), db.resources.size(), assigned, waitingPatients,
            utilization, waiting, makespan
        );
        send(ex, 200, response);
    }

    static void reset(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"POST required\"}");
            return;
        }
        db.reset();
        send(ex, 200, "{\"message\":\"Data reset\"}");
    }

    static Map<String,String> form(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String,String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            if (pair.isBlank()) continue;
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1
                ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                : "";
            map.put(key, value);
        }
        return map;
    }

    static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
