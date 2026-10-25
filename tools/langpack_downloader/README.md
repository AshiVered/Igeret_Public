# Telegram LangPack Downloader

סקריפט להורדת תרגומים רשמיים של טלגרם באמצעות ה-MTProto API והמרתם לפורמט Android `strings.xml`.

## התקנה והרצה

### שלב 1: יצירת Virtual Environment

```bash
cd tools/langpack_downloader
python3 -m venv venv
source venv/bin/activate
pip install telethon
```

### שלב 2: הורדת התרגום העברי

```bash
python3 download_langpack.py --lang he
```

**בהרצה ראשונה** — Telethon יבקש:
1. מספר טלפון (של חשבון טלגרם כלשהו)
2. קוד אימות (שיגיע לטלגרם)

הסקריפט ייצור session שישמר לשימוש חוזר.

### שלב 3: הפלט

הקובץ יישמר ב:
```
tools/langpack_downloader/output/he/strings.xml
```

### פקודות נוספות

רשימת כל השפות הזמינות:
```bash
python3 download_langpack.py --list
```

הורדת שפה אחרת:
```bash
python3 download_langpack.py --lang fr
```

## שילוב בפרויקט (הוראות ידניות)

אחרי שהורדת את `strings.xml` העברי, יש לבצע 2 פעולות:

### 1. העתקת קובץ הסטרינגים

```bash
# מתיקיית הפרויקט הראשית:
mkdir -p TMessagesProj/src/main/res/values-iw
cp tools/langpack_downloader/output/he/strings.xml TMessagesProj/src/main/res/values-iw/strings.xml
```

> **הערה:** Android משתמש בקוד `iw` (ולא `he`) לעברית בתיקיות resources.

### 2. הוספת עברית כשפה מובנית

ב-`TMessagesProj/src/main/java/org/telegram/messenger/LocaleController.java`, 
הוסף את הבלוק הבא **אחרי** הבלוק של Russian (בסביבות שורה 683):

```java
localeInfo = new LocaleInfo();
localeInfo.name = "עברית";
localeInfo.nameEnglish = "Hebrew";
localeInfo.shortName = localeInfo.pluralLangCode = "he";
localeInfo.pathToFile = null;
localeInfo.builtIn = true;
localeInfo.isRtl = true;
languages.add(localeInfo);
languagesDict.put(localeInfo.shortName, localeInfo);
```

## הערות

- ה-API credentials נלקחים מ-`BuildVars.java` של הפרויקט
- הסטרינגים הם התרגום הרשמי של טלגרם (מ-translations.telegram.org)
- ניתן להריץ שוב בעתיד לעדכון התרגום
- קובץ ה-session (`langpack_session.session`) נשמר מקומית — **אל תעלה אותו לגיט**
