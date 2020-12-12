# Development
Cooperation is welcome!

## How to contribute

1. Logon to Github and Fork this repository
2. start Android Studio (4.0.1)
3. File > New > Project from version control
4. select "Github" on left side
5. paste URL of your Github repository, optional select folder
6. Clone button
7. (open project in Android Studio...)
8. press Shift Shift, enter "new branch" -> enter branch name (e.g. "feature-95") -> press Create
9. (development)
10. commit and push your changes to the new branch to your repository
11. go to https://github.com/SoltauFintel/blockpuzzle -> There should be a button to create the Pull Request.

## Versionierung (DE)

versionName: Major Version + "." + Bugfix Version

versionCode: Major Version * 10 + Bugfix Version

Die Major Version wird bei jeder Weiterentwicklung inkrementiert.
Die Bugfix Version wird nur bei Bugfixes hochgezählt. 10 Bugfixes (0-9) sollten doch reichen.
Eine der Major Version übergeordnete Versionsnummer würde durch Namenserweiterung/-änderung
kenntlich gemacht.
Die fastlane/../changelogs Dateien heißen versionCode + ".txt"

Beispiel: versionName = 12.0, versionCode: 120, changelog: 120.txt

## Veröffentlichung (DE)

- versionCode und versionName hochzählen
- alles pushen

### F-Droid
- Fastlane Dateien aktualisieren (Neue Datei für changelogs)
- einfach nur auf Github taggen

### Google Play
- Google Play Console aufrufen
- Block Puzzle Stone Wars wählen
- Produktion
- Reiter: Releases
- Build > Generate Signed Bundle/APK verwenden > APK. D:\dev\AndroidKeystores\android12_2020.jks. V1 und V2 anhaken.
- Dann die APK Datei im Browser per Drag&Drop hochladen und die weiteren Schritte durchgehen.
