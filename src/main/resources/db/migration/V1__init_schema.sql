CREATE TABLE holiday_calendar (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    locale        VARCHAR(2)   NOT NULL,
    year          INT          NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_holiday_calendar_locale_year (locale, year)
) ENGINE = InnoDB;

CREATE TABLE holiday (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    holiday_calendar_id   BIGINT        NOT NULL,
    date                  DATE          NOT NULL,
    name                  VARCHAR(255)  NOT NULL,
    type                  VARCHAR(30)   NOT NULL,
    description           VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_holiday_date (holiday_calendar_id, date),
    CONSTRAINT fk_holiday_holiday_calendar
        FOREIGN KEY (holiday_calendar_id) REFERENCES holiday_calendar (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE topic (
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE topic_calendar (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    topic_id      VARCHAR(36)  NOT NULL,
    locale        VARCHAR(2)   NOT NULL,
    year          INT          NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    modified_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_topic_calendar (topic_id, locale, year),
    CONSTRAINT fk_topic_calendar_topic
        FOREIGN KEY (topic_id) REFERENCES topic (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE topic_holiday (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    topic_calendar_id    BIGINT        NOT NULL,
    date                 DATE          NOT NULL,
    name                 VARCHAR(255)  NOT NULL,
    type                 VARCHAR(30)   NOT NULL DEFAULT 'TOPIC_HOLIDAY',
    description          VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_topic_holiday_date (topic_calendar_id, date),
    CONSTRAINT fk_topic_holiday_topic_calendar
        FOREIGN KEY (topic_calendar_id) REFERENCES topic_calendar (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE topic_workday (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    topic_calendar_id    BIGINT        NOT NULL,
    date                 DATE          NOT NULL,
    name                 VARCHAR(255)  NOT NULL,
    type                 VARCHAR(30)   NOT NULL,
    description          VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_topic_workday_date (topic_calendar_id, date),
    CONSTRAINT fk_topic_workday_topic_calendar
        FOREIGN KEY (topic_calendar_id) REFERENCES topic_calendar (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE topic_work_schedule (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    topic_id       VARCHAR(36)  NOT NULL,
    mon_is_on      BOOLEAN      NOT NULL,
    mon_is_24h     BOOLEAN      NOT NULL,
    mon_start_time TIME         NULL,
    mon_end_time   TIME         NULL,
    tue_is_on      BOOLEAN      NOT NULL,
    tue_is_24h     BOOLEAN      NOT NULL,
    tue_start_time TIME         NULL,
    tue_end_time   TIME         NULL,
    wed_is_on      BOOLEAN      NOT NULL,
    wed_is_24h     BOOLEAN      NOT NULL,
    wed_start_time TIME         NULL,
    wed_end_time   TIME         NULL,
    thu_is_on      BOOLEAN      NOT NULL,
    thu_is_24h     BOOLEAN      NOT NULL,
    thu_start_time TIME         NULL,
    thu_end_time   TIME         NULL,
    fri_is_on      BOOLEAN      NOT NULL,
    fri_is_24h     BOOLEAN      NOT NULL,
    fri_start_time TIME         NULL,
    fri_end_time   TIME         NULL,
    sat_is_on      BOOLEAN      NOT NULL,
    sat_is_24h     BOOLEAN      NOT NULL,
    sat_start_time TIME         NULL,
    sat_end_time   TIME         NULL,
    sun_is_on      BOOLEAN      NOT NULL,
    sun_is_24h     BOOLEAN      NOT NULL,
    sun_start_time TIME         NULL,
    sun_end_time   TIME         NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_topic_work_schedule_topic (topic_id),
    CONSTRAINT fk_topic_work_schedule_topic
        FOREIGN KEY (topic_id) REFERENCES topic (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;
