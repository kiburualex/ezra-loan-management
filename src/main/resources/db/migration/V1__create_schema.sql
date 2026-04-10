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

-- 2. PRODUCTS (Loan Products with full fee & tenure config)
CREATE TABLE products
(
    id                           BIGSERIAL PRIMARY KEY,
    name                         VARCHAR(100)   NOT NULL,
    description                  TEXT,
    tenure_type                  VARCHAR(20)    NOT NULL CHECK (tenure_type IN ('DAYS', 'MONTHS')),
    tenure_value                 INT            NOT NULL CHECK (tenure_value > 0),
    interest_rate                DECIMAL(5, 2)  NOT NULL CHECK (interest_rate >= 0), -- e.g. 12.50 = 12.5%
    service_fee_type             VARCHAR(20) CHECK (service_fee_type IN ('FIXED', 'PERCENTAGE')),
    service_fee_value            DECIMAL(15, 2) NOT NULL CHECK (service_fee_value >= 0),
    daily_fee_type               VARCHAR(20) CHECK (daily_fee_type IN ('FIXED', 'PERCENTAGE')),
    daily_fee_value              DECIMAL(15, 2) CHECK (daily_fee_value >= 0),
    late_fee_type                VARCHAR(20) CHECK (late_fee_type IN ('FIXED', 'PERCENTAGE')),
    late_fee_value               DECIMAL(15, 2) NOT NULL CHECK (late_fee_value >= 0),
    days_after_due_for_late_fee  INT            NOT NULL DEFAULT 3 CHECK (days_after_due_for_late_fee >= 0),
    days_after_due_for_daily_fee INT                     DEFAULT NULL CHECK (days_after_due_for_daily_fee >= 0),
    min_loan_amount              DECIMAL(15, 2) NOT NULL DEFAULT 1000.00,
    max_loan_amount              DECIMAL(15, 2) NOT NULL DEFAULT 50000.00,
    active                       BOOLEAN                 DEFAULT TRUE,
    created_at                   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at                   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

-- 3. LOANS
CREATE TABLE loans
(
    id                     BIGSERIAL PRIMARY KEY,
    customer_id            BIGINT         NOT NULL REFERENCES customers (id) ON DELETE CASCADE,
    product_id             BIGINT         NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    principal_amount       DECIMAL(15, 2) NOT NULL CHECK (principal_amount > 0),
    disbursed_amount       DECIMAL(15, 2) NOT NULL CHECK (disbursed_amount > 0),
    total_repayable_amount DECIMAL(15, 2) NOT NULL,                           -- principal + interest + fees (calculated at disbursement)
    origination_date       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_installment_based   BOOLEAN        NOT NULL DEFAULT FALSE,
    number_of_installments INT CHECK (number_of_installments > 0 OR number_of_installments IS NULL),
    billing_type           VARCHAR(20)    NOT NULL DEFAULT 'INDIVIDUAL' CHECK (billing_type IN ('INDIVIDUAL', 'CONSOLIDATED')),
    consolidated_due_day   INT CHECK (consolidated_due_day BETWEEN 1 AND 31), -- for consolidated billing (e.g. 5th of month)
    status                 VARCHAR(20)    NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED', 'OVERDUE', 'WRITTEN_OFF')),
    created_at             TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- INDEXES FOR PERFORMANCE (sweep jobs, scoring, reporting)
-- =============================================