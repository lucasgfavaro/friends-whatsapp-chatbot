package com.lgf.chatbotamigo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhatsAppChatParser {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}), \\d{1,2}:\\d{2} - (.*?): (.*)");

    public static Map<String, List<String>> parseChatFile(String file) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/", file));
        Map<String, List<String>> messagesByUser = new HashMap<>();

        for (String line : lines) {
            Matcher matcher = MESSAGE_PATTERN.matcher(line);
            if (matcher.matches()) {
                String timestamp = matcher.group(1);
                String user = matcher.group(2);
                String message = matcher.group(3);

                messagesByUser.computeIfAbsent(user, k -> new ArrayList<>()).add(message);
            }
        }

        return messagesByUser;
    }
}
