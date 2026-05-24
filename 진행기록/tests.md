# 테스트 (2단계 · JUnit 5)

> 순수 로직에 단위 테스트를 붙여 CI(3단계)가 자동 검증할 토대 마련.
> 작업일: 2026-05-25 · 브랜치: `feature/tests` · 이전 단계: [gradle.md](gradle.md)

## 1. 왜 했나

CI는 PR/푸시마다 "자동으로 돌릴 검증"이 있어야 의미가 있다. 그런데 이 앱의 GUI(`MainFrame`)·전역 후킹(`AutoPasteService`)·`Robot`·클립보드는 **헤드리스(화면 없는) CI 환경에서 테스트 불가**다. 그래서 화면과 무관한 **순수 로직(설정 파싱)부터** 단위 테스트를 붙인다.

## 2. 무엇이 달라졌나

- 빌드에 **JUnit 5**가 들어왔고, `gradlew test`로 검증 가능.
- `AppConfig`의 CSV 파싱이 `load()`(파일 읽기) 안에 묻혀 있어 테스트하기 어려웠음 → **순수 함수 `parsePrograms(String)`로 추출**해 파일 없이 검증 가능하게 함.

## 3. 한 일

- **build.gradle:** JUnit 5 추가
  ```groovy
  testImplementation platform('org.junit:junit-bom:5.11.4')
  testImplementation 'org.junit.jupiter:junit-jupiter'
  testRuntimeOnly 'org.junit.platform:junit-platform-launcher'  // Gradle 9는 런처를 자동 제공하지 않음
  tasks.named('test') { useJUnitPlatform() }
  ```
- **AppConfig.java:** 파싱을 `static List<String> parsePrograms(String csv)`로 추출 (동작 동일, 테스트 가능화)
- **src/test/java/app/AppConfigTest.java:** 테스트 4개
  - 콤마로 분리 / 콤마 주변 공백 트림 / 단일 항목 / `load()`가 비어있지 않고 마지막 항목이 `"사용자 지정"`

## 4. 검증 결과

```
.\gradlew test  →  tests=4, failures=0, errors=0  ✅
```
리포트: `build/reports/tests/test/index.html`

## 5. 메모

- **CI 테스트 대상에서 제외(수동 확인 영역):** `MainFrame`(GUI), `AutoPasteService`(JNativeHook 네이티브 후킹), `ClipboardService`(AWT 시스템 클립보드 — 헤드리스에서 `HeadlessException`).
- 새 테스트는 `src/test/java/app/`에 `*Test.java`로 두면 `gradlew test`가 자동 수집.
- **다음(3단계 CI):** GitHub Actions에서 `gradlew build`(테스트 포함)를 push/PR마다 자동 실행.
