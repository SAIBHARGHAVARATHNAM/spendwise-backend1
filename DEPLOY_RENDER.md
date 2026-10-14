# SpendWise backend — Render + Aiven MySQL

1. Upload the contents of this directory to the **spendwise-backend** GitHub repository root (where pom.xml lives).
2. In Aiven MySQL, check the host, port, database, user, and password. Create the database first if needed. Ensure the Aiven service allows connections from Render; use Aiven trusted CA / certificate validation for production if available (the default configuration requires TLS but does not pin the CA).
3. Render → New → Web Service → select backend repository → Language: Docker → Dockerfile path: ./Dockerfile.
4. Add environment variables: DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD, JWT_SECRET (at least 32 random characters), FRONTEND_URL (the exact https://...vercel.app URL, no trailing slash).
5. Deploy. The /api endpoints require authentication except /api/auth/**. A 401 from a protected API route means the server is responding.
6. When Vercel assigns your frontend domain, update FRONTEND_URL in Render and redeploy.

Never commit passwords or JWT_SECRET to GitHub.
