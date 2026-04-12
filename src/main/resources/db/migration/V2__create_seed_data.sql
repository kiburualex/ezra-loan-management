-- =====================================================
-- 1. CUSTOMERS (3 distinct types)
-- =====================================================
INSERT INTO customers (first_name, last_name, email, phone, address, date_of_birth, national_id, preferred_channel, current_loan_limit) VALUES

-- Customer 1: Good credit (high limit)
('John', 'Doe', 'john.doe@email.com', '0712345678', '123 Nairobi Street', '1985-03-15', '12345678', 'EMAIL', 500000.00),

-- Customer 2: Medium credit (medium limit)
('Sarah', 'Wanjiku', 'sarah.wanjiku@email.com', '0745678901', '101 Nakuru Street', '1992-05-18', '45678901', 'SMS', 250000.00),

-- Customer 3: Poor credit (low limit)
('Peter', 'Otieno', 'peter.otieno@email.com', '0778901234', '404 Machakos Street', '1993-08-25', '78901234', 'SMS', 50000.00);

-- =====================================================
-- 2. PRODUCTS (3 distinct types)
-- =====================================================
INSERT INTO products (name, description, tenure_type, tenure_value, interest_rate,
                      service_fee_type, service_fee_value,
                      daily_fee_type, daily_fee_value,
                      late_fee_type, late_fee_value,
                      days_after_due_for_late_fee, days_after_due_for_daily_fee,
                      min_loan_amount, max_loan_amount, active) VALUES

-- Product 1: Emergency Loan (Short-term, DAYS, high interest, has daily fee)
('Emergency Loan', 'Quick short-term loan', 'DAYS', 30, 15.00,
 'FIXED', 500.00,
 'PERCENTAGE', 0.50,
 'FIXED', 1000.00,
 3, 7,
 1000.00, 50000.00, true),

-- Product 2: Business Loan (Long-term, MONTHS, installment-based)
('Business Loan', 'Business expansion loan', 'MONTHS', 12, 12.00,
 'PERCENTAGE', 2.50,
 'PERCENTAGE', 0.20,
 'PERCENTAGE', 5.00,
 5, 10,
 50000.00, 500000.00, true),

-- Product 3: Salary Advance (Short-term, no daily fee, simple)
('Salary Advance', 'Salary advance loan', 'DAYS', 30, 8.00,
 'FIXED', 200.00,
 NULL, NULL,
 'FIXED', 500.00,
 3, NULL,
 5000.00, 100000.00, true);

-- =====================================================
-- 3. LOANS (3 distinct scenarios)
-- =====================================================

-- Loan 1: Customer 1 (good credit) with Business Loan (installment-based, OPEN)
INSERT INTO loans (customer_id, product_id, principal_amount, disbursed_amount, total_repayable_amount,
                   origination_date, is_installment_based, number_of_installments, billing_type, status)
VALUES (1, 2, 100000.00, 100000.00, 124500.00,
        CURRENT_DATE - INTERVAL '30 days', true, 12, 'INDIVIDUAL', 'OPEN');

-- Loan 2: Customer 2 (medium credit) with Emergency Loan (lump-sum, OVERDUE)
INSERT INTO loans (customer_id, product_id, principal_amount, disbursed_amount, total_repayable_amount,
                   origination_date, is_installment_based, number_of_installments, billing_type, status)
VALUES (2, 1, 25000.00, 25000.00, 28750.00,
        CURRENT_DATE - INTERVAL '40 days', false, NULL, 'INDIVIDUAL', 'OVERDUE');

-- Loan 3: Customer 3 (poor credit) with Salary Advance (lump-sum, CLOSED - fully paid)
INSERT INTO loans (customer_id, product_id, principal_amount, disbursed_amount, total_repayable_amount,
                   origination_date, is_installment_based, number_of_installments, billing_type, status)
VALUES (3, 3, 10000.00, 10000.00, 11200.00,
        CURRENT_DATE - INTERVAL '90 days', false, NULL, 'INDIVIDUAL', 'CLOSED');