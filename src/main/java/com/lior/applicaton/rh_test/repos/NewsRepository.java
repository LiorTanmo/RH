package com.lior.applicaton.rh_test.repos;

import com.lior.applicaton.rh_test.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
     List<News> findByTextStartingWithOrTitleStartingWith(String text, String title);
}
