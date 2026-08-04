package ru.klimachkov.workplaces.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.dto.user.UserCredentialsDto;
import ru.klimachkov.workplaces.dto.user.UserRegistrationDto;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.dto.AccessTokenResponseDto;
import ru.klimachkov.workplaces.exceptions.EntityConflictException;
import ru.klimachkov.workplaces.exceptions.security.RefreshTokenException;
import ru.klimachkov.workplaces.exceptions.security.RefreshTokenMissingException;
import ru.klimachkov.workplaces.exceptions.security.UserLockedException;
import ru.klimachkov.workplaces.mappers.UserMapper;
import ru.klimachkov.workplaces.models.RefreshToken;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.security.CustomUserDetails;
import ru.klimachkov.workplaces.security.jwt.JwtService;

/**
 * Реализация сервиса {@link AuthService} для аутентификации и регистрации пользователей.
 *
 * @see JwtService
 * @see RefreshTokenService
 * @see UserService
 * @see UserMapper
 * @see PasswordEncoder
 * @see AuthenticationManager
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * Сервис для работы с JWT токенами
     */
    private final JwtService jwtService;

    /**
     * Сервис управления refresh-токенами
     */
    private final RefreshTokenService refreshTokenService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Маппер для преобразования сущности {@link User} в DTO представление
     */
    private final UserMapper userMapper;

    /**
     * Компонент для кодирования паролей.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Менеджер аутентификации Spring Security.
     *
     * <p>Используется для проверки учетных данных пользователя.</p>
     */
    private final AuthenticationManager authenticationManager;


    /**
     * Регистрирует нового пользователя в системе.
     *
     * <p>Проверяет уникальность email, создает пользователя
     * с закодированным паролем и возвращает DTO с его данными.</p>
     *
     * @param userRegistrationDto данные для регистрации пользователя
     * @return DTO с информацией о созданном пользователе
     * @throws EntityConflictException если пользователь с email уже существует
     */
    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationDto userRegistrationDto) {
        if (userService.existsByEmail(userRegistrationDto.email())) {
            throw new EntityConflictException(
                    "The email address \"%s\" is already in use".formatted(userRegistrationDto.email()),
                    "error.user.email.already_in_use",
                    new Object[]{userRegistrationDto.email()}
            );
        }

        User insertedUser = userService.insert(
                userRegistrationDto.name(),
                userRegistrationDto.email(),
                passwordEncoder.encode(userRegistrationDto.password())
        );

        return userMapper.toResponseDto(insertedUser);
    }

    /**
     * Аутентифицирует пользователя по email и паролю.
     *
     * <p>При успешной аутентификации генерирует access и refresh токены,
     * сохраняет refresh-токен в базе и добавляет его в HTTP-only cookie ответа.</p>
     *
     * @param userCredentialsDto учетные данные пользователя
     * @param response HTTP-ответ, в который добавляется cookie с refresh-токеном
     * @return DTO с access-токеном
     */
    @Override
    @Transactional
    public AccessTokenResponseDto authenticate(UserCredentialsDto userCredentialsDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                        userCredentialsDto.email(),
                        userCredentialsDto.password())
        );

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(customUserDetails);
        String refreshToken = jwtService.generateRefreshToken(customUserDetails);

        refreshTokenService.save(customUserDetails.user(), refreshToken);
        jwtService.addRefreshTokenCookie(response, refreshToken);

        return new AccessTokenResponseDto(accessToken);
    }

    /**
     * Обновляет access-токен на основе refresh-токена.
     *
     * <p>Извлекает refresh-токен из cookie запроса, проверяет его наличие,
     * валидность и соответствие пользователю, после чего генерирует новый
     * access-токен.</p>
     *
     * @param request HTTP-запрос, содержащий cookie с refresh-токеном
     * @return DTO с новым access-токеном
     *
     * @throws RefreshTokenMissingException если refresh-токен отсутствует в запросе
     * @throws RefreshTokenException если токен не найден или невалиден
     * @throws UserLockedException если пользователь заблокирован
     */
    @Override
    @Transactional
    public AccessTokenResponseDto refreshAccessToken(HttpServletRequest request) {
        String extractedRefreshToken = jwtService.extractRefreshTokenFromCookies(request);
        if (extractedRefreshToken == null) {
            throw new RefreshTokenMissingException();
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(extractedRefreshToken)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not exists in database"));

        User user = refreshToken.getUser();
        if (user.isLocked()) {
            throw new UserLockedException("User with id %d is locked".formatted(user.getId()));
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        if (!jwtService.isValidRefreshToken(extractedRefreshToken, userDetails)) {
            throw new RefreshTokenException("Refresh token is invalid");
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        return new AccessTokenResponseDto(newAccessToken);
    }
}
