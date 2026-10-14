package es.trainertemplate;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry, String> {
  List<Entry> findByOwnerNameOrderByIdAsc(String ownerName);

  Optional<Entry> findByIdAndOwnerName(String id, String ownerName);
}
