package ru.klimachkov.workplaces.services;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.dsl.predicates.UserPredicateBuilder;
import ru.klimachkov.workplaces.dto.user.UserFilterModel;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.exceptions.ForbiddenOperationException;
import ru.klimachkov.workplaces.exceptions.NotFoundEntityException;
import ru.klimachkov.workplaces.exceptions.UserNotFoundException;
import ru.klimachkov.workplaces.exceptions.security.UnauthenticatedException;
import ru.klimachkov.workplaces.mappers.UserMapper;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.models.UserRole;
import ru.klimachkov.workplaces.repositories.UserRepository;
import ru.klimachkov.workplaces.util.UserUtils;

/**
 * Реализация сервиса {@link UserService} для работы с данными о пользователях
 *
 * @see UserRepository
 * @see UserMapper
 * @see UserPredicateBuilder
 * @see RefreshTokenService
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Маппер для преобразования сущностей {@link User} в DTO представление.
     */
    private final UserMapper userMapper;

    /**
     * Билдер предиката для формирования условий фильтрации пользователей.
     */
    private final UserPredicateBuilder userPredicateBuilder;

    /**
     * Сервис для работы с refresh-токенами пользователей.
     */
    private final RefreshTokenService refreshTokenService;

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     *
     * @throws NotFoundEntityException если пользователь не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(
                        "User with id %d not found".formatted(id),
                        "error.user.not_found",
                        new Object[]{id})
                );
    }

    /**
     * Возвращает DTO представление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO представление пользователя
     *
     * @throws NotFoundEntityException если пользователь не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getDtoById(Long id) {
        User user = findById(id);
        return userMapper.toResponseDto(user);
    }

    /**
     * Возвращает текущего авторизованного пользователя.
     *
     * @return текущий пользователь
     *
     * @throws UnauthenticatedException если пользователь не аутентифицирован
     * @throws NotFoundEntityException если пользователь не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public User getMe() {
        return findById(UserUtils.getCurrentUser().getId());
    }

    /**
     * Возвращает DTO представление текущего авторизованного пользователя.
     *
     * @return DTO представление текущего пользователя
     * @throws UnauthenticatedException если пользователь не аутентифицирован
     * @throws NotFoundEntityException если пользователь не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMeDto() {
        User me = getMe();
        return userMapper.toResponseDto(me);
    }

    /**
     * Выполняет поиск пользователей с учетом фильтрации и пагинации.
     *
     * <p>Фильтрация выполняется с использованием {@link UserPredicateBuilder}.</p>
     *
     * @param userFilterModel модель с параметрами фильтрации пользователей
     * @param pageable параметры пагинации
     *
     * @return страница DTO представлений пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public PagedModel<UserResponseDto> findAll(UserFilterModel userFilterModel, Pageable pageable) {
        Predicate predicate = userPredicateBuilder.build(userFilterModel);
        Page<UserResponseDto> usersPage = userRepository.findAll(predicate, pageable)
                .map(userMapper::toResponseDto);

        return new PagedModel<>(usersPage);
    }

    /**
     * Проверяет существование пользователя с указанным адресом электронной почты.
     *
     * @param email адрес электронной почты пользователя
     *
     * @return {@code true}, если пользователь с таким email существует,
     * иначе {@code false}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * Возвращает пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя
     *
     * @return найденный пользователь
     *
     * @throws UserNotFoundException если пользователь с указанным email не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User with email %s not found".formatted(email)));
    }

    /**
     * Создает нового пользователя.
     *
     * <p>Новому пользователю автоматически назначается роль
     * {@link UserRole#EMPLOYEE}.</p>
     *
     * @param name имя пользователя
     * @param email адрес электронной почты
     * @param password пароль пользователя
     *
     * @return созданный пользователь
     */
    @Override
    @Transactional
    public User insert(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(UserRole.EMPLOYEE)
                .build();

        return userRepository.save(user);
    }

    /**
     * Изменяет роль пользователя.
     *
     * <p>Изменение роли системного администратора и назначение роли
     * системного администратора ограничены правилами безопасности.</p>
     *
     * @param id идентификатор пользователя
     * @param role новая роль пользователя
     *
     * @return DTO представление пользователя с обновленной ролью
     *
     * @throws NotFoundEntityException если пользователь не найден
     * @throws ForbiddenOperationException если операция изменения роли запрещена
     */
    @Override
    @Transactional
    public UserResponseDto changeRole(Long id, UserRole role) {
        User user = findById(id);

        if (UserUtils.isSystemAdmin(user)) {
            if (role != UserRole.SYSTEM_ADMIN) {
                throw new ForbiddenOperationException(
                        "It is unacceptable to downgrade the role of the system administrator",
                        "error.user.unacceptable_downgrade_system_admin"
                );
            }
        } else if (role == UserRole.SYSTEM_ADMIN) {
            throw new ForbiddenOperationException(
                    "The user's role cannot be upgraded to system administrator",
                    "error.user.role_cannot_be_upgraded_to_system_admin"
            );
        }

        user.setRole(role);
        return userMapper.toResponseDto(user);
    }

    /**
     * Блокирует учетную запись пользователя.
     *
     * <p>При блокировке пользователя удаляются его refresh-токены,
     * после чего учетная запись становится недоступной для авторизации.</p>
     *
     * @param id идентификатор пользователя
     *
     * @throws NotFoundEntityException если пользователь не найден
     * @throws ForbiddenOperationException при попытке заблокировать
     * системного администратора
     */
    @Override
    @Transactional
    public void lock(Long id) {
        User user = findById(id);
        if (UserUtils.isSystemAdmin(user)) {
            throw new ForbiddenOperationException(
                    "System administrator cannot be locked",
                    "error.user.system_admin_cannot_be_locked"
            );
        }

        refreshTokenService.deleteByUser(user);
        user.setLocked(true);
    }

    /**
     * Разблокирует учетную запись пользователя.
     *
     * @param id идентификатор пользователя
     *
     * @throws NotFoundEntityException если пользователь не найден
     */
    @Override
    @Transactional
    public void unlock(Long id) {
        User user = findById(id);
        user.setLocked(false);
    }
}
