package van.karm.bid.config;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import van.karm.auction.AuctionServiceGrpc;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class GrpcConfiguration {

    @Bean
    public AuctionServiceGrpc.AuctionServiceBlockingStub auctionServiceStub(
            @Value("${grpc.client.auction-service.security.trust-store.password}") String password,
            @Value("${grpc.client.auction-service.security.trust-store.path}") String trustStorePath,
            @Value("${grpc.client.auction-service.security.trust-store.type}") String trustStoreType
            ) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        ClassPathResource resource = new ClassPathResource(trustStorePath);
        try (InputStream fis = resource.getInputStream()) {
            trustStore.load(fis, password.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SslContext sslContext = GrpcSslContexts.forClient()
                .trustManager(tmf)
                .build();

        ManagedChannel channel = NettyChannelBuilder
                .forAddress("localhost", 5434)
                .sslContext(sslContext)
                .build();

        return AuctionServiceGrpc.newBlockingStub(channel);
    }
}
