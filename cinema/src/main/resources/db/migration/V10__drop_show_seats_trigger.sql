-- Drop the trigger that auto-creates show seats
-- ShowService.generateShowSeats() handles this with proper pricing logic (VIP, Weekend, Couple)
DROP TRIGGER IF EXISTS trigger_create_show_seats ON shows;
DROP FUNCTION IF EXISTS create_show_seats_for_show();
