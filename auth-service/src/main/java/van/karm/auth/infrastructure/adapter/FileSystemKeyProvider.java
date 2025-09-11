package van.karm.auth.infrastructure.adapter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import van.karm.auth.application.port.KeyProvider;
import van.karm.auth.infrastructure.config.props.JwtProperties;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Component
public class FileSystemKeyProvider implements KeyProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public FileSystemKeyProvider(JwtProperties props) throws Exception {
        this.privateKey = loadPrivateKey(props.privateKeyPath());
        this.publicKey = loadPublicKey(props.publicKeyPath());
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        Resource resource = new ClassPathResource(path);
        String key = Files.readString(resource.getFile().toPath())
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        Resource resource = new ClassPathResource(path);
        String key = Files.readString(resource.getFile().toPath())
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
