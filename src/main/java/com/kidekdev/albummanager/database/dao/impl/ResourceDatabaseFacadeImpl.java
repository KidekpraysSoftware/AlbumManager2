package com.kidekdev.albummanager.database.dao.impl;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.dao.ResourceDatabaseFacade;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.mapper.ResourceMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
            session.persist(entity);
            transaction.commit();
            return new OperationResult(true, "Resource saved with id %s".formatted(entity.getId()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save resource: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult getById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            ResourceEntity entity = session.get(ResourceEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Resource not found for id %s".formatted(id));
            }
            return new OperationResult(true, "Resource loaded for id %s".formatted(id));
        } catch (Exception ex) {
            return new OperationResult(false, "Failed to load resource: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult getAllById(List<UUID> idList) {
        try (Session session = sessionFactory.openSession()) {
            List<ResourceEntity> results = session.createQuery(
                    "from ResourceEntity where id in :ids", ResourceEntity.class)
                    .setParameter("ids", idList)
                    .list();
            if (results.size() == idList.size()) {
                return new OperationResult(true, "Loaded %d resources".formatted(results.size()));
            }
            return new OperationResult(false,
                    "Loaded %d of %d resources".formatted(results.size(), idList.size()));
        } catch (Exception ex) {
            return new OperationResult(false, "Failed to load resources: " + ex.getMessage());
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
    public List<ResourceDto> findAllByHash(List<String> hashList) {
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
