package no.bibsys.handlers.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HumeReader {
    public static void main(String... args) {
        ObjectMapper objectMapper = new ObjectMapper();
        
        Map<String, String> keyMapper = new HashMap<>();
        keyMapper.put("toppterm-id", "broader1");
        keyMapper.put("se-id", "related");
        keyMapper.put("dato", "dato");
        keyMapper.put("term-id", "identifier");
        keyMapper.put("overordnetterm-id", "broader");
        keyMapper.put("kvalifikator", "alternativeLabel");
        keyMapper.put("hovedemnefrase", "preferredLabel");
        keyMapper.put("definisjon", "definition");
        keyMapper.put("se-ogsa-id", "seeAlso");
        
        List<Map<String, ?>> convertedList = new CopyOnWriteArrayList<>();
        
        try {
            List<Map<String, ?>> list = objectMapper.readValue(new InputStreamReader(new FileInputStream("src/main/resources/humord.json"), Charset.forName("UTF-8")), List.class);
            Set<String> keySet = new HashSet<>();
            list.forEach(item -> {
                System.out.println(item);
                 item.entrySet().forEach(entry -> System.out.println(entry.getKey()));
                 Map<String, ?> newMap = item.entrySet().stream().collect(Collectors.toMap(entry -> keyMapper.get(entry.getKey()), entry -> entry.getValue()));
                 convertedList.add(newMap);
            });
            
            System.out.println(keySet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        convertedList.forEach(System.out::println);
    }

    @XmlRootElement
    public static class Hume
    {
        private Post[] post;

        public Post[] getPost ()
        {
            return post;
        }

        @XmlElement
        public void setPost (Post[] post)
        {
            this.post = post;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [post = "+post+"]";
        }
    }
    
    public static class Post
    {
        private String dato;

        private String term_id;

        private String hovedemnefrase;

        public String getDato ()
        {
            return dato;
        }

        @XmlElement
        public void setDato (String dato)
        {
            this.dato = dato;
        }

        public String getTerm_id ()
        {
            return term_id;
        }

        @XmlElement
        public void setTerm_id (String term_id)
        {
            this.term_id = term_id;
        }

        public String getHovedemnefrase ()
        {
            return hovedemnefrase;
        }

        @XmlElement
        public void setHovedemnefrase (String hovedemnefrase)
        {
            this.hovedemnefrase = hovedemnefrase;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [dato = "+dato+", term_id = "+term_id+", hovedemnefrase = "+hovedemnefrase+"]";
        }
    }
}
    