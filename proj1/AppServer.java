import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class AppServer {
    private static final String DEFAULT_HTML = """
            <!doctype html>
            <html>
              <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Java Demo Results</title>
              </head>
              <body>
                <h1>Java Demo Results</h1>
                <p>Frontend file was not found. Create web/index.html to customize UI.</p>
              </body>
            </html>
            """;

    public static void main(String[] args) throws IOException {
        int port = readPort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new FrontendHandler());
        server.createContext("/api/results", new ResultsHandler());
        server.createContext("/health", new TextHandler("ok"));
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("UI server running at http://localhost:" + port);
    }

    private static int readPort() {
        String env = System.getenv("PORT");
        if (env == null || env.trim().isEmpty()) {
            return 8080;
        }
        try {
            return Integer.parseInt(env);
        } catch (NumberFormatException ex) {
            return 8080;
        }
    }

    private static class FrontendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                write(exchange, 405, "text/plain; charset=utf-8", "Method Not Allowed");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (!"/".equals(path) && !"/index.html".equals(path)) {
                write(exchange, 404, "text/plain; charset=utf-8", "Not Found");
                return;
            }

            String html = loadHtml();
            write(exchange, 200, "text/html; charset=utf-8", html);
        }
    }

    private static class ResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                write(exchange, 405, "application/json; charset=utf-8",
                        "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            String json = buildResultsJson();
            write(exchange, 200, "application/json; charset=utf-8", json);
        }
    }

    private static class TextHandler implements HttpHandler {
        private final String responseText;

        TextHandler(String responseText) {
            this.responseText = responseText;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            write(exchange, 200, "text/plain; charset=utf-8", responseText);
        }
    }

    private static String loadHtml() {
        Path path = Path.of("web", "index.html");
        if (Files.exists(path)) {
            try {
                return Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
                return DEFAULT_HTML;
            }
        }

        try (InputStream in = AppServer.class.getResourceAsStream("/web/index.html")) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
            return DEFAULT_HTML;
        }

        return DEFAULT_HTML;
    }

    private static String buildResultsJson() {
        User youngUser = new User("ssar", 27);
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");
        youngUser.borrow(book);
        youngUser.haveBirthday();

        Shape[] shapes = new Shape[]{
                new Circle(2.0),
                new Rectangle(3.0, 4.0)
        };

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"user\":{");
        sb.append("\"name\":\"").append(escapeJson(youngUser.getName())).append("\",");
        sb.append("\"age\":").append(youngUser.getAge()).append(",");
        sb.append("\"borrowedCount\":").append(youngUser.getBorrowedCount()).append(",");
        sb.append("\"books\":[");
        for (int i = 0; i < youngUser.getBorrowedBooks().size(); i++) {
            Book borrowed = youngUser.getBorrowedBooks().get(i);
            sb.append("{");
            sb.append("\"title\":\"").append(escapeJson(borrowed.getTitle())).append("\",");
            sb.append("\"author\":\"").append(escapeJson(borrowed.getAuthor())).append("\"");
            sb.append("}");
            if (i < youngUser.getBorrowedBooks().size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        sb.append("},");

        sb.append("\"shapes\":[");
        for (int i = 0; i < shapes.length; i++) {
            Shape s = shapes[i];
            sb.append("{");
            sb.append("\"name\":\"").append(escapeJson(s.getName())).append("\",");
            sb.append("\"area\":").append(String.format(java.util.Locale.US, "%.2f", s.area()));
            sb.append("}");
            if (i < shapes.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void write(HttpExchange exchange, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
