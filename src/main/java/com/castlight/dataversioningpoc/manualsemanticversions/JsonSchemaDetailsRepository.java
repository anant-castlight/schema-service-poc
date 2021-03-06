package com.castlight.dataversioningpoc.manualsemanticversions;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by anantm on 8/9/17.
 */
@Repository
public interface JsonSchemaDetailsRepository extends CrudRepository<JsonSchemaDetail, Long> {

    @Query("SELECT jsd.version FROM JsonSchemaDetail jsd WHERE jsd.name=:name ORDER BY INET_ATON(SUBSTRING_INDEX(CONCAT(jsd.version,'0.0.0'),'.',3)) DESC")
    List<String> findLatestVersionByName(@Param("name") String name, Pageable limit);

    @Query("SELECT jsd.version FROM JsonSchemaDetail jsd WHERE jsd.name=:name AND jsd.version LIKE CONCAT(:majorVersion,'%') ORDER BY INET_ATON(SUBSTRING_INDEX(CONCAT(jsd.version,'0.0.0'),'.',3)) DESC")
    List<String> findLatestVersionForGivenMajorVersionByName(@Param("name") String name, @Param("majorVersion") String majorVersion, Pageable limit);

    @Query("SELECT jsd.version FROM JsonSchemaDetail jsd WHERE jsd.name=:name AND jsd.version LIKE CONCAT(:majorVersion,'.',:minorVersion,'%') ORDER BY INET_ATON(SUBSTRING_INDEX(CONCAT(jsd.version,'0.0.0'),'.',3)) DESC")
    List<String> findLatestVersionForGivenMajorAndMinorVersionByName(@Param("name") String name, @Param("majorVersion") String majorVersion, @Param("minorVersion") String minorVersion, Pageable limit);

    @Query("SELECT jsd FROM JsonSchemaDetail jsd WHERE jsd.name=:name  ORDER BY INET_ATON(SUBSTRING_INDEX(CONCAT(jsd.version,'0.0.0'),'.',3)) DESC")
    List<JsonSchemaDetail> findAllJsonSchemaDetailsByName(@Param("name") String name);

    @Query("SELECT jsd FROM JsonSchemaDetail jsd WHERE jsd.name=:name ORDER BY INET_ATON(SUBSTRING_INDEX(CONCAT(jsd.version,'0.0.0'),'.',3)) DESC")
    List<JsonSchemaDetail> findLatestSchemaDetailsByName(@Param("name") String name, Pageable limit);

    @Query("SELECT jsd FROM JsonSchemaDetail jsd WHERE jsd.name=:name AND jsd.version=:version")
    JsonSchemaDetail findJsonSchemaDetailsByNameAndVersion(@Param("name") String name, @Param("version") String version);

}
