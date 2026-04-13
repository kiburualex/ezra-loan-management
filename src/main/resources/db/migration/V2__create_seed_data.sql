-- =====================================================
-- 1. CUSTOMERS SEED DATA
-- =====================================================
INSERT INTO customers (first_name, last_name, email, phone, address, date_of_birth, national_id, preferred_channel, current_loan_limit) VALUES

-- Customer 1: Good credit (high limit)
('John', 'Doe', 'john.doe@email.com', '0712345678', '123 Nairobi Street', '1985-03-15', '12345678', 'EMAIL', 500000.00),

-- Customer 2: Medium credit (medium limit)
('Sarah', 'Wanjiku', 'sarah.wanjiku@email.com', '0745678901', '101 Nakuru Street', '1992-05-18', '45678901', 'SMS', 250000.00),

-- Customer 3: Poor credit (low limit)
('Peter', 'Otieno', 'peter.otieno@email.com', '0778901234', '404 Machakos Street', '1993-08-25', '78901234', 'SMS', 50000.00),

-- Customer 4: Very good credit (very high limit)
('James', 'Mwangi', 'james.mwangi@email.com', '0723456789', '202 Kiambu Road', '1988-11-20', '11223344', 'EMAIL', 1000000.00),

-- Customer 5: Fair credit (medium-low limit)
('Mary', 'Atieno', 'mary.atieno@email.com', '0734567890', '303 Kisumu Road', '1995-02-10', '22334455', 'PUSH', 75000.00);

-- =====================================================
-- 2. PRODUCTS SEED DATA
-- =====================================================
INSERT INTO products (name, description, tenure_type, tenure_value, interest_rate,
                      service_fee_type, service_fee_value,
                      daily_fee_type, daily_fee_value,
                      late_fee_type, late_fee_value,
                      days_after_due_for_late_fee, days_after_due_for_daily_fee,
                      min_loan_amount, max_loan_amount, active) VALUES

-- Product 1: Emergency Loan (Short-term, DAYS, high interest, has daily fee)
('Emergency Loan', 'Quick short-term loan for emergencies', 'DAYS', 30, 15.00,
 'FIXED', 500.00,
 'PERCENTAGE', 0.50,
 'FIXED', 1000.00,
 3, 7,
 1000.00, 50000.00, true),

-- Product 2: Business Loan (Long-term, MONTHS, installment-based, percentage fees)
('Business Loan', 'Business expansion loan for enterprises', 'MONTHS', 12, 12.00,
 'PERCENTAGE', 2.50,
 'PERCENTAGE', 0.20,
 'PERCENTAGE', 5.00,
 5, 10,
 50000.00, 500000.00, true),

-- Product 3: Salary Advance (Short-term, no daily fee, simple structure)
('Salary Advance', 'Salary advance loan for employees', 'DAYS', 30, 8.00,
 'FIXED', 200.00,
 NULL, NULL,
 'FIXED', 500.00,
 3, NULL,
 5000.00, 100000.00, true),

-- Product 4: Education Loan (Medium-term, no daily fee, moderate interest)
('Education Loan', 'School fees and education expenses financing', 'MONTHS', 18, 10.00,
 'PERCENTAGE', 1.50,
 NULL, NULL,
 'FIXED', 800.00,
 7, NULL,
 10000.00, 200000.00, true),

-- Product 5: Car Loan (Long-term, high principal, fixed fees)
('Car Loan', 'Vehicle purchase financing', 'MONTHS', 36, 11.50,
 'FIXED', 5000.00,
 NULL, NULL,
 'PERCENTAGE', 3.00,
 5, NULL,
 100000.00, 1500000.00, true);

-- =====================================================
-- 3. LOANS SEED DATA
-- =====================================================
INSERT INTO loans (customer_id, product_id, principal_amount, disbursed_amount, total_repayable_amount,
                   origination_date, is_installment_based, number_of_installments, credit_score, billing_type, consolidated_due_day, status) VALUES

-- Loan 1: Customer 1 (good credit) with Business Loan (installment-based, OPEN, 12 months)
(1, 2, 100000.00, 100000.00, 124500.00, CURRENT_DATE - INTERVAL '30 days', true, 12, 823, 'INDIVIDUAL', NULL, 'OPEN'),

-- Loan 2: Customer 2 (medium credit) with Emergency Loan (lump-sum, OVERDUE, 30 days)
(2, 1, 25000.00, 25000.00, 28750.00, CURRENT_DATE - INTERVAL '40 days', false, NULL, 650, 'INDIVIDUAL', NULL, 'OVERDUE'),

