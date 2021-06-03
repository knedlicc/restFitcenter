package cz.cvut.kbss.ear.rest;

import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.PermanentCard;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.service.PermanentCardService;
import cz.cvut.kbss.ear.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseControllerTestRunner{


    @Mock
    private PermanentCardService permanentCardServiceMock;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private UserController sut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        super.setUp(sut);
    }


    @Test
    public void addPermanentCardToUser_success() throws Exception {
        final PermanentCard permanentCard = Generator.generatePermanentCard();
        permanentCard.setId(111);
        final User user = Generator.generateUser();
        user.setId(123);
        when(permanentCardServiceMock.find(any())).thenReturn(permanentCard);
        when(userServiceMock.find(any())).thenReturn(user);

        mockMvc.perform(put("/rest/users/" + user.getId() + "/card").content(toJson(permanentCard)).contentType(
                MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());

        final ArgumentCaptor<PermanentCard> captor = ArgumentCaptor.forClass(PermanentCard.class);
        verify(permanentCardServiceMock).addPermanentCardToUser(eq(user),captor.capture());
        assertEquals(permanentCard.getId(), captor.getValue().getId());
    }

}
