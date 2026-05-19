/**
 * Spring Data JPA repositories for the SilkRoad application.
 *
 * <p>Each repository extends {@link org.springframework.data.jpa.repository.JpaRepository}
 * and provides CRUD operations plus custom derived and JPQL queries for
 * their respective entities.</p>
 *
 * <ul>
 *   <li>{@link com.talha11bu.silkroad.repo.SessionRepo} &mdash; Queries for sessions including expiration and abandonment lookups.</li>
 *   <li>{@link com.talha11bu.silkroad.repo.UserRepo} &mdash; User lookups scoped to a specific session.</li>
 *   <li>{@link com.talha11bu.silkroad.repo.FilesRepo} &mdash; File lookups by filename within a session.</li>
 * </ul>
 */
package com.talha11bu.silkroad.repo;
