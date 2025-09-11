package van.karm.auction.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.domain.dto.AccessAndHashCodes;
import van.karm.auction.domain.model.Auction;
import van.karm.auction.domain.model.CurrencyType;
import van.karm.auction.infrastructure.enricher.AuctionFieldEnricher;
import van.karm.auction.infrastructure.provider.AuctionAllowedFieldsProvider;
import van.karm.auction.infrastructure.sanitizer.AuctionFieldSanitizerImpl;
import van.karm.auction.infrastructure.security.encode.Encoder;
import van.karm.auction.infrastructure.service.AuctionServiceImpl;
import van.karm.auction.infrastructure.validator.AuctionValidator;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.exception.AccessDeniedException;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.application.rule.FieldRule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {

    @Mock private TransactionTemplate transactionTemplate;
    @Mock private QueryExecutor queryExecutor;
    @Mock private Encoder encoder;
    @Mock private AuctionFieldEnricher auctionFieldEnricher;
    @Spy private final AuctionFieldSanitizerImpl sanitizer = new AuctionFieldSanitizerImpl();
    @Spy private AllowedFieldsProvider allowedFieldsProvider = new AuctionAllowedFieldsProvider();
    @Mock private FieldRule fieldRule;
    @Mock private AuctionValidator auctionValidator;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private CreateAuction validCreateAuctionDto(boolean isPrivate) {
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

    @Test
    void testCreatePublicAuction_ShouldReturnNullPassword() {
        Jwt jwt = mock(Jwt.class);

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());

        when(encoder.encode(false)).thenReturn(new AccessAndHashCodes(null, null));

        CreatedAuction result = auctionService.createAuction(jwt, validCreateAuctionDto(false));
        assertNull(result.password());
    }

    @Test
    void testCreatePrivateAuction_ShouldReturnNonNullPassword() {
        Jwt jwt = mock(Jwt.class);
        var codes = new AccessAndHashCodes("secret", "hashed");

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());
        when(encoder.encode(true)).thenReturn(codes);

        CreatedAuction result = auctionService.createAuction(jwt, validCreateAuctionDto(true));
        assertEquals("secret", result.password());
    }

    @Test
    void testCreateAuction_ShouldThrow_WhenJwtMissingUserId() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.hasClaim("userId")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> auctionService.createAuction(jwt, validCreateAuctionDto(true)));
    }

    @Test
    void testCreateAuction_ShouldThrow_WhenRepoFails() {
        Jwt jwt = mock(Jwt.class);
        var codes = new AccessAndHashCodes("secret", "hashed");

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());
        when(encoder.encode(true)).thenReturn(codes);
        doAnswer(invocation -> {
            throw new DataAccessResourceFailureException("DB error");
        }).when(transactionTemplate).executeWithoutResult(any());


        assertThrows(DataAccessResourceFailureException.class, () -> auctionService.createAuction(jwt, validCreateAuctionDto(true)));
    }

    @Test
    void testGetPublicAuction_ShouldReturnSanitizedResponse() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id", "title");

        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("id", auctionId);
        fieldsMap.put("title", "Auction Title");
        fieldsMap.put("isPrivate", false);
        fieldsMap.put("accessCodeHash", null);

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(userId.toString());
        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq("id"),
                eq(auctionId),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);

        var response = auctionService.getAuctionInfo(jwt, auctionId, null, fields);

        assertEquals("Auction Title", response.data().get("title"));
        assertFalse(response.data().containsKey("accessCodeHash"));
    }

    @Test
    void testGetPrivateAuction_ShouldThrow_WhenPasswordMissing() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id");

        Map<String, Object> fieldsMap = Map.of(
                "id", auctionId,
                "isPrivate", true,
                "accessCodeHash", "hashed"
        );

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(userId.toString());

        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq("id"),
                eq(auctionId),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);
        doThrow(new AccessDeniedException("A password must be provided for a private auction"))
                .when(auctionValidator).validate(null, true, auctionId, userId, "hashed");

        assertThrows(AccessDeniedException.class,
                () -> auctionService.getAuctionInfo(jwt, auctionId, null, fields));
    }

    @Test
    void testGetPrivateAuction_ShouldThrow_WhenPasswordIncorrect() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id");

        Map<String, Object> fieldsMap = Map.of(
                "id", auctionId,
                "isPrivate", true,
                "accessCodeHash", "hashed"
        );

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(userId.toString());

        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq("id"),
                eq(auctionId),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);
        doThrow(new AccessDeniedException("Invalid password"))
                .when(auctionValidator).validate("wrong", true, auctionId, userId, "hashed");

        assertThrows(AccessDeniedException.class,
                () -> auctionService.getAuctionInfo(jwt, auctionId, "wrong", fields));
    }

    @Test
    void testGetPrivateAuction_ShouldEnrichAndSanitize() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id", "ownerName", "allowedUsersCount");

        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("id", auctionId);
        fieldsMap.put("isPrivate", true);
        fieldsMap.put("accessCodeHash", "hashed");
        fieldsMap.put("ownerId", UUID.randomUUID());

        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(userId.toString());

        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq("id"),
                eq(auctionId),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);

        // Валидатор не выбрасывает исключение
        doNothing().when(auctionValidator).validate("secret", true, auctionId, userId, "hashed");

        // Энричер добавляет ownerName и allowedUsersCount
        doAnswer(inv -> {
            Map<String, Object> map = inv.getArgument(0);
            map.put("ownerName", "Ivan");
            map.put("allowedUsersCount", 3);
            return null;
        }).when(auctionFieldEnricher).enrich(fieldsMap, true, fields, auctionId);

        // Санитайзер удаляет accessCodeHash и ownerId
        doAnswer(inv -> {
            Map<String, Object> map = inv.getArgument(0);
            map.remove("accessCodeHash");
            map.remove("ownerId");
            return null;
        }).when(sanitizer).sanitize(fieldsMap, fields);

        var response = auctionService.getAuctionInfo(jwt, auctionId, "secret", fields);

        assertEquals("Ivan", response.data().get("ownerName"));
        assertEquals(3, response.data().get("allowedUsersCount"));
        assertFalse(response.data().containsKey("accessCodeHash"));
        assertFalse(response.data().containsKey("ownerId"));
    }
}

