CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number INTEGER NOT NULL,
    ordered_at TIMESTAMPTZ NOT NULL,
    item TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    price_cents INTEGER NOT NULL
);