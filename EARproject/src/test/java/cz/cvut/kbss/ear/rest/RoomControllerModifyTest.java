package cz.cvut.kbss.ear.rest;

import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.service.GroupLectureService;
import cz.cvut.kbss.ear.service.RoomService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoomControllerModifyTest extends BaseControllerTestRunner {
    @Mock
    private RoomService roomServiceMock;
    @Mock
    private GroupLectureService lectureServiceMock;
    @InjectMocks
    private RoomController sut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        super.setUp(sut);
    }

    @Test
    public void createRoomCreatesRoomUsingService() throws Exception {
        final Room toCreate = new Room();
        toCreate.setName("Room");
        final ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

        mockMvc.perform(post("/rest/rooms").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        verify(roomServiceMock).persist(captor.capture());
        assertEquals(toCreate.getName(), captor.getValue().getName());
    }

    @Test
    public void addGroupLectureToRoomAddsLectureToSpecifiedRoom() throws Exception {
        final Room room = new Room();
        room.setName("Room");
        room.setId(Generator.randomInt());
        when(roomServiceMock.find(any())).thenReturn(room);
        final GroupLecture lecture = Generator.generateGroupLecture();
        lecture.setId(Generator.randomInt());
        mockMvc.perform(post("/rest/rooms/" + room.getId() + "/lectures").content(toJson(lecture)).contentType(
                MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());
        final ArgumentCaptor<GroupLecture> captor = ArgumentCaptor.forClass(GroupLecture.class);
        verify(roomServiceMock).addGroupLectureToSchedule(captor.capture(), eq(room));
        assertEquals(lecture.getId(), captor.getValue().getId());
    }

}