-- Loan 3: Customer 3 (poor credit) with Salary Advance (lump-sum, CLOSED - fully paid)
(3, 3, 10000.00, 10000.00, 11200.00, CURRENT_DATE - INTERVAL '90 days', false, NULL, 450, 'INDIVIDUAL', NULL, 'CLOSED'),

-- Loan 4: Customer 4 (very good credit) with Education Loan (installment-based, OPEN, 18 months)
(4, 4, 50000.00, 50000.00, 62500.00, CURRENT_DATE - INTERVAL '15 days', true, 18, 780, 'INDIVIDUAL', NULL, 'OPEN'),

-- Loan 5: Customer 5 (fair credit) with Car Loan (installment-based, OPEN, 36 months, CONSOLIDATED billing)
(5, 5, 200000.00, 200000.00, 250000.00, CURRENT_DATE - INTERVAL '60 days', true, 36, 600, 'CONSOLIDATED', 15, 'OPEN'),

-- Loan 6: Customer 1 (good credit) with Emergency Loan (lump-sum, OPEN - new loan)
(1, 1, 15000.00, 15000.00, 17250.00, CURRENT_DATE - INTERVAL '5 days', false, NULL, 823, 'INDIVIDUAL', NULL, 'OPEN');

-- =====================================================
-- 4. LOAN INSTALLMENTS SEED DATA
-- =====================================================

-- Installments for Loan 1: Business Loan (12 monthly installments, 2 paid)
DO $$
DECLARE
loan_id_val INTEGER := 1;
    installment_num INTEGER;
    due_date_val DATE;
    amount_due_val DECIMAL := 10375.00;
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
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (2, 1, (CURRENT_DATE - INTERVAL '10 days'), 28750.00, 0, 'OVERDUE', NULL);

-- Installment for Loan 3: Salary Advance (single lump sum, fully PAID)
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (3, 1, (CURRENT_DATE - INTERVAL '60 days'), 11200.00, 11200.00, 'PAID', CURRENT_DATE - INTERVAL '60 days');

-- Installments for Loan 4: Education Loan (18 monthly installments, all PENDING)
DO $$
DECLARE
loan_id_val INTEGER := 4;
    installment_num INTEGER;
    due_date_val DATE;
    amount_due_val DECIMAL := 3472.22;
BEGIN
FOR installment_num IN 1..18 LOOP
        due_date_val := (CURRENT_DATE - INTERVAL '15 days' + (installment_num * INTERVAL '1 month'))::DATE;
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (loan_id_val, installment_num, due_date_val, amount_due_val, 0, 'PENDING', NULL);
END LOOP;
END $$;

-- Installments for Loan 5: Car Loan (36 monthly installments, CONSOLIDATED billing, 2 paid)
DO $$
DECLARE
loan_id_val INTEGER := 5;
    installment_num INTEGER;
    due_date_val DATE;
    amount_due_val DECIMAL := 6944.44;
BEGIN
FOR installment_num IN 1..36 LOOP
        due_date_val := (CURRENT_DATE - INTERVAL '60 days' + (installment_num * INTERVAL '1 month'))::DATE;
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (loan_id_val, installment_num, due_date_val, amount_due_val,
        CASE WHEN installment_num <= 2 THEN amount_due_val ELSE 0 END,
        CASE WHEN installment_num <= 2 THEN 'PAID' ELSE 'PENDING' END,
        CASE WHEN installment_num <= 2 THEN CURRENT_DATE - INTERVAL '2 days' ELSE NULL END);
END LOOP;
END $$;

-- Installment for Loan 6: Emergency Loan (single lump sum, PENDING)
INSERT INTO loan_installments (loan_id, installment_number, due_date, amount_due, amount_paid, status, paid_date)
VALUES (6, 1, (CURRENT_DATE + INTERVAL '25 days'), 17250.00, 0, 'PENDING', NULL);

-- =====================================================
-- 5. REPAYMENTS SEED DATA
-- =====================================================
INSERT INTO repayments (loan_id, installment_id, amount, payment_date, payment_method) VALUES

-- Repayment 1: Loan 1, Installment 1 (fully paid)
(1, 1, 10375.00, CURRENT_DATE - INTERVAL '28 days', 'MPESA'),

-- Repayment 2: Loan 1, Installment 2 (fully paid)
(1, 2, 10375.00, CURRENT_DATE - INTERVAL '2 days', 'BANK_TRANSFER'),

