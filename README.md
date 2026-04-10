# 🎁 Christmas Presents Plugin

Plugin do zbierania świątecznych prezentów na serwerze Minecraft.

## 🎮 Komendy

| Komenda | Opis | Permissja |
|---------|------|-----------|
| `/prezent give [gracz]` | Daje prezent graczowi | `svhard.christmas.presents.give` |
| `/prezent remove <id>` | Usuwa prezent (ID numeryczne) | `svhard.christmas.presents.admin` |
| `/prezent list` | Lista wszystkich prezentów | `svhard.christmas.presents.admin` |
| `/prezent reload` | Przeładowuje konfigurację | `svhard.christmas.presents.admin` |

**Aliasy:** `/present`, `/gift`

## 🔑 Permissje

| Permissja | Opis |
|-----------|------|
| `svhard.christmas.presents.use` | Zbieranie prezentów |
| `svhard.christmas.presents.give` | Dawanie prezentów |
| `svhard.christmas.presents.admin` | Zarządzanie prezentami |

## ✨ Funkcje

### Dla Graczy
- ✅ Zbieranie prezentów z mapy
- ✅ Per-player rendering (każdy widzi tylko te, których nie zebrał)
- ✅ Animacje cząsteczek wokół prezentów
- ✅ Nagrody za każdy prezent
- ✅ Nagroda specjalna za zebranie wszystkich

### Dla Adminów
- ✅ Numeryczne ID (1, 2, 3...)
- ✅ Usuwanie prezentów komendą
- ✅ Usuwanie prezentów poprzez **Shift+Klik** (wymaga potwierdzenia)
- ✅ Konfigurowalne efekty fajerwerków
- ✅ Real-time aktualizacja stanu prezentów

## 📖 Jak używać?

### Gracz
1. Znajdź prezent na mapie (główka z animacją)
2. Kliknij prawym przyciskiem lub zniszcz blok
3. Odbierz nagrody!

### Admin - Dawanie prezentów
```
/prezent give           # Daje prezent sobie
/prezent give Notch     # Daje prezent graczowi Notch
```
Otrzymana główka może być postawiona gdziekolwiek na mapie.

### Admin - Usuwanie prezentów

**Metoda 1: Komenda**
```
/prezent list           # Zobacz listę z numerycznymi ID
/prezent remove 1       # Usuń prezent o ID 1
```

**Metoda 2: Interakcja**
1. Kucnij (Shift)
2. Kliknij prawym na główkę prezentu
3. Pojawi się komunikat potwierdzenia
4. Kliknij ponownie (Shift+Prawy) w ciągu 5 sekund
5. Prezent zostanie usunięty
