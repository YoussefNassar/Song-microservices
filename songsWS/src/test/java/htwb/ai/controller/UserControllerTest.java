//package htwb.ai.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import htwb.ai.dao.TestUserDao;
//import htwb.ai.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class UserControllerTest {
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new UserController(new TestUserDao("songDB-PU-tests"))).build();
//
//    }
//
//    @Test
//    void authenticateUser() throws Exception {
//        User user = new User();
//        user.setUserId("mmuster");
//        user.setPassword("pass1234");
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(user);
//
//        System.out.println(requestJson);
//
//        mockMvc.perform(post("/auth")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.TEXT_PLAIN));
//    }
//
//    @Test
//    void authenticateUser_BadLogin() throws Exception {
//        User user = new User();
//        user.setUserId("mmuster");
//        user.setPassword("pass12345");
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(user);
//
//        System.out.println(requestJson);
//
//        mockMvc.perform(post("/auth")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void authenticateUser_BadLoginNoUserId() throws Exception {
//        User user = new User();
//        user.setUserId("");
//        user.setPassword("pass12345");
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(user);
//
//        System.out.println(requestJson);
//
//        mockMvc.perform(post("/auth")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void authenticateUser_BadLoginNoPassword() throws Exception {
//        User user = new User();
//        user.setUserId("mmuster");
//        user.setPassword("");
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(user);
//
//        System.out.println(requestJson);
//
//        mockMvc.perform(post("/auth")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//
//    @Test
//    void authenticateUser_BadContentType() throws Exception {
//        User user = new User();
//        user.setUserId("mmuster");
//        user.setPassword("pass1234");
//
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = xmlMapper.writeValueAsString(user);
//
//        mockMvc.perform(post("/auth")
//                .contentType(MediaType.APPLICATION_XML)
//                .content(xml))
//                .andExpect(status().isBadRequest());
//    }
//
//}