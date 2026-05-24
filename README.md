# RideEase

RideEase is a JavaFX application for scheduling and paying for rides.

**Requirements:**
- JDK 17+ (or compatible)
- Maven (bundled wrapper available: `mvnw` / `mvnw.cmd`)

**Quick start**

1. Copy `.env.example` to `.env` and update values with your credentials and API keys.

2. Build the project (Windows):

```
.\mvnw.cmd clean package
```

3. Run the packaged application (replace `<artifact>.jar` with the produced JAR name):

```
java -jar target/<artifact>.jar
```

Or run from your IDE as a JavaFX application.

**Configuration (.env)**

The application reads runtime configuration from environment variables or a root `.env` file (if present). A `.env.example` file with placeholders is included. Key variables:

- `DB_URL`, `DB_USER`, `DB_PASSWORD` — database connection
- `GOOGLE_API_KEY` — Google Maps APIs
- `TRANSACTION_URL`, `STORE_ID`, `STORE_PASSWORD` — payment gateway
- `PAYMENT_SUCCESS_URL`, `PAYMENT_FAIL_URL`, `PAYMENT_CANCEL_URL` — payment callbacks

.env is ignored by Git by default; do not commit secrets.

**Images used by the UI**

The app references these image files under `src/main/resources/com/example/rideease/assets`:

- 5cdde1b7000f7857fa155eb7f6f7fd59.jpeg
- picture1.jpg
- wp1981801-rolls-royce-logo-wallpapers.JPG
- wallpaperflare.com_wallpaper.jpg
- wallpaperflare.com_wallpaper12.JPG

FXML files that reference images:

- `src/main/resources/com/example/rideease/dashboard.fxml`
- `src/main/resources/com/example/rideease/login.fxml`
- `src/main/resources/com/example/rideease/scene1.fxml`
- `src/main/resources/com/example/rideease/signup.fxml`
- `src/main/resources/com/example/rideease/myprofile.fxml`

If the image files are missing the UI falls back to CSS/gradient backgrounds so the app still opens and resizes cleanly.

**Notes about code changes**

- Configuration is loaded via `src/main/java/com/example/rideease/Config.java` which reads OS environment variables, system properties, and an optional root `.env` file.
- Database and API keys were moved from in-code constants into environment variables and `.env`:
	- `DatabaseConnection.java` now uses `Config.get("DB_URL", ...)`, etc.
	- `LocationSelectionController.java` now uses `Config.get(...)` for `GOOGLE_API_KEY`, payment and DB settings.

**Git / repository guidance**

- Binary assets and build-time copies are ignored by `.gitignore`:
	- `/src/main/resources/com/example/rideease/assets/*`
	- `/target/classes/com/example/rideease/assets/*`
	- `/.env`
- If you want images tracked in Git, remove the assets lines from `.gitignore`.

**Contributing**

Create issues or pull requests for fixes and feature requests. When adding sensitive keys for CI, use secret management rather than committing `.env`.

---

This file is the single README for the repository; the previous per-folder asset README has been consolidated here.