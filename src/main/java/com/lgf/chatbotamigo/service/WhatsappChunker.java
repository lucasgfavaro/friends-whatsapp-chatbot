package com.lgf.chatbotamigo.service;

import com.lgf.chatbotamigo.model.Message;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class WhatsappChunker {

    private static final Duration CHUNK_WINDOW = Duration.ofMinutes(1440);
    private static final Pattern MENSAJE_PATTERN = Pattern.compile(
            "^(\\d{1,2}/\\d{1,2}/\\d{4}), (\\d{1,2}:\\d{2}) - ([^:]+): (.+)$"
    );
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("d/M/yyyy H:mm");

    private static final Pattern SISTEMA_PATTERN = Pattern.compile(
            "^(\\d{1,2}/\\d{1,2}/\\d{4}), (\\d{1,2}:\\d{2}) - (.*)$"
    );

    public static List<Chunk> chunkChat(String filename) throws IOException {

        List<String> lines = getStringLinesFrom(filename);
        List<Message> messages = parseMessages(lines);
        return buildChunks(messages);
    }

    @NotNull
    private static List<String> getStringLinesFrom(String filename) {

        Path path = Paths.get(filename);

        try (Stream<String> linesStream = Files.lines(path, StandardCharsets.UTF_8)) {
            List<String> lines = linesStream.collect(Collectors.toList());
            log.info("File: {} Total Lines: {}", filename, lines.size());
            return lines;
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading file: " + filename, e);
        }
    }

    public static Double getSizeInKB(String text) {
        return text.getBytes(StandardCharsets.UTF_8).length / 1024.0;
    }

    public static List<Message> parseMessages(List<String> lineas) {
        List<Message> mensajes = new ArrayList<>();
        LocalDateTime hora = null;
        String persona = null;
        StringBuilder texto = new StringBuilder();

        for (String linea : lineas) {
            Matcher matcher = MENSAJE_PATTERN.matcher(linea);

            if (matcher.matches()) {
                if (hora != null && persona != null && !texto.isEmpty() && !limpiarTexto(texto.toString()).isEmpty()) {
                    mensajes.add(new Message(hora, persona, limpiarTexto(texto.toString())));
                }

                String fechaHoraStr = matcher.group(1) + " " + matcher.group(2);
                hora = LocalDateTime.parse(fechaHoraStr, FORMATO_FECHA);
                persona = matcher.group(3);
                texto = new StringBuilder(matcher.group(4));
            } else if (esLineaDeSistema(linea)) {
                continue;
            } else if (linea.trim().isEmpty()) {
                continue;
            } else {
                // Línea que continúa el mensaje anterior
                if (!texto.isEmpty()) {
                    texto.append("\n");
                }
                texto.append(linea);
            }
        }

        // Agregar el último mensaje si quedó
        if (hora != null && persona != null && !texto.isEmpty()) {
            mensajes.add(new Message(hora, persona, limpiarTexto(texto.toString())));
        }

        log.info("Total Lines parsed: {}", mensajes.size());

        return mensajes;
    }

    private static boolean esLineaDeSistema(String linea) {
        if (linea.contains("creó el grupo") ||
                linea.contains("cambió el ícono") ||
                linea.contains("te añadió") ||
                linea.contains("están cifrados") ||
                linea.contains("<Multimedia omitido>") ||
                !linea.contains(":")) {
            return true;
        }
        return false;
    }

    private static String limpiarTexto(String texto) {
        // Sacamos emojis (esto es muy básico)
        return texto.replaceAll("[\\p{So}\\p{Cn}]+", "").trim();
    }

    private static List<Chunk> buildChunks(List<Message> messages) {
        List<Chunk> chunks = new ArrayList<>();
        Chunk current = new Chunk();

        for (Message msg : messages) {
            if (current.messages.isEmpty() || current.isWithinWindow(msg)) {
                current.add(msg);
            } else {
                chunks.add(current);
                current = new Chunk();
                current.add(msg);
            }
        }
        if (!current.messages.isEmpty()) {
            chunks.add(current);
        }

        chunks.forEach(c -> log.info("=== Chunk - Start time:{} Kb:{} ===", c.startTime, String.format("%.2f", c.getKbSize())));
        log.info(" Total Chunks: {} Total kb: {}", chunks.size(), chunks.stream().map(Chunk::getKbSize).reduce(Double::sum).orElse(0.0));
        return chunks;
    }

    @Getter
    public static class Chunk {
        List<Message> messages = new ArrayList<>();
        LocalDateTime startTime;
        LocalDateTime endTime;
        Double kbSize = 0.0;

        void add(Message msg) {
            if (messages.isEmpty()) {
                startTime = msg.getTimestamp();
            }
            endTime = msg.getTimestamp();
            kbSize = kbSize + getSizeInKB(msg.getText());
            messages.add(msg);
        }

        boolean isWithinWindow(Message msg) {
            return Duration.between(startTime, msg.getTimestamp()).compareTo(CHUNK_WINDOW) <= 0;
        }

        String getChunkText() {
            return messages.stream()
                    .map(Message::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

}
