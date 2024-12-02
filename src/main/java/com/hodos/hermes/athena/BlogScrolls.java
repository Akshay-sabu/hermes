package com.hodos.hermes.athena;

import com.hodos.hermes.dao.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogScrolls extends JpaRepository<Blog,Long> {
}
