package van.karm.auction.application.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.auction.presentation.dto.request.ChangePasswordRequest;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

public interface AuctionService {
    CreatedAuction createAuction(Jwt jwt, CreateAuction auctionInfo);
    DynamicResponse getAuctionInfo(Jwt jwt, UUID id, String password, Set<String> fields);
    PagedResponse getAllAuctions(Set<String> fields, int size, int page);
    void deleteAuction(UUID id, Jwt jwt);
    void forcedDeleteAuction(UUID id);
    void changePassword(Jwt jwt, UUID id, ChangePasswordRequest request);
}
