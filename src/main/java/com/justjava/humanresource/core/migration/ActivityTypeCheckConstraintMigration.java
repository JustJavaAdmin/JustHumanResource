package com.justjava.humanresource.core.migration;

import com.justjava.humanresource.request.enums.RequestActivityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ONE-TIME MIGRATION SHIM.
 *
 * Why this exists:
 * This project has no Flyway/Liquibase - the schema is managed entirely by
 * Hibernate's ddl-auto=update. Since Hibernate 6, an @Enumerated(EnumType.STRING)
 * column gets an auto-generated Postgres CHECK constraint listing the enum's
 * literal values AT THE TIME the table/column was first created. ddl-auto=update
 * never revisits that constraint when the enum gains new values later.
 *
 * RequestActivityType gained FREE_ROUTE_SENT and FREE_ROUTE_FORWARDED after the
 * workflow_request_activities table already existed, so Postgres was still
 * rejecting inserts with those values:
 *
 *   ERROR: new row for relation "workflow_request_activities" violates check
 *   constraint "workflow_request_activities_activity_type_check"
 *
 * This runner drops and recreates that single constraint using the CURRENT set
 * of RequestActivityType values (read via reflection, not hardcoded, so it
 * can't drift again if more activity types are added later).
 *
 * This is safe to leave in permanently (it's idempotent - DROP IF EXISTS then
 * ADD, and it no-ops harmlessly once the constraint already matches), but it is
 * a workaround for the lack of a migration tool, not a replacement for one.
 * DELETE THIS CLASS once you've added Flyway/Liquibase, or once you're
 * confident this constraint won't need to change again.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Integer.MIN_VALUE + 100) // run early, well before normal application runners
public class ActivityTypeCheckConstraintMigration implements CommandLineRunner {

    private static final String TABLE_NAME = "workflow_request_activities";
    private static final String COLUMN_NAME = "activity_type";
    private static final String CONSTRAINT_NAME = "workflow_request_activities_activity_type_check";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        String allowedValues = Arrays.stream(RequestActivityType.values())
                .map(v -> "'" + v.name() + "'")
                .collect(Collectors.joining(","));

        try {
            jdbcTemplate.execute(
                    "ALTER TABLE " + TABLE_NAME + " DROP CONSTRAINT IF EXISTS " + CONSTRAINT_NAME
            );
            jdbcTemplate.execute(
                    "ALTER TABLE " + TABLE_NAME + " ADD CONSTRAINT " + CONSTRAINT_NAME
                            + " CHECK (" + COLUMN_NAME + " IN (" + allowedValues + "))"
            );
            log.info("Migrated check constraint {} on {}.{} to current RequestActivityType values: {}",
                    CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, allowedValues);
        } catch (Exception e) {
            // Do not fail application startup over this - log loudly and let the
            // app continue, since only Free Route send/forward activity logging
            // is affected until this is fixed.
            log.error("Failed to migrate check constraint {} on {}.{}. Free Route send/forward " +
                            "activity logging will keep failing until this is corrected manually.",
                    CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, e);
        }
    }
}