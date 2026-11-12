INSERT INTO orders (order_number, ordered_at, item, quantity, price_cents)
SELECT
    (g + 2) / 3,
    TIMESTAMPTZ '2026-01-01 07:00:00+00' + ((g + 2) / 3) * INTERVAL '1 minute',
    (ARRAY['latte', 'cappuccino', 'espresso', 'drip coffee', 'chai', 'croissant', 'muffin', 'scone'])[1 + (g * 7) % 8],
    1 + (g % 3),
    (ARRAY[450, 425, 300, 250, 400, 375, 325, 350])[1 + (g * 7) % 8]
FROM generate_series(1, 400000) AS g;