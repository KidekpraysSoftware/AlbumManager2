package com.kidekdev.albummanager.database.dao.impl;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.HibernateUtil;
import com.kidekdev.albummanager.database.OrderingUtils;
import com.kidekdev.albummanager.database.dao.TagDatabaseFacade;
import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.database.dto.TagGroupDto;
import com.kidekdev.albummanager.database.entity.TagEntity;
import com.kidekdev.albummanager.database.entity.TagGroupEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TagDatabaseFacadeImpl implements TagDatabaseFacade {

    private final SessionFactory sessionFactory;

    public TagDatabaseFacadeImpl() {
        this(HibernateUtil.getSessionFactory());
    }

    public TagDatabaseFacadeImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public OperationResult saveGroup(TagGroupDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int nextOrdering = resolveNextOrdering(session);

            TagGroupEntity group = new TagGroupEntity();
            group.setName(dto.name());
            group.setOrdering(nextOrdering);

            if (dto.tags() != null) {
                for (TagDto tagDto : dto.tags()) {
                    TagEntity tag = new TagEntity();
                    tag.setName(tagDto.name());
                    tag.setGroup(group);
                    group.getTags().add(tag);
                }
            }

            session.persist(group);
            transaction.commit();
            return new OperationResult(true, "Tag group saved with id %s".formatted(group.getId()));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to save tag group: " + ex.getMessage());
        }
    }

    @Override
    public TagDto findTagById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            TagEntity entity = session.get(TagEntity.class, id);
            return entity == null ? null : toDto(entity);
        }
    }

    @Override
    public TagGroupDto findGroupById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            TagGroupEntity entity = session.get(TagGroupEntity.class, id);
            return entity == null ? null : toDto(entity);
        }
    }

    @Override
    public TagDto findByName(String tagName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagEntity where name = :name", TagEntity.class)
                    .setParameter("name", tagName)
                    .uniqueResultOptional()
                    .map(this::toDto)
                    .orElse(null);
        }
    }

    @Override
    public List<TagDto> findAllTags() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagEntity", TagEntity.class)
                    .list()
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TagGroupDto> findAllGroups() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagGroupEntity", TagGroupEntity.class)
                    .list()
                    .stream()
                    .sorted(Comparator.comparingInt(TagGroupEntity::getOrdering))
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TagDto> findAllByNames(Collection<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TagEntity where name in :names", TagEntity.class)
                    .setParameter("names", names)
                    .list()
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TagDto> findAllByGroupName(String groupName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "select t from TagEntity t join t.group g where g.name = :groupName",
                            TagEntity.class)
                    .setParameter("groupName", groupName)
                    .list()
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OperationResult renameTag(String oldName, String newName) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = session.createQuery("from TagEntity where name = :name", TagEntity.class)
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
    public OperationResult renameGroup(String oldName, String newName) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagGroupEntity entity = session.createQuery("from TagGroupEntity where name = :name", TagGroupEntity.class)
                    .setParameter("name", oldName)
                    .uniqueResult();

            if (entity == null) {
                return new OperationResult(false, "Tag group not found for name %s".formatted(oldName));
            }

            entity.setName(newName);
            session.merge(entity);
            transaction.commit();
            return new OperationResult(true, "Tag group renamed from %s to %s".formatted(oldName, newName));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to rename tag group: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult updateGroups(List<TagGroupDto> groups) {
        if (groups == null || groups.isEmpty()) {
            return new OperationResult(true, "No groups to update");
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            for (TagGroupDto dto : groups) {
                applyGroupUpdate(session, dto);
            }
            transaction.commit();
            return new OperationResult(true, "Groups updated");
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to update groups: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult updateGroup(TagGroupDto group) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            applyGroupUpdate(session, group);
            transaction.commit();
            return new OperationResult(true, "Group updated");
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to update group: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult updateGroupOrdering(List<UUID> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return new OperationResult(false, "No ordered ids provided");
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<TagGroupEntity> entities = session.createQuery("from TagGroupEntity", TagGroupEntity.class)
                    .list();
            OrderingUtils.updateTagGroupOrdering(entities, orderedIds);
            transaction.commit();
            return new OperationResult(true, "Ordering updated");
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to update ordering: " + ex.getMessage());
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
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult deleteTagByName(String name) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagEntity entity = session.createQuery("from TagEntity where name = :name", TagEntity.class)
                    .setParameter("name", name)
                    .uniqueResult();
            if (entity == null) {
                return new OperationResult(false, "Tag not found for name %s".formatted(name));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag deleted for name %s".formatted(name));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult deleteGroupById(UUID id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagGroupEntity entity = session.get(TagGroupEntity.class, id);
            if (entity == null) {
                return new OperationResult(false, "Tag group not found for id %s".formatted(id));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag group deleted for id %s".formatted(id));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag group: " + ex.getMessage());
        }
    }

    @Override
    public OperationResult deleteGroupByName(String name) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TagGroupEntity entity = session.createQuery("from TagGroupEntity where name = :name", TagGroupEntity.class)
                    .setParameter("name", name)
                    .uniqueResult();
            if (entity == null) {
                return new OperationResult(false, "Tag group not found for name %s".formatted(name));
            }
            session.remove(entity);
            transaction.commit();
            return new OperationResult(true, "Tag group deleted for name %s".formatted(name));
        } catch (Exception ex) {
            rollbackQuietly(transaction);
            return new OperationResult(false, "Failed to delete tag group: " + ex.getMessage());
        }
    }

    private void applyGroupUpdate(Session session, TagGroupDto dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Group id is required for update");
        }
        TagGroupEntity existing = session.get(TagGroupEntity.class, dto.id());
        if (existing == null) {
            throw new IllegalArgumentException("Tag group not found: " + dto.id());
        }
        existing.setName(dto.name());
        existing.setOrdering(dto.ordering());

        if (dto.tags() != null) {
            Map<String, TagEntity> currentByName = existing.getTags().stream()
                    .collect(Collectors.toMap(TagEntity::getName, t -> t));

            List<TagEntity> updated = new ArrayList<>();
            for (TagDto tagDto : dto.tags()) {
                TagEntity entity = currentByName.getOrDefault(tagDto.name(), new TagEntity());
                entity.setName(tagDto.name());
                entity.setGroup(existing);
                updated.add(entity);
            }
            existing.getTags().clear();
            existing.getTags().addAll(updated);
        }

        session.merge(existing);
    }

    private int resolveNextOrdering(Session session) {
        Integer minOrdering = session.createQuery("select min(ordering) from TagGroupEntity", Integer.class)
                .uniqueResultOptional()
                .orElse(null);
        return minOrdering == null ? 0 : minOrdering - 1;
    }

    private TagDto toDto(TagEntity entity) {
        return TagDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .tagGroup(entity.getGroup() != null ? entity.getGroup().getName() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private TagGroupDto toDto(TagGroupEntity entity) {
        List<TagDto> tags = entity.getTags() == null ? List.of() : entity.getTags().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return TagGroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .ordering(entity.getOrdering())
                .tags(tags)
                .build();
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
