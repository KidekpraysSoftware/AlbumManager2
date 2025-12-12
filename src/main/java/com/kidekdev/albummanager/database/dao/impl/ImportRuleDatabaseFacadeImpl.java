package com.kidekdev.albummanager.database.dao.impl;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.dao.ImportRuleDatabaseFacade;
import com.kidekdev.albummanager.database.dto.ImportRuleDto;
import com.kidekdev.albummanager.database.entity.ImportRuleEntity;
import com.kidekdev.albummanager.database.mapper.ImportRuleMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ImportRuleDatabaseFacadeImpl implements ImportRuleDatabaseFacade {

    private final SessionFactory sessionFactory;
    private final ImportRuleMapper mapper;

    public ImportRuleDatabaseFacadeImpl() {
        this(HibernateUtil.getSessionFactory(), ImportRuleMapper.INSTANCE);
    }

    public ImportRuleDatabaseFacadeImpl(SessionFactory sessionFactory, ImportRuleMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    @Override
    public OperationResult save(ImportRuleDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ImportRuleEntity entity = mapper.toEntity(dto);
            session.persist(entity);
            transaction.commit();
            return new OperationResult(true, "Import rule saved with id %s".formatted(entity.getId()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save import rule: " + ex.getMessage());
        }
    }

    @Override
    public List<ImportRuleDto> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from ImportRuleEntity", ImportRuleEntity.class)
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
            ImportRuleEntity entity = session.get(ImportRuleEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Import rule not found for id %s".formatted(id));
            }
            entity.setIsActive(false);
            session.merge(entity);
            transaction.commit();
            return new OperationResult(true, "Import rule deactivated for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to deactivate import rule: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult delete(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ImportRuleEntity entity = session.get(ImportRuleEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Import rule not found for id %s".formatted(id));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Import rule deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete import rule: " + ex.getMessage());
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
