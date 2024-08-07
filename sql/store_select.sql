SELECT
    u.name,
    u.email,
    SUM(p.price) AS sum
FROM users u
JOIN orders o ON u.id = o.user_id
JOIN products p ON p.id = o.product_id
WHERE p.category = 'electronics'
GROUP BY u.name, u.email
HAVING COUNT(o.product_id) >= 3 AND SUM(p.price) > 1000
ORDER BY sum DESC;
