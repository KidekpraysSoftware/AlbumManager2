package com.kidekdev.albummanager.database.dao.impl;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.OrderingUtils;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.dao.ResourceDatabaseFacade;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.mapper.ResourceMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceDatabaseFacadeImpl implements ResourceDatabaseFacade {

    private final SessionFactory sessionFactory;
    private final ResourceMapper mapper;

    public ResourceDatabaseFacadeImpl() {
        this(HibernateUtil.getSessionFactory(), ResourceMapper.INSTANCE);
    }

    public ResourceDatabaseFacadeImpl(SessionFactory sessionFactory, ResourceMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    @Override
    public OperationResult save(ResourceDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ResourceEntity entity = mapper.toEntity(dto);
            entity.setOrdering(resolveNextOrdering(session));
            entity.setTags(resolveTags(session, dto.tags()));
            session.persist(entity);
            transaction.commit();
            return new OperationResult(true, "Resource saved with id %s".formatted(entity.getId()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save resource: " + ex.getMessage());
        }
    }

    @Override
    public ResourceDto getById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            ResourceEntity entity = session.get(ResourceEntity.class, id);
            return entity == null ? null : mapper.toDto(entity);
        }
    }

    @Override
    public List<ResourceDto> getAllById(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from ResourceEntity where id in :ids", ResourceEntity.class)
                    .setParameter("ids", ids)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OperationResult update(ResourceDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            if (dto.id() == null) {
                throw new IllegalArgumentException("Resource id is required for update");
            }
            ResourceEntity existing = session.get(ResourceEntity.class, dto.id());
            if (existing == null) {
                return new OperationResult(false, "Resource not found for id %s".formatted(dto.id()));
            }
            ResourceEntity toUpdate = mapper.toEntity(dto);
            toUpdate.setOrdering(existing.getOrdering());
            toUpdate.setTags(resolveTags(session, dto.tags()));
            session.merge(toUpdate);
            transaction.commit();
            return new OperationResult(true, "Resource updated for id %s".formatted(dto.id()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to update resource: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult deactivate(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ResourceEntity entity = session.get(ResourceEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Resource not found for id %s".formatted(id));
            }
            entity.setIsActive(false);
            session.merge(entity);
            transaction.commit();
            return new OperationResult(true, "Resource deactivated for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to deactivate resource: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult delete(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ResourceEntity entity = session.get(ResourceEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Resource not found for id %s".formatted(id));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Resource deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete resource: " + ex.getMessage());
        }
    }

    @Override
    public ResourceDto findByHash(String hash) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from ResourceEntity where hash = :hash", ResourceEntity.class)
                    .setParameter("hash", hash)
                    .uniqueResultOptional()
                    .map(mapper::toDto)
                    .orElse(null);
        }
    }

    @Override
    public List<ResourceDto> findAllByHash(Collection<String> hashList) {
        if (hashList == null || hashList.isEmpty()) {
            return Collections.emptyList();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from ResourceEntity where hash in :hashes", ResourceEntity.class)
                    .setParameter("hashes", hashList)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OperationResult updateResourceOrdering(List<UUID> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return new OperationResult(false, "No ordered ids provided");
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<ResourceEntity> entities = session.createQuery("from ResourceEntity", ResourceEntity.class)
                    .list();
            OrderingUtils.updateSourceOrdering(entities, orderedIds);
            transaction.commit();
            return new OperationResult(true, "Ordering updated");
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to update ordering: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult isExist(String resourceFileHash) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from ResourceEntity where hash = :hash", ResourceEntity.class)
                    .setParameter("hash", resourceFileHash)
                    .uniqueResultOptional()
                    .map(entity -> {
                        String author = Optional.ofNullable(entity.getAuthorName()).orElse("");
                        String name = Optional.ofNullable(entity.getResourceName()).orElse("");
                        String message = "Такой ресурс уже есть: %s - %s".formatted(author, name).trim();
                        return new OperationResult(false, message);
                    })
                    .orElseGet(() -> new OperationResult(true, ""));
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

    private int resolveNextOrdering(Session session) {
        Integer minOrdering = session.createQuery("select min(ordering) from ResourceEntity", Integer.class)
                .uniqueResultOptional()
                .orElse(null);
        return minOrdering == null ? 0 : minOrdering - 1;
    }

    private Set<TagEntity> resolveTags(Session session, Set<TagDto> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }

        Map<UUID, TagEntity> tagsById = loadTagsById(session, tags);
        Map<String, TagEntity> tagsByName = loadTagsByName(session, tags);

        Set<TagEntity> resolved = new HashSet<>();
        for (TagDto tagDto : tags) {
            TagEntity entity = Optional.ofNullable(tagDto.id())
                    .map(tagsById::get)
                    .orElseGet(() -> tagsByName.get(tagDto.name()));

            if (entity == null) {
                throw new IllegalArgumentException("Tag not found: " + (tagDto.id() != null ? tagDto.id() : tagDto.name()));
            }

            resolved.add(entity);
        }

        return resolved;
    }

    private Map<UUID, TagEntity> loadTagsById(Session session, Set<TagDto> tags) {
        List<UUID> ids = tags.stream()
                .map(TagDto::id)
                .filter(Objects::nonNull)
                .toList();

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return session.createQuery("from TagEntity where id in :ids", TagEntity.class)
                .setParameter("ids", ids)
                .list()
                .stream()
                .collect(Collectors.toMap(TagEntity::getId, e -> e));
    }

    private Map<String, TagEntity> loadTagsByName(Session session, Set<TagDto> tags) {
        List<String> names = tags.stream()
                .map(TagDto::name)
                .filter(Objects::nonNull)
                .toList();

        if (names.isEmpty()) {
            return Collections.emptyMap();
        }

        return session.createQuery("from TagEntity where name in :names", TagEntity.class)
                .setParameter("names", names)
                .list()
                .stream()
                .collect(Collectors.toMap(TagEntity::getName, e -> e));
    }
}
