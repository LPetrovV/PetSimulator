# Appendix A – AI Interaction Log

**Tool:** Claude (Anthropic), Claude Code desktop app
**Date:** 2026-10-20
**Used by:** AJ Franchuk
**Scope:** the Derby/JDBC database layer only. The rest of the game was
written by the team before this session.

Every place AI-written code ended up in the project is marked in the source
with a comment that starts `//AI-assisted (Claude):`. Each entry below points
to those comments so the log and the code match.

> Before submitting, read each entry and fix the **Decision** column if you
> changed or threw away anything after this session.

---

### Entry 1 – Plan the database

**Prompt (verbatim, summarised for length):**
> "My teammate has already built the rest of the stuff, we just need to build
> the database. The files need to save to an embedded Derby Java database and
> use GitHub to push. Here's what's required: the program contains a database
> element. Database interactions and operations (input and output) using
> JDBC/Hibernate are part of the code. JDBC/Hibernate must contribute to the
> functionalities of the project…" (plus the Canvas submission instructions
> and the GitHub URL)

**AI output:** Read the existing code and found that all file I/O was already
in one class, `SaveSystem` (save file, item list, activity log). Proposed
replacing the text files with four Derby tables and keeping `SaveSystem`'s
public methods so `Game` and `GameController` would barely change.

| Suggestion | Decision | Why |
|---|---|---|
| Use plain JDBC instead of Hibernate | **Accepted** | Only four small tables; Hibernate would add many jars and config for no gain |
| Embedded Derby, DB folder `database/` inside the project | **Accepted** | Matches the brief (DB folder must be in the ZIP) |
| Derby network server mode | **Rejected** | Brief asks for embedded; network mode needs a server started first |
| Store the item list as one text column, like the old save file | **Rejected** | Replaced with a separate `INVENTORY` table + foreign key – proper relational design |
| Commit the database files to GitHub | **Rejected** | DB files change on every run; `database/` is in `.gitignore` and goes in the ZIP instead |

---

### Entry 2 – `DatabaseManager` (connection + tables)

**Code comment:** `DatabaseManager.java`, class header – *"the table layout and
the check DatabaseMetaData before CREATE TABLE approach were suggested by AI"*

**AI output:** a class holding the JDBC URL, user/password (`petsim`/`petsim`),
and `createTableIfMissing()` which looks the table up with
`DatabaseMetaData.getTables()` before running `CREATE TABLE`.

| Suggestion | Decision | Why |
|---|---|---|
| Check metadata before `CREATE TABLE` | **Accepted** | Safer than catching the "table already exists" error (X0Y32) |
| Second constructor taking a DB path | **Accepted** | Lets the unit tests use their own database |
| Send `derby.log` into the database folder | **Accepted** | Keeps the project root clean |
| `ON DELETE CASCADE` on `INVENTORY` | **Accepted** | Deleting a save removes its items in one statement |

---

### Entry 3 – Shutting Derby down

**Code comment:** `DatabaseManager.addShutdownHook()` – *"the window closes with
EXIT_ON_CLOSE … A JVM shutdown hook runs on every exit, including the X button."*

**AI output:** noticed the window uses `EXIT_ON_CLOSE`, so there is no single
GUI method where the DB could be closed. Added a shutdown hook calling
`jdbc:derby:;shutdown=true` and ignoring the expected `XJ015` "success" error.

**Decision:** **Accepted.**

---

### Entry 4 – Rewrite `SaveSystem` with JDBC

**Code comment:** `SaveSystem.java`, class header – *"The JDBC version was
written with AI help, keeping the same public methods…"*

**AI output:** every method rewritten with `PreparedStatement`:
`exists`, `save` (was `saveToFile`), `loadSave`, `deleteSave`, `loadItems`,
`seedDefaultItems` (was `writeDefaultItems`), `appendLog`, `readLog`.

| Suggestion | Decision | Why |
|---|---|---|
| Save inside one transaction (`setAutoCommit(false)` → commit / rollback) | **Accepted** | A failed save can never leave a save with the wrong items |
| `PreparedStatement` for all values | **Accepted** | Player and pet names come from the user – prevents SQL injection |
| `fit()` to cut text to the column size | **Accepted** | A 300-character name used to make the INSERT fail |
| Rename `saveToFile` → `save` | **Accepted** – one-line change in `Game.java` | Old name no longer true |
| Keep `rebuildItems` and the `"FoodItem:apple:2:10"` format in `SaveData` | **Accepted** | Means `SaveData` did not need to change |

---

### Entry 5 – Reading the newest log rows

**Code comment:** `SaveSystem.readLog()` – *"FETCH FIRST n ROWS ONLY is the Derby
version of LIMIT…"*

**AI output:** `ORDER BY LOG_ID DESC FETCH FIRST ? ROWS ONLY`, then reverse the
list in Java so the newest n entries come back oldest-first.

**Decision:** **Accepted.** (`LIMIT` does not exist in Derby.)

---

### Entry 6 – Unit tests and project config

**Code comment:** `test/petsimulator/SaveSystemTest.java`, class header

**AI output:** 8 JUnit 4 tests (empty DB, save/load round trip, overwrite,
item classes, cascade delete, catalog seeding only once, log order, long
names). Added the Derby and JUnit jars to `lib/` and to
`nbproject/project.properties`, and changed `main.class` from
`petsimulator.Game` (which has no `main` method) to `petsimulator.Main`.

**Result:** all 8 tests pass. A manual end-to-end check (new game → quit →
restart → load) showed the player, pet, inventory and log all come back.

**Decision:** **Accepted.**

---

### Issues the AI spotted but did not change

* `GameController.checkNewGameRequirements()` calls
  `model.newGame(playerName, petName)` but `Game.newGame` is declared as
  `newGame(String petName, String playerName)` – so the pet gets the player's
  name. Left for the team to fix (outside the database work).
