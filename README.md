System architecture
LMSv2/
│
├─ src/
│   ├─ LibrarySystem.java
│   ├─ Library.java
│   ├─ User.java
│   ├─ Book.java
│   ├─ IssuedBookInfo.java
│   └─ Log.java
│
├─ data/                <- JSON files
│   ├─ users.json
│   ├─ books.json
│   ├─ issuedbooks.json
│   ├─ logs.json
│   └─ userCounter.json   <- stores countUser for unique IDs
│
├─ lib/                  <- json-simple jar (or json.jar)
├─ .vscode/
└─ README.md
