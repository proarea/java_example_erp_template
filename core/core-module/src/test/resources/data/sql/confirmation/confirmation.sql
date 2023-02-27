INSERT INTO confirmation(user_id, code, expiration_time, created_at)
VALUES (2, '$2a$10$oM4z0xhsmW5jhdoevowF7udowhBtxlPJlcYPvv4Pe5fgj3QyHI1Ly',
        now() AT TIME ZONE 'UTC' + interval '15 minutes', now() AT TIME ZONE 'UTC');
