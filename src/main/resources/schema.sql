-- =============================================================================
-- Insurance Policy Management Platform - Database Schema
-- =============================================================================
-- This schema implements the core entities: Customer, Policy, Risk,
-- AuditLog, and policy_risk_mapping join table.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- customers Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
    customer_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_email_format CHECK (email LIKE '%@%.%'),
    CONSTRAINT chk_first_name_not_empty CHECK (first_name != ''),
    CONSTRAINT chk_last_name_not_empty CHECK (last_name != '')
);

CREATE INDEX IF NOT EXISTS idx_customer_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customer_created_at ON customers(created_at);

-- -----------------------------------------------------------------------------
-- policies Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS policies (
    policy_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    policy_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    coverage_amount NUMERIC(12, 2) NOT NULL,
    premium_amount NUMERIC(12, 2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    
    CONSTRAINT fk_policy_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT chk_policy_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'CANCELLED')),
    CONSTRAINT chk_coverage_amount CHECK (coverage_amount > 0),
    CONSTRAINT chk_premium_amount CHECK (premium_amount > 0),
    CONSTRAINT chk_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS idx_policy_customer_id ON policies(customer_id);
CREATE INDEX IF NOT EXISTS idx_policy_status ON policies(status);
CREATE INDEX IF NOT EXISTS idx_policy_created_at ON policies(created_at);
CREATE INDEX IF NOT EXISTS idx_policy_customer_status ON policies(customer_id, status);

-- -----------------------------------------------------------------------------
-- risks Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS risks (
    risk_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    risk_name VARCHAR(255) NOT NULL UNIQUE,
    risk_category VARCHAR(50) NOT NULL,
    risk_severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    description VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_risk_severity CHECK (risk_severity IN ('HIGH', 'MEDIUM', 'LOW')),
    CONSTRAINT chk_risk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'MONITORED')),
    CONSTRAINT chk_description_not_empty CHECK (description != '')
);

CREATE INDEX IF NOT EXISTS idx_risk_name ON risks(risk_name);
CREATE INDEX IF NOT EXISTS idx_risk_category ON risks(risk_category);
CREATE INDEX IF NOT EXISTS idx_risk_severity ON risks(risk_severity);
CREATE INDEX IF NOT EXISTS idx_risk_status ON risks(status);

-- -----------------------------------------------------------------------------
-- policy_risk_mapping Table (Join Table for Many-to-Many)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS policy_risk_mapping (
    policy_id UUID NOT NULL,
    risk_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (policy_id, risk_id),
    CONSTRAINT fk_mapping_policy FOREIGN KEY (policy_id) 
        REFERENCES policies(policy_id) ON DELETE CASCADE,
    CONSTRAINT fk_mapping_risk FOREIGN KEY (risk_id) 
        REFERENCES risks(risk_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_policy_risk_mapping_policy ON policy_risk_mapping(policy_id);
CREATE INDEX IF NOT EXISTS idx_policy_risk_mapping_risk ON policy_risk_mapping(risk_id);

-- -----------------------------------------------------------------------------
-- audit_log Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    operation VARCHAR(20) NOT NULL,
    user_id VARCHAR(100),
    old_values TEXT,
    new_values TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),

    CONSTRAINT chk_operation CHECK (operation IN ('CREATE', 'UPDATE', 'DELETE'))
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_log(user_id);

-- -----------------------------------------------------------------------------
-- core_retry_request Table
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS core_retry_request (
    retry_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(50) NOT NULL,
    payload TEXT NOT NULL,
    error_message VARCHAR(1000),
    retry_count INTEGER NOT NULL DEFAULT 0,
    next_retry_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_core_retry_status_time ON core_retry_request(status, next_retry_time);

-- =============================================================================
-- End of Schema
-- =============================================================================