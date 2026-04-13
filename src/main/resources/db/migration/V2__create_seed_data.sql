-- =====================================================
-- 1. CUSTOMERS SEED DATA
-- =====================================================
INSERT INTO customers (first_name, last_name, email, phone, address, date_of_birth, national_id, preferred_channel,
                       current_loan_limit)
VALUES
-- Customer 1: Good credit (high limit)
('John', 'Doe', 'john.doe@email.com', '0712345678', '123 Nairobi Street', '1985-03-15', '12345678', 'EMAIL', 500000.00),
-- Customer 2: Medium credit (medium limit)
('Sarah', 'Wanjiku', 'sarah.wanjiku@email.com', '0745678901', '101 Nakuru Street', '1992-05-18', '45678901', 'SMS',
 250000.00),
-- Customer 3: Poor credit (low limit)
('Peter', 'Otieno', 'peter.otieno@email.com', '0778901234', '404 Machakos Street', '1993-08-25', '78901234', 'SMS',
 50000.00);

-- =====================================================
-- 2. PRODUCTS SEED DATA
-- =====================================================
INSERT INTO products (name, description, tenure_type, tenure_value, interest_rate,
                      service_fee_type, service_fee_value,
                      daily_fee_type, daily_fee_value,
                      late_fee_type, late_fee_value,
                      days_after_due_for_late_fee, days_after_due_for_daily_fee,
                      min_loan_amount, max_loan_amount, active)
VALUES
-- Product 1: Emergency Loan (Short-term, DAYS, high interest, has daily fee)
('Emergency Loan', 'Quick short-term loan for emergencies', 'DAYS', 30, 15.00,
 'FIXED', 500.00, 'PERCENTAGE', 0.50, 'FIXED', 1000.00, 3, 7, 1000.00, 50000.00, true),
-- Product 2: Business Loan (Long-term, MONTHS, installment-based, percentage fees)
('Business Loan', 'Business expansion loan for enterprises', 'MONTHS', 12, 12.00,
 'PERCENTAGE', 2.50, 'PERCENTAGE', 0.20, 'PERCENTAGE', 5.00, 5, 10, 50000.00, 500000.00, true),
-- Product 3: Salary Advance (Short-term, no daily fee, simple structure)
('Salary Advance', 'Salary advance loan for employees', 'DAYS', 30, 8.00,
 'FIXED', 200.00, NULL, NULL, 'FIXED', 500.00, 3, NULL, 5000.00, 100000.00, true);

-- =====================================================
-- 3. LOANS SEED DATA
-- =====================================================
INSERT INTO loans (customer_id, product_id, principal_amount, disbursed_amount, total_repayable_amount,
                   origination_date, is_installment_based, number_of_installments, credit_score, billing_type, status)
VALUES
-- Loan 1: Customer 1 (good credit) with Business Loan (installment-based, OPEN)
(1, 2, 100000.00, 100000.00, 124500.00, CURRENT_DATE - INTERVAL '30 days', true, 12, 823, 'INDIVIDUAL', 'OPEN'),
-- Loan 2: Customer 2 (medium credit) with Emergency Loan (lump-sum, OVERDUE)
(2, 1, 25000.00, 25000.00, 28750.00, CURRENT_DATE - INTERVAL '40 days', false, NULL, 650, 'INDIVIDUAL', 'OVERDUE'),
-- Loan 3: Customer 3 (poor credit) with Salary Advance (lump-sum, CLOSED)
(3, 3, 10000.00, 10000.00, 11200.00, CURRENT_DATE - INTERVAL '90 days', false, NULL, 450, 'INDIVIDUAL', 'CLOSED');

-- =====================================================
-- 4. LOAN INSTALLMENTS SEED DATA
-- =====================================================
-- Installments for Loan 1: Business Loan (12 monthly installments, 2 paid)
DO
$$
DECLARE
loan_id_val INTEGER := 1;
    installment_num
INTEGER;
    due_date_val
DATE;
    amount_due_val
