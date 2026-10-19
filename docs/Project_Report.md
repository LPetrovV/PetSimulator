# Pet Simulator – Project 2 Report

**GitHub:** https://github.com/LPetrovV/PetSimulator.git
**Team:** Lisa Petrov, AJ Franchuk

## 1. Project setup

| Item | Value |
|---|---|
| IDE | Apache NetBeans (Ant project – open the `PetSimulator` folder) |
| JDK | 21 or newer (project is set to 26; Derby 10.17 needs 21+) |
| Main class | `petsimulator.Main` |
| Database | Apache Derby 10.17.1.0, **embedded** (no server to start) |
| JDBC URL | `jdbc:derby:database/PetSimulatorDB;create=true` |
| DB user name | `petsim` |
| DB password | `petsim` |
| DB schema | `PETSIM` |
| Libraries | `lib/derby-10.17.1.0.jar`, `lib/derbyshared-10.17.1.0.jar`, `lib/derbytools-10.17.1.0.jar` (JUnit 4.13.2 + Hamcrest 1.3 for tests) |

**To run:** open the project in NetBeans and press Run (F6). The libraries are
already referenced from `lib/`, so nothing needs to be added by hand. On the
first run the game creates `database/PetSimulatorDB` inside the project folder
and builds all four tables automatically.

**To run the unit tests:** Test Project (Alt+F6). The tests use a separate
database in `test-database/`, so they never touch a real saved game.

**Viewing the database in NetBeans:** close the game first (embedded Derby only
allows one program to open the database at a time). Then Services → Databases →
New Connection → *Java DB (Embedded)*, select the three Derby jars from `lib/`,
use the JDBC URL above with the full path to the `database/PetSimulatorDB`
folder, user `petsim`, password `petsim`.

## 2. Database design

All database code is in two classes:

* **`DatabaseManager`** – opens the JDBC connection, creates any missing
  tables (checked with `DatabaseMetaData`), and shuts Derby down cleanly when
  the program exits (shutdown hook).
* **`SaveSystem`** – every SQL statement in the game. It uses
  `PreparedStatement` for all values, so names typed by the player can never
  break the SQL.

| Table | Purpose | Used by |
|---|---|---|
| `GAME_SAVE` | Player name and coins, pet type/name/age/needs, apple tree, adoption time and last-save time | Save & Quit, Load Game, start screen ("Save file found") |
| `INVENTORY` | One row per item the player owns. Foreign key to `GAME_SAVE` with `ON DELETE CASCADE` | Save & Quit, Load Game |
| `ITEM_CATALOG` | Starting items for a new game (type, name, price, buff, quantity). Filled with defaults on first run; can be edited in the DB to change the game without changing code | New Game |
| `ACTIVITY_LOG` | Timestamped history of NEW GAME / SAVE / LOAD / LOST events | Every new game, save, load and lost pet |

**How JDBC drives the game's features**

| Feature | SQL |
|---|---|
| Start screen shows whether there is a save | `SELECT COUNT(*) FROM GAME_SAVE` |
| New Game – starting items | `SELECT ... FROM ITEM_CATALOG` (batch `INSERT` of defaults on first run) |
| Save & Quit | One transaction: `DELETE` old save → `INSERT` `GAME_SAVE` → batch `INSERT` `INVENTORY` → `COMMIT` (rolls back on error) |
| Load Game | `SELECT` from `GAME_SAVE` and `INVENTORY`, rebuilt into `Player`, `Pet`, `AppleTree`, `TimeSystem` |
| Pet leaves (game over) | `DELETE FROM GAME_SAVE` (inventory removed by cascade) |
| Activity history | `INSERT INTO ACTIVITY_LOG`, read back with `ORDER BY ... FETCH FIRST n ROWS ONLY` |

## 3. Contribution of each team member

> **Fill this in yourselves** – be accurate; the course penalises anyone under
> 40% of the coding.

| Member | Contribution | % |
|---|---|---|
| Lisa Petrov | _e.g. GUI (StartScene, GameSceneGUI, NewGamePromptGUI, GameMessageGUI), GameController, actions…_ | |
| AJ Franchuk | _e.g. SaveSystem / SaveData, TimeSystem, Derby database layer (DatabaseManager, JDBC SaveSystem), unit tests…_ | |

## Appendix A – AI interaction log

See [`AI_Interaction_Log.md`](AI_Interaction_Log.md).

## Appendix B – Reflective summary on AI use

See [`AI_Reflection.md`](AI_Reflection.md) — **must be written in your own words.**
