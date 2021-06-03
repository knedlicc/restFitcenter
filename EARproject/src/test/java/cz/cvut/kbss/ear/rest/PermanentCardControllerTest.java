package cz.cvut.kbss.ear.rest;


import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.PermanentCard;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.rest.handler.ErrorInfo;
import cz.cvut.kbss.ear.service.PermanentCardService;
import cz.cvut.kbss.ear.service.UserService;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PermanentCardControllerTest extends BaseControllerTestRunner{

    @Mock
    private PermanentCardService permanentCardServiceMock;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private PermanentCardController sut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        super.setUp(sut);
    }


    @Test
    public void getByIdThrowsNotFoundForUnknownId() throws Exception {
        final int id = 65656;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/permanent_cards/" + id)).andExpect(status().isNotFound())
                .andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        MatcherAssert.assertThat(result.getMessage(), containsString("PermanentCard identified by "));
        MatcherAssert.assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }


    @Test
    public void getByIdReturnsPermanentCardWithMatchingId() throws Exception {
        final PermanentCard permanentCard = Generator.generatePermanentCard();
        permanentCard.setId(123);
        when(permanentCardServiceMock.find(permanentCard.getId())).thenReturn(permanentCard);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/permanent_cards/" + permanentCard.getId())).andReturn();
        final PermanentCard result = readValue(mvcResult, PermanentCard.class);
        assertNotNull(result);
        assertEquals(permanentCard.getId(), result.getId());
        assertEquals(permanentCard.getCode(), result.getCode());
        assertEquals(permanentCard.getValidFrom(),result.getValidFrom());
    }

    @Test
    public void getCardsReturnsAllCards() throws Exception {
        final List<PermanentCard> per = IntStream.range(0, 5).mapToObj(i -> Generator.generatePermanentCard()).collect(
                Collectors.toList());
        when(permanentCardServiceMock.findAll()).thenReturn(per);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/permanent_cards/")).andReturn();
        final List<PermanentCard> result = readValue(mvcResult, new TypeReference<List<PermanentCard>>() {
        });
        assertNotNull(result);
        assertEquals(per.size(), result.size());
        for (int i = 0; i < per.size(); i++) {
            assertEquals(per.get(i).getId(), result.get(i).getId());
            assertEquals(per.get(i).getCode(), result.get(i).getCode());
        }
    }



    @Test
    public void createCardCreatesCardUsingService() throws Exception {
        final PermanentCard toCreate = Generator.generatePermanentCard();

        mockMvc.perform(post("/rest/permanent_cards").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        final ArgumentCaptor<PermanentCard> captor = ArgumentCaptor.forClass(PermanentCard.class);
        verify(permanentCardServiceMock).persist(captor.capture());
        assertEquals(toCreate.getCode(), captor.getValue().getCode());
    }

}
