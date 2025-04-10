package com.lgf.chatbotamigo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class FriendChatbotService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final String FRIEND_ASKING_CONTEXT = "CONTEXTO AMIGO QUE PREGUNTA";
    private final String PREVIOUS_MESSAGES_CONTEXT = "CONTEXTO MENSAJES ANTERIORES";
    private final String FRIEND_CALLING_CONTEXT = "CONTEXTO NOMBRES AMIGOS";

    public FriendChatbotService(ChatClient.Builder builder, VectorStore vectorStore //            , ChatMemory chatMemory
    ) {
        this.vectorStore = vectorStore;
        this.chatClient = builder
//                .defaultSystem("""
//                        You are a friend of the user that is asking questions. You must limit the answer style to the context provided in the user prompt
//                        """)
//                .defaultAdvisors(
//                        //new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
//                        new QuestionAnswerAdvisor(vectorStore), // RAG
//                        new SimpleLoggerAdvisor())
                .build();
    }

    public String askQuestion(String fromFriend, String toFriend, String topic) {
        String prompt = getSystemContext(fromFriend) + "\n\n"
                //+ getPreviousMessagesContext(toFriend, topic) + "\n\n"
                //+ getFriendWhoIsAskingContext(fromFriend) + "\n\n"
                + getFriendCallingContext() + "\n\n"
                + "PREGUNTA DEL AMIGO: " + topic;

        String response = chatClient.prompt(prompt).call().content();

        log.info(String.format("Prompt: %s \n\nResponse: %s", prompt, response));

        return response;
    }

    private String getFriendWhoIsAskingContext(String fromFriend) {
        return FRIEND_ASKING_CONTEXT + " " + fromFriend;
    }


    private String getSystemContext(String fromFriend) {
        return "Sos el amigo de " + fromFriend + " que esta haciendo preguntas. Todos son Argentinos y el contexto de la conversacion es un chat intimo de amigos de whatsapp. " +
                "Debes limitarte a un estilo de respuesta provisto en la seccion " + PREVIOUS_MESSAGES_CONTEXT + " de este contexto. La forma en que cada amigo llama a otro " +
                "es diferente y esta provista en la seccion " + FRIEND_CALLING_CONTEXT + ".";
    }

    private String getPreviousMessagesContext(String toFriend, String topic) {

        List<Document> similarMessages = Objects.requireNonNull(vectorStore.similaritySearch(topic)).stream()
                .filter(doc -> toFriend.equals(doc.getMetadata().get("user")))
                .toList();

        String context = similarMessages.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        return PREVIOUS_MESSAGES_CONTEXT + "\n" + context;
    }

    private String getFriendCallingContext() {


        try {
            // Cargar el archivo desde la carpeta resources
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("friendCallingContext.json");

            if (inputStream == null) {
                throw new IllegalArgumentException("No se encontró el archivo 'friendCallingContext.json' en resources.");
            }
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Map<String, List<String>>> data =
                    mapper.readValue(inputStream, new TypeReference<>() {
                    });

            StringBuilder context = new StringBuilder();

            context.append(FRIEND_CALLING_CONTEXT).append("\n");

            for (Map.Entry<String, Map<String, List<String>>> outerEntry : data.entrySet()) {
                String personaPrincipal = outerEntry.getKey(); // e.g. "Lucas Serra"
                Map<String, List<String>> personasYApodos = outerEntry.getValue();

                context.append("Si sos ").append(personaPrincipal).append(", ");

                for (Map.Entry<String, List<String>> innerEntry : personasYApodos.entrySet()) {
                    String referida = innerEntry.getKey();
                    List<String> apodos = innerEntry.getValue();
                    context.append("a ").append(referida).append(" lo vas a llamar por ").append(unirApodos(apodos)).append(", ");
                }

                context.append("\n");
            }

            return context.toString();
        } catch (IOException e) {
            return "";
        }


    }

    private String unirApodos(List<String> apodos) {
        if (apodos == null || apodos.isEmpty()) {
            return "";
        }

        int size = apodos.size();

        if (size == 1) {
            return apodos.get(0);
        }

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < size; i++) {
            if (i > 0 && i == size - 1) {
                resultado.append(" o ");
            } else if (i > 0) {
                resultado.append(", ");
            }
            resultado.append(apodos.get(i));
        }

        return resultado.toString();
    }

    public String generateConversation(String topic) {
        return null;
    }

    //        return this.chatClient
//                .prompt()
//                .user(userPrompt)
//                //                //.advisors(a -> a
////                 //       .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
////                  //      .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
//                .call()
//                .content()
//                ;
}
