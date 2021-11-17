//package htwb.ai.controller;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import htwb.ai.dao.TestSongDao;
//import htwb.ai.dao.TestUserDao;
//import htwb.ai.model.Song;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class SongControllerTest {
//
//    private MockMvc mockMvc;
//    TestSongDao testSongDao;
//    TestUserDao testUserDao;
//
//    @BeforeEach
//    void setUp() {
//        testSongDao = new TestSongDao("songDB-PU-tests");
//        testUserDao = new TestUserDao("songDB-PU-tests");
//        mockMvc = MockMvcBuilders.standaloneSetup(new SongController(testSongDao, testUserDao)).build();
//        testUserDao.disableForeignKeyCheck();
//        testUserDao.resetSongsInSonglistsTable();
//        testUserDao.resetSonglistsTable();
//        testUserDao.resetSongsTable();
//        testUserDao.resetUserTable();
//        testUserDao.populateUserTable();
//        testUserDao.populateSongsTable();
//        testUserDao.populateSongslistsTable();
//        testUserDao.populateSongsInSonglistsTable();
//    }
//
//    @AfterEach
//    void tearDown() throws InterruptedException {
//        testSongDao.getEntityManagerFactory().close();
//        testUserDao.getEntityManagerFactory().close();
//        Thread.sleep(10000);
//    }
//
//    @Test
//    void getSongsJson_BadTocken() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/json")
//                .header("Authorization", "badTocken"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void getSongsJson() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/json")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.[0].id").value(1))
//                .andExpect(jsonPath("$.[0].title").value("Wrecking Balddls"))
//                .andExpect(jsonPath("$.[0].artist").value("Hallo"))
//                .andExpect(jsonPath("$.[0].label").value("RCA"))
//                .andExpect(jsonPath("$.[0].released").value("0"));
//    }
//
//    //https://stackoverflow.com/questions/22202254/mockhttpservletresponse-checking-xml-content
//    @Test
//    void getSongsXml() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/xml")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_XML))
//                .andExpect(xpath("List/item/id").string("1"))
//                .andExpect(xpath("List/item/title").string("Wrecking Balddls"))
//                .andExpect(xpath("List/item/artist").string("Hallo"))
//                .andExpect(xpath("List/item/label").string("RCA"))
//                .andExpect(xpath("List/item/released").string("0"));
//    }
//
//    @Test
//    void getSongs_NotAcceptable() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/pdf")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNotAcceptable());
//    }
//
//    @Test
//    void getSongsById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/json")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("Wrecking Balddls"))
//                .andExpect(jsonPath("$.artist").value("Hallo"))
//                .andExpect(jsonPath("$.label").value("RCA"))
//                .andExpect(jsonPath("$.released").value("0"));
//    }
//
//    @Test
//    void getSongsById_BadToken() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/json")
//                .header("Authorization", "mmuster1234567"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void getSongsByIdXml() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/xml")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_XML))
//                .andExpect(xpath("Song/id").string("1"))
//                .andExpect(xpath("Song/title").string("Wrecking Balddls"))
//                .andExpect(xpath("Song/artist").string("Hallo"))
//                .andExpect(xpath("Song/label").string("RCA"))
//                .andExpect(xpath("Song/released").string("0"));
//    }
//
//    @Test
//    void getSongsByIdJson_NotFound() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1000")
//                .header("Accept", "application/json")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getSongsByIdXml_NotFound() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1000")
//                .header("Accept", "application/xml")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getSongsByIdJson_NotAcceptable() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/pdf")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNotAcceptable());
//    }
//
//    @Test
//    void getSongsByIdXml_NotAcceptable() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/text")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNotAcceptable());
//    }
//
//    //https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
//    @Test
//    void createSong() throws Exception {
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songs")
//                .header("Authorization", "mmuster123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNoContent())
//                .andExpect(header().string("Location", "/songsWS-Marin/rest/songs/10"));
//    }
//
//    @Test
//    void createSong_BadToken() throws Exception {
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songs")
//                .header("Authorization", "mmuster1234567")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void createSong_tooLongTitle() throws Exception {
//        Song song = new Song();
//        song.setTitle("good titleeqweqweqweqweqweqwpdajsdiasdpoadasdassadasdasdsadssadasdasdasdadsadasdasdasdasdasdasdasdasdasdadadasdasdaospdapsodpasoeqweqwewqewqewqeqweqweqw");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .header("Authorization", "mmuster123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createSong_withoutTitle() throws Exception {
//        Song song = new Song();
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456")
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createSong_wrongContentType() throws Exception {
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = xmlMapper.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_XML_VALUE)
//                .header("Authorization", "mmuster123456")
//                .content(xml))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateSong() throws Exception {
//        Song song_new = new Song();
//        song_new.setId(1);
//        song_new.setTitle("good title_new");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456")
//                .content(requestJson))
//                .andExpect(status().isNoContent());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/json")
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("good title_new"));
//    }
//
//    @Test
//    void updateSong_TooLongTitle() throws Exception {
//        Song song_new = new Song();
//        song_new.setId(1);
//        song_new.setTitle("good title_newaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456")
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateSong_BadToken() throws Exception {
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/songs/1")
//                .header("Authorization", "mmuster1234567")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void updateSong_EmptyTitle() throws Exception {
//
//        Song song_new = new Song();
//        song_new.setId(1);
//        song_new.setTitle("");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456")
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateSong_NotFound() throws Exception {
//
//        Song song_new = new Song();
//        song_new.setId(1000);
//        song_new.setTitle("hello");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1000")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456")
//                .content(requestJson))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void updateSong_BadContentType() throws Exception {
//
//        Song song = new Song();
//        song.setId(1);
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = xmlMapper.writeValueAsString(song);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_XML_VALUE)
//                .header("Authorization", "mmuster123456")
//                .content(xml))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void deleteSong() throws Exception {
//        mockMvc.perform(delete("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNoContent());
//
//    }
//
//    @Test
//    void deleteSong_BadToken() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/songs/1")
//                .header("Authorization", "mmuster1234567")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void deleteSong_NotFound() throws Exception {
//        mockMvc.perform(delete("/songs/200")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "mmuster123456"))
//                .andExpect(status().isNotFound());
//    }
//
//}
//
//
//
//
///*
//package htwb.ai.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import htwb.ai.dao.TestSongDao;
//import htwb.ai.dao.TestUserDao;
//import htwb.ai.model.Song;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class SongControllerTest {
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new SongController(new TestSongDao(), new TestUserDao())).build();
//    }
//
//    @Test
//    void getSongsJson() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/json"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.[0].id").value(1))
//                .andExpect(jsonPath("$.[0].title").value("title"))
//                .andExpect(jsonPath("$.[0].artist").value("artist"))
//                .andExpect(jsonPath("$.[0].label").value("label"))
//                .andExpect(jsonPath("$.[0].released").value("2000"));
//
//    }
//
//    //https://stackoverflow.com/questions/22202254/mockhttpservletresponse-checking-xml-content
//    @Test
//    void getSongsXml() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/xml"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_XML))
//                .andExpect(xpath("List/item/id").string("1"))
//                .andExpect(xpath("List/item/title").string("title"))
//                .andExpect(xpath("List/item/artist").string("artist"))
//                .andExpect(xpath("List/item/label").string("label"))
//                .andExpect(xpath("List/item/released").string("2000"));
//
//    }
//
//    @Test
//    void getSongs_NotFound() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs")
//                .header("Accept", "application/pdf"))
//                .andExpect(status().isNotAcceptable());
//    }
//
//    @Test
//    void getSongsById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/json"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("title"))
//                .andExpect(jsonPath("$.artist").value("artist"))
//                .andExpect(jsonPath("$.label").value("label"))
//                .andExpect(jsonPath("$.released").value("2000"));
//    }
//
//    @Test
//    void getSongsByIdXml() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/xml"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_XML))
//                .andExpect(xpath("Song/id").string("1"))
//                .andExpect(xpath("Song/title").string("title"))
//                .andExpect(xpath("Song/artist").string("artist"))
//                .andExpect(xpath("Song/label").string("label"))
//                .andExpect(xpath("Song/released").string("2000"));
//    }
//
//
//    @Test
//    void getSongsByIdJson_NotFound() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/100")
//                .header("Accept", "application/json"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getSongsByIdXml_NotFound() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/100")
//                .header("Accept", "application/xml"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getSongsByIdJson_BadRequest() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/pdf"))
//                .andExpect(status().isNotAcceptable());
//    }
//
//    @Test
//    void getSongsByIdXml_BadRequest() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/songs/1")
//                .header("Accept", "application/text"))
//                .andExpect(status().isNotAcceptable());
//    }
//
//    //https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
//    @Test
//    void createSong() throws Exception {
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNoContent())
//                .andExpect(header().string("Location", "/songsWS-Marin/rest/songs/1"));
//    }
//
//    @Test
//    void createSong_tooLongTitle() throws Exception {
//        Song song = new Song();
//        song.setTitle("good titleeqweqweqweqweqweqwpdajsdiasdpoasdasdaospdapsodpasoeqweqwewqewqewqeqweqweqw");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createSong_withoutTitle() throws Exception {
//        Song song = new Song();
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createSong_wrongContentType() throws Exception {
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = xmlMapper.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_XML_VALUE)
//                .content(xml))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createSong_NullPointerException() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new SongController(null, null)).build();
//
//        Song song = new Song();
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song);
//
//        mockMvc.perform(post("/songs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void updateSong() throws Exception {
//        Song song = new Song();
//        song.setId(1);
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//
//        Song song_new = new Song();
//        song_new.setId(1);
//        song_new.setTitle("good title_new");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void updateSong_tooLongTitle() throws Exception {
//        Song song_new = new Song();
//        song_new.setId(1);
//        song_new.setTitle("good titleeqweqweqweqweqweqwpdajsdiasdpoasdasdaospdapsodpasoeqweqwewqewqewqeqweqweqw");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateSong_EmptyTitle() throws Exception {
//
//        Song song_new = new Song();
//        song_new.setId(1);
//        song_new.setTitle("");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateSong_NotFound() throws Exception {
//
//        Song song_new = new Song();
//        song_new.setId(2);
//        song_new.setTitle("hello");
//        song_new.setArtist("good artist_new");
//        song_new.setLabel("good lable_new");
//        song_new.setReleased(2006);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(song_new);
//
//        mockMvc.perform(put("/songs/2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void updateSong_BadContentType() throws Exception {
//
//        Song song = new Song();
//        song.setId(1);
//        song.setTitle("good title");
//        song.setArtist("good artist");
//        song.setLabel("good lable");
//        song.setReleased(2005);
//
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = xmlMapper.writeValueAsString(song);
//
//        mockMvc.perform(put("/songs/1")
//                .contentType(MediaType.APPLICATION_XML_VALUE)
//                .content(xml))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void deleteSong() throws Exception {
//        mockMvc.perform(delete("/songs/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//
//    }
//    @Test
//    void deleteSong_NotFoundPersistenceException() throws Exception {
//        mockMvc.perform(delete("/songs/100")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//
//    @Test
//    void deleteSong_NotFound() throws Exception {
//        mockMvc.perform(delete("/songs/2")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//
//    }
//}
//*/