-- Enable Row Level Security
ALTER TABLE owner ENABLE ROW LEVEL SECURITY;
ALTER TABLE staff ENABLE ROW LEVEL SECURITY;
ALTER TABLE animal ENABLE ROW LEVEL SECURITY;
ALTER TABLE appointment ENABLE ROW LEVEL SECURITY;

-- Create Policies
-- Note: current_setting('app.current_clinic_id', true) returns null if not set, 
-- effectively hiding all rows if the session variable is missing (fail-safe).

-- Owner Policy
CREATE POLICY clinic_isolation_owner ON owner
    USING (clinic_id = current_setting('app.current_clinic_id', true)::bigint);

-- Staff Policy (Staff see other staff in same clinic)
CREATE POLICY clinic_isolation_staff ON staff
    USING (clinic_id = current_setting('app.current_clinic_id', true)::bigint);

-- Animal Policy (Indirect link via Owner)
CREATE POLICY clinic_isolation_animal ON animal
    USING (owner_id IN (
        SELECT owner_id FROM owner 
        WHERE clinic_id = current_setting('app.current_clinic_id', true)::bigint
    ));

-- Appointment Policy (Indirect link via Animal -> Owner)
CREATE POLICY clinic_isolation_appointment ON appointment
    USING (animal_id IN (
        SELECT a.animal_id 
        FROM animal a
        JOIN owner o ON a.owner_id = o.owner_id
        WHERE o.clinic_id = current_setting('app.current_clinic_id', true)::bigint
    ));
