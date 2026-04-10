-- 1. CUSTOMERS
CREATE TABLE customers
(
    id                 BIGSERIAL PRIMARY KEY,
    first_name         VARCHAR(100)        NOT NULL,
    last_name          VARCHAR(100)        NOT NULL,
    email              VARCHAR(255) UNIQUE NOT NULL,
    phone              VARCHAR(50) UNIQUE  NOT NULL,
    address            TEXT,
    date_of_birth      DATE                NOT NULL,
    national_id        VARCHAR(50) UNIQUE  NOT NULL,
    preferred_channel  VARCHAR(20)    DEFAULT 'SMS' CHECK (preferred_channel IN ('EMAIL', 'SMS', 'PUSH')),
    current_loan_limit DECIMAL(15, 2) DEFAULT 0.00 CHECK (current_loan_limit >= 0),
    created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);


-- =============================================
-- INDEXES FOR PERFORMANCE (sweep jobs, scoring, reporting)
-- =============================================