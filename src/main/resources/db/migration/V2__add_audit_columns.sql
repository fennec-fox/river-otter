ALTER TABLE holiday_calendar
    ADD COLUMN modified_at DATETIME(6) NULL,
    ADD COLUMN auditor     VARCHAR(120) NULL;

UPDATE holiday_calendar
SET modified_at = created_at
WHERE modified_at IS NULL;

ALTER TABLE holiday_calendar
    MODIFY COLUMN modified_at DATETIME(6) NOT NULL;

ALTER TABLE topic
    ADD COLUMN modified_at DATETIME(6) NULL,
    ADD COLUMN auditor     VARCHAR(120) NULL;

UPDATE topic
SET modified_at = created_at
WHERE modified_at IS NULL;

ALTER TABLE topic
    MODIFY COLUMN modified_at DATETIME(6) NOT NULL;

ALTER TABLE topic_calendar
    ADD COLUMN auditor VARCHAR(120) NULL;