-- Repayment 3: Loan 3, Installment 3 (fully paid)
(3, 3, 11200.00, CURRENT_DATE - INTERVAL '60 days', 'CASH'),

-- Repayment 4: Loan 5, Installment 1 (fully paid)
(5, 1, 6944.44, CURRENT_DATE - INTERVAL '58 days', 'MPESA'),

-- Repayment 5: Loan 5, Installment 2 (fully paid)
(5, 2, 6944.44, CURRENT_DATE - INTERVAL '2 days', 'MPESA'),

-- Repayment 6: Partial payment on Loan 4 (extra payment)
(4, NULL, 5000.00, CURRENT_DATE - INTERVAL '5 days', 'BANK_TRANSFER');

-- =====================================================
-- 6. LOAN FEES SEED DATA
-- =====================================================
INSERT INTO loan_fees (loan_id, fee_type, amount, applied_date, reason) VALUES

-- Fee 1: Service fee for Loan 1 (Business Loan)
(1, 'SERVICE', 2500.00, CURRENT_DATE - INTERVAL '30 days', 'Service fee for loan origination (Product: Business Loan)'),

-- Fee 2: Service fee for Loan 2 (Emergency Loan)
(2, 'SERVICE', 500.00, CURRENT_DATE - INTERVAL '40 days', 'Service fee for loan origination (Product: Emergency Loan)'),

-- Fee 3: Late fee for Loan 2 (overdue)
(2, 'LATE', 1000.00, CURRENT_DATE - INTERVAL '7 days', 'Late fee for overdue installment'),

-- Fee 4: Service fee for Loan 4 (Education Loan)
(4, 'SERVICE', 750.00, CURRENT_DATE - INTERVAL '15 days', 'Service fee for loan origination (Product: Education Loan)'),

-- Fee 5: Service fee for Loan 5 (Car Loan)
(5, 'SERVICE', 5000.00, CURRENT_DATE - INTERVAL '60 days', 'Service fee for loan origination (Product: Car Loan)'),

-- Fee 6: Service fee for Loan 6 (Emergency Loan)
(6, 'SERVICE', 500.00, CURRENT_DATE - INTERVAL '5 days', 'Service fee for loan origination (Product: Emergency Loan)');

-- =====================================================
-- 7. CUSTOMER FINANCIAL METRICS SEED DATA
-- =====================================================
INSERT INTO customer_financial_metrics (customer_id, total_loans_taken, total_amount_borrowed, total_amount_repaid,
                                        on_time_repayment_rate, number_of_defaults, total_days_late,
                                        average_days_late, last_loan_date, last_repayment_date) VALUES

-- Customer 1: John Doe (2 loans, good repayment history)
(1, 2, 115000.00, 20750.00, 0.8500, 0, 0, 0.00, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '2 days'),

-- Customer 2: Sarah Wanjiku (1 loan, defaulted, overdue)
(2, 1, 25000.00, 0, 0.0000, 1, 30, 30.00, CURRENT_DATE - INTERVAL '40 days', NULL),

-- Customer 3: Peter Otieno (1 loan, fully paid)
(3, 1, 10000.00, 11200.00, 1.0000, 0, 0, 0.00, CURRENT_DATE - INTERVAL '90 days', CURRENT_DATE - INTERVAL '60 days'),

-- Customer 4: James Mwangi (1 loan, no payments yet)
(4, 1, 50000.00, 5000.00, 0.5000, 0, 0, 0.00, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '5 days'),

-- Customer 5: Mary Atieno (1 loan, 2 payments made)
(5, 1, 200000.00, 13888.88, 0.5000, 0, 0, 0.00, CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '2 days');

-- =====================================================
-- 8. CREDIT SCORING HISTORY SEED DATA
-- =====================================================
INSERT INTO credit_scoring_history (customer_id, score, decision, calculated_at, repayment_factor, default_factor, utilization_factor, raw_formula, notes) VALUES

-- Customer 1: John Doe (Excellent score)
(1, 823, 'APPROVE', CURRENT_DATE - INTERVAL '35 days', 100.00, 100.00, 80.00, 'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5', 'Excellent credit score, approved for high limits'),

-- Customer 2: Sarah Wanjiku (Good score, but has default)
(2, 650, 'APPROVE', CURRENT_DATE - INTERVAL '45 days', 75.00, 80.00, 60.00, 'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5', 'Good score, approved with medium limit'),

-- Customer 3: Peter Otieno (Poor score, rejected)
(3, 450, 'REJECT', CURRENT_DATE - INTERVAL '95 days', 40.00, 30.00, 50.00, 'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5', 'Poor credit score, loan rejected'),

