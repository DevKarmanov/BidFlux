package van.karm.auction.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import van.karm.auction.application.service.AuctionService;
import van.karm.auction.domain.model.CurrencyType;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.presentation.controller.auction.AuctionController;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.exception.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@AutoConfigureMockMvc
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuctionRepo auctionRepo;

    @MockitoBean
    private AuctionService auctionService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private CreateAuction validAuctionDto(boolean isPrivate) {
        return new CreateAuction(
                "Valid Title",
                "Valid Description",
                new BigDecimal("10"),
                new BigDecimal("1"),
                new BigDecimal("15"),
                isPrivate,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusHours(1),
                CurrencyType.USD
        );
    }

    private ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .with(withJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    private ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .with(withJwt())
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetWithParams(String url, MultiValueMap<String, String> params) throws Exception {
        MockHttpServletRequestBuilder builder = get(url)
                .with(withJwt())
                .contentType(MediaType.APPLICATION_JSON);

        if (params != null) {
            params.forEach((key, values) -> values.forEach(value -> builder.param(key, value)));
        }

        return mockMvc.perform(builder);
    }


    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private RequestPostProcessor withJwt() {
        return jwt().jwt(jwt -> jwt.claim("userId", UUID.randomUUID().toString()));
    }


    @Test
    void testCreateAuction_ShouldReturn201() throws Exception {
        CreatedAuction created = new CreatedAuction(UUID.randomUUID(), "secret");

        when(auctionService.createAuction(any(), any())).thenReturn(created);

        performPost("/auction", validAuctionDto(true))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.password").value("secret"));
    }

    @Test
    void testCreateAuction_ShouldReturn400_WhenDtoInvalid() throws Exception {
        CreateAuction invalidDto = validAuctionDto(true).withTitle("");

        performPost("/auction", invalidDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuctionInfo_ShouldReturn200_ForPublicAuction() throws Exception {
        Map<String, Object> data = Map.of("id", UUID.randomUUID(), "title", "Public Auction");
        when(auctionService.getAuctionInfo(any(), any(), any(), any())).thenReturn(new DynamicResponse(data));

        performGet("/auction/" + UUID.randomUUID())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Public Auction"));
    }

    @Test
    void testAuctionInfo_ShouldReturn403_WhenPasswordMissingForPrivateAuction() throws Exception {
        when(auctionService.getAuctionInfo(any(), any(), eq(null), any()))
                .thenThrow(new AccessDeniedException("Password is required"));

        performGet("/auction/" + UUID.randomUUID())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Password is required"));
    }

    @Test
    void testAuctionInfo_ShouldReturn404_WhenAuctionNotFound() throws Exception {
        UUID auctionId = UUID.randomUUID();
        when(auctionService.getAuctionInfo(any(), eq(auctionId), any(), any()))
                .thenThrow(new EntityNotFoundException("Auction not found"));

        performGet("/auction/" + auctionId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Auction not found"));
    }

    @Test
    void testAuctionInfo_ShouldReturn500_OnUnexpectedError() throws Exception {
        UUID auctionId = UUID.randomUUID();
        when(auctionService.getAuctionInfo(any(), eq(auctionId), any(), any()))
                .thenThrow(new RuntimeException("Unexpected"));

        performPost("/auction/" + auctionId,null)
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testAuctionInfo_ShouldReturn200_WithFieldsParam() throws Exception {
        UUID auctionId = UUID.randomUUID();
        Map<String, Object> data = Map.of("id", auctionId, "ownerName", "Ivan");

        when(auctionService.getAuctionInfo(any(), eq(auctionId), any(), eq(Set.of("id", "ownerName"))))
                .thenReturn(new DynamicResponse(data));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("fields", "id");
        params.add("fields", "ownerName");

        performGetWithParams("/auction/" + auctionId, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownerName").value("Ivan"));
    }

}
