package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.CommentaryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentaryServiceImpl implements CommentaryService {

    private final CommentaryRepository commentaryRepository;

    private final BookRepository bookRepository;

    private final MutableAclService aclService;

    @Override
    @Transactional(readOnly = true)
    public Optional<Commentary> findById(long id) {
        Optional<Commentary> commentary = commentaryRepository.findById(id);
        commentary.ifPresent(this::initializeGenres);
        return commentary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Commentary> findByBookId(long bookId) {
        List<Commentary> commentaries = commentaryRepository.findByBookId(bookId);
        if (!commentaries.isEmpty()) {
            initializeGenres(commentaries.get(0));
        }

        return commentaries;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Commentary> findByBookIdWithoutFetch(long bookId) {
        return commentaryRepository.findByBookIdWithoutFetch(bookId);
    }

    @Override
    @Transactional
    public Commentary insert(String text, long bookId) {
        Commentary commentary = save(0L, text, bookId);
        createAcl(commentary);
        return commentary;
    }

    @Override
    @Transactional
    @PreAuthorize("canWrite(#id, 'ru.klimachkov.hw.models.Commentary')")
    public Commentary update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    @PreAuthorize("canDelete(#id, 'ru.klimachkov.hw.models.Commentary')")
    public void deleteById(long id) {
        commentaryRepository.deleteById(id);
        aclService.deleteAcl(new ObjectIdentityImpl(Commentary.class, id), true);
    }

    private Commentary save(long id, String text, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        Commentary commentary = commentaryRepository.save(new Commentary(id, text, book));
        initializeGenres(commentary);
        return commentary;
    }

    private void initializeGenres(Commentary commentary) {
        Hibernate.initialize(commentary.getBook().getGenres());
    }

    private void createAcl(Commentary commentary) {
        ObjectIdentity commentaryObjectIdentity = new ObjectIdentityImpl(Commentary.class, commentary.getId());
        Sid owner = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());

        MutableAcl acl = aclService.createAcl(commentaryObjectIdentity);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, owner, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, owner, true);

        aclService.updateAcl(acl);
    }
}
