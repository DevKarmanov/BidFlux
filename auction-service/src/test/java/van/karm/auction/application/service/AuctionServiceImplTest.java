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
import van.karm.auction.domain.dto.AccessCode;
import van.karm.auction.domain.dto.AccessCodeHash;
import van.karm.auction.domain.model.Auction;
import van.karm.auction.domain.model.CurrencyType;
import van.karm.auction.application.enricher.FieldEnricher;
import van.karm.auction.infrastructure.provider.AuctionAllowedFieldsProvider;
import van.karm.auction.infrastructure.sanitizer.auction.AuctionFieldSanitizerImpl;
import van.karm.auction.infrastructure.security.encode.Encoder;
import van.karm.auction.infrastructure.service.AuctionServiceImpl;
import van.karm.auction.application.validator.AuctionValidator;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.exception.AccessDeniedException;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

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
    @Mock private FieldEnricher auctionFieldEnricher;
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

        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());
        lenient().when(encoder.encode(false)).thenReturn(new AccessAndHashCodes(null, null));

        CreatedAuction result = auctionService.createAuction(jwt, validCreateAuctionDto(false));
        assertNull(result.password());
    }

    @Test
    void testCreatePrivateAuction_ShouldReturnNonNullPassword() {
        Jwt jwt = mock(Jwt.class);
        var codes = new AccessAndHashCodes(new AccessCode("secret"), new AccessCodeHash("hashed"));

        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());
        when(encoder.encode(true)).thenReturn(codes);

        CreatedAuction result = auctionService.createAuction(jwt, validCreateAuctionDto(true));
        assertEquals("secret", result.password());
    }

    @Test
    void testCreateAuction_ShouldThrow_WhenRepoFails() {
        Jwt jwt = mock(Jwt.class);
        var codes = new AccessAndHashCodes(new AccessCode("secret"), new AccessCodeHash("hashed"));

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
        Map<String,Object> filter  = Map.of("id",auctionId);

        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("id", auctionId);
        fieldsMap.put("title", "Auction Title");
        fieldsMap.put("isPrivate", false);
        fieldsMap.put("accessCodeHash", null);

        when(jwt.getClaim("userId")).thenReturn(userId.toString());
        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq(filter),
                eq(LogicalOperator.NONE),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);

        var response = auctionService.getAuctionInfo(jwt, auctionId, null, fields);

        assertEquals("Auction Title", response.data().get("title"));
        assertFalse(response.data().containsKey("codeHash"));
    }

    @Test
    void testGetPrivateAuction_ShouldThrow_WhenPasswordMissing() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id");
        Map<String,Object> filter  = Map.of("id",auctionId);

        Map<String, Object> fieldsMap = Map.of(
                "id", auctionId,
                "isPrivate", true,
                "codeHash", "hashed"
        );

        when(jwt.getClaim("userId")).thenReturn(userId.toString());

        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq(filter),
                eq(LogicalOperator.NONE),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);

        doThrow(new AccessDeniedException("A password must be provided for a private auction"))
                .when(auctionValidator).validate(eq(null),
                        eq(true),
                        eq(auctionId),
                        eq(userId),
                        any());

        assertThrows(AccessDeniedException.class,
                () -> auctionService.getAuctionInfo(jwt, auctionId, null, fields));
    }

    @Test
    void testGetPrivateAuction_ShouldThrow_WhenPasswordIncorrect() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id");
        Map<String,Object> filter  = Map.of("id",auctionId);

        Map<String, Object> fieldsMap = Map.of(
                "id", auctionId,
                "isPrivate", true,
                "codeHash", "hashed"
        );

        when(jwt.getClaim("userId")).thenReturn(userId.toString());

        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq(filter),
                eq(LogicalOperator.NONE),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);
        doThrow(new AccessDeniedException("Invalid password"))
                .when(auctionValidator).validate(eq("wrong"), eq(true), eq(auctionId), eq(userId), any());

        assertThrows(AccessDeniedException.class,
                () -> auctionService.getAuctionInfo(jwt, auctionId, "wrong", fields));
    }

    @Test
    void testGetPrivateAuction_ShouldEnrichAndSanitize() {
        Jwt jwt = mock(Jwt.class);
        UUID auctionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<String> fields = Set.of("id", "ownerName", "allowedUsersCount");
        Map<String,Object> filter  = Map.of("id",auctionId);

        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("id", auctionId);
        fieldsMap.put("isPrivate", true);
        fieldsMap.put("accessCodeHash", "hashed");
        fieldsMap.put("ownerId", UUID.randomUUID());

        when(jwt.getClaim("userId")).thenReturn(userId.toString());

        when(queryExecutor.selectQueryByField(
                eq(Auction.class),
                eq(filter),
                eq(LogicalOperator.NONE),
                eq(fields),
                eq(allowedFieldsProvider),
                any(FieldRule.class)
        )).thenReturn(fieldsMap);

        // Валидатор не выбрасывает исключение
        doNothing().when(auctionValidator).validate(eq("secret"), eq(true), eq(auctionId), eq(userId), any());

        // Энричер добавляет ownerName и allowedUsersCount
        doAnswer(inv -> {
            Map<String, Object> map = inv.getArgument(0);
            map.put("ownerName", "Ivan");
            map.put("allowedUsersCount", 3);
            return null;
        }).when(auctionFieldEnricher).enrich(fieldsMap, fields);

        // Санитайзер удаляет codeHash и ownerId
        doAnswer(inv -> {
            Map<String, Object> map = inv.getArgument(0);
            map.remove("codeHash");
            map.remove("ownerId");
            return null;
        }).when(sanitizer).sanitize(fieldsMap, fields);

        var response = auctionService.getAuctionInfo(jwt, auctionId, "secret", fields);

        assertEquals("Ivan", response.data().get("ownerName"));
        assertEquals(3, response.data().get("allowedUsersCount"));
        assertFalse(response.data().containsKey("codeHash"));
        assertFalse(response.data().containsKey("ownerId"));
    }
}

