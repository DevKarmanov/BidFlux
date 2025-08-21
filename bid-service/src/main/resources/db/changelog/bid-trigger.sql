CREATE OR REPLACE FUNCTION check_bid_amount()
    RETURNS trigger AS $$
DECLARE
    last_amount numeric(19,4);
BEGIN
    SELECT COALESCE(MAX(amount), 0) INTO last_amount
    FROM bid
    WHERE auction_id = NEW.auction_id;

    IF NEW.amount <= last_amount THEN
        RAISE EXCEPTION 'Bid must be greater than previous bid: %', last_amount;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER bid_amount_trigger
    BEFORE INSERT ON bid
    FOR EACH ROW
EXECUTE FUNCTION check_bid_amount();