-- Customer 4: James Mwangi (Borderline score, requires review)
(4, 580, 'REVIEW', CURRENT_DATE - INTERVAL '20 days', 60.00, 70.00, 55.00, 'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5', 'Borderline score, requires manual review'),

-- Customer 5: Mary Atieno (Fair score, approved)
(5, 600, 'APPROVE', CURRENT_DATE - INTERVAL '65 days', 70.00, 75.00, 45.00, 'Score = 300 + (repayment_factor * 0.40 + default_factor * 0.30 + utilization_factor * 0.20 + tenure_factor * 0.10) * 5.5', 'Fair score, approved for car loan');

-- =====================================================
-- 9. NOTIFICATION TEMPLATES SEED DATA
-- =====================================================
INSERT INTO notification_templates (event_type, channel, subject, body, product_id) VALUES

-- Loan Creation Events
('LOAN_CREATED', 'SMS', NULL, 'Dear {customer_name}, your loan of {principal_amount} has been approved and disbursed. Total repayable: {total_repayable}. Thank you for choosing us!', NULL),
('LOAN_CREATED', 'EMAIL', 'Loan Approved - {loan_id}', 'Dear {customer_name},<br/><br/>Congratulations! Your loan application has been approved.<br/><br/>Loan Details:<br/>- Loan ID: {loan_id}<br/>- Principal Amount: {principal_amount}<br/>- Total Repayable: {total_repayable}<br/><br/>Thank you for banking with us.', NULL),

-- Payment Reminders
('PAYMENT_REMINDER_3_DAYS', 'SMS', NULL, 'Reminder: Your loan payment of {amount_due} is due in 3 days. Please ensure sufficient funds.', NULL),
('PAYMENT_REMINDER_1_DAY', 'SMS', NULL, 'FINAL REMINDER: Your loan payment of {amount_due} is due TOMORROW. Please make your payment to avoid late fees.', NULL),

-- Overdue Notifications
('LOAN_OVERDUE_FIRST', 'SMS', NULL, 'Your loan payment is now overdue. Please make a payment to avoid additional fees.', NULL),
('OVERDUE_3_DAYS', 'SMS', NULL, 'URGENT: Your loan is 3 days overdue. Late fees have been applied. Please pay immediately.', NULL),
('OVERDUE_7_DAYS', 'SMS', NULL, 'FINAL WARNING: Your loan is 7 days overdue. Your account may be sent to collections if not paid within 7 days.', NULL),
('OVERDUE_14_DAYS', 'EMAIL', 'URGENT: Loan Overdue - Action Required', 'Dear {customer_name},<br/><br/>Your loan #{loan_id} is now 14 days overdue.<br/><br/>Please contact us immediately to arrange payment.<br/><br/>Regards,<br/>Credit Department', NULL),
('OVERDUE_30_DAYS', 'EMAIL', 'FINAL NOTICE: Loan Written Off', 'Dear {customer_name},<br/><br/>Your loan #{loan_id} has been written off due to non-payment.<br/><br/>Please contact our collections department immediately.', NULL),

-- Repayment Confirmation
('REPAYMENT_RECEIVED', 'SMS', NULL, 'Payment of {amount} received for your loan. Thank you!', NULL),
('REPAYMENT_RECEIVED', 'EMAIL', 'Payment Confirmation - Loan {loan_id}', 'Dear {customer_name},<br/><br/>We have received your payment of {amount}.<br/><br/>Thank you for your prompt payment.', NULL),

-- Fee Applied
('FEE_APPLIED', 'SMS', NULL, 'A fee of {amount} has been applied to your loan. Please check your account.', NULL),

-- Product-specific templates
('BUSINESS_LOAN_REMINDER', 'SMS', NULL, 'Business loan payment reminder for {customer_name}', 2),
('EDUCATION_LOAN_REMINDER', 'EMAIL', 'Education Loan Update', 'Your education loan payment is due soon. Please ensure funds are available.', 4);

-- =====================================================
-- 10. NOTIFICATIONS SEED DATA (sent history)
-- =====================================================
INSERT INTO notifications (customer_id, loan_id, channel, rendered_message, sent_at, status) VALUES

