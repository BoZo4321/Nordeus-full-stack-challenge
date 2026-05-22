# RPG Full Stack Challenge — Nordeus Job Fair 2026

A turn-based RPG where the player controls a knight fighting through a gauntlet of 5 monsters. Built as a Full Stack project — Unity frontend and Spring Boot backend with PostgreSQL.

---

## Technologies

- **Frontend:** Unity 6 (C#) — Standalone Windows application
- **Backend:** Java Spring Boot
- **Database:** PostgreSQL 16
- **Containerization:** Docker / Docker Compose

---

## Running the Project

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- Windows OS (for the .exe frontend)

### 1. Start the backend and database

```bash
cd backend
docker compose up --build
```

This will start:
- PostgreSQL database on port `5432`
- Spring Boot backend on port `8080`

The backend is ready when you see:
```
Started RpgBackendApplication
```

### 2. Start the frontend

Download the `.exe` from [GitHub Releases](../../releases) and run `RpgGame.exe`.

> **Note:** The backend must be running before launching the game.

### Shutdown

```bash
docker compose down
```

Database data is preserved between sessions. To wipe all data:

```bash
docker compose down -v
```

---

## How to Play

### Main Menu
- **Start** — begin a new run
- **Exit** — quit the game

### Map Screen
- Shows all 5 monsters to defeat in order
- Click an encounter to start a battle
- **Choose Moves** opens the Move Management screen to swap equipped moves
- Defeated monsters can be challenged again for XP and move farming

### Battle Screen
- Hero and monster take turns using moves
- Choose one of your 4 equipped moves each turn
- HP bars show the current state of both combatants
- Battle log tracks everything that happens in the fight
- **Hover** over a move to see its tooltip description

### Post Battle
- **Victory:** Earn XP and learn a random move from the defeated monster
- **Defeat:** Retry the fight or return to the map

### Progression
- Every battle awards XP
- At 100 XP the hero levels up — Attack, Defense, Health and Magic all increase
- Learned moves are available in the Move Management screen before the next fight

---

## Game Systems

### Stats
| Stat | Description |
|------|-------------|
| Health | Hit points — reaching 0 means defeat |
| Attack | Scales physical damage moves |
| Defense | Reduces incoming physical damage |
| Magic | Scales magic damage and healing |

### Move Types
- **Physical** — scales with Attack, reduced by target's Defense
- **Magic** — scales with Magic, bypasses Defense entirely
- **Support** — buffs, debuffs, healing

### Monsters (in order)
1. Goblin Warrior
2. Giant Spider
3. Goblin Mage
4. Witch
5. Dragon

---

## Building the Frontend (for developers)

If you want to build the frontend yourself:

1. Install [Unity 6](https://unity.com/download) with the **Windows Build Support** module
2. Open the `frontend/` folder as a Unity project
3. **File → Build Profiles → Windows → Build**
4. Run the generated `.exe`