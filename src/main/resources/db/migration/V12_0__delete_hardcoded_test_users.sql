-- Delete hardcoded test users (djdefkon and paula)

-- Step 1: Delete drugs owned by these users (if any)
DELETE FROM drugs
WHERE user_id IN (
    SELECT user_id FROM app_user
    WHERE email IN ('djdefkon@gmail.com', 'paula.konarska@gmail.com')
);

-- Step 2: Remove user-role associations (foreign key constraint)
DELETE FROM app_user_role
WHERE user_id IN (
    SELECT user_id FROM app_user
    WHERE email IN ('djdefkon@gmail.com', 'paula.konarska@gmail.com')
);

-- Step 3: Delete the users
DELETE FROM app_user
WHERE email IN ('djdefkon@gmail.com', 'paula.konarska@gmail.com');
