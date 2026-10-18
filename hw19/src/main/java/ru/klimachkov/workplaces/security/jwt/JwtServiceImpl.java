package ru.klimachkov.workplaces.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

/**
 * Реализация сервиса {@link JwtService} для работы с JWT-токенами.
 *
 * @see JwtService
 * @see JwtProperties
 * @see JwtTokenType
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    /**
     * Claim, содержащий тип JWT-токена
     *
     * <p>Используется для разделения access и refresh токенов при обработке JWT.</p>
     */
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    /**
     * Имя cookie, в которой хранится refresh-токен пользователя
     */
    private static final String REFRESH_COOKIE_NAME = "RefreshTokenCookie";

    /**
     * Параметры конфигурации JWT
     */
    private final JwtProperties jwtProperties;

    /**
     * Генерирует JWT access-токен для указанного пользователя.
     *
     * <p>Создаваемый токен содержит информацию о пользователе,
     * тип токена {@link JwtTokenType#ACCESS} и срок действия,
     * заданный в настройках приложения.</p>
     *
     * @param userDetails данные пользователя
     * @return сгенерированный access-токен
     */
    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, JwtTokenType.ACCESS, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Генерирует JWT refresh-токен для указанного пользователя.
     *
     * <p>Создаваемый токен содержит информацию о пользователе,
     * тип токена {@link JwtTokenType#REFRESH} и срок действия,
     * заданный в настройках приложения.</p>
     *
     * @param userDetails данные пользователя
     * @return сгенерированный refresh-токен
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, JwtTokenType.REFRESH, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Извлекает JWT-токен из HTTP-заголовка запроса.
     *
     * <p>Токен извлекается из заголовка {@code Authorization}
     * с использованием схемы {@code Bearer}.</p>
     *
     * @param request HTTP-запрос
     * @return JWT-токен,
     * либо {@code null}, если токен отсутствует
     */
    @Override
    public String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : null;
    }

    /**
     * Проверяет валидность JWT-токена.
     *
     * <p>Проверяет соответствие пользователя, указанного в токене,
     * переданным данным пользователя, а также срок действия токена.</p>
     *
     * @param token JWT-токен
     * @param userDetails данные пользователя
     * @return {@code true}, если токен является валидным,
     * иначе {@code false}
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception ex) {
            log.warn("Error occurred while validating JWT token");
            return false;
        }
    }

    /**
     * Проверяет валидность refresh-токена пользователя.
     *
     * <p>Дополнительно к стандартной проверке JWT выполняет проверку,
     * что тип токена соответствует {@link JwtTokenType#REFRESH}.</p>
     *
     * @param token JWT-токен
     * @param userDetails данные пользователя
     * @return {@code true}, если токен является валидным refresh-токеном,
     * иначе {@code false}
     */
    @Override
    public boolean isValidRefreshToken(String token, UserDetails userDetails) {
        if (!isTokenValid(token, userDetails)) {
            return false;
        }

        return extractTokenType(token) == JwtTokenType.REFRESH;
    }

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * <p>Имя пользователя извлекается из стандартного claim {@code subject}.</p>
     *
     * @param token JWT-токен
     * @return имя пользователя, указанное в токене
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Добавляет refresh-токен в HTTP-only cookie ответа.
     *
     * <p>Cookie создается с параметрами безопасности, заданными в конфигурации приложения.</p>
     *
     * @param response HTTP-ответ
     * @param refreshToken refresh-токен
     */
    @Override
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken, jwtProperties.getRefreshCookieMaxAge());
        response.addCookie(refreshTokenCookie);
    }

    /**
     * Извлекает refresh-токен из cookie HTTP-запроса.
     *
     * @param request HTTP-запрос
     * @return значение refresh-токена из cookie,
     * либо {@code null}, если cookie отсутствует
     */
    @Override
    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * Удаляет refresh-токен cookie.
     *
     * <p>Для удаления cookie создается новая cookie с тем же именем и нулевым временем жизни.</p>
     *
     * @param response HTTP-ответ
     */
    @Override
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie clearedCookie = createRefreshTokenCookie(null, 0);
        response.addCookie(clearedCookie);
    }

    /**
     * Создает JWT-токен указанного типа.
     *
     * <p>В токен добавляется имя пользователя, тип токена, время создания и срок действия. Токен подписывается
     * секретным ключом.</p>
     *
     * @param userDetails данные пользователя
     * @param tokenType тип создаваемого токена
     * @param expirationTime время жизни токена
     * @return подписанный JWT-токен
     */
    private String generateToken(UserDetails userDetails, JwtTokenType tokenType, long expirationTime) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(TOKEN_TYPE_CLAIM, tokenType.name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Проверяет, истек ли срок действия JWT-токена.
     *
     * @param token JWT-токен
     * @return {@code true}, если срок действия токена истек,
     * иначе {@code false}
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату окончания действия JWT-токена.
     *
     * @param token JWT-токен
     * @return дата истечения срока действия токена
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает тип JWT-токена из его claims.
     *
     * @param token JWT-токен
     * @return тип токена
     */
    private JwtTokenType extractTokenType(String token) {
        return extractClaim(token, claims -> JwtTokenType.valueOf((String) claims.get(TOKEN_TYPE_CLAIM)));
    }

    /**
     * Извлекает указанное значение claim из JWT-токена.
     *
     * @param token JWT-токен
     * @param claimsResolver функция извлечения необходимого claim
     * @param <T> тип возвращаемого значения
     * @return значение claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все claims из JWT-токена после проверки подписи.
     *
     * @param token JWT-токен
     * @return набор claims из токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Создает ключ подписи JWT на основе секретного ключа из конфигурации.
     *
     * @return ключ для подписи и проверки JWT-токенов
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Создает cookie для хранения refresh-токена.
     *
     * <p>Cookie создается с параметрами безопасности: HTTP-only, Secure и ограничением SameSite.</p>
     *
     * @param value значение refresh-токена
     * @param age время жизни cookie
     * @return настроенная cookie refresh-токена
     */
    private Cookie createRefreshTokenCookie(String value, int age) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(jwtProperties.getRefreshCookiePath());
        cookie.setMaxAge(age);
        cookie.setAttribute("SameSite", "Strict");

        return cookie;
    }
}
