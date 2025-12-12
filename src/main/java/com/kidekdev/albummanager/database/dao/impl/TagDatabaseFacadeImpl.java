package com.kidekdev.albummanager.database.dao.impl;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.dao.TagDatabaseFacade;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.mapper.TagMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
    public OperationResult saveTag(TagDto dto) {
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
    public OperationResult saveAllTags(Collection<TagDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new OperationResult(true, "No tags to save");
        }
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            for (TagDto dto : dtos) {
                TagEntity entity = mapper.toEntity(dto);
                session.persist(entity);
            }
            transaction.commit();
            return new OperationResult(true, "All tags saved successfully");
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save tags: " + ex.getMessage());
        }
    }

    @Override
    public TagDto findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            TagEntity entity = session.get(TagEntity.class, id);
            return entity == null ? null : mapper.toDto(entity);
        }
    }

    @Override
    public TagDto findByName(String tagName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from TagEntity where name = :name", TagEntity.class)
                    .setParameter("name", tagName)
                    .uniqueResultOptional()
                    .map(mapper::toDto)
                    .orElse(null);
        }
    }

    @Override
    public List<TagDto> findAllTags() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagEntity", TagEntity.class)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TagDto> findAllByNames(Collection<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from TagEntity where name in :names", TagEntity.class)
                    .setParameter("names", names)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TagDto> findAllByGroup(String groupName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from TagEntity t where t.tagGroup = :groupName", TagEntity.class)
                    .setParameter("groupName", groupName)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OperationResult renameTag(String oldName, String newName) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = session.createQuery(
                            "from TagEntity where name = :name", TagEntity.class)
                    .setParameter("name", oldName)
                    .uniqueResult();
            if (entity == null) {
                return new OperationResult(false, "Tag not found for name %s".formatted(oldName));
            }
            entity.setName(newName);
            session.merge(entity);
            transaction.commit();
            return new OperationResult(true, "Tag renamed from %s to %s".formatted(oldName, newName));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to rename tag: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult updateTag(TagDto dto) {
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
    public OperationResult deleteTagByName(String tagName) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = session.createQuery(
                            "from TagEntity where name = :name", TagEntity.class)
                    .setParameter("name", tagName)
                    .uniqueResult();
            if (entity == null) {
                return new OperationResult(false, "Tag not found for name %s".formatted(tagName));
            }
            removeTagFromResources(session, entity.getId());
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag deleted for name %s".formatted(tagName));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult deleteTagById(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = session.get(TagEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Tag not found for id %s".formatted(id));
            }
            removeTagFromResources(session, id);
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag: " + ex.getMessage());
        }
    }

    private void removeTagFromResources(Session session, UUID tagId) {
        List<ResourceEntity> resources = session.createQuery("from ResourceEntity", ResourceEntity.class).list();
        for (ResourceEntity resource : resources) {
            LinkedHashSet<UUID> tags = resource.getTags();
            if (tags != null && tags.remove(tagId)) {
                session.merge(resource);
            }
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