-- Loan 1 notifications (John Doe)
(1, 1, 'SMS', 'Dear John, your loan of 100000.00 has been approved and disbursed.', CURRENT_DATE - INTERVAL '30 days', 'SENT'),
(1, 1, 'EMAIL', 'Dear John, your loan has been approved', CURRENT_DATE - INTERVAL '30 days', 'SENT'),
(1, 1, 'SMS', 'Reminder: Your loan payment is due in 3 days', CURRENT_DATE - INTERVAL '5 days', 'SENT'),
(1, 1, 'SMS', 'Payment of 10375.00 received for your loan. Thank you!', CURRENT_DATE - INTERVAL '28 days', 'SENT'),
(1, 1, 'SMS', 'Payment of 10375.00 received for your loan. Thank you!', CURRENT_DATE - INTERVAL '2 days', 'SENT'),

-- Loan 2 notifications (Sarah Wanjiku - overdue)
(2, 2, 'SMS', 'Your loan payment is now overdue. Please make a payment to avoid additional fees.', CURRENT_DATE - INTERVAL '10 days', 'SENT'),
(2, 2, 'SMS', 'URGENT: Your loan is 3 days overdue. Late fees have been applied.', CURRENT_DATE - INTERVAL '7 days', 'SENT'),
(2, 2, 'SMS', 'FINAL WARNING: Your loan is 7 days overdue.', CURRENT_DATE - INTERVAL '3 days', 'SENT'),
(2, 2, 'EMAIL', 'Your loan #2 is now 14 days overdue. Please contact us immediately.', CURRENT_DATE - INTERVAL '0 days', 'PENDING'),

-- Loan 3 notifications (Peter Otieno - closed)
(3, 3, 'SMS', 'Your loan of 10000.00 has been approved and disbursed.', CURRENT_DATE - INTERVAL '90 days', 'SENT'),
(3, 3, 'SMS', 'Payment of 11200.00 received for your loan. Thank you!', CURRENT_DATE - INTERVAL '60 days', 'SENT'),

-- Loan 4 notifications (James Mwangi)
(4, 4, 'SMS', 'Your loan of 50000.00 has been approved and disbursed.', CURRENT_DATE - INTERVAL '15 days', 'SENT'),
(4, 4, 'SMS', 'Reminder: Your loan payment is due in 3 days', CURRENT_DATE - INTERVAL '12 days', 'PENDING'),
(4, 4, 'EMAIL', 'Your education loan has been disbursed', CURRENT_DATE - INTERVAL '15 days', 'SENT'),

-- Loan 5 notifications (Mary Atieno)
(5, 5, 'SMS', 'Your loan of 200000.00 has been approved and disbursed.', CURRENT_DATE - INTERVAL '60 days', 'SENT'),
(5, 5, 'SMS', 'A fee of 5000.00 has been applied to your loan.', CURRENT_DATE - INTERVAL '60 days', 'SENT'),
(5, 5, 'SMS', 'Payment of 6944.44 received for your loan. Thank you!', CURRENT_DATE - INTERVAL '58 days', 'SENT'),
(5, 5, 'SMS', 'Payment of 6944.44 received for your loan. Thank you!', CURRENT_DATE - INTERVAL '2 days', 'SENT'),

-- Loan 6 notifications (John Doe - new loan)
(1, 6, 'SMS', 'Dear John, your loan of 15000.00 has been approved and disbursed.', CURRENT_DATE - INTERVAL '5 days', 'SENT');

-- =====================================================
-- 11. CREDIT NOTES SEED DATA
-- =====================================================
INSERT INTO credit_notes (loan_id, amount, reason, used, created_date, used_date, used_against_loan_id, notes) VALUES

-- Credit Note 1: Overpayment on Loan 1 (unused)
(1, 500.00, 'OVERPAYMENT', false, CURRENT_DATE - INTERVAL '5 days', NULL, NULL, 'Customer overpaid by 500.00, credit note issued'),

-- Credit Note 2: Fee reversal on Loan 2 (used)
(2, 250.00, 'FEE_REVERSAL', true, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '10 days', 2, 'Late fee reversal due to customer dispute'),

-- Credit Note 3: Manual adjustment on Loan 3 (unused)
(3, 100.00, 'ADJUSTMENT', false, CURRENT_DATE - INTERVAL '30 days', NULL, NULL, 'Manual adjustment for system error'),

-- Credit Note 4: Overpayment on Loan 4 (unused)
(4, 750.00, 'OVERPAYMENT', false, CURRENT_DATE - INTERVAL '10 days', NULL, NULL, 'Customer paid extra 750.00'),

-- Credit Note 5: Fee reversal on Loan 5 (used)
(5, 1000.00, 'FEE_REVERSAL', true, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '15 days', 5, 'Service fee reversal approved by manager');

-- =====================================================
-- UPDATE SEQUENCES (reset auto-increment counters)
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