DECIMAL := 10375.00;
BEGIN
FOR installment_num IN 1..12 LOOP
        due_date_val := (CURRENT_DATE - INTERVAL '30 days' + (installment_num * INTERVAL '1 month'))::DATE;
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (loan_id_val, installment_num, due_date_val, amount_due_val,
        CASE WHEN installment_num <= 2 THEN amount_due_val ELSE 0 END,
        CASE WHEN installment_num <= 2 THEN 'PAID' ELSE 'PENDING' END,
        CASE WHEN installment_num <= 2 THEN CURRENT_DATE - INTERVAL '2 days' ELSE NULL END);
END LOOP;
END $$;

-- Installment for Loan 2: Emergency Loan (single lump sum, OVERDUE)
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status)
VALUES (2, 1, (CURRENT_DATE - INTERVAL '10 days'), 28750.00, 0, 'OVERDUE');

-- Installment for Loan 3: Salary Advance (single lump sum, PAID)
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (3, 1, (CURRENT_DATE - INTERVAL '60 days'), 11200.00, 11200.00, 'PAID', CURRENT_DATE - INTERVAL '60 days');

-- =====================================================
-- 5. REPAYMENTS SEED DATA
-- =====================================================
INSERT INTO repayments (loan_id, installment_id, amount, payment_date, payment_method)
VALUES
-- Loan 1 payments
(1, 1, 10375.00, CURRENT_DATE - INTERVAL '28 days', 'MPESA'),
(1, 2, 10375.00, CURRENT_DATE - INTERVAL '2 days', 'BANK_TRANSFER'),
-- Loan 3 payment
(3, 1, 11200.00, CURRENT_DATE - INTERVAL '60 days', 'CASH');

-- =====================================================
-- 6. LOAN FEES SEED DATA
-- =====================================================
INSERT INTO loan_fees (loan_id, fee_type, amount, applied_date, reason)
VALUES
-- Service fees
(1, 'SERVICE', 2500.00, CURRENT_DATE - INTERVAL '30 days', 'Service fee for loan origination'),
(2, 'SERVICE', 500.00, CURRENT_DATE - INTERVAL '40 days', 'Service fee for loan origination'),
(3, 'SERVICE', 200.00, CURRENT_DATE - INTERVAL '90 days', 'Service fee for loan origination'),
-- Late fee
(2, 'LATE', 1000.00, CURRENT_DATE - INTERVAL '7 days', 'Late fee for overdue installment');

-- =====================================================
-- 7. CUSTOMER FINANCIAL METRICS SEED DATA
-- =====================================================
INSERT INTO customer_financial_metrics (customer_id, total_loans_taken, total_amount_borrowed, total_amount_repaid,
                                        on_time_repayment_rate, number_of_defaults, total_days_late,
                                        last_loan_date, last_repayment_date)
VALUES (1, 1, 100000.00, 20750.00, 0.8500, 0, 0, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '2 days'),
       (2, 1, 25000.00, 0, 0.0000, 1, 30, CURRENT_DATE - INTERVAL '40 days', NULL),
       (3, 1, 10000.00, 11200.00, 1.0000, 0, 0, CURRENT_DATE - INTERVAL '90 days', CURRENT_DATE - INTERVAL '60 days');

-- =====================================================
-- 8. CREDIT SCORING HISTORY SEED DATA
-- =====================================================
INSERT INTO credit_scoring_history (customer_id, score, decision, calculated_at, repayment_factor, default_factor,
                                    utilization_factor, raw_formula, notes)
VALUES (1, 823, 'APPROVE', CURRENT_DATE - INTERVAL '35 days', 100.00, 100.00, 80.00,
        'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5',
        'Excellent credit score'),
       (2, 650, 'APPROVE', CURRENT_DATE - INTERVAL '45 days', 75.00, 80.00, 60.00,
        'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5',
        'Good score, has default'),
       (3, 450, 'REJECT', CURRENT_DATE - INTERVAL '95 days', 40.00, 30.00, 50.00,
        'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5',
        'Poor credit score');

-- =====================================================
-- 9. NOTIFICATION TEMPLATES SEED DATA
-- =====================================================
INSERT INTO notification_templates (event_type, channel, subject, body)
VALUES
-- Loan Creation
('LOAN_CREATED', 'SMS', NULL,
 'Dear {customer_name}, your loan of {principal_amount} has been approved. Total repayable: {total_repayable}.'),
