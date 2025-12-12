package com.kidekdev.albummanager.database.dao;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.mapper.TagMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TagDatabaseFacadeImpl implements TagDatabaseFacade {

    private final SessionFactory sessionFactory;
    private final TagMapper mapper;

    public TagDatabaseFacadeImpl() {
        this(HibernateUtil.getSessionFactory(), TagMapper.INSTANCE);
    }

    public TagDatabaseFacadeImpl(SessionFactory sessionFactory, TagMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    @Override
    public OperationResult save(TagDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = mapper.toEntity(dto);
            session.persist(entity);
            transaction.commit();
            return new OperationResult(true, "Tag saved with id %s".formatted(entity.getId()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save tag: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult update(TagDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            if (dto.id() == null) {
                throw new IllegalArgumentException("Tag id is required for update");
            }
            TagEntity existing = session.get(TagEntity.class, dto.id());
            if (existing == null) {
                return new OperationResult(false, "Tag not found for id %s".formatted(dto.id()));
            }
            TagEntity toUpdate = mapper.toEntity(dto);
            session.merge(toUpdate);
            transaction.commit();
            return new OperationResult(true, "Tag updated for id %s".formatted(dto.id()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to update tag: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult delete(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = session.get(TagEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Tag not found for id %s".formatted(id));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag: " + ex.getMessage());
        }
    }

    @Override
    public TagDto getById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            TagEntity entity = session.get(TagEntity.class, id);
            return entity != null ? mapper.toDto(entity) : null;
        }
    }

    @Override
    public List<TagDto> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagEntity", TagEntity.class)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public TagDto findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagEntity where name = :name", TagEntity.class)
                    .setParameter("name", name)
                    .uniqueResultOptional()
                    .map(mapper::toDto)
                    .orElse(null);
        }
    }

    private void rollbackQuietly(Transaction transaction) {
        if (transaction != null) {
            try {
                transaction.rollback();
            } catch (Exception ignored) {
                // ignored
            }
        }
    }
}
