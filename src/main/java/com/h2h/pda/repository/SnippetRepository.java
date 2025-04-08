package com.h2h.pda.repository;

import com.h2h.pda.entity.SnippetEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetRepository extends CrudRepository<SnippetEntity, String> {
    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId= ?1 AND s.deletedAt IS NULL")
    List<SnippetEntity> findByNotDeleted(String userId);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId= ?1 AND s.deletedAt IS NULL ORDER BY s.title")
    List<SnippetEntity> findByNotDeletedOrderName(String userId);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId= ?1 AND s.deletedAt IS NULL ORDER BY s.title DESC")
    List<SnippetEntity> findByNotDeletedOrderNameDesc(String userId);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId= ?1 AND s.deletedAt IS NULL ORDER BY s.createdAt")
    List<SnippetEntity> findByNotDeletedOrderCreatedTime(String userId);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId= ?1 AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<SnippetEntity> findByNotDeletedOrderCreatedTimeDesc(String userId);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId <> ?1 AND s.deletedAt IS NULL ")
    List<SnippetEntity> findAllNotDeletedNotUserid(String userId);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.deletedAt IS NULL")
    List<SnippetEntity> findBySoftDeletedById();

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.userId = ?1")
    List<SnippetEntity> findbyUserId(String id);

    @Query(value = "SELECT s FROM SnippetEntity s WHERE s.snippetId = ?1")
    SnippetEntity findSnippetbyId(String snippetId);
}