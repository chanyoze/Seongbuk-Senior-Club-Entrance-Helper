# Seongbuk-Senior-Club-Entrance-Helper

[![CI](https://github.com/chanyoze/Seongbuk-Senior-Club-Entrance-Helper/actions/workflows/ci.yml/badge.svg)](https://github.com/chanyoze/Seongbuk-Senior-Club-Entrance-Helper/actions/workflows/ci.yml)

> **⬇️ 다운로드:** [다운로드 페이지](https://chanyoze.github.io/Seongbuk-Senior-Club-Entrance-Helper/) · [최신 zip 직접 받기](https://github.com/chanyoze/Seongbuk-Senior-Club-Entrance-Helper/releases/latest/download/EntranceHelper.zip)
> Windows · 자바 설치 불필요 · 압축 풀고 `EntranceHelper.exe` 실행

복지관 출입도우미 헬퍼

사용 언어: JAVA

코로나 기간 동안 들어오시는 모든 어르신들의 성함과 전화번호, 체온, 방문 목적 등을 기록하는 단순 반복이 계속 이어졌기 때문에 이를 수기로 매번 입력하지 않을 수 있게 하고자 제작

버튼에 키보드 이벤트 명령어를 할당해 자동으로 복사, 붙여 넣기를 할 수 있게 하여 마우스만 사용해서 시간을 훨씬 단축할 수 있게 되었음

## v2

- 외부 출석관리 프로그램(`ATTEND_RF.exe`) 자동 실행 기능 제거 — 설치 경로 차이로 매번 뜨던 실행 실패 다이얼로그를 원천 해소
- 버튼 클릭 후 다음 좌클릭 시 자동 붙여넣기 + 클립보드 초기화 — 사용자가 Ctrl+V 따로 누를 필요 없음
- 구현: JNativeHook(전역 마우스 후킹) + `java.awt.Robot`(키 입력 시뮬레이션); 후킹 초기화 실패 시 자동 비활성화로 폴백
