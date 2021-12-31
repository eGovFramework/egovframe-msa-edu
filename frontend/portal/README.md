# Frontend Portal

Next.js + Typescript 활용한 React 기반 프론트엔드.

## Getting Started

First, run the development server:

```bash
npm install

# 기본 소규모 모드
npm run dev

# 소규모 모드 for Windows
npm run dev:smWin
# 소규모 모드 for others
npm run dev:sm

# 대규모 모드 for Windows
npm run dev:lgWin
# 대규모 모드 for others
npm run dev:lg
```

run the local to production mode:

```bash
npm install
npm run build
npm run start
```

### 환경변수

- `.env.local.sample` 파일처럼 `.env.local` 파일을 생성하여 필요한 환경변수를 세팅한다.
- 사용하고 있는 환경변수는 `./src/constants/env.ts`, `./next.config.js` 파일을 확인한다.

## 소셜 로그인

소셜 로그인이 필요한 경우 `SOCIAL_LOGIN_ENABLED` 환경변수를 `true` 로 설정한다. (기본값: `false`)
아래를 참고하여 각 소셜 로그인의 key를 환경변수로 등록한다.

```typescript
// @constants/env.ts
export const GOOGLE_CLIENT_ID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID
export const KAKAO_JAVASCRIPT_KEY = process.env.NEXT_PUBLIC_KAKAO_JAVASCRIPT_KEY
export const NAVER_CLIENT_ID = process.env.NEXT_PUBLIC_NAVER_CLIENT_ID
export const NAVER_CALLBACK_URL = process.env.NEXT_PUBLIC_NAVER_CALLBACK_URL
```

**`SOCIAL_LOGIN_ENABLED=true`이고 각 key의 값이 존재하는 경우 각 소셜로그인의 버튼이 활성화 된다.**

> e.g.
> SOCIAL_LOGIN_ENABLED=true
> NEXT_PUBLIC_KAKAO_JAVASCRIPT_KEY=kakao

위 처럼 환경변수를 설정한 경우 카카오 로그인 버튼만 활성화 된다.
user-service 에서 backend 설정(`application.yml`)도 해주어야 한다.

## 폴더 구조

```bash
├─public                 # static resource root
│  ├─locales             # 다국어 message.json
│  └─styles				 # css + images
│
├─server                 # custom server
│  └─index.ts
│
├─src                   # source root
│   ├─@types                # type declaration
│   ├─components            # components
│   ├─constants             # 상수
│   ├─hooks                 # custom hooks
│   ├─libs                  # deps library custom
│   ├─pages                 # next.js page routing
│   │   ├─api               # next.js api routing
│   │   └─auth              # 로그인 관련
│   ├─service               # API 호출
│   ├─stores                # recoil 상태관리
│   ├─styles                # material ui theme 관리
│   └─utils                 # utils
│
├─test                      # test 관련
│
│ .babelrc              # babel config
│ .dockerignore         # docker ignore
│ .env.local            # environment variables
│ .eslintrc.js          # eslint config
│ .gitignore            # git ignore
│ .prettierrc.js        # prettier config
│ Dockerfile            # Docker 배포 시
│ jest.config.js        # jest config
│ jest.setup.ts         # jest에서 testing-library 사용하기 위한 설정(그외 jest에 필요한 외부 라이브러리 설정)
│ manifest.yml          # cf 배포 시
│ next-env.d.ts         # next.js type declare
│ next.config.js        # next.js 설정
│ package.json.
│ README.md
│ tsconfig.json          # typescirpt config
└ tsconfig.server.json   # custom server 사용 시 typescript config
```

### Tech stack

- [Next.js](https://nextjs.org/docs/getting-started)
- [Typescript](https://www.typescriptlang.org/docs/)
- [ESLint](https://eslint.org/) + [Prettier](https://prettier.io/) + [airbnb Style Guide](https://github.com/airbnb/javascript)
- [Jest](https://jestjs.io/docs/next/getting-started) + [testing-library](https://testing-library.com/docs/)
