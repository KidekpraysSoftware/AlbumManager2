package com.kidekdev.albummanager.database;

import com.kidekdev.albummanager.database.entity.ResourceEntity;
import com.kidekdev.albummanager.database.model.common.ResourceType;
import com.kidekdev.albummanager.database.model.resource.ResourceExtension;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Minimal CRUD workflow for interacting with the embedded H2 database via Hibernate.
 * <p>
 * This example creates a resource, reads it back, updates the description and finally deletes the row.
 */
public class HibernateCrudExample {

    public static void main(String[] args) {
        ResourceEntity created = createResource();
        ResourceEntity loaded = readResource(created.getId());
        updateDescription(loaded.getId(), "Updated description from example");
        deleteResource(loaded.getId());
    }

    private static ResourceEntity createResource() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            ResourceEntity entity = new ResourceEntity();
            entity.setIsActive(true);
            entity.setPath("/demo/path.wav");
            entity.setInFolder(false);
            entity.setHash("demo-hash");
            entity.setResourceType(ResourceType.TRACK);
            entity.setExtension(ResourceExtension.WAV);
            entity.setDescription("Demo resource created in example");
            entity.setSizeBytes(1024L);
            entity.setImportedAt(System.currentTimeMillis());
            entity.setCreated(System.currentTimeMillis());
            entity.setModified(System.currentTimeMillis());
            entity.setTags(List.of("demo", "example"));
            session.persist(entity);

            transaction.commit();
            return entity;
        }
    }

    private static ResourceEntity readResource(java.util.UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ResourceEntity.class, id);
        }
    }

    private static void updateDescription(java.util.UUID id, String description) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            ResourceEntity entity = session.get(ResourceEntity.class, id);
            entity.setDescription(description);
            session.merge(entity);
            transaction.commit();
        }
    }

    private static void deleteResource(java.util.UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            ResourceEntity entity = session.get(ResourceEntity.class, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        }
    }
}
