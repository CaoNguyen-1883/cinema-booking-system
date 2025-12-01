-- Function to automatically create show_seats when a show is created
CREATE OR REPLACE FUNCTION create_show_seats_for_show()
RETURNS TRIGGER AS $$
DECLARE
    seat_record RECORD;
    calculated_price DECIMAL(10, 2);
BEGIN
    FOR seat_record IN
        SELECT id, seat_type
        FROM seats
        WHERE hall_id = NEW.hall_id AND status = 'ACTIVE'
    LOOP
        -- Calculate price based on seat type
        calculated_price := NEW.base_price;
        
        IF seat_record.seat_type = 'VIP' THEN
            calculated_price := NEW.base_price * 1.5;
        ELSIF seat_record.seat_type = 'COUPLE' THEN
            calculated_price := NEW.base_price * 2.0;
        END IF;

        INSERT INTO show_seats (show_id, seat_id, price, status)
        VALUES (NEW.id, seat_record.id, calculated_price, 'AVAILABLE');
    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_create_show_seats
AFTER INSERT ON shows
FOR EACH ROW
EXECUTE FUNCTION create_show_seats_for_show();

-- Function to update timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply update trigger to all tables with updated_at
CREATE TRIGGER trigger_users_updated_at BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_cinemas_updated_at BEFORE UPDATE ON cinemas
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_halls_updated_at BEFORE UPDATE ON halls
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_seats_updated_at BEFORE UPDATE ON seats
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_movies_updated_at BEFORE UPDATE ON movies
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_shows_updated_at BEFORE UPDATE ON shows
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_show_seats_updated_at BEFORE UPDATE ON show_seats
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_bookings_updated_at BEFORE UPDATE ON bookings
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_payments_updated_at BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Function to generate booking code
CREATE OR REPLACE FUNCTION generate_booking_code()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.booking_code IS NULL THEN
        NEW.booking_code := 'BK' || TO_CHAR(CURRENT_TIMESTAMP, 'YYMMDD') || 
                           LPAD(NEXTVAL('bookings_id_seq')::TEXT, 6, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_generate_booking_code
BEFORE INSERT ON bookings
FOR EACH ROW
EXECUTE FUNCTION generate_booking_code();
