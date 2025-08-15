package van.karm.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.CreatedAuction;
import van.karm.auction.model.Auction;
import van.karm.auction.model.AuctionStatus;
import van.karm.auction.model.CurrencyType;
import van.karm.auction.repo.AuctionRepo;
import van.karm.auction.service.AuctionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionControllerImpl.class)
@Import(AuctionServiceImpl.class)
public class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuctionRepo auctionRepo;

    @MockitoSpyBean
    private AuctionServiceImpl auctionService;

    @MockitoBean
    private PasswordEncoder argon2;


    private CreateAuction validPublicCreateAuctionDto() {
        return new CreateAuction(
                "Valid Title",
                "Valid Description",
                new BigDecimal("10"),
                new BigDecimal("1"),
                new BigDecimal("15"),
                false,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusHours(1),
                CurrencyType.USD
        );
    }

    private Auction createAuctionEntity(boolean isPrivate) {
        Auction auction = Auction.builder()
                .id(UUID.randomUUID())
                .title("Название")
                .description("Описание")
                .startPrice(BigDecimal.valueOf(100))
                .bidIncrement(BigDecimal.valueOf(10))
                .reservePrice(BigDecimal.valueOf(150))
                .status(AuctionStatus.ACTIVE)
                .isPrivate(isPrivate)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .currency(CurrencyType.USD)
                .build();
        if (isPrivate) auction.setAccessCodeHash(UUID.randomUUID().toString());
        return auction;
    }


    private ResultActions postCreateAuction(CreateAuction auction) throws Exception {
        return mockMvc.perform(post("/auction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auction)));
    }

    private void postCreateAuctionExpectBadRequest(CreateAuction auction) throws Exception {
        postCreateAuction(auction).andExpect(status().isBadRequest());
    }

    private ResultActions postGetAuction(UUID id, String password) throws Exception {
        MockHttpServletRequestBuilder builder = post("/auction/" + id);
        if (password != null) builder.param("password", password);
        return mockMvc.perform(builder);
    }


    @Test
    void testCreateAuction_withInvalidFields_shouldReturnBadRequest() throws Exception {
        List<CreateAuction> invalidAuctions = List.of(
                validPublicCreateAuctionDto().withTitle(""),
                validPublicCreateAuctionDto().withDescription("TooShort"),
                validPublicCreateAuctionDto().withStartPrice(BigDecimal.valueOf(-1)),
                validPublicCreateAuctionDto().withBidIncrement(BigDecimal.ZERO),
                validPublicCreateAuctionDto().withStartDate(LocalDateTime.now().minusDays(1)),
                validPublicCreateAuctionDto().withEndDate(LocalDateTime.now().minusDays(1)),
                validPublicCreateAuctionDto().withCurrency(null),
                validPublicCreateAuctionDto().withDescription(null),
                validPublicCreateAuctionDto().withStartPrice(null),
                validPublicCreateAuctionDto().withBidIncrement(null)
        );

        for (CreateAuction auction : invalidAuctions) {
            postCreateAuctionExpectBadRequest(auction);
        }
    }


    @Test
    void testCreateAuction_withValidInfo_shouldReturnOkAndDto() throws Exception {
        CreatedAuction createdAuction = new CreatedAuction(UUID.randomUUID(), "password");
        doReturn(createdAuction).when(auctionService).createAuction(any(CreateAuction.class));

        postCreateAuction(validPublicCreateAuctionDto())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void testGetPublicAuctionInfo_withValidInfo_shouldReturnOk() throws Exception {
        when(auctionRepo.findById(any(UUID.class))).thenReturn(Optional.of(createAuctionEntity(false)));

        postGetAuction(UUID.randomUUID(), null).andExpect(status().isOk());
    }

    @Test
    void testGetPrivateAuctionInfo_withValidPassword_shouldReturnOk() throws Exception {
        Auction privateAuction = createAuctionEntity(true);
        when(auctionRepo.findById(any(UUID.class))).thenReturn(Optional.of(privateAuction));
        when(argon2.matches(any(CharSequence.class), any(String.class))).thenReturn(true);

        postGetAuction(UUID.randomUUID(), "password").andExpect(status().isOk());
    }

    @Test
    void testGetPrivateAuctionInfo_withWrongPassword_shouldReturnForbidden() throws Exception {
        Auction privateAuction = createAuctionEntity(true);
        when(auctionRepo.findById(any(UUID.class))).thenReturn(Optional.of(privateAuction));
        when(argon2.matches(any(CharSequence.class), any(String.class))).thenReturn(false);

        postGetAuction(UUID.randomUUID(), "password").andExpect(status().isForbidden());
    }

    @Test
    void testGetAuctionInfo_withNonExistingId_shouldReturnBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        when(auctionRepo.findById(id)).thenReturn(Optional.empty());

        postGetAuction(id, null).andExpect(status().isBadRequest());
        postGetAuction(id, "password").andExpect(status().isBadRequest());
    }
}

