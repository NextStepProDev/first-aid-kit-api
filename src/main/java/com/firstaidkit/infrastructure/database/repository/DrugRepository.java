package com.firstaidkit.infrastructure.database.repository;

import com.firstaidkit.infrastructure.database.entity.DrugEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<DrugEntity, Integer>, JpaSpecificationExecutor<DrugEntity> {

    // ============== User-scoped queries (for multi-tenancy) ==============

    Optional<DrugEntity> findByDrugIdAndOwnerUserId(Integer drugId, Integer userId);

    long countByOwnerUserId(Integer userId);

    long countByOwnerUserIdAndExpirationDateBefore(Integer userId, OffsetDateTime date);

    long countByOwnerUserIdAndAlertSentAtGreaterThanEqualAndAlertSentAtLessThan(Integer userId, OffsetDateTime from, OffsetDateTime to);

    @Query("SELECT d.drugForm.name, COUNT(d) FROM DrugEntity d WHERE d.owner.userId = :userId GROUP BY d.drugForm.name")
    List<Object[]> countGroupedByFormAndUserId(Integer userId);

    @EntityGraph(attributePaths = {"drugForm"})
    List<DrugEntity> findByOwnerUserIdAndExpirationDateLessThanEqualAndAlertSentFalse(
            Integer userId, OffsetDateTime date);

    @Modifying
    @Transactional
    @Query("UPDATE DrugEntity d SET d.alertSent = true, d.alertSentAt = :now WHERE d.drugId IN :ids")
    void markAlertsAsSent(@Param("ids") List<Integer> ids, @Param("now") OffsetDateTime now);

    // ============== Global queries (for scheduled tasks) ==============

    @NonNull
    Page<DrugEntity> findAll(@NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"drugForm"})
    @NonNull
    Page<DrugEntity> findAll(@NonNull Specification<DrugEntity> spec, @NonNull Pageable pageable);

    @Query("SELECT DISTINCT d.owner.userId FROM DrugEntity d WHERE d.expirationDate <= :date AND d.alertSent = false")
    List<Integer> findDistinctOwnerIdsWithExpiringDrugs(OffsetDateTime date);

    @Modifying
    @Transactional
    void deleteAllByOwnerUserId(Integer userId);
}
