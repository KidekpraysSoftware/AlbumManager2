package com.kidekdev.albummanager.database.dao.impl;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.dao.DynamicResourceDatabaseFacade;
import com.kidekdev.albummanager.database.dto.DynamicResourceDto;
import com.kidekdev.albummanager.database.entity.DynamicResourceEntity;
import com.kidekdev.albummanager.database.mapper.DynamicResourceMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DynamicResourceDatabaseFacadeImpl implements DynamicResourceDatabaseFacade {

    private final SessionFactory sessionFactory;
    private final DynamicResourceMapper mapper;

    public DynamicResourceDatabaseFacadeImpl() {
        this(HibernateUtil.getSessionFactory(), DynamicResourceMapper.INSTANCE);
    }

    public DynamicResourceDatabaseFacadeImpl(SessionFactory sessionFactory, DynamicResourceMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    @Override
    public OperationResult save(DynamicResourceDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            DynamicResourceEntity entity = mapper.toEntity(dto);
            session.persist(entity);
            transaction.commit();
            return new OperationResult(true, "Dynamic resource saved with id %s".formatted(entity.getId()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save dynamic resource: " + ex.getMessage());
        }
    }

    @Override
    public List<DynamicResourceDto> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from DynamicResourceEntity", DynamicResourceEntity.class)
                    .list()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OperationResult deactivate(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            DynamicResourceEntity entity = session.get(DynamicResourceEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Dynamic resource not found for id %s".formatted(id));
            }
            entity.setIsActive(false);
            session.merge(entity);
            transaction.commit();
            return new OperationResult(true, "Dynamic resource deactivated for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to deactivate dynamic resource: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult delete(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            DynamicResourceEntity entity = session.get(DynamicResourceEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Dynamic resource not found for id %s".formatted(id));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Dynamic resource deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete dynamic resource: " + ex.getMessage());
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
