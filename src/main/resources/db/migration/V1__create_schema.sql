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
    customer_id            BIGINT         NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    product_id             BIGINT         NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    principal_amount       DECIMAL(15, 2) NOT NULL CHECK (principal_amount > 0),
    disbursed_amount       DECIMAL(15, 2) NOT NULL CHECK (disbursed_amount > 0),
    total_repayable_amount DECIMAL(15, 2) NOT NULL,                           -- principal + interest + fees (calculated at disbursement)
    origination_date       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_installment_based   BOOLEAN        NOT NULL DEFAULT FALSE,
    number_of_installments INT CHECK (number_of_installments > 0 OR number_of_installments IS NULL),
    credit_score           INT            NOT NULL DEFAULT 0,
    billing_type           VARCHAR(20)    NOT NULL DEFAULT 'INDIVIDUAL' CHECK (billing_type IN ('INDIVIDUAL', 'CONSOLIDATED')),
    consolidated_due_day   INT CHECK (consolidated_due_day BETWEEN 1 AND 31), -- for consolidated billing (e.g. 5th of month)
    status                 VARCHAR(20)    NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED', 'OVERDUE', 'WRITTEN_OFF')),
    created_at             TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

-- 4. LOAN INSTALLMENTS (unifies lump-sum + installment loans)
CREATE TABLE loan_installments
(
    id                 BIGSERIAL PRIMARY KEY,
    loan_id            BIGINT         NOT NULL REFERENCES loans (id) ON DELETE RESTRICT,
    installment_number INT            NOT NULL,
    due_date           DATE           NOT NULL,
    amount_due         DECIMAL(15, 2) NOT NULL CHECK (amount_due > 0),
    amount_paid        DECIMAL(15, 2) DEFAULT 0.00 CHECK (amount_paid >= 0),
    status             VARCHAR(20)    DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PAID', 'OVERDUE')),
    paid_date          TIMESTAMP,
    created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- 5. REPAYMENTS (audit trail of all payments)
CREATE TABLE repayments
(
    id             BIGSERIAL PRIMARY KEY,
    loan_id        BIGINT         NOT NULL REFERENCES loans (id) ON DELETE RESTRICT,
    installment_id BIGINT         REFERENCES loan_installments (id) ON DELETE SET NULL,
    amount         DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    payment_date   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50),
    created_at     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

-- 6. LOAN FEES (applied fees - service, daily, late)
CREATE TABLE loan_fees
(
    id           BIGSERIAL PRIMARY KEY,
    loan_id      BIGINT         NOT NULL REFERENCES loans (id) ON DELETE RESTRICT,
    fee_type     VARCHAR(20)    NOT NULL CHECK (fee_type IN ('SERVICE', 'DAILY', 'LATE')),
    amount       DECIMAL(15, 2) NOT NULL CHECK (amount >= 0),
    applied_date TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason       TEXT
);

-- 7. FINANCIAL HISTORY & METRICS (denormalized summary for fast scoring)
-- Updated automatically by sweep jobs / repayment listeners
CREATE TABLE customer_financial_metrics
(
    customer_id            BIGINT PRIMARY KEY REFERENCES customers (id) ON DELETE RESTRICT,
    total_loans_taken      INT            DEFAULT 0,
    total_amount_borrowed  DECIMAL(15, 2) DEFAULT 0.00,
    total_amount_repaid    DECIMAL(15, 2) DEFAULT 0.00,
    on_time_repayment_rate DECIMAL(5, 4)  DEFAULT 1.0000, -- 0.0000 to 1.0000
    number_of_defaults     INT            DEFAULT 0,
    total_days_late        INT            DEFAULT 0,
    average_days_late      DECIMAL(8, 2)  DEFAULT 0.00,
    last_loan_date         DATE,
    last_repayment_date    DATE,
    created_at             TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- 8. CREDIT SCORING HISTORY (audit of every score calculation)
CREATE TABLE credit_scoring_history
(
    id                 BIGSERIAL PRIMARY KEY,
    customer_id        BIGINT NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    score              INT    NOT NULL CHECK (score BETWEEN 300 AND 850),
    decision           VARCHAR(20) CHECK (decision IN ('APPROVE', 'REJECT', 'REVIEW')),
    calculated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    repayment_factor   DECIMAL(5, 2),
    default_factor     DECIMAL(5, 2),
    utilization_factor DECIMAL(5, 2),
    raw_formula        TEXT, -- stores the exact formula used for audit
    notes              TEXT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. NOTIFICATION TEMPLATES
CREATE TABLE notification_templates
(
    id         BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,                             -- e.g. LOAN_CREATED, OVERDUE_3_DAYS
    channel    VARCHAR(20)  NOT NULL CHECK (channel IN ('EMAIL', 'SMS', 'PUSH')),
    subject    VARCHAR(255),
    body       TEXT         NOT NULL,                                    -- supports placeholders: {customer_name}, {loan_id}, {amount_due}, etc.
    product_id BIGINT       REFERENCES products (id) ON DELETE SET NULL, -- optional product-specific template
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. NOTIFICATIONS (sent history)
CREATE TABLE notifications
(
    id               BIGSERIAL PRIMARY KEY,
    customer_id      BIGINT      NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    loan_id          BIGINT REFERENCES loans (id) ON DELETE RESTRICT,
    channel          VARCHAR(20) NOT NULL,
    rendered_message TEXT        NOT NULL,
    sent_at          TIMESTAMP,
    status           VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    failure_reason   TEXT,
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- 11. CREDIT NOTES (for handling overpayments and adjustments)
CREATE TABLE credit_notes
(
    id         BIGSERIAL PRIMARY KEY,
    loan_id    BIGINT         NOT NULL REFERENCES loans (id) ON DELETE RESTRICT,
    amount     DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    reason     VARCHAR(50)    NOT NULL CHECK (reason IN ('OVERPAYMENT', 'ADJUSTMENT', 'FEE_REVERSAL')),
    used       BOOLEAN        NOT NULL DEFAULT FALSE,
    notes      TEXT,
    created_at TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);


-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Customers indexes
CREATE INDEX idx_customers_email ON customers (email);
CREATE INDEX idx_customers_phone ON customers (phone);
CREATE INDEX idx_customers_national_id ON customers (national_id);
CREATE INDEX idx_customers_created_at ON customers (created_at);

-- Products indexes
CREATE INDEX idx_products_active ON products (active);
CREATE INDEX idx_products_tenure_type ON products (tenure_type);
CREATE INDEX idx_products_min_max_loan ON products (min_loan_amount, max_loan_amount);

-- Loans indexes
CREATE INDEX idx_loans_customer_id ON loans (customer_id);
CREATE INDEX idx_loans_product_id ON loans (product_id);
CREATE INDEX idx_loans_status ON loans (status);
CREATE INDEX idx_loans_origination_date ON loans (origination_date);
CREATE INDEX idx_loans_customer_status ON loans (customer_id, status);
CREATE INDEX idx_loans_created_at ON loans (created_at);

-- Loan Installments indexes
CREATE INDEX idx_loan_installments_loan_id ON loan_installments (loan_id);
CREATE INDEX idx_loan_installments_due_date ON loan_installments (due_date);
CREATE INDEX idx_loan_installments_status ON loan_installments (status);
CREATE INDEX idx_loan_installments_loan_status ON loan_installments (loan_id, status);
CREATE INDEX idx_loan_installments_due_date_status ON loan_installments (due_date, status);

-- Repayments indexes
CREATE INDEX idx_repayments_loan_id ON repayments (loan_id);
CREATE INDEX idx_repayments_installment_id ON repayments (installment_id);
CREATE INDEX idx_repayments_payment_date ON repayments (payment_date);
CREATE INDEX idx_repayments_loan_date ON repayments (loan_id, payment_date);

-- Loan Fees indexes
CREATE INDEX idx_loan_fees_loan_id ON loan_fees (loan_id);
CREATE INDEX idx_loan_fees_fee_type ON loan_fees (fee_type);
CREATE INDEX idx_loan_fees_applied_date ON loan_fees (applied_date);
CREATE INDEX idx_loan_fees_loan_type ON loan_fees (loan_id, fee_type);

-- Customer Financial Metrics indexes
CREATE INDEX idx_customer_financial_metrics_updated ON customer_financial_metrics (updated_at);
CREATE INDEX idx_customer_financial_metrics_repayment_rate ON customer_financial_metrics (on_time_repayment_rate);

-- Credit Scoring History indexes
CREATE INDEX idx_credit_scoring_customer_id ON credit_scoring_history (customer_id);
CREATE INDEX idx_credit_scoring_calculated_at ON credit_scoring_history (calculated_at);
CREATE INDEX idx_credit_scoring_decision ON credit_scoring_history (decision);
CREATE INDEX idx_credit_scoring_customer_date ON credit_scoring_history (customer_id, calculated_at);

-- Notification Templates indexes
CREATE INDEX idx_notification_templates_event_type ON notification_templates (event_type);
CREATE INDEX idx_notification_templates_product_id ON notification_templates (product_id);
CREATE INDEX idx_notification_templates_channel ON notification_templates (channel);

-- Notifications indexes
CREATE INDEX idx_notifications_customer_id ON notifications (customer_id);
CREATE INDEX idx_notifications_loan_id ON notifications (loan_id);
CREATE INDEX idx_notifications_status ON notifications (status);
CREATE INDEX idx_notifications_sent_at ON notifications (sent_at);
CREATE INDEX idx_notifications_customer_status ON notifications (customer_id, status);
CREATE INDEX idx_notifications_created_at ON notifications (created_at);

-- Credit Notes indexes
CREATE INDEX idx_credit_notes_loan_id ON credit_notes (loan_id);
CREATE INDEX idx_credit_notes_used ON credit_notes (used);
CREATE INDEX idx_credit_notes_loan_used ON credit_notes (loan_id, used);