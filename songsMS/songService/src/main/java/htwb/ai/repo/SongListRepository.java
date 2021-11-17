package htwb.ai.repo;

import htwb.ai.model.SongList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongListRepository extends JpaRepository<SongList, Integer> {

    List<SongList> findByOwnerId(String userId);

    @Query(value = "SELECT * FROM songlists WHERE ownerId = ?1 AND isPrivate = false", nativeQuery = true)
    List<SongList> findByOwnerIdAndNotPrivate(String userId);
}
