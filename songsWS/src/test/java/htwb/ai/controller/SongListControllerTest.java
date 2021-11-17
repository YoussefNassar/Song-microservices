//package htwb.ai.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import htwb.ai.dao.TestSongDao;
//import htwb.ai.dao.TestSongListDAO;
//import htwb.ai.dao.TestUserDao;
//import htwb.ai.model.Song;
//import htwb.ai.model.SongList;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.core.Is.isA;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//class SongListControllerTest {
//
//    private MockMvc mockMvc;
//    TestSongDao testSongDao;
//    TestUserDao testUserDao;
//    TestSongListDAO testSongListDAO;
//
//    @BeforeEach
//    void setUp() {
//        testSongDao = new TestSongDao("songDB-PU-tests");
//        testUserDao = new TestUserDao("songDB-PU-tests");
//        testSongListDAO = new TestSongListDAO("songDB-PU-tests");
//        mockMvc = MockMvcBuilders.standaloneSetup(new SongListController(testSongListDAO, testUserDao, testSongDao)).build();
//    }
//
//    @AfterEach
//    void tearDown() throws InterruptedException {
//        testSongDao.getEntityManagerFactory().close();
//        testUserDao.getEntityManagerFactory().close();
//        testSongListDAO.getEntityManagerFactory().close();
//        //Thread.sleep(10000);
//    }
//
//    @Test
//    void getSongListWithUserId() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songLists?userId=youssef")
//                .header("Accept", "application/json")
//                .header("Authorization", "youssef123456"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.[0].name").value("ylist"))
//                .andExpect(jsonPath("$.[0].isPrivate").value(false))
//                .andExpect(jsonPath("$.[0].songList", isA(ArrayList.class)))
//                .andExpect(jsonPath("$.[0].songList", hasSize(2)))
//                .andExpect(jsonPath("$.*", isA(ArrayList.class)));
//
//    }
//
//    @Test
//    void getSongListWithUserId_BadUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songLists?userId=youssef")
//                .header("Accept", "application/json")
//                .header("Authorization", "youssef1234567"))
//                .andExpect(status().isUnauthorized());
//
//    }
//
//    @Test
//    void getSongListWithUserId_EmptyList() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songLists?userId=eschuler")
//                .header("Accept", "application/json")
//                .header("Authorization", "eschuler123456"))
//                .andExpect(status().isNotFound());
//
//    }
//
//    @Test
//    void getSongListWithSongId() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songLists/1")
//                .header("Accept", "application/json")
//                .header("Authorization", "youssef123456"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.name").value("ylist"))
//                .andExpect(jsonPath("$.isPrivate").value(false))
//                .andExpect(jsonPath("$.songList", isA(ArrayList.class)))
//                .andExpect(jsonPath("$.songList", hasSize(2)));
//
//    }
//
//    @Test
//    void getSongListWithSongId_BadUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songLists/1")
//                .header("Accept", "application/json")
//                .header("Authorization", "youssef1234567"))
//                .andExpect(status().isUnauthorized());
//
//    }
//
//    @Test
//    void getSongListWithSongId_PrivateListFromDifferentUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/songLists/3")
//                .header("Accept", "application/json")
//                .header("Authorization", "youssef123456"))
//                .andExpect(status().isForbidden());
//
//    }
//
//    @Test
//    void deleteSongList() throws Exception {
//
//
//        List<Song> songs = new ArrayList<Song>();
//        songs.add(testSongDao.getSongById(1).get(0));
//
//        SongList songlist = new SongList();
//
//        songlist.setOwnerId("youssef");
//        songlist.setIsPrivate(true);
//        songlist.setName("testList");
//        songlist.setSongList(songs);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(songlist);
//
//        // https://stackoverflow.com/a/18336481/6400752
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/songLists")
//                .header("Authorization", "youssef123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNoContent())
//                .andExpect(header().exists("Location"))
//                .andReturn();
//
//        String location_header = result.getResponse().getHeader("Location").replace("/songsWS-Marin/rest", "");
//
//        mockMvc.perform(MockMvcRequestBuilders.delete(location_header)
//                .header("Authorization", "youssef123456"))
//                .andExpect(status().isNoContent());
//
//    }
//
//    @Test
//    void deleteSongList_BadUser() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/songLists/11")
//                .header("Authorization", "youssef1234567"))
//                .andExpect(status().isUnauthorized());
//
//    }
//
//    @Test
//    void deleteSongList_FromSomeoneElse() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/songLists/3")
//                .header("Authorization", "youssef123456"))
//                .andExpect(status().isForbidden());
//
//    }
//
//    @Test
//    void createSongList() throws Exception {
//
//
//        List<Song> songs = new ArrayList<Song>();
//        songs.add(testSongDao.getSongById(1).get(0));
//
//        SongList songlist = new SongList();
//
//        songlist.setOwnerId("youssef");
//        songlist.setIsPrivate(true);
//        songlist.setName("testList");
//        songlist.setSongList(songs);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(songlist);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songLists")
//                .header("Authorization", "youssef123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isNoContent())
//                .andExpect(header().exists("Location"));
//
//
//    }
//
//    @Test
//    void createSongList_BadUser() throws Exception {
//
//
//        List<Song> songs = new ArrayList<Song>();
//        songs.add(testSongDao.getSongById(1).get(0));
//
//        SongList songlist = new SongList();
//
//        songlist.setOwnerId("youssef");
//        songlist.setIsPrivate(true);
//        songlist.setName("testList");
//        songlist.setSongList(songs);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(songlist);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songLists")
//                .header("Authorization", "youssef1234567")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isUnauthorized());
//
//
//    }
//
//    @Test
//    void createSongList_EmptySongName() throws Exception {
//
//        Song song = testSongDao.getSongById(1).get(0);
//        song.setTitle("");
//
//        List<Song> songs = new ArrayList<Song>();
//        songs.add(song);
//
//        SongList songlist = new SongList();
//
//        songlist.setOwnerId("youssef");
//        songlist.setIsPrivate(true);
//        songlist.setName("testList");
//        songlist.setSongList(songs);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(songlist);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songLists")
//                .header("Authorization", "youssef123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//
//
//    }
//
//    @Test
//    void createSongList_BadSongID() throws Exception {
//
//        Song song = new Song();
//        song.setId(9999);
//
//        List<Song> songs = new ArrayList<Song>();
//        songs.add(song);
//
//        SongList songlist = new SongList();
//
//        songlist.setOwnerId("youssef");
//        songlist.setIsPrivate(true);
//        songlist.setName("testList");
//        songlist.setSongList(songs);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(songlist);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songLists")
//                .header("Authorization", "youssef123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//
//
//    }
//
//    @Test
//    void createSongList_TooLongName() throws Exception {
//
//        List<Song> songs = new ArrayList<Song>();
//        songs.add(testSongDao.getSongById(1).get(0));
//
//        SongList songlist = new SongList();
//
//        songlist.setOwnerId("youssef");
//        songlist.setIsPrivate(true);
//        songlist.setName("testListaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        songlist.setSongList(songs);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//        String requestJson = objectWriter.writeValueAsString(songlist);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/songLists")
//                .header("Authorization", "youssef123456")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestJson))
//                .andExpect(status().isBadRequest());
//
//
//    }
//}