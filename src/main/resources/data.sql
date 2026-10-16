-- =============================================================================
-- Insurance Policy Management Platform - Initial Test Data
-- =============================================================================
-- This file contains sample data for customers and risks for testing purposes.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Insert Sample Customers
-- -----------------------------------------------------------------------------
INSERT INTO customers (customer_id, email, first_name, last_name, phone, created_at, updated_at)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'john.doe@email.com', 'John', 'Doe', '+1-555-0101', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440002', 'jane.smith@email.com', 'Jane', 'Smith', '+1-555-0102', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440003', 'mike.johnson@email.com', 'Michael', 'Johnson', '+1-555-0103', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440004', 'sarah.williams@email.com', 'Sarah', 'Williams', '+1-555-0104', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440005', 'david.brown@email.com', 'David', 'Brown', '+1-555-0105', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- -----------------------------------------------------------------------------
-- Insert Sample Risks
-- -----------------------------------------------------------------------------
INSERT INTO risks (risk_id, risk_name, risk_category, risk_severity, status, description, created_at, updated_at)
VALUES 
    ('660e8400-e29b-41d4-a716-446655440001', 'Collision', 'ACCIDENT', 'HIGH', 'ACTIVE', 'Damage to vehicle from collision with another vehicle or object', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440002', 'Theft', 'THEFT', 'MEDIUM', 'ACTIVE', 'Loss of vehicle due to theft or attempted theft', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440003', 'Natural Disaster', 'NATURAL_DISASTER', 'HIGH', 'ACTIVE', 'Damage from floods, earthquakes, hurricanes, or other natural events', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440004', 'Explosion', 'EXPLOSION', 'HIGH', 'ACTIVE', 'Damage from explosive events affecting insured property', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440005', 'Vandalism', 'VANDALISM', 'MEDIUM', 'ACTIVE', 'Intentional damage to vehicle by malicious parties', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440006', 'Glass Damage', 'ACCIDENT', 'LOW', 'ACTIVE', 'Damage to vehicle windows and windshield', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440007', 'Fire Damage', 'FIRE', 'HIGH', 'ACTIVE', 'Coverage for fire-related losses on insured assets', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440008', 'Comprehensive', 'NATURAL_DISASTER', 'MEDIUM', 'ACTIVE', 'Coverage for non-collision damages including weather and animals', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (risk_name) DO NOTHING;

-- -----------------------------------------------------------------------------
-- Note: Policies are typically created through API endpoints and will reference
-- existing customer and risk IDs. This allows for dynamic policy creation during
-- testing while ensuring referential integrity.
-- =============================================================================
