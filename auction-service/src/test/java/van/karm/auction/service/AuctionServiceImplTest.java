package van.karm.auction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.CreatedAuction;
import van.karm.auction.exception.AccessDeniedException;
import van.karm.auction.model.Auction;
import van.karm.auction.model.AuctionStatus;
import van.karm.auction.model.CurrencyType;
import van.karm.auction.repo.AuctionRepo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {
    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private AuctionRepo auctionRepo;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    @Mock
    private PasswordEncoder argon2;

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
                .allowedUserIds(new HashSet<>())
                .currency(CurrencyType.USD)
                .build();
        if (isPrivate) auction.setAccessCodeHash(UUID.randomUUID().toString());
        return auction;
    }

    @Test
    void testCreateAuction_ShouldReturnValidPassword() {
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });

        when(argon2.encode(any(String.class))).thenReturn("passwordHash");

        CreatedAuction publicAuction = auctionService.createAuction(validCreateAuctionDto(false));
        CreatedAuction privateAuction = auctionService.createAuction(validCreateAuctionDto(true));

        assertNull(publicAuction.password());
        assertNotNull(privateAuction.password());
    }

    @ParameterizedTest
    @ValueSource(strings = { "password" })
    void testGetPrivateAuction_ShouldThrow_WhenPasswordDoesNotMatch(String password) {
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());

        when(auctionRepo.findById(any(UUID.class))).thenReturn(Optional.of(createAuctionEntity(true)));
        when(argon2.matches(any(CharSequence.class), any(String.class))).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> auctionService.getAuctionInfo(jwt, UUID.randomUUID(), password));
    }

    @Test
    void testGetPrivateAuction_ShouldThrow_WhenPasswordIsNull() {
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn(UUID.randomUUID().toString());

        when(auctionRepo.findById(any(UUID.class))).thenReturn(Optional.of(createAuctionEntity(true)));

        assertThrows(AccessDeniedException.class,
                () -> auctionService.getAuctionInfo(jwt, UUID.randomUUID(), null));
    }



}