('LOAN_CREATED', 'EMAIL', 'Loan Approved - {loan_id}',
 'Dear {customer_name},<br/><br/>Your loan has been approved.<br/>Loan ID: {loan_id}<br/>Principal: {principal_amount}<br/>Total Repayable: {total_repayable}'),
-- Payment Reminders
('PAYMENT_REMINDER_3_DAYS', 'SMS', NULL, 'Reminder: Your loan payment is due in 3 days.'),
('PAYMENT_REMINDER_1_DAY', 'SMS', NULL, 'FINAL REMINDER: Your loan payment is due TOMORROW.'),
-- Overdue Notifications
('LOAN_OVERDUE_FIRST', 'SMS', NULL, 'Your loan payment is now overdue.'),
('OVERDUE_3_DAYS', 'SMS', NULL, 'URGENT: Your loan is 3 days overdue. Late fees applied.'),
('OVERDUE_7_DAYS', 'SMS', NULL, 'FINAL WARNING: Your loan is 7 days overdue.'),
('OVERDUE_14_DAYS', 'EMAIL', 'URGENT: Loan Overdue',
 'Your loan #{loan_id} is 14 days overdue. Contact us immediately.'),
('OVERDUE_30_DAYS', 'EMAIL', 'FINAL NOTICE: Loan Written Off', 'Your loan #{loan_id} has been written off.'),
-- Repayment Confirmation
('REPAYMENT_RECEIVED', 'SMS', NULL, 'Payment of {amount} received. Thank you!'),
('REPAYMENT_RECEIVED', 'EMAIL', 'Payment Confirmation', 'We have received your payment of {amount}.'),
-- Fee Applied
('FEE_APPLIED', 'SMS', NULL, 'A fee of {amount} has been applied to your loan.');

-- =====================================================
-- 10. NOTIFICATIONS SEED DATA
-- =====================================================
INSERT INTO notifications (customer_id, loan_id, channel, rendered_message, sent_at, status)
VALUES (1, 1, 'SMS', 'Dear John, your loan has been approved.', CURRENT_DATE - INTERVAL '30 days', 'SENT'),
       (1, 1, 'SMS', 'Payment of 10375.00 received.', CURRENT_DATE - INTERVAL '28 days', 'SENT'),
       (2, 2, 'SMS', 'Your loan payment is now overdue.', CURRENT_DATE - INTERVAL '10 days', 'SENT'),
       (2, 2, 'SMS', 'URGENT: Your loan is 3 days overdue.', CURRENT_DATE - INTERVAL '7 days', 'SENT'),
       (3, 3, 'SMS', 'Your loan has been approved.', CURRENT_DATE - INTERVAL '90 days', 'SENT'),
       (3, 3, 'SMS', 'Payment of 11200.00 received.', CURRENT_DATE - INTERVAL '60 days', 'SENT');

-- =====================================================
-- 11. CREDIT NOTES SEED DATA
-- =====================================================
INSERT INTO credit_notes (loan_id, amount, reason, used, notes)
VALUES
-- Credit Note 1: Overpayment on Loan 1 (unused)
(1, 500.00, 'OVERPAYMENT', false, 'Customer overpayment'),
-- Credit Note 2: Fee reversal on Loan 2 (used)
(2, 250.00, 'FEE_REVERSAL', true, 'Fee reversal due to dispute'),
-- Credit Note 3: Manual adjustment on Loan 3 (unused)
(3, 100.00, 'ADJUSTMENT', false, 'Manual adjustment');
-- =====================================================
-- UPDATE SEQUENCES
-- =====================================================
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('loans_id_seq', (SELECT MAX(id) FROM loans));
SELECT setval('loan_installments_id_seq', (SELECT MAX(id) FROM loan_installments));
SELECT setval('repayments_id_seq', (SELECT MAX(id) FROM repayments));
SELECT setval('loan_fees_id_seq', (SELECT MAX(id) FROM loan_fees));
SELECT setval('credit_scoring_history_id_seq', (SELECT MAX(id) FROM credit_scoring_history));
SELECT setval('notification_templates_id_seq', (SELECT MAX(id) FROM notification_templates));
SELECT setval('notifications_id_seq', (SELECT MAX(id) FROM notifications));
SELECT setval('credit_notes_id_seq', (SELECT MAX(id) FROM credit_notes));