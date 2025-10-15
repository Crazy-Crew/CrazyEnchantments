## Fixes
- Fixed an issue on startup where config#getString("Player") was null
  - getStringList() returns an empty list, but getString() throws an NPE, rather than an empty String.
  - The solution was `config#getString("Player", "")` which is so very amusing.