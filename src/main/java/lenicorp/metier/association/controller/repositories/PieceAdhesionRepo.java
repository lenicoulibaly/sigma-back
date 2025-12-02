package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.PieceAdhesionDTO;
import lenicorp.metier.association.model.entities.PieceAdhesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PieceAdhesionRepo extends JpaRepository<PieceAdhesion, Long>
{
    @Query(value = """
            SELECT NEW lenicorp.metier.association.model.dtos.PieceAdhesionDTO(
                p.pieceId,
                tp.code, tp.name,
                a.assoId, a.assoName,
                so.code, so.name,
                p.description
            )
            FROM PieceAdhesion p
            LEFT JOIN p.association a
            LEFT JOIN p.typePiece tp
            LEFT JOIN p.statutObligation so
            WHERE (
                UPPER(FUNCTION('unaccent', COALESCE(tp.code, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(tp.name, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(so.code, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(so.name, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(p.description, ''))) LIKE CONCAT('%', UPPER(:key), '%')
            )
            AND (:assoId IS NULL OR a.assoId = :assoId)
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM PieceAdhesion p
            LEFT JOIN p.association a
            LEFT JOIN p.typePiece tp
            LEFT JOIN p.statutObligation so
            WHERE (
                UPPER(FUNCTION('unaccent', COALESCE(tp.code, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(tp.name, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(so.code, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(so.name, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(p.description, ''))) LIKE CONCAT('%', UPPER(:key), '%')
            )
            AND (:assoId IS NULL OR a.assoId = :assoId)
            """)
    Page<PieceAdhesionDTO> search(@Param("key") String key, @Param("assoId") Long assoId, Pageable pageable);
}